package net.edge.content.skill.construction.furniture;

import net.edge.content.skill.SkillData;
import net.edge.content.skill.Skills;
import net.edge.content.skill.action.impl.ProducingSkillAction;
import net.edge.content.skill.construction.Construction;
import net.edge.content.skill.construction.House;
import net.edge.content.skill.construction.room.RoomFurniture;
import net.edge.content.skill.construction.data.Constants;
import net.edge.content.skill.construction.room.Room;
import net.edge.task.Task;
import net.edge.world.Animation;
import net.edge.world.node.entity.player.Player;
import net.edge.world.node.entity.player.assets.Rights;
import net.edge.world.node.item.Item;

import java.util.Optional;

/**
 * Represents the process for building {@link Furniture}.
 * @author Artem Batutin <artembatutin@gmail.com></artembatutin@gmail.com>
 */
public final class ConstructFurniture extends ProducingSkillAction {
	
	private final ConstructionPlan plan;
	
	/**
	 * Constructs a new {@link ConstructFurniture} skill action.
	 * @param player     the player we're starting this action for.
	 * @param plan the furniture planned to be built.
	 */
	public ConstructFurniture(Player player, ConstructionPlan plan) {
		super(player, Optional.empty());
		this.plan = plan;
	}
	
	@Override
	public void onProduce(Task t, boolean success) {
		if(success) {
			t.cancel();
			House house = getPlayer().getHouse();
			int[] myTiles = Construction.getMyChunk(getPlayer());
			int toHeight = (house.get().isDungeon() ? 4 : getPlayer().getPosition().getZ());
			Room room = house.get().getRooms()[toHeight][myTiles[0] - 1][myTiles[1] - 1];
			int roomRot = room.getRotation();
			int myRoomType = room.data().getId();
			HotSpots[] hsses = room.data().getSpots();
			HotSpots s = null;
			if(hsses.length == 1) {
				s = hsses[0];
			} else {
				for(HotSpots find : hsses) {
					int actualX = Constants.BASE_X + (myTiles[0] * 8);
					actualX += Constants.getXOffsetForObjectId(find.getObjectId(), find, roomRot);
					int actualY = Constants.BASE_Y + (myTiles[1] * 8);
					actualY += Constants.getYOffsetForObjectId(find.getObjectId(), find, roomRot);
					if(plan.getObjectX() == actualX && plan.getObjectY() == actualY && myRoomType == find.getRoomType() || find.getCarpetDim() != null && myRoomType == find.getRoomType()) {
						s = find;
						break;
					}
				}
			}
			if(s == null)
				return;
			int actualX = Constants.BASE_X + (myTiles[0] * 8);
			actualX += Constants.getXOffsetForObjectId(plan.getSelected().getId(), s, house.get().getRooms()[toHeight][myTiles[0] - 1][myTiles[1] - 1].getRotation());
			int actualY = Constants.BASE_Y + (myTiles[1] * 8);
			actualY += Constants.getYOffsetForObjectId(plan.getSelected().getId(), s, roomRot);
			if(s.getRoomType() != myRoomType && s.getCarpetDim() == null) {
				getPlayer().message("You can't build this furniture in this room.");
				return;
			}
			Construction.doFurniturePlace(s, plan.getSelected(), hsses, myTiles, actualX, actualY, roomRot, getPlayer(), false, getPlayer().getPosition().getZ());
			room.addFurniture(new RoomFurniture(plan.getSelected(), s.getXOffset(), s.getYOffset()));
			house.get().setPlan(new ConstructionPlan());//clearing plan.
		}
	}
	
	@Override
	public boolean init() {
		player.animation(new Animation(3684));
		player.getMessages().sendCloseWindows();
		return true;
	}
	
	@Override
	public int delay() {
		return 2;
	}
	
	@Override
	public boolean instant() {
		return false;
	}
	
	@Override
	public boolean canExecute() {
		if(player.getSkills()[Skills.CONSTRUCTION].getRealLevel() < plan.getSelected().getLevel()) {
			player.message("You need a construction level of " + plan.getSelected().getLevel() + " to build this.");
			return false;
		}
		return true;
	}
	
	@Override
	public double experience() {
		return plan.getSelected().getXP();
	}
	
	@Override
	public SkillData skill() {
		return SkillData.CONSTRUCTION;
	}
	
	@Override
	public Optional<Animation> startAnimation() {
		return Optional.empty();
	}
	
	@Override
	public Optional<Item[]> removeItem() {
		if(getPlayer().getRights() == Rights.DEVELOPER)
			return Optional.empty();
		return Optional.of(plan.getSelected().getRequiredItems());
	}
	
	@Override
	public Optional<Item[]> produceItem() {
		return Optional.empty();
	}
}