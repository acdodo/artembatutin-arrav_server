package net.edge.content.dialogue.impl;

import net.edge.util.ActionListener;
import net.edge.content.dialogue.Dialogue;
import net.edge.content.dialogue.DialogueBuilder;
import net.edge.content.dialogue.DialogueType;
import net.edge.world.node.item.Item;

import java.util.Optional;

/**
 * The dialogue chain entry that request the player an item.
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public final class RequestItemDialogue extends Dialogue {
	
	/**
	 * The item to remove to the player during this chain.
	 */
	private final Item item;
	
	/**
	 * The optional reward that's given to the player.
	 */
	private final Optional<Item> reward;
	
	/**
	 * The action to execute when the requested item is given.
	 */
	private final Optional<ActionListener> action;

	private final boolean displayReward;
	
	/**
	 * Creates a new {@link RequestItemDialogue}.
	 * @param item the item to remove to the player during this chain.
	 * @param text the text to display when the item is removed.
	 */
	public RequestItemDialogue(Item item, Optional<Item> reward, String text, Optional<ActionListener> action, boolean displayReward) {
		super(text);
		this.item = item;
		this.reward = reward;
		this.action = action;
		this.displayReward = displayReward;
	}
	
	public RequestItemDialogue(Item item, Item reward, String text, Optional<ActionListener> action) {
		this(item, Optional.of(reward), text, action, false);
	}
	
	public RequestItemDialogue(Item item, int reward, String text, Optional<ActionListener> action) {
		this(item, new Item(reward), text, action);
	}
	
	public RequestItemDialogue(Item item, String text, Optional<ActionListener> action) {
		this(item, Optional.empty(), text, action, false);
	}
	
	@Override
	public void accept(DialogueBuilder dialogue) {
		if(dialogue.getPlayer().getInventory().contains(item)) {
			dialogue.getPlayer().getInventory().remove(item);
			action.ifPresent(ActionListener::execute);
			reward.ifPresent(dialogue.getPlayer().getInventory()::addOrDrop);
			dialogue.getPlayer().getMessages().sendString(getText()[0], 308);
			int id = displayReward && reward.isPresent() ? reward.get().getId() : item.getId();
			dialogue.getPlayer().getMessages().sendItemModelOnInterface(307, 200, id);
			dialogue.getPlayer().getMessages().sendChatInterface(306);
		} else {
			dialogue.getPlayer().getMessages().sendChatboxString("You don't have the requested item...");
		}
	}
	
	@Override
	public DialogueType type() {
		return DialogueType.REQUEST_ITEM_DIALOGUE;
	}
}