package net.arrav.world.entity.actor.update;

import net.arrav.net.codec.game.GamePacket;
import net.arrav.world.entity.actor.player.Player;

/**
 * An {@link PlayerUpdateBlock} implementation that handles the {@code FORCE_CHAT} update block.
 * @author Artem Batutin
 */
public final class PlayerForceChatUpdateBlock extends PlayerUpdateBlock {
	
	/**
	 * Creates a new {@link PlayerForceChatUpdateBlock}.
	 */
	public PlayerForceChatUpdateBlock() {
		super(4, UpdateFlag.FORCE_CHAT);
	}
	
	@Override
	public int write(Player player, Player other, GamePacket buf) {
		buf.putCString(other.getForcedText());
		return -1;
	}
}
