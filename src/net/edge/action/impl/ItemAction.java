package net.edge.action.impl;

import net.edge.action.Action;
import net.edge.content.item.*;
import net.edge.content.object.cannon.Multicannon;
import net.edge.content.skill.herblore.Herb;
import net.edge.content.skill.prayer.PrayerBoneBury;
import net.edge.content.skill.slayer.Slayer;
import net.edge.content.skill.summoning.SummoningData;
import net.edge.net.packet.in.ItemActionPacket;
import net.edge.net.packet.in.ItemInterfacePacket;
import net.edge.world.entity.actor.player.Player;
import net.edge.world.entity.item.Item;

/**
 * Action handling item action clicks.
 * Can be both interface and action wise.
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public abstract class ItemAction extends Action {
	
	public abstract boolean click(Player player, Item item, int container, int slot, int click);
	
	public void register(int item) {
		ItemActionPacket.ITEM_ACTION.register(item, this);
	}
	
	public void registerEquip(int item) {
		ItemInterfacePacket.EQUIP.register(item, this);
	}
	
	public static void init() {
		PrayerBoneBury.action();
		FoodConsumable.action();
		PotionConsumable.action();
		Herb.action();
		Slayer.actionItem();
		Dice.action();
		MithrilSeed.action();
		SummoningData.action();
		ExperienceLamp.handleItem();
		Multicannon.action();
	}
	
}
