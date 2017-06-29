package net.edge.content.commands.impl;

import net.edge.content.commands.Command;
import net.edge.content.commands.CommandSignature;
import net.edge.content.skill.construction.Construction;
import net.edge.locale.Position;
import net.edge.world.World;
import net.edge.world.node.entity.player.Player;
import net.edge.world.node.entity.player.assets.Rights;

/**
 * The assist command for staff members.
 */
@CommandSignature(alias = {"house"}, rights = {Rights.DEVELOPER}, syntax = "Use this command as ::house")
public final class HouseCommand implements Command {
	
	@Override
	public void execute(Player player, String[] cmd, String command) throws Exception {
		Construction.buyHouse(player);
		Construction.enterHouse(player, true);
	}
	
}