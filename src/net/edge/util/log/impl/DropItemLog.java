package net.edge.util.log.impl;

import net.edge.util.TextUtils;
import net.edge.util.log.Log;
import net.edge.world.World;
import net.edge.world.entity.actor.player.Player;
import net.edge.world.entity.item.GroundItem;
import net.edge.world.entity.item.Item;
import net.edge.world.locale.Position;

import java.util.Optional;

/**
 * The class which represents a drop log.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class DropItemLog extends Log {

    /**
     * Constructs a new {@link Log}.
     */
    public DropItemLog(Player player, Item item, Position position, Optional<GroundItem> pickedup) {
        super(null, "item_drop", formatInformation(player, item, position, pickedup));
    }

    @Override
    public String getPath() {
        return World.getLoggingManager().parent.getAbsolutePath() + "/item_drops";
    }

    private static String formatInformation(Player player, Item item, Position pos, Optional<GroundItem> pickedup) {
        StringBuilder builder = new StringBuilder();
        builder.append(player.getFormatUsername() + " " + (pickedup.isPresent() ? "picked up" : "dropped") + " " + item.getAmount() + "x " + item.getDefinition().getName() + (item.getAmount() > 1 ? TextUtils.determinePluralCheck(item.getDefinition().getName()) : "") + " on " + pos.toString().toLowerCase() + (pickedup.isPresent() ? (" from owner = " + pickedup.get().getPlayer().getFormatUsername()) : "."));
        return builder.toString();
    }
}