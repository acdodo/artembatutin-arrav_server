package net.arrav.net.codec.login;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.arrav.GameConstants;
import net.arrav.net.Session;
import net.arrav.net.codec.crypto.IsaacRandom;
import net.arrav.net.host.HostListType;
import net.arrav.net.host.HostManager;
import net.arrav.util.StatefulFrameDecoder;
import net.arrav.util.TextUtils;
import net.arrav.world.World;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

/**
 * A {@link StatefulFrameDecoder} implementation of all login fragments.
 * @author Artem Batutin
 */
public class LoginDecoder extends StatefulFrameDecoder<LoginState> {
	
	/**
	 * A cryptographically secure random number generator.
	 */
	private static final Random RANDOM = new SecureRandom();
	
	/**
	 * The size of the last portion of the protocol.
	 */
	private int rsaBlockSize;
	
	/**
	 * Creates the stateful frame decoder with the specified initial state.
	 * @throws NullPointerException If the state is {@code null}.
	 */
	public LoginDecoder() {
		super(LoginState.HANDSHAKE);
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out, LoginState state) throws Exception {
		switch(state) {
			case HANDSHAKE:
				decodeHandshake(ctx, in, out);
				break;
			case LOGIN_HEADER:
				decodeHeader(ctx, in, out);
				break;
			case LOGIN_BLOCK:
				decodeLoginBlock(ctx, in, out);
				break;
		}
	}
	
	/**
	 * Decodes the handshake portion of the login protocol.
	 */
	private void decodeHandshake(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if(in.readableBytes() >= 1) {
			int build = in.get();
			if(build != GameConstants.CLIENT_BUILD) {
				write(ctx, LoginCode.WRONG_BUILD_NUMBER);
				return;
			}
			ByteBuf buf = ctx.alloc().buffer(9);
			buf.writeByte(0);
			buf.writeLong(RANDOM.nextLong());
			ctx.writeAndFlush(buf, ctx.voidPromise());
			setState(LoginState.LOGIN_HEADER);
			decodeHeader(ctx, in, out);
		}
	}
	
	/**
	 * Decodes the header portion of the login protocol.
	 */
	private void decodeHeader(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if(rsaBlockSize == 0 && in.readableBytes() >= 1) {
			//RSA size
			rsaBlockSize = in.readUnsignedByte();
			if(rsaBlockSize == 0) {
				write(ctx, LoginCode.COULD_NOT_COMPLETE_LOGIN);
				return;
			}
			decodeLoginBlock(ctx, in, out);
		}
	}
	
	/**
	 * Decodes the portion of the login protocol to successfully login.
	 */
	private void decodeLoginBlock(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if(in.readableBytes() >= rsaBlockSize) {
			int expectedSize = in.readUnsignedByte();
			if(expectedSize != rsaBlockSize - 1) {
				write(ctx, LoginCode.COULD_NOT_COMPLETE_LOGIN);
				return;
			}
			byte[] rsaBytes = new byte[rsaBlockSize - 1];
			in.readBytes(rsaBytes);
			byte[] rsaData = new BigInteger(rsaBytes).toByteArray();
			ByteBuf rsaBuffer = Unpooled.wrappedBuffer(rsaData);
			try {
				//keys
				long clientHalf = rsaBuffer.readLong();
				long serverHalf = rsaBuffer.readLong();
				int[] isaacSeed = {(int) (clientHalf >> 32), (int) clientHalf, (int) (serverHalf >> 32), (int) serverHalf};
				
				//ciphers
				IsaacRandom decryptor = new IsaacRandom(isaacSeed);
				for(int i = 0; i < isaacSeed.length; i++) {
					isaacSeed[i] += 50;
				}
				IsaacRandom encryptor = new IsaacRandom(isaacSeed);
				
				//mac address
				int macId = rsaBuffer.readInt();
				String macAddress = String.valueOf(macId);
				if(Session.validMac(macAddress) && HostManager.contains(macAddress, HostListType.BANNED_MAC)) {
					write(ctx, LoginCode.ACCOUNT_DISABLED);
					return;
				}
				
				String username = rsaBuffer.getCString().toLowerCase().replaceAll("_", " ").toLowerCase().trim();
				String password = rsaBuffer.getCString().toLowerCase();
				long usernameHash = TextUtils.nameToHash(username);
				
				if(World.get().getPlayer(usernameHash).isPresent()) {
					write(ctx, LoginCode.ACCOUNT_ONLINE);
					return;
				}
				
				ctx.pipeline().remove(this);
				out.add(new LoginRequest(usernameHash, username, password, encryptor, decryptor, macAddress));
			} finally {
				if(rsaBuffer.isReadable()) {
					rsaBuffer.release();
				}
			}
		}
	}
	
	/**
	 * Writes a closed response to the login channel.
	 */
	public static void write(ChannelHandlerContext ctx, LoginCode response) {
		Channel channel = ctx.channel();
		LoginResponse message = new LoginResponse(response);
		if(response == LoginCode.NORMAL) {
			ByteBuf initialMessage = ctx.alloc().buffer(9).writeLong(0); // Write initial message.
			channel.write(initialMessage, channel.voidPromise());
		}
		channel.writeAndFlush(message.toBuf(ctx)).addListener(ChannelFutureListener.CLOSE); // Write response message.
	}
	
}
