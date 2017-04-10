package net.edge.world.content.skill.firemaking.pits;

import java.util.Optional;

import net.edge.utils.TextUtils;
import net.edge.world.World;
import net.edge.world.content.skill.firemaking.LogType;
import net.edge.world.model.node.entity.player.Player;
import net.edge.world.model.node.object.ObjectDirection;
import net.edge.world.model.node.object.ObjectNode;
import net.edge.world.model.locale.Position;

/**
 * Represents a single fire pit object.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class FirepitObject extends ObjectNode {

	/**
	 * The data for this fire pit object.
	 */
	FirepitData data;

	/**
	 * The count of logs this fire pit has.
	 */
	int count;

	/**
	 * Determines if this fire pit is active.
	 */
	Optional<FirepitTask> active;

	/**
	 * Constructs a new {@link FirepitObject}.
	 */
	FirepitObject() {
		super(FirepitData.PHASE_ONE.objectId, new Position(3081, 3497), ObjectDirection.SOUTH);
		this.data = FirepitData.PHASE_ONE;
		this.count = 0;
		this.active = Optional.empty();//never active when constructed.
	}

	public int getCount() {
		return count;
	}

	public boolean isActive() {
		return active.isPresent();
	}
	
	public Optional<FirepitTask> getTask() {
		return active;
	}

	public int getTime() {
		return (FirepitManager.EVENT_TIME_IN_TICKS - active.get().getCounter()) * 600;
	}
	
	public void setActive(Optional<FirepitTask> active) {
		this.active = active;
	}

	public void fire(Player player) {
		PitFiring firemaking = new PitFiring(player, this);
		firemaking.start();
	}

	/**
	 * Increments the amount of logs this fire pit has.
	 */
	public void increment() {
		count++;
		if(count >= data.count && this.data.getNext().isPresent()) {
			this.data = this.data.getNext().get();
			this.setId(data.objectId);
			World.getRegions().getRegion(this.getPosition()).register(this);
		}

	}
	
	/**
	 * Gets the log requirement into a string.
	 * @return the string which identifies the log requirement.
	 */
	public String getLogRequirement() {
		return TextUtils.capitalize(data.log.toString());
	}

	/**
	 * Determines if the specified {@code log} can be added to the fire pit.
	 * @param player the player attempting to add the log.
	 * @param log    the log that was added.
	 * @return {@code true} if the log is permissible, {@code false} otherwise.
	 */
	public boolean isPermissable(Player player, int log) {
		LogType type = LogType.getDefinition(log).orElse(null);

		if(type == null) {
			player.message("You can only add logs to this fire pit.");
			return false;
		}

		if(count >= data.count && !this.data.getNext().isPresent() && this.data.equals(FirepitData.PHASE_FIVE)) {
			player.message("You can't add logs anymore... You have to fire the pile of logs.");
			return false;
		}

		if(type.ordinal() < this.data.log.ordinal()) {
			player.message("Your log does not seem right for this fire pit...");
			return false;
		}
		return true;
	}

}
