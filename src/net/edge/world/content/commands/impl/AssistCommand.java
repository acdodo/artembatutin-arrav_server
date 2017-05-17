package net.edge.world.content.commands.impl;

import net.edge.world.World;
import net.edge.world.content.commands.Command;
import net.edge.world.content.commands.CommandSignature;
import net.edge.world.model.locale.Position;
import net.edge.world.model.node.entity.player.Player;
import net.edge.world.model.node.entity.player.assets.Rights;

import java.util.Optional;

/**
 * The assist command for staff members.
 */
@CommandSignature(alias = {"assist", "help"}, rights = {Rights.DEVELOPER, Rights.ADMINISTRATOR, Rights.SUPER_MODERATOR, Rights.MODERATOR}, syntax = "Use this command as ::assist username")
public final class AssistCommand implements Command {
	
	@Override
	public void execute(Player player, String[] cmd, String command) throws Exception {
		Player assisted = World.getPlayer(cmd[1].replaceAll("_", " ")).orElse(null);
		if(assisted != null && assisted != player) {
			Optional<Position> pos = World.getTraversalMap().getNearbyTraversableTiles(assisted.getPosition(), 1).stream().findAny();
			player.move(pos.orElse(assisted.getPosition()));
			player.forceChat("Hello, my name is " + player.getFormatUsername() + ", I'm here to assist you.");
		}
	}
	
}
