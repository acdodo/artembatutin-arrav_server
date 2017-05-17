package net.edge.world.content.skill.agility.impl.gnome;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.edge.world.content.skill.Skills;
import net.edge.world.content.skill.agility.AgilityCourse;
import net.edge.world.content.skill.agility.AgilityCourseType;
import net.edge.world.content.skill.agility.impl.gnome.impl.JumpOverBarrier;
import net.edge.world.content.skill.agility.impl.gnome.impl.PoleSwing;
import net.edge.world.content.skill.agility.obstacle.ObstacleAction;
import net.edge.world.content.skill.agility.obstacle.ObstacleType;
import net.edge.world.content.skill.agility.obstacle.impl.Climbable;
import net.edge.world.content.skill.agility.obstacle.impl.Movable;
import net.edge.world.content.skill.agility.obstacle.impl.Walkable;
import net.edge.world.model.locale.Position;
import net.edge.world.model.node.entity.player.Player;
import net.edge.world.model.node.object.ObjectNode;

import java.util.EnumSet;
import java.util.Optional;

/**
 * Holds functionality for passing obstacles for the GnomeStronghold Agility course.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class GnomeStrongholdAgility extends AgilityCourse {
	
	/**
	 * The definition for this obstacle course.
	 */
	private final GnomeAgilityData obstacle;
	
	/**
	 * Constructs a new {@link GnomeStrongholdAgility} course.
	 * @param player   {@link #getPlayer()}.
	 * @param object   {@link #getObject()}.
	 * @param obstacle the obstacle this player is trying to cross.
	 */
	private GnomeStrongholdAgility(Player player, ObjectNode object, GnomeAgilityData obstacle) {
		super(player, object, AgilityCourseType.GNOME_AGILITY);
		
		this.obstacle = obstacle;
	}
	
	/**
	 * Starts this skill action.
	 * @param player {@link #getPlayer()}.
	 * @param object the object this player is interacting with.
	 * @return <true> if the player can execute the logic, <false> otherwise.
	 */
	public static boolean execute(Player player, ObjectNode object) {
		Optional<GnomeAgilityData> data = GnomeAgilityData.getDefinition(object.getId());
		if(!data.isPresent()) {
			return false;
		}
		
		GnomeStrongholdAgility agility = new GnomeStrongholdAgility(player, object, data.get());
		agility.start();
		return true;
	}
	
	@Override
	public void onSuccess() {
		player.getAgilityBonus().addGnomeObstacle(obstacle);
		
		if(player.getAgilityBonus().hasCompletedGnomeAgilityCourse()) {
			player.getAgilityBonus().clearGnomeObstacles();
			
			player.message("You have successfully completed the gnome agility laps.");
			Skills.experience(player, 39, Skills.AGILITY);
		}
	}
	
	@Override
	public ObstacleAction obstacleAction() {
		return obstacle.obstacleAction;
	}
	
	@Override
	public double experience() {
		return obstacle.obstacleAction.activity(getPlayer()).getExperience();
	}
	
	@Override
	public String message() {
		return obstacle.message;
	}
	
	@Override
	public Optional<String> crossedMessage() {
		return obstacle.crossedMessage;
	}
	
	public enum GnomeAgilityData {
		LOG_BALANCE(new int[]{2295}, ObstacleType.LOG_BALANCE, player1 -> new Walkable(new Position(2474, 3436), new Position(2474, 3429), ObstacleType.LOG_BALANCE.getAnimation(), 1, 7)),
		OBSTACLE_NET_UP(new int[]{2285}, ObstacleType.NETTING, player1 -> new Climbable(new Position(player1.getPosition().getX(), 3426, 0), new Position(player1.getPosition().getX(), 3424, 1), ObstacleType.NETTING.getAnimation(), 2, 1, 7.5)),
		TREE_BRANCH_UP(new int[]{35970}, ObstacleType.TREE_BRANCH_UP, player1 -> new Climbable(new Position(player1.getPosition().getX(), 3423, 1), new Position(2473, 3420, 2), ObstacleType.TREE_BRANCH_UP.getAnimation(), 2, 1, 5)),
		TIGHT_ROPE(new int[]{2312}, ObstacleType.TIGHT_ROPE, player1 -> new Walkable(new Position(2477, 3420, 2), new Position(2483, 3420, 2), ObstacleType.TIGHT_ROPE.getAnimation(), 1, 7.5)),
		TREE_BRANCH_DOWN(new int[]{2314, 2315}, ObstacleType.TREE_BRANCH_DOWN, player1 -> new Climbable(player1.getPosition(), new Position(2486, 3420, 0), ObstacleType.TREE_BRANCH_DOWN.getAnimation(), 2, 1, 5)),
		OBSTACLE_NET(new int[]{2286}, ObstacleType.NETTING, player1 -> new Climbable(new Position(player1.getPosition().getX(), 3425, 0), new Position(player1.getPosition().getX(), 3427, 0), ObstacleType.NETTING.getAnimation(), 2, 1, 7.5)),
		OBSTACLE_PIPE(new int[]{43544, 43543}, ObstacleType.PIPE, player1 -> new Walkable(new Position(player1.getPosition().getX(), 3430, 0), new Position(player1.getPosition().getX(), 3437, 0), ObstacleType.PIPE.getAnimation(), 1, 7.5) {
			@Override
			public boolean canExecute(Player player1) {
				if(player1.getPosition().same(new Position(2483, 3430, 0)) || player1.getPosition().same(new Position(2487, 3430, 0))) {
					return true;
				}
				player1.message("You can't cross this obstacle from this side.");
				return false;
			}
		}),
		/**
		 * Advanced course
		 */
		ADVANCED_TREE_BRANCH_UP(new int[]{85531}, ObstacleType.TREE_BRANCH_UP_ADVANCED, player1 -> new Climbable(new Position(player1.getPosition().getX(), 3420, 2), new Position(2472, 3419, 3), ObstacleType.TREE_BRANCH_UP_ADVANCED.getAnimation(), 2, 85, 25)),
		RUN_ACROSS_SIGNPOST(new int[]{85584}, ObstacleType.RUN_ACROSS_SIGNPOST, player1 -> new Movable(new Position(2476, 3418, 3), new Position(2484, 3418, 3), ObstacleType.RUN_ACROSS_SIGNPOST.getAnimation(), 105, 6, 60, 25)),
		POLE_SWING(new int[]{85532}, ObstacleType.POLE_SWING, PoleSwing::new),
		JUMP_OVER_BARRIER(new int[]{85542}, ObstacleType.JUMP_OVER_BARRIER, player1 -> new JumpOverBarrier());
		
		/**
		 * Caches our enum values.
		 */
		private static final ImmutableSet<GnomeAgilityData> VALUES = Sets.immutableEnumSet(EnumSet.allOf(GnomeAgilityData.class));
		
		/**
		 * The object identifications for this Obstacle.
		 */
		private final int[] objectIds;
		
		/**
		 * The message sent when attempting to cross this obstacle.
		 */
		private final String message;
		
		/**
		 * The message sent when successfully crossing this obstacle.
		 */
		private final Optional<String> crossedMessage;
		
		/**
		 * The policy chained to this Obstacle.
		 */
		private final ObstacleAction obstacleAction;
		
		/**
		 * Constructs a new {@link GnomeAgilityData}.
		 * @param objectIds      {@link #objectId}.
		 * @param message        {@link #message}.
		 * @param crossedMessage {@link #crossedMessage}.
		 * @param obstacleAction {@link #obstacleAction}.
		 */
		GnomeAgilityData(int[] objectIds, String message, Optional<String> crossedMessage, ObstacleAction obstacleAction) {
			this.objectIds = objectIds;
			this.message = message;
			this.crossedMessage = crossedMessage;
			this.obstacleAction = obstacleAction;
		}
		
		/**
		 * Constructs a new {@link GnomeAgilityData}.
		 * @param objectIds      {@link #objectId}.
		 * @param message        {@link #message}.
		 * @param crossedMessage {@link #crossedMessage}.
		 * @param obstacleAction {@link #obstacleAction}.
		 */
		GnomeAgilityData(int[] objectIds, String message, String crossedMessage, ObstacleAction obstacleAction) {
			this.objectIds = objectIds;
			this.message = message;
			this.crossedMessage = Optional.ofNullable(crossedMessage);
			this.obstacleAction = obstacleAction;
		}
		
		/**
		 * Constructs a new {@link GnomeAgilityData}.
		 * @param objectIds      {@link #objectIds}.
		 * @param message        {@link #message}.
		 * @param crossedMessage {@link #crossedMessage}.
		 * @param obstacleAction {@link #obstacleAction}.
		 */
		GnomeAgilityData(int[] objectIds, ObstacleType type, ObstacleAction obstacleAction) {
			this.objectIds = objectIds;
			this.message = type.getMessage();
			this.crossedMessage = type.getCrossedMessage();
			this.obstacleAction = obstacleAction;
		}
		
		/**
		 * Gets an Optional of the matched value.
		 * @param identifier the identifier to check for matches.
		 * @return an optional with the {@link GnomeAgilityData} found, {@link Optional#empty} otherwise.
		 */
		protected static Optional<GnomeAgilityData> getDefinition(int identifier) {
			for(GnomeAgilityData data : VALUES) {
				for(int object : data.objectIds) {
					if(object == identifier || object == identifier - 42003) {
						return Optional.of(data);
					}
				}
			}
			return Optional.empty();
		}
	}
}
