package net.edge.world.content.pets;

import net.edge.net.message.OutputMessages;
import net.edge.utils.TextUtils;
import net.edge.world.World;
import net.edge.world.content.TabInterface;
import net.edge.world.content.dialogue.Expression;
import net.edge.world.content.item.FoodConsumable;
import net.edge.world.content.skill.summoning.familiar.Familiar;
import net.edge.world.model.locale.Position;
import net.edge.world.model.node.entity.model.Animation;
import net.edge.world.model.node.entity.npc.Npc;
import net.edge.world.model.node.entity.npc.impl.Follower;
import net.edge.world.model.node.entity.player.Player;
import net.edge.world.model.node.item.Item;

import java.util.Arrays;
import java.util.Optional;

/**
 * The class which represents a single pet.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class Pet extends Follower {
	
	/**
	 * The progress that this pet has made.
	 */
	private final PetProgress progress;
	
	/**
	 * The optional task running for this pet.
	 */
	private Optional<PetTask> task;
	
	/**
	 * Constructs a new {@link Pet}.
	 * @param data     the data to construct this pet from.
	 * @param position the position this pet should be spawned at.
	 */
	private Pet(PetData data, Position position) {
		super(data.getPolicy().getNpcId(), position);
		this.progress = new PetProgress(data);
	}
	
	/**
	 * Constructs a new {@link Pet}.
	 * @param progress {@link #progress}.
	 * @param position the position this pet should be spawned at.
	 */
	public Pet(PetProgress progress, Position position) {
		super(progress.getData().getPolicy().getNpcId(), position);
		this.progress = progress;
	}
	
	@Override
	public boolean isPet() {
		return true;
	}
	
	/**
	 * Attempts to feed the pet the player has spawned.
	 * @param player the player feeding the pet.
	 * @param npc    the npc representing the pet.
	 * @param food   the item representing the food.
	 * @return {@code true} if the pet was fed, {@code false} if not.
	 */
	public static boolean feed(Player player, Npc npc, Item food) {
		PetData data = PetData.getNpc(npc.getId()).orElse(null);
		
		if(data == null) {
			return false;
		}
		
		Pet pet = player.getPetManager().getPet().orElse(null);
		
		if(pet == null) {
			player.message("This is not your pet.");
			return false;
		}
		
		if(pet.getProgress().getHunger() < 15) {
			player.message("Your pet is not hungry at the moment...");
			return false;
		}
		
		FoodConsumable consumable = FoodConsumable.forId(food.getId()).orElse(null);
		Optional<PetFoodType> type = PetFoodType.getType(consumable);
		
		if(consumable == null || !type.isPresent()) {
			player.message("You can't feed " + TextUtils.appendIndefiniteArticle(food.getDefinition().getName()) + " to your " + pet.progress.getData().getType().toString() + ".");
			return false;
		}
		
		if(Arrays.stream(pet.progress.getData().getType().getConsumables()).noneMatch(t -> t.equals(type.get()))) {
			player.message("Your pet wont eat " + TextUtils.appendIndefiniteArticle(food.getDefinition().getName()) + ".");
			return false;
		}
		
		player.facePosition(pet.getPosition());
		player.animation(new Animation(827));
		player.getInventory().remove(food);
		pet.getProgress().setHunger(pet.getProgress().getHunger() - 15D);
		pet.forceChat("yum!");
		player.message("Your pet happily eats the " + food.getDefinition().getName() + ".");
		return true;
	}
	
	/**
	 * Attempts to spawn the pet.
	 * @param player the player spawning the pet.
	 * @param item   the item that represents the pet.
	 * @return {@code true} if the pet was spawned, {@code false} otherwise.
	 */
	public static boolean canDrop(Player player, Item item) {
		PetData data = PetData.getItem(item.getId()).orElse(null);
		
		if(data == null) {
			return false;
		}
		
		Optional<Pet> hasPet = player.getPetManager().getPet();
		
		if(hasPet.isPresent()) {
			player.message("You already have a pet summoned.");
			return true;
		}
		
		Optional<Familiar> hasFamiliar = player.getFamiliar();
		
		if(hasFamiliar.isPresent()) {
			player.message("You already have a familiar summoned.");
			return true;
		}
		
		Pet pet = new Pet(data, player.getPosition());
		
		pet = player.getPetManager().put(pet);
		
		World.getNpcs().add(pet);
		
		pet.getMovementQueue().follow(player);
		
		setInterface(player, pet);
		
		pet.task = pet.getProgress().getData().getPolicy().getNext().isPresent() ? Optional.of(new PetTask(player, pet)) : Optional.empty();
		
		pet.task.ifPresent(World.getTaskManager()::submit);
		
		player.getInventory().remove(item);
		return true;
	}
	
	/**
	 * Functionality that should occur when a player logs in.
	 * @param player the player logging in.
	 */
	public static void onLogin(Player player) {
		Pet pet = player.getPetManager().getPet().orElse(null);
		
		if(pet == null) {
			return;
		}
		
		pet.setPosition(player.getPosition());
		
		World.getNpcs().add(pet);
		
		pet.getMovementQueue().follow(player);
		
		setInterface(player, pet);
		
		pet.task = pet.getProgress().getData().getPolicy().getNext().isPresent() ? Optional.of(new PetTask(player, pet)) : Optional.empty();
		
		pet.task.ifPresent(World.getTaskManager()::submit);
	}
	
	/**
	 * The functionality that should occur when a player logs out.
	 * @param player the player logging out.
	 */
	public static void onLogout(Player player) {
		Pet pet = player.getPetManager().getPet().orElse(null);
		
		if(pet == null) {
			return;
		}
		World.getNpcs().remove(pet);
		
		pet.task.ifPresent(t -> t.setRunning(false));
		
		pet.task = Optional.empty();
	}
	
	/**
	 * Attempts to pickup the pet.
	 * @param player the player picking up the pet.
	 * @param npc    the npc that was picked up.
	 * @return {@code true} if the pet was picked up, {@code false} otherwise.
	 */
	public static boolean pickup(Player player, Npc npc) {
		PetData data = PetData.getNpc(npc.getId()).orElse(null);
		
		if(data == null) {
			return false;
		}
		
		Pet pet = player.getPetManager().getPet().orElse(null);
		
		if(pet == null) {
			player.message("This is not your pet.");
			return false;
		}
		
		if(!player.getInventory().add(data.getPolicy().getItem())) {
			player.message("You don't have enough space in your inventory!");
			return false;
		}
		
		World.getNpcs().remove(pet);
		
		player.getPetManager().reset();
		
		TabInterface.SUMMONING.sendInterface(player, -1);
		
		player.message("You picked up your pet.");
		
		pet.task.ifPresent(t -> t.setRunning(false));
		
		pet.task = Optional.empty();
		return true;
	}
	
	/**
	 * Attempts to set the interface for the specified player.
	 * @param player the player to set the interface for.
	 * @param pet    the pet that was spawned.
	 */
	private static void setInterface(Player player, Pet pet) {
		OutputMessages encoder = player.getMessages();
		
		encoder.sendString(pet.getDefinition().getName(), 19021);
		
		encoder.sendString(pet.progress.getGrowth() + "%", 19030);
		
		encoder.sendString(pet.progress.getHunger() + "%", 19032);
		
		encoder.sendNpcModelOnInterface(19019, pet.getId());
		encoder.sendInterfaceAnimation(19019, Expression.CALM.getExpression());
		
		TabInterface.SUMMONING.sendInterface(player, 19017);
	}
	
	/**
	 * @return the progress.
	 */
	public PetProgress getProgress() {
		return progress;
	}
	
	/**
	 * @return the task
	 */
	public Optional<PetTask> getTask() {
		return task;
	}
	
	/**
	 * @param task the task to set
	 */
	public void setTask(Optional<PetTask> task) {
		this.task = task;
	}
}
