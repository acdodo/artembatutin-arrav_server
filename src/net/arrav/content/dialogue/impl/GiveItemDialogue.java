package net.arrav.content.dialogue.impl;

import net.arrav.content.dialogue.Dialogue;
import net.arrav.content.dialogue.DialogueBuilder;
import net.arrav.content.dialogue.DialogueType;
import net.arrav.net.packet.out.SendItemModelInterface;
import net.arrav.util.ActionListener;
import net.arrav.world.entity.item.Item;

import java.util.Optional;

/**
 * The dialogue chain entry that gives the player an item.
 * @author lare96 <http://github.com/lare96>
 */
public final class GiveItemDialogue extends Dialogue {

	/**
	 * The item to give to the player during this chain.
	 */
	private final Item item;

	/**
	 * The action to execute when the requested item is given.
	 */
	private final Optional<ActionListener> action;

	/**
	 * Creates a new {@link GiveItemDialogue}.
	 * @param item the item to give to the player during this chain.
	 * @param text the text to display when the item is given.
	 */
	public GiveItemDialogue(Item item, String text, Optional<ActionListener> action) {
		super(text);
		this.item = item;
		this.action = action;
	}

	@Override
	public void accept(DialogueBuilder t) {
		if(t.getPlayer().getInventory().canAdd(item)) {
			t.getPlayer().getInventory().add(item);
			action.ifPresent(ActionListener::execute);
			t.getPlayer().text(308, getText()[0]);
			t.getPlayer().out(new SendItemModelInterface(307, 200, item.getId()));
			t.getPlayer().chatWidget(306);
		} else {
			t.getPlayer().text(357, "You do not have enough space in your inventory!");
			t.getPlayer().text(358, "Click here to continue");
			t.getPlayer().chatWidget(356);
		}
	}

	@Override
	public DialogueType type() {
		return DialogueType.GIVE_ITEM_DIALOGUE;
	}
}