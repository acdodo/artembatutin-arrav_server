package net.edge.net.message.impl;

import net.edge.net.codec.ByteMessage;
import net.edge.world.model.node.entity.player.Player;
import net.edge.net.message.InputMessageListener;

/**
 * The message sent from the client when an {@link Player} enters an idle state.
 * @author lare96 <http://github.com/lare96>
 */
public final class IdleStateMessage implements InputMessageListener {
	
	@Override
	public void handleMessage(Player player, int opcode, int size, ByteMessage payload) {
		
	}
}
