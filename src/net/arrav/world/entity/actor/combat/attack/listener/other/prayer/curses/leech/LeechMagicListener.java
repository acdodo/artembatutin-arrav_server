package net.arrav.world.entity.actor.combat.attack.listener.other.prayer.curses.leech;

import net.arrav.content.skill.prayer.Prayer;
import net.arrav.world.Animation;
import net.arrav.world.Graphic;
import net.arrav.world.entity.actor.Actor;
import net.arrav.world.entity.actor.combat.attack.listener.SimplifiedListener;
import net.arrav.world.entity.actor.combat.hit.Hit;
import net.arrav.world.entity.actor.player.Player;

import static net.arrav.world.Animation.AnimationPriority.HIGH;

public class LeechMagicListener extends SimplifiedListener<Player> {
	
	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		if(attacker.curseManager.isActivated(Prayer.LEECH_MAGIC)) {
			defender.graphic(new Graphic(2232, 0, 2));
			attacker.animation(new Animation(12575, HIGH));
			attacker.message("You leech the targets magic.");
			attacker.curseManager.deactivate(Prayer.LEECH_MAGIC);
		}
	}
	
	@Override
	public int modifyMagicLevel(Player attacker, Actor defender, int level) {
		return attacker.curseManager.modifyOutgoingLevel(level, 5, 10, Prayer.LEECH_MAGIC);
	}
	
}
