package net.edge.world.model.node.entity.update;

import net.edge.net.codec.ByteMessage;
import net.edge.net.codec.ByteOrder;
import net.edge.world.model.node.entity.model.Animation;
import net.edge.world.model.node.entity.player.Player;
import net.edge.net.codec.ByteTransform;

/**
 * An {@link PlayerUpdateBlock} implementation that handles the {@link Animation} update block.
 * @author lare96 <http://github.org/lare96>
 */
public final class PlayerAnimationUpdateBlock extends PlayerUpdateBlock {

	/**
	 * Creates a new {@link PlayerAnimationUpdateBlock}.
	 */
	public PlayerAnimationUpdateBlock() {
		super(8, UpdateFlag.ANIMATION);
	}

	@Override
	public int write(Player player, Player mob, ByteMessage msg) {
		msg.putShort(mob.getAnimation().getId(), ByteOrder.LITTLE);
		msg.put(mob.getAnimation().getDelay(), ByteTransform.C);
		return -1;
	}
}
