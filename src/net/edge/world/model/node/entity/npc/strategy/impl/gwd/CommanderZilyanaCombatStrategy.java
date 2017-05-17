package net.edge.world.model.node.entity.npc.strategy.impl.gwd;

import net.edge.utils.rand.RandomUtils;
import net.edge.world.content.combat.CombatSessionData;
import net.edge.world.content.combat.CombatType;
import net.edge.world.content.combat.magic.CombatNormalSpell;
import net.edge.world.model.node.NodeState;
import net.edge.world.model.node.entity.EntityNode;
import net.edge.world.model.node.entity.model.Animation;
import net.edge.world.model.node.entity.model.Graphic;
import net.edge.world.model.node.entity.model.Projectile;
import net.edge.world.model.node.entity.npc.impl.gwd.CommanderZilyana;
import net.edge.world.model.node.entity.npc.strategy.DynamicCombatStrategy;
import net.edge.world.model.node.entity.player.Player;
import net.edge.world.model.node.item.Item;

import java.util.Optional;

/**
 * The dynamic combat strategy for the commander zilyana boss.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class CommanderZilyanaCombatStrategy extends DynamicCombatStrategy<CommanderZilyana> {
	
	/**
	 * Constructs a new {@link CommanderZilyanaCombatStrategy}.
	 * @param npc the npc this strategy is for.
	 */
	public CommanderZilyanaCombatStrategy(CommanderZilyana npc) {
		super(npc);
	}
	
	@Override
	public boolean canOutgoingAttack(EntityNode victim) {
		return victim.isPlayer() && CommanderZilyana.CHAMBER.inLocation(victim.getPosition());
	}
	
	@Override
	public CombatSessionData outgoingAttack(EntityNode victim) {
		CombatType[] data = npc.getPosition().withinDistance(victim.getPosition(), 2) ? new CombatType[]{CombatType.MELEE, CombatType.MAGIC} : new CombatType[]{CombatType.MAGIC};
		CombatType c = RandomUtils.random(data);
		CommanderZilyana.MINIONS.forEach(minion -> {
			if(!minion.isDead() && minion.getState() == NodeState.ACTIVE) {
				minion.getCombatBuilder().attack(victim);
			}
		});
		return type(victim, c);
	}
	
	private CombatSessionData melee(EntityNode victim) {
		npc.animation(new Animation(6964));
		return new CombatSessionData(npc, victim, 1, CombatType.MELEE, true);
	}
	
	private CombatSessionData magic(EntityNode victim) {
		npc.setCurrentlyCasting(SPELL);
		npc.animation(new Animation(6967));
		npc.graphic(new Graphic(1220));
		return new CombatSessionData(npc, victim, 2, CombatType.MAGIC, true, 3);
	}
	
	private CombatSessionData type(EntityNode victim, CombatType type) {
		switch(type) {
			case MELEE:
				return melee(victim);
			case MAGIC:
				return magic(victim);
			default:
				return magic(victim);
		}
	}
	
	private static final CombatNormalSpell SPELL = new CombatNormalSpell() {
		
		@Override
		public int spellId() {
			return 0;
		}
		
		@Override
		public int maximumHit() {
			return 310;
		}
		
		@Override
		public Optional<Animation> castAnimation() {
			return Optional.empty();
		}
		
		@Override
		public Optional<Graphic> startGraphic() {
			return Optional.empty();
		}
		
		@Override
		public Optional<Projectile> projectile(EntityNode cast, EntityNode castOn) {
			return Optional.empty();
		}
		
		@Override
		public Optional<Graphic> endGraphic() {
			return Optional.empty();
		}
		
		@Override
		public int levelRequired() {
			return 0;
		}
		
		@Override
		public double baseExperience() {
			return 0;
		}
		
		@Override
		public Optional<Item[]> itemsRequired(Player player) {
			return Optional.empty();
		}
		
		@Override
		public Optional<Item[]> equipmentRequired(Player player) {
			return Optional.empty();
		}
		
	};
	
	@Override
	public void incomingAttack(EntityNode attacker, CombatSessionData data) {
		
	}
	
	@Override
	public int attackDelay() {
		return npc.getAttackSpeed();
	}
	
	@Override
	public int attackDistance() {
		return 7;
	}
}
