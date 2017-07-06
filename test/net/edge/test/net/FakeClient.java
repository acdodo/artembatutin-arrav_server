package net.edge.test.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.edge.net.NetworkConstants;
import net.edge.net.packet.PacketHelper;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

/**
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @since 6-7-2017.
 */
public final class FakeClient {
    private static final BigInteger RSA_MODULUS = new BigInteger("94306533927366675756465748344550949689550982334568289470527341681445613288505954291473168510012417401156971344988779343797488043615702971738296505168869556915772193568338164756326915583511871429998053169912492097791139829802309908513249248934714848531624001166946082342750924060600795950241816621880914628143");
    private static final BigInteger RSA_EXPONENT = new BigInteger("65537");

    public static void main(String[] args) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        SecureRandom random = new SecureRandom();

        Bootstrap b = new Bootstrap(); // (1)
        b.group(workerGroup); // (2)
        b.channel(NioSocketChannel.class); // (3)
        b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("handler", new ByteToMessageDecoder() {
                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        cause.printStackTrace();
                    }

                    private boolean loggedIn = false;

                    @Override
                    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
                        if (!loggedIn) {
                            ByteBuf rsa = ctx.alloc().buffer();

                            // rsa block prefix
                            rsa.writeByte(10);

                            // session keys
                            rsa.writeInt(random.nextInt());
                            rsa.writeInt(random.nextInt());
                            rsa.writeInt(random.nextInt());
                            rsa.writeInt(random.nextInt());

                            rsa.writeInt(0); // uid

                            PacketHelper.writeCString(rsa, "standhdhdh3");
                            PacketHelper.writeCString(rsa, "123456");

                            byte[] rsaBytes = new byte[rsa.readableBytes()];
                            rsa.readBytes(rsaBytes);

                            byte[] rsaData = new BigInteger(rsaBytes).modPow(RSA_EXPONENT, RSA_MODULUS).toByteArray();

                            ByteBuf payload = ctx.alloc().buffer();

                            payload.writeByte(255); // magic value
                            payload.writeShort(22); // revision
                            payload.writeBoolean(false); // low mem

                            for (int i = 0; i < 9; i++) {
                                payload.writeInt(0);
                            }

                            payload.writeByte(rsaData.length); // rsa block size
                            payload.writeBytes(rsaData);

                            ByteBuf out = ctx.alloc().buffer();
                            out.writeByte(16); // connection type
                            out.writeByte(payload.readableBytes()); // payload size
                            out.writeBytes(payload);

                            ctx.writeAndFlush(out, ctx.voidPromise());

                            loggedIn = true;
                        } else {
                            int pktId = in.readUnsignedByte();

                            System.out.println("received pkt id: " + pktId);
                        }
                    }
                });
            }
        });

        // Start the client.
        Channel f = b.connect("127.0.0.1", 43594).sync().channel(); // (5)

        ByteBuf buffer = f.alloc().buffer();

        buffer.writeByte(14);
        buffer.writeByte(0);

        f.writeAndFlush(buffer, f.voidPromise());
    }
}