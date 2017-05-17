package net.edge.net.message.impl;

import net.edge.net.PunishmentHandler;
import net.edge.net.codec.ByteMessage;
import net.edge.net.codec.ByteTransform;
import net.edge.net.message.InputMessageListener;
import net.edge.world.model.node.entity.player.Player;
import net.edge.world.model.node.entity.player.assets.activity.ActivityManager.ActivityType;
import net.edge.world.model.node.entity.update.UpdateFlag;

/**
 * The message sent from the client when the player speaks.
 * @author lare96 <http://github.com/lare96>
 */
public final class ChatMessage implements InputMessageListener {
	
	@Override
	public void handleMessage(Player player, int opcode, int size, ByteMessage payload) {
		if(player.getActivityManager().contains(ActivityType.CHAT_MESSAGE))
			return;
		
		if(player.isMuted() || PunishmentHandler.isIPMuted(player.getSession().getHost())) {
			player.message("You are currently muted.");
			return;
		}
		
		int effects = payload.get(false, ByteTransform.S);
		int color = payload.get(false, ByteTransform.S);
		int chatLength = (size - 2);
		byte[] text = payload.getBytesReverse(chatLength, ByteTransform.A);
		if(effects < 0 || color < 0 || chatLength < 0)
			return;
		player.setChatEffects(effects);
		player.setChatColor(color);
		player.setChatText(text);
		player.getFlags().flag(UpdateFlag.CHAT);
		player.getActivityManager().execute(ActivityType.CHAT_MESSAGE);
	}
}
