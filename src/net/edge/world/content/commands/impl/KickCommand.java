package net.edge.world.content.commands.impl;

import net.edge.world.content.commands.CommandSignature;
import net.edge.world.model.node.entity.player.Player;
import net.edge.world.World;
import net.edge.world.content.commands.Command;
import net.edge.world.model.node.entity.player.assets.Rights;

@CommandSignature(alias = {"kick"}, rights = {Rights.DEVELOPER, Rights.ADMINISTRATOR, Rights.MODERATOR}, syntax = "Use this command as ::kick username")
public final class KickCommand implements Command {
	
	@Override
	public void execute(Player player, String[] cmd, String command) throws Exception {
		Player kick = World.getPlayer(cmd[1].replaceAll("_", " ")).orElse(null);
		if(kick != null && (kick.getRights().less(Rights.MODERATOR) || player.getRights().equals(Rights.DEVELOPER)) && kick != player) {
			player.message("Successfully kicked " + kick.getFormatUsername() + ".");
			World.queueLogout(kick);
		}
	}
	
}
