package net.edge.world.entity.actor.update;

import net.edge.net.codec.GameBuffer;
import net.edge.world.Hit;
import net.edge.world.entity.actor.player.Player;

/**
 * An {@link PlayerUpdateBlock} implementation that handles the secondary {@link Hit} update block.
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public final class PlayerSecondaryHitUpdateBlock extends PlayerUpdateBlock {

	/**
	 * Creates a new {@link PlayerSecondaryHitUpdateBlock}.
	 */
	public PlayerSecondaryHitUpdateBlock() {
		super(0x200, UpdateFlag.SECONDARY_HIT);
	}

	@Override
	public int write(Player player, Player other, GameBuffer msg) {
		Hit hit = other.getSecondaryHit();
		boolean local = other == player || (hit.hasSource() && hit.getSource() == player.getSlot());
		msg.putShort(hit.getDamage());
		msg.put(hit.getType().getId() + (!local ? 5 : 0));
		int change = msg.getBuffer().writerIndex() - 1;
		msg.put(hit.getIcon().getId());
		msg.putShort(hit.getSoak());
		msg.putShort(other.getMaximumHealth() / 10);
		msg.putShort(other.getCurrentHealth() / 10);
		if(local) {
			return change;
		}
		return -1;
	}
}