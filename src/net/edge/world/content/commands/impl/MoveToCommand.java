package net.edge.world.content.commands.impl;

import net.edge.world.World;
import net.edge.world.content.commands.Command;
import net.edge.world.content.commands.CommandSignature;
import net.edge.world.model.node.entity.player.Player;
import net.edge.world.model.node.entity.player.assets.Rights;

@CommandSignature(alias = {"moveto"}, rights = {Rights.DEVELOPER, Rights.ADMINISTRATOR, Rights.MODERATOR}, syntax = "Use this command as ::moveto username")
public final class MoveToCommand implements Command {
	
	@Override
	public void execute(Player player, String[] cmd, String command) throws Exception {
		Player p = World.getPlayer(cmd[1].replaceAll("_", " ")).orElse(null);
		if(p == null) {
			player.message("Cant find " + cmd[1].replaceAll("_", " ") + ".");
			return;
		}
		player.move(p.getPosition());
		player.message("You moved to " + p.getFormatUsername() + "'s position.");
	}
	
}
