package net.edge.content.object.cannon;

import net.edge.content.combat.CombatHit;
import net.edge.content.combat.CombatHitTask;
import net.edge.content.combat.CombatType;
import net.edge.net.packet.out.SendObjectAnimation;
import net.edge.task.Task;
import net.edge.util.rand.RandomUtils;
import net.edge.world.Direction;
import net.edge.world.Hit;
import net.edge.world.Projectile;
import net.edge.world.World;
import net.edge.world.entity.Entity;
import net.edge.world.entity.actor.Actor;
import net.edge.world.entity.actor.mob.Mob;
import net.edge.world.entity.actor.player.Player;
import net.edge.world.entity.item.GroundItem;
import net.edge.world.entity.item.Item;
import net.edge.world.entity.region.Region;
import net.edge.world.locale.Position;
import net.edge.world.object.ObjectDirection;

/**
 * A {@link Task} handling the {@link Multicannon} shooting.
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public class MulticannonTask extends Task {
	
	/**
	 * The cannon shooting.
	 */
	private final Multicannon cannon;
	
	public MulticannonTask(Multicannon cannon) {
		super(1, false);
		this.cannon = cannon;
	}
	
	@Override
	protected void execute() {
		if(cannon.isDisabled()) {
			cancel();
			return;
		}
		if(cannon.getGlobalPos().getDistance(cannon.player.getPosition()) > 30) {
			cannon.player.message("Your cannon is now on the floor because you weren't close enough to repair it in time.");
			cancel();
			cannon.remove();
			cannon.getRegion().ifPresent(r -> {
				if(cannon.getElements() > 0) {
					r.register(new GroundItem(new Item(2, cannon.getElements()), cannon.getGlobalPos(), cannon.player));
				}
				r.register(new GroundItem(new Item(6), cannon.getGlobalPos(), cannon.player));
				r.register(new GroundItem(new Item(8), cannon.getGlobalPos(), cannon.player));
				r.register(new GroundItem(new Item(10), cannon.getGlobalPos(), cannon.player));
				r.register(new GroundItem(new Item(12), cannon.getGlobalPos(), cannon.player));
			});
		} else if(cannon.getElements() < 1) {
			cannon.player.message("Your cannon has run out of ammo!");
			cancel();
		} else {
			if (cannon.facing == null) {
				cannon.facing = Direction.NORTH;
				rotate(cannon);
				fire();
				return;
			}
			switch (cannon.facing) {
				case NORTH: // north
					cannon.facing = Direction.NORTH_EAST;
					break;
				case NORTH_EAST: // north-east
					cannon.facing = Direction.EAST;
					break;
				case EAST: // east
					cannon.facing = Direction.SOUTH_EAST;
					break;
				case SOUTH_EAST: // south-east
					cannon.facing = Direction.SOUTH;
					break;
				case SOUTH: // south
					cannon.facing = Direction.SOUTH_WEST;
					break;
				case SOUTH_WEST: // south-west
					cannon.facing = Direction.WEST;
					break;
				case WEST: // west
					cannon.facing = Direction.NORTH_WEST;
					break;
				case NORTH_WEST: // north-west
					cannon.facing = null;
					break;
			}
			
			rotate(cannon);
			fire();
		}
	}
	
	/**
	 * Rotate the cannon and change the object animation based on the direction
	 * we are facing.
	 */
	private void rotate(Multicannon cannon) {
		Player p = cannon.player;
		switch (cannon.facing) {
			case NORTH: // north
				p.out(new SendObjectAnimation(cannon.getGlobalPos(), 516, cannon.getObjectType(), -1));
				break;
			case NORTH_EAST: // north-east
				p.out(new SendObjectAnimation(cannon.getGlobalPos(), 517, cannon.getObjectType(), -1));
				break;
			case EAST: // east
				p.out(new SendObjectAnimation(cannon.getGlobalPos(), 518, cannon.getObjectType(), -1));
				break;
			case SOUTH_EAST: // south-east
				p.out(new SendObjectAnimation(cannon.getGlobalPos(), 519, cannon.getObjectType(), -1));
				break;
			case SOUTH: // south
				p.out(new SendObjectAnimation(cannon.getGlobalPos(), 520, cannon.getObjectType(), -1));
				break;
			case SOUTH_WEST: // south-west
				p.out(new SendObjectAnimation(cannon.getGlobalPos(), 521, cannon.getObjectType(), -1));
				break;
			case WEST: // west
				p.out(new SendObjectAnimation(cannon.getGlobalPos(), 514, cannon.getObjectType(), -1));
				break;
			case NORTH_WEST: // north-west
				p.out(new SendObjectAnimation(cannon.getGlobalPos(), 515, cannon.getObjectType(), -1));
				cannon.facing = null;
				break;
		}
	}
	
	private void fire() {
		Actor victim = getVictim();
		if(victim == null)
			return;
		int damage = RandomUtils.inclusive(300);
		cannon.setElements(cannon.getElements() - 1);
		int delay = new Projectile(cannon.getGlobalPos().move(1, 1), victim.getCenterPosition(), (victim.isPlayer() ? -victim.getSlot() - 1 : victim.getSlot() + 1), 53, 60, 20, 35, 30, 30, cannon.player.getInstance(), CombatType.RANGED).sendProjectile().getTravelTime();
		CombatHit data = new CombatHit(cannon.player, victim, 1, CombatType.RANGED, true, delay + 1);
		data.experience();
		new Task(delay, false) {
			@Override
			protected void execute() {
				victim.damage(new Hit(damage, Hit.HitType.NORMAL, Hit.HitIcon.CANON));
				if(victim.isAutoRetaliate() && !victim.getCombat().isAttacking() && !data.isIgnored()) {
					victim.getCombat().attack(cannon.player);
				}
				this.cancel();
			}
		}.submit();
	}
	
	private Actor getVictim() {
		int myX = cannon.getGlobalPos().getX();
		int myY = cannon.getGlobalPos().getY();
		if(cannon.player.inMulti()) {
			for(Mob m : cannon.player.getLocalMobs()) {
				if(m.isDead())
					continue;
				if(!m.getDefinition().isAttackable())
					continue;
				if(m.getPosition().getDistance(cannon.getGlobalPos()) > 8)
					continue;
				if(check(m, myX, myY)) {
					return m;
				}
			}
		} else if(cannon.player.getCombat().isBeingAttacked()) {
			Actor actor = cannon.player.getCombat().getAggressor();
			if(check(actor, myX, myY)) {
				return actor;
			}
		}
		return null;
	}
	
	private boolean check(Actor a, int myX, int myY) {
		int theirX = a.getPosition().getX();
		int theirY = a.getPosition().getY();
		
		if (cannon.facing == null) {
			cannon.facing = Direction.NORTH;
		}
		
		switch (cannon.facing) {
			case NORTH:
				if (theirY > myY && theirX >= myX - 1 && theirX <= myX + 1)
					return true;
				break;
			case NORTH_EAST:
				if (theirX >= myX + 1 && theirY >= myY + 1)
					return true;
				break;
			case EAST:
				if (theirX > myX && theirY >= myY - 1 && theirY <= myY + 1)
					return true;
				break;
			case SOUTH_EAST:
				if (theirY <= myY - 1 && theirX >= myX + 1)
					return true;
				break;
			case SOUTH:
				if (theirY < myY && theirX >= myX - 1 && theirX <= myX + 1)
					return true;
				break;
			case SOUTH_WEST:
				if (theirX <= myX - 1 && theirY <= myY - 1)
					return true;
				break;
			case WEST:
				if (theirX < myX && theirY >= myY - 1 && theirY <= myY + 1)
					return true;
				break;
			case NORTH_WEST:
				if (theirX <= myX - 1 && theirY >= myY + 1)
					return true;
				break;
		}
		return false;
	}
	
	@Override
	protected void onCancel() {
		cannon.firing = false;
	}
}