package net.edge.world.entity.actor.update;

import net.edge.net.codec.GameBuffer;
import net.edge.world.Hit;
import net.edge.world.entity.actor.mob.Mob;
import net.edge.world.entity.actor.player.Player;

/**
 * An {@link MobUpdateBlock} implementation that handles the {@code PRIMARY_HIT} update block.
 * @author lare96 <http://github.org/lare96>
 */
public final class MobPrimaryHitUpdateBlock extends MobUpdateBlock {
	
	/**
	 * Creates a new {@link MobPrimaryHitUpdateBlock}.
	 */
	public MobPrimaryHitUpdateBlock() {
		super(2, UpdateFlag.PRIMARY_HIT);
	}
	
	@Override
	public int write(Player player, Mob mob, GameBuffer msg) {
		Hit hit = mob.getPrimaryHit();
		msg.putShort(hit.getDamage());
		msg.put(hit.getType().getId() + (hit.hasSource() && hit.getSource() != player.getSlot() ? 5 : 0));
		msg.put(hit.getIcon().getId());
		msg.putShort((int) Math.round((((double) mob.getCurrentHealth()) / ((double) mob.getMaxHealth())) * 100));
		msg.put(mob.getSpecial().isPresent() ? mob.getSpecial().getAsInt() : 101);
		return -1;
	}
}