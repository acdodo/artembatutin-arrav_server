package net.edge.content.minigame.pestcontrol.pest;

import net.edge.content.minigame.pestcontrol.PestControlMinigame;
import net.edge.content.minigame.pestcontrol.defence.PestGate;
import net.edge.locale.Position;
import net.edge.world.Hit;
import net.edge.world.PoisonType;
import net.edge.world.World;
import net.edge.world.node.entity.npc.Npc;
import net.edge.world.node.entity.npc.NpcDeath;
import net.edge.world.node.region.Region;

public class Splatter extends Pest {
	
	/**
	 * The nearest gate.
	 */
	private PestGate gate;
	
	/**
	 * Creates a new {@link Npc}.
	 * @param id       the identification for this NPC.
	 * @param position the position of this character in the world.
	 */
	public Splatter(int id, Position position) {
		super(id, position);
	}
	
	@Override
	public void sequence(Npc knight) {
		//when dead, explode and cause damage around, no damage on portals.
		if(gate == null) {
			gate = PestControlMinigame.getNearestGate(getPosition());
		}
	}
	
	@Override
	public boolean aggressive() {
		return true;
	}
	
	@Override
	public void setPosition(Position position) {
		//Updating the region if the entity entered another one.
		if(getSlot() != -1 && getPosition() != null && getPosition().getRegion() != position.getRegion()) {
			World.getRegions().getRegion(getPosition().getRegion()).removeChar(this);
			World.getRegions().getRegion(position.getRegion()).addChar(this);
		}
		super.setPosition(position);
		if(gate != null && gate.getPos().withinDistance(getPosition(), 1)) {
			//exploding near gates.
			damage(new Hit(getCurrentHealth(), Hit.HitType.NORMAL, Hit.HitIcon.NONE));
		}
	}
	
	@Override
	public void appendDeath() {
		setDead(true);
		World.get().submit(new NpcDeath(this));
		Region reg = getRegion();
		//hitting players.
		reg.getPlayers().forEach((i, p) -> {
			if(p.getPosition().withinDistance(getPosition(), 1)) {
				p.damage(new Hit(p.getMaximumHealth() / 5, Hit.HitType.NORMAL, Hit.HitIcon.NONE));
			}
		});
		//hitting npcs.
		reg.getNpcs().forEach((i, n) -> {
			int id = n.getId();
			if(id < 6142 || id > 6145) {//ignoring portals.
				if(n.getPosition().withinDistance(getPosition(), 1)) {
					n.damage(new Hit(n.getMaxHealth() / 3, Hit.HitType.NORMAL, Hit.HitIcon.NONE));
				}
			}
		});
	}
	
}
