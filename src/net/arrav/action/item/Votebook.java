package net.arrav.action.item;

import net.arrav.action.ActionInitializer;
import net.arrav.action.impl.ItemAction;
import net.arrav.content.PlayerPanel;
import net.arrav.content.VoteRewards;
import net.arrav.util.TextUtils;
import net.arrav.world.entity.actor.player.Player;
import net.arrav.world.entity.item.Item;
import net.arrav.world.entity.item.container.impl.Inventory;

public class Votebook extends ActionInitializer {
	@Override
	public void init() {
		ItemAction e = new ItemAction() {
			@Override
			public boolean click(Player player, Item item, int container, int slot, int click) {
				if(container != Inventory.INVENTORY_DISPLAY_ID)
					return true;
				player.votePoints += 1;
				player.totalVotes += 1;
				PlayerPanel.TOTAL_VOTES.refresh(player, "@or2@ - Total votes: @yel@" + player.totalVotes);
				PlayerPanel.VOTE.refresh(player, "@or3@ - Vote points: @yel@" + player.votePoints + " points", true);
				player.message("You received a vote point, you can access the vote shop in the quest tab.");
				Item reward = VoteRewards.getReward().orElse(null);
				player.getInventory().remove(item);
				if(reward == null) {
					player.message("... but you were unlucky and didn't receive a extra reward.");
					return true;
				}
				String name = reward.getDefinition().getName();
				player.message("... and you were lucky and received x" + item.getAmount() + " " + TextUtils.capitalize(name) + ".");
				player.getInventory().addOrBank(reward);
				return true;
			}
		};
		e.register(6829);
	}
}
