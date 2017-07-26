package net.edge.action.impl;

import net.edge.content.WebSlashing;
import net.edge.content.minigame.fightcaves.FightcavesMinigame;
import net.edge.content.minigame.pestcontrol.PestControlWaitingLobby;
import net.edge.content.minigame.warriorsguild.WarriorsGuild;
import net.edge.content.skill.agility.impl.Shortcuts;
import net.edge.content.skill.agility.impl.barb.BarbarianOutpostAgility;
import net.edge.content.skill.agility.impl.gnome.GnomeStrongholdAgility;
import net.edge.content.skill.agility.impl.wild.WildernessAgility;
import net.edge.content.skill.construction.furniture.HotSpots;
import net.edge.content.skill.crafting.PotClaying;
import net.edge.content.skill.mining.Mining;
import net.edge.content.skill.runecrafting.Runecrafting;
import net.edge.content.skill.smithing.Smelting;
import net.edge.content.skill.thieving.impl.Stalls;
import net.edge.content.skill.woodcutting.Woodcutting;
import net.edge.content.wilderness.Obelisk;
import net.edge.action.Action;
import net.edge.net.packet.in.ObjectActionPacket;
import net.edge.world.entity.actor.mob.impl.gwd.GodwarsFaction;
import net.edge.world.entity.actor.player.Player;
import net.edge.world.object.GameObject;

/**
 * Action handling object action clicks.
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public abstract class ObjectAction extends Action {
	
	public abstract boolean click(Player player, GameObject object, int click);
	
	public void registerFirst(int object) {
		ObjectActionPacket.FIRST.register(object, this);
	}
	
	public void registerSecond(int object) {
		ObjectActionPacket.SECOND.register(object, this);
	}
	
	public void registerThird(int object) {
		ObjectActionPacket.THIRD.register(object, this);
	}
	
	public void registerFourth(int object) {
		ObjectActionPacket.FOURTH.register(object, this);
	}
	
	public void registerFifth(int object) {
		ObjectActionPacket.FIFTH.register(object, this);
	}
	
	public void registerCons(int object) { ObjectActionPacket.CONSTRUCTION.register(object, this);}
	
	public static void init() {
		WebSlashing.event();
		WarriorsGuild.event();
		Obelisk.event();
		FightcavesMinigame.event();
		Woodcutting.event();
		Mining.event();
		Runecrafting.event();
		GnomeStrongholdAgility.event();
		BarbarianOutpostAgility.event();
		WildernessAgility.event();
		Shortcuts.event();
		PotClaying.objects();
		Stalls.event();
		Smelting.event();
		HotSpots.event();
		PestControlWaitingLobby.event();
		GodwarsFaction.event();
	}
	
}