package net.edge.content.commands.impl;

import net.edge.content.commands.Command;
import net.edge.content.commands.CommandSignature;
import net.edge.content.market.MarketCounter;
import net.edge.world.entity.actor.player.Player;
import net.edge.world.entity.actor.player.assets.Rights;

@CommandSignature(alias = {"shop"}, rights = {Rights.ADMINISTRATOR}, syntax = "Opens a shop, ::shop id")
public final class OpenShopCommand implements Command {

	@Override
	public void execute(Player player, String[] cmd, String command) throws Exception {
		int id = Integer.parseInt(cmd[1]);
		if(MarketCounter.getShops().containsKey(id))
			MarketCounter.getShops().get(id).openShop(player);
		else
			player.message("Shop doesn't exist.");
	}

}
