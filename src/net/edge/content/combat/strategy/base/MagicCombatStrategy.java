package net.edge.content.combat.strategy.base;

import net.edge.content.combat.Combat;
import net.edge.content.combat.CombatHit;
import net.edge.content.combat.CombatType;
import net.edge.content.combat.magic.CombatSpell;
import net.edge.content.combat.special.CombatSpecial;
import net.edge.content.combat.strategy.CombatStrategy;
import net.edge.content.minigame.MinigameHandler;
import net.edge.world.node.entity.EntityNode;
import net.edge.world.node.entity.npc.Npc;
import net.edge.world.node.entity.player.Player;

public final class MagicCombatStrategy implements CombatStrategy {
	
	@Override
	public boolean canOutgoingAttack(EntityNode character, EntityNode victim) {
		if(character.isNpc()) {
			return true;
		}
		
		Player player = (Player) character;
		
		if(!MinigameHandler.execute(player, m -> m.canHit(player, victim, CombatType.MAGIC))) {
			return false;
		}
		
		boolean canCast = player.getCombatSpecial() != null && player.getCombatSpecial() == CombatSpecial.KORASI_SWORD || get(player).canCast(player);
		
		if(!canCast) {
			return false;
		}
		
		player.getCombatBuilder().setCombatType(CombatType.MAGIC);
		return true;
	}
	
	@Override
	public CombatHit outgoingAttack(EntityNode character, EntityNode victim) {
		int delay = 0;
		if(character.isPlayer()) {
			Player player = (Player) character;
			if(player.getAttr().get("lunar_spellbook_swap").getBoolean()) {
				player.getAttr().get("lunar_spellbook_swap").set(false);
			}
			delay = player.prepareSpell(get(player), victim);
		} else if(character.isNpc()) {
			Npc npc = (Npc) character;
			delay = npc.prepareSpell(Combat.prepareSpellCast(npc).getSpell(), victim);
		}
		
		if(character.getCurrentlyCasting().maximumHit() == -1) {
			return new CombatHit(character, victim, 0, CombatType.MAGIC, true, delay);
		}
		return new CombatHit(character, victim, 1, CombatType.MAGIC, true, delay);
	}
	
	@Override
	public int attackDelay(EntityNode character) {
		return 7;
	}
	
	@Override
	public int attackDistance(EntityNode character) {
		return 8;
	}
	
	@Override
	public int[] getNpcs() {
		return new int[]{6278, 6257, 6221, 13, 172, 174, 2025,
				3752, 3753, 3754, 3755, 3756, 3757, 3758, 3759, 3760, 3761//pest torchers
		};
	}
	
	private CombatSpell get(Player player) {
		if(player.isAutocast() && player.getCastSpell() != null && player.getAutocastSpell() != null || !player.isAutocast() && player.getAutocastSpell() == null) {
			return player.getCastSpell();
		}
		return player.getAutocastSpell();
	}
}
