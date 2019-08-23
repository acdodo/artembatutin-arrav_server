package net.arrav.net.packet.out;

import net.arrav.content.TabInterface;
import net.arrav.net.codec.game.GamePacket;
import net.arrav.net.packet.OutgoingPacket;
import net.arrav.world.entity.actor.player.Player;

public final class SendForceTab implements OutgoingPacket {
	
	private final TabInterface tab;
	
	public SendForceTab(TabInterface tab) {
		this.tab = tab;
	}
	
	@Override
	public GamePacket write(Player player) {
		GamePacket out = new GamePacket(this);
		out.message(106);
		out.put(tab.getOld());
		out.put(tab.getNew());
		return out;
	}
}
