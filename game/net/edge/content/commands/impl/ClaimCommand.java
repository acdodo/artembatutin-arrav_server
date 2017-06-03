package net.edge.content.commands.impl;

import net.edge.net.database.connection.use.Donating;
import net.edge.World;
import net.edge.content.commands.Command;
import net.edge.content.commands.CommandSignature;
import net.edge.world.node.entity.player.Player;
import net.edge.world.node.entity.player.assets.Rights;

@CommandSignature(alias = {"claim"}, rights = {Rights.DEVELOPER, Rights.ADMINISTRATOR, Rights.SUPER_MODERATOR, Rights.MODERATOR, Rights.EXTREME_DONATOR, Rights.SUPER_DONATOR, Rights.DONATOR, Rights.RESPECTED_MEMBER, Rights.DESIGNER, Rights.PLAYER}, syntax = "Use this command as ::claim username")
public final class ClaimCommand implements Command {

	@Override
	public void execute(Player player, String[] cmd, String command) throws Exception {
		new Donating(player, World.getDonation());
	}

}
