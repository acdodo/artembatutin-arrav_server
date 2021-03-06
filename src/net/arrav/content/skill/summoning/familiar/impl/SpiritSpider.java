package net.arrav.content.skill.summoning.familiar.impl;

import net.arrav.content.dialogue.impl.NpcDialogue;
import net.arrav.content.dialogue.impl.OptionDialogue;
import net.arrav.content.skill.summoning.Summoning;
import net.arrav.content.skill.summoning.SummoningData;
import net.arrav.content.skill.summoning.familiar.Familiar;
import net.arrav.content.skill.summoning.familiar.FamiliarAbility;
import net.arrav.content.skill.summoning.familiar.impl.forager.ForagerPassiveAbility;
import net.arrav.content.skill.summoning.familiar.passive.PassiveAbility;
import net.arrav.content.skill.summoning.familiar.passive.impl.PeriodicalAbility;
import net.arrav.util.rand.RandomUtils;
import net.arrav.world.entity.actor.mob.Mob;
import net.arrav.world.entity.actor.player.Player;
import net.arrav.world.entity.item.GroundItemStatic;
import net.arrav.world.entity.item.Item;

import java.util.Optional;

/**
 * Represents the SpiritSpider familiar.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class SpiritSpider extends Familiar {
	
	/**
	 * The identification of the red spiders eggs.
	 */
	private static final int RED_SPIDERS_EGGS = 223;
	
	/**
	 * Constructs a new {@link SpiritSpider}.
	 */
	public SpiritSpider() {
		super(SummoningData.SPIRIT_SPIDER);
	}
	
	private final ForagerPassiveAbility ability = new ForagerPassiveAbility(RED_SPIDERS_EGGS);
	
	@Override
	public FamiliarAbility getAbilityType() {
		return ability;
	}
	
	private final PeriodicalAbility passiveAbility = new PeriodicalAbility(350, t -> {
		if(!t.getFamiliar().isPresent()) {
			return;
		}
		if(RandomUtils.inclusive(100) < 40) {
			t.getFamiliar().get().forceChat("Rawrgh, my eggs won't come out!");
			return;
		}
		GroundItemStatic eggs = new GroundItemStatic(new Item(223), t.getFamiliar().get().getPosition());
		eggs.create();
	});
	
	@Override
	public Optional<PassiveAbility> getPassiveAbility() {
		return Optional.of(passiveAbility);
	}
	
	@Override
	public void interact(Player player, Mob mob, int id) {
		if(id == 1) {
			player.getDialogueBuilder().append(new OptionDialogue(t -> {
				if(t.equals(OptionDialogue.OptionType.FIRST_OPTION)) {
					player.getDialogueBuilder().advance();
				} else if(t.equals(OptionDialogue.OptionType.SECOND_OPTION)) {
					Summoning.openBeastOfBurden(player, mob);
				} else if(t.equals(OptionDialogue.OptionType.THIRD_OPTION)) {
					player.closeWidget();
				}
			}, "Interact", "View storage", "Nevermind"), new NpcDialogue(getId(), RandomUtils.random(RANDOM_DIALOGUE)));
		}
	}
	
	@Override
	public boolean isCombatic() {
		return true;
	}
	
	private final String[] RANDOM_DIALOGUE = {"When can I lay my eggs?", "Where are we going?", "Why are you not responding to me?"};
}
