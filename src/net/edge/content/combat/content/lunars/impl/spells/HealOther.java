package net.edge.content.combat.content.lunars.impl.spells;

import net.edge.content.combat.content.MagicRune;
import net.edge.content.combat.content.RequiredRune;
import net.edge.content.combat.content.lunars.impl.LunarCombatSpell;
import net.edge.content.combat.hit.Hit;
import net.edge.world.entity.actor.Actor;
import net.edge.world.Animation;
import net.edge.world.Graphic;
import net.edge.world.entity.actor.player.Player;
import net.edge.world.entity.item.Item;

import java.util.Optional;

/**
 * Holds functionality for the heal other lunar spell.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class HealOther extends LunarCombatSpell {

	/**
	 * Constructs a new {@link LunarCombatSpell}.
	 */
	public HealOther() {
		super("Heal Other", 30306, 92, 101, new RequiredRune(MagicRune.BLOOD_RUNE, 1), new RequiredRune(MagicRune.LAW_RUNE, 3), new RequiredRune(MagicRune.ASTRAL_RUNE, 3));
	}

	@Override
	public void effect(Actor caster, Optional<Actor> victim) {
		super.effect(caster, victim);

		Player target = victim.get().toPlayer();
		
		int transfer = (int) ((caster.getCurrentHealth() / 100.0f) * 75.0f);
		
		caster.faceEntity(target);
		caster.damage(new Hit(transfer));
		
		int victimMaxHealth = target.getMaximumHealth();
		
		if(transfer + target.getCurrentHealth() > victimMaxHealth) {
			transfer = victimMaxHealth - transfer;
		}
		
		target.healEntity(transfer);
		target.graphic(new Graphic(745, 90));
	}
	
	@Override
	public boolean canCast(Actor caster, Optional<Actor> victim) {
		if(!super.canCast(caster, victim)) {
			return false;
		}

		Player target = victim.get().toPlayer();
		
		caster.getMovementQueue().reset();
		
		int targetHitpoints = target.getCurrentHealth();
		
		if(!target.getAttr().get("accept_aid").getBoolean()) {
			caster.toPlayer().message("This player is not accepting any aid.");
			return false;
		}
		
		if(caster.getCurrentHealth() < ((caster.toPlayer().getMaximumHealth()) / 100.0f) * 11.0f) {
			caster.toPlayer().message("Your hitpoints are too low to cast this spell.");
			return false;
		}
		
		if(targetHitpoints >= target.getMaximumHealth()) {
			caster.toPlayer().message("This players hitpoints are currently full.");
			return false;
		}
		return true;
	}
	
	@Override
	public Optional<Animation> startAnimation() {
		return Optional.of(new Animation(4411));
	}
}
