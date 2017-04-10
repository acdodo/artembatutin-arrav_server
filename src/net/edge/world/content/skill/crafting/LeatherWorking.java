package net.edge.world.content.skill.crafting;

import com.google.common.collect.ImmutableMap;
import net.edge.utils.TextUtils;
import net.edge.world.model.node.entity.model.Animation;
import net.edge.world.model.node.entity.player.Player;
import net.edge.world.model.node.item.Item;
import net.edge.world.content.skill.SkillData;
import net.edge.world.content.skill.action.impl.ProducingSkillAction;
import net.edge.task.Task;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds functionality for leather working.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class LeatherWorking extends ProducingSkillAction {
	
	/**
	 * The leather data this skill action is dependent of.
	 */
	private final LeatherData data;
	
	/**
	 * The amount of times this task should run for.
	 */
	private int amount;
	
	/**
	 * Constructs a new {@link LeatherWorking}.
	 * @param player {@link #getPlayer()}.
	 * @param data   {@link #data}.
	 * @param amount {@link #amount}.
	 */
	public LeatherWorking(Player player, LeatherData data, int amount) {
		super(player, Optional.empty());
		this.data = data;
		this.amount = amount;
	}
	
	/**
	 * A constant defining the leather item.
	 */
	private static final Item LEATHER = new Item(1741);
	
	/**
	 * A constant defining the needle item.
	 */
	private static final Item NEEDLE = new Item(1733);
	
	/**
	 * A constant defining the thread item.
	 */
	private static final Item THREAD = new Item(1734);
	
	/**
	 * Attempts to register products from leather.
	 * @param player   the player attempting to register the products.
	 * @param buttonId the button the player has clicked.
	 * @return {@code true} if the skill action was submitted, {@code false} otherwise.
	 */
	public static boolean create(Player player, int buttonId) {
		LeatherData data = LeatherData.VALUES.get(buttonId);
		
		if(buttonId == 9118) {
			player.getMessages().sendCloseWindows();
			return false;
		}
		
		if(data == null) {
			return false;
		}
		
		LeatherWorking crafting = new LeatherWorking(player, data, data.amount);
		crafting.start();
		return true;
	}
	
	/**
	 * Attempts to open the leather working interface.
	 * @param player the player that attempted to open the interface.
	 * @param npc    the npc that was interacted with.
	 * @return {@code true} if the interface was opened, {@code false} otherwise.
	 */
	public static boolean openInterface(Player player, Item itemUsed, Item usedOn) {
		if(itemUsed.getId() == NEEDLE.getId() && usedOn.getId() == LEATHER.getId() || itemUsed.getId() == LEATHER.getId() && usedOn.getId() == NEEDLE.getId()) {
			player.getMessages().sendInterface(2311);
			return true;
		}
		return false;
	}
	
	@Override
	public void onProduce(Task t, boolean success) {
		if(success) {
			amount--;
			
			if(amount <= 0)
				t.cancel();
		}
	}
	
	@Override
	public Optional<Animation> animation() {
		return Optional.of(new Animation(1249));
	}
	
	@Override
	public Optional<Item[]> removeItem() {
		return Optional.of(new Item[]{LEATHER, THREAD});
	}
	
	@Override
	public Optional<Item[]> produceItem() {
		return Optional.of(new Item[]{data.produced});
	}
	
	@Override
	public int delay() {
		return 5;
	}
	
	@Override
	public boolean instant() {
		return true;
	}
	
	@Override
	public boolean init() {
		player.getMessages().sendCloseWindows();
		return checkCrafting();
	}
	
	@Override
	public boolean canExecute() {
		return checkCrafting();
	}
	
	@Override
	public double experience() {
		return data.experience;
	}
	
	@Override
	public SkillData skill() {
		return SkillData.CRAFTING;
	}
	
	private boolean checkCrafting() {
		if(!player.getSkills()[skill().getId()].reqLevel(data.requirement)) {
			player.message("You need a crafting level of " + data.requirement + " to register " + TextUtils.appendIndefiniteArticle(data.produced.getDefinition().getName()));
			return false;
		}
		return true;
	}
	
	/**
	 * The enumerated type whose elements represent a set of constants used to
	 * define the data for tanning items.
	 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
	 */
	private enum LeatherData {
		LEATHER_GLOVES(33190, 1059, 1, 13.8, 1),
		LEATHER_GLOVES5(33189, LEATHER_GLOVES, 5),
		LEATHER_GLOVES10(33188, LEATHER_GLOVES, 10),
		
		LEATHER_BOOTS(33193, 1061, 7, 16.3, 1),
		LEATHER_BOOTS5(33192, LEATHER_BOOTS, 5),
		LEATHER_BOOTS10(33191, LEATHER_BOOTS, 10),
		
		LEATHER_COWL(33205, 1167, 9, 18.5, 1),
		LEATHER_COWL5(33204, LEATHER_COWL, 5),
		LEATHER_COWL10(33203, LEATHER_COWL, 10),
		
		LEATHER_VAMBRACES(33196, 1063, 11, 22, 1),
		LEATHER_VAMBRACES5(33195, LEATHER_VAMBRACES, 5),
		LEATHER_VAMBRACES10(33194, LEATHER_VAMBRACES, 10),
		
		LEATHER_BODY(33187, 1129, 14, 25, 1),
		LEATHER_BODY5(33186, LEATHER_BODY, 5),
		LEATHER_BODY10(33185, LEATHER_BODY, 10),
		
		LEATHER_CHAPS(33199, 1095, 18, 27, 1),
		LEATHER_CHAPS5(33198, LEATHER_CHAPS, 5),
		LEATHER_CHAPS10(33197, LEATHER_CHAPS, 10),
		
		LEATHER_COIF(33202, 1169, 38, 37, 1),
		LEATHER_COIF5(33201, LEATHER_COIF, 5),
		LEATHER_COIF10(33200, LEATHER_COIF, 10);
		
		/**
		 * Caches our enum values.
		 */
		private static final ImmutableMap<Integer, LeatherData> VALUES = ImmutableMap.copyOf(Stream.of(values()).collect(Collectors.toMap(t -> t.buttonId, Function.identity())));
		
		/**
		 * The button identification.
		 */
		private final int buttonId;
		
		/**
		 * The produced product.
		 */
		private final Item produced;
		
		/**
		 * The requirement to tan.
		 */
		private final int requirement;
		
		/**
		 * The experience gained upon tanning.
		 */
		private final double experience;
		
		/**
		 * The amount to register.
		 */
		private final int amount;
		
		/**
		 * Constructs a new {@link LeatherWorking}.
		 * @param buttonId    {@link #buttonId}.
		 * @param produced    {@link #produced}.
		 * @param requirement {@link #requirement}.
		 * @param experience  {@link #experience}.
		 * @param amount      {@link #amount}.
		 */
		LeatherData(int buttonId, int produced, int requirement, double experience, int amount) {
			this.buttonId = buttonId;
			this.produced = new Item(produced);
			this.requirement = requirement;
			this.experience = experience;
			this.amount = amount;
		}
		
		/**
		 * Constructs a new {@link LeatherData}.
		 * @param buttonId {@link #buttonId}.
		 * @param data     the tanning data to construct a new one from.
		 * @param amount   {@link #amount}.
		 */
		LeatherData(int buttonId, LeatherData data, int amount) {
			this.buttonId = buttonId;
			this.produced = data.produced;
			this.requirement = data.requirement;
			this.experience = data.experience;
			this.amount = amount;
		}
	}
}
