package net.edge.net.packet.out;

import io.netty.buffer.ByteBuf;
import net.edge.content.market.MarketItem;
import net.edge.net.codec.ByteOrder;
import net.edge.net.codec.ByteTransform;
import net.edge.net.codec.GameBuffer;
import net.edge.net.codec.PacketType;
import net.edge.net.packet.OutgoingPacket;
import net.edge.world.entity.actor.player.Player;

public final class SendShopStock implements OutgoingPacket {

	private final MarketItem item;

	public SendShopStock(MarketItem item) {
		this.item = item;
	}

	@Override
	public ByteBuf write(Player player, GameBuffer msg) {
		msg.message(55, PacketType.VARIABLE_SHORT);
		if(item.getStock() > 254) {
			msg.put(255);
			msg.putInt(item.getStock(), ByteOrder.INVERSE_MIDDLE);
		} else {
			msg.put(item.getStock());
		}
		msg.putShort(item.getId() + 1, ByteTransform.A, ByteOrder.LITTLE);
		msg.endVarSize();
		return msg.getBuffer();
	}
}
