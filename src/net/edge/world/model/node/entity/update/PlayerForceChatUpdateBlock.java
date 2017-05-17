package net.edge.world.model.node.entity.update;

import net.edge.net.codec.ByteMessage;
import net.edge.world.model.node.entity.player.Player;

/**
 * An {@link PlayerUpdateBlock} implementation that handles the {@code FORCE_CHAT} update block.
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public final class PlayerForceChatUpdateBlock extends PlayerUpdateBlock {
	
	/**
	 * Creates a new {@link PlayerForceChatUpdateBlock}.
	 */
	public PlayerForceChatUpdateBlock() {
		super(4, UpdateFlag.FORCE_CHAT);
	}
	
	@Override
	public int write(Player player, Player mob, ByteMessage msg) {
		msg.putString(mob.getForcedText());
		return -1;
	}
}
