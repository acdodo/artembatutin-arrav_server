package net.arrav.net.packet.in;

import io.netty.buffer.ByteBuf;
import net.arrav.Arrav;
import net.arrav.action.ActionContainer;
import net.arrav.action.impl.ButtonAction;
import net.arrav.content.Emote;
import net.arrav.content.TabInterface;
import net.arrav.content.clanchat.ClanManager;
import net.arrav.content.dialogue.Dialogues;
import net.arrav.content.item.Skillcape;
import net.arrav.content.market.MarketShop;
import net.arrav.content.minigame.MinigameHandler;
import net.arrav.content.skill.SkillData;
import net.arrav.content.skill.cooking.Cooking;
import net.arrav.content.skill.cooking.CookingData;
import net.arrav.content.skill.cooking.DoughCreation;
import net.arrav.content.skill.crafting.*;
import net.arrav.content.skill.fletching.BowCarving;
import net.arrav.content.skill.magic.EnchantCrossbowBolts;
import net.arrav.content.skill.magic.Spellbook;
import net.arrav.content.skill.prayer.Prayer;
import net.arrav.content.skill.slayer.Slayer;
import net.arrav.content.skill.smithing.Smelting;
import net.arrav.content.skill.summoning.Summoning;
import net.arrav.net.packet.IncomingPacket;
import net.arrav.net.packet.out.SendConfig;
import net.arrav.net.packet.out.SendEnterName;
import net.arrav.net.packet.out.SendLogout;
import net.arrav.world.entity.actor.combat.attack.FightType;
import net.arrav.world.entity.actor.combat.magic.CombatSpell;
import net.arrav.world.entity.actor.combat.magic.lunars.LunarSpells;
import net.arrav.world.entity.actor.combat.weapon.WeaponInterface;
import net.arrav.world.entity.actor.player.Player;
import net.arrav.world.entity.actor.player.assets.Rights;
import net.arrav.world.entity.actor.player.assets.activity.ActivityManager;
import net.arrav.world.entity.item.Item;
import net.arrav.world.entity.item.container.impl.Equipment;
import net.arrav.world.entity.item.container.session.ExchangeSessionManager;
import net.arrav.world.object.GameObject;

import java.util.concurrent.TimeUnit;

/**
 * The message sent from the client when the player clicks some sort of button or
 * module.
 * @author lare96 <http://github.com/lare96>
 */
public final class ClickButtonPacket implements IncomingPacket {
	
	public static final ActionContainer<ButtonAction> BUTTONS = new ActionContainer<>();
	
	/**
	 * The flag that determines if this message should be read properly.
	 */
	private static final boolean PROPER_READ = false;
	
	private static int hexToInt(byte[] data) {
		int value = 0;
		int n = 1000;
		for(byte aData : data) {
			int num = (aData & 0xFF) * n;
			value += num;
			if(n > 1) {
				n = n / 1000;
			}
		}
		return value;
	}
	
	@Override
	public void handle(Player player, int opcode, int size, ByteBuf buf) {
		int button = PROPER_READ ? buf.getShort() : hexToInt(buf.getBytes(2));
		if(Arrav.DEBUG && player.getRights().equals(Rights.ADMINISTRATOR)) {
			player.message("Clicked button " + button + ".");
		}
		
		if(button != 9154 && button != 200 && button != 201 && player.getActivityManager().contains(ActivityManager.ActivityType.CLICK_BUTTON)) {
			return;
		}
		if(button == 123) {
			player.closeWidget();
			if(player.getMarketShop() != null) {
				MarketShop.clearFromShop(player);
			}
		}
		ButtonAction e = BUTTONS.get(button);
		if(e != null) {
			if(e.click(player, button)) {
				return;
			}
		}
		if(Prayer.activate(player, true, button)) {
			return;
		}
		if(Prayer.activateQuickPrayer(player, button)) {
			return;
		}
		if(Prayer.toggleQuickPrayer(player, button)) {
			return;
		}
		if(EnchantCrossbowBolts.openInterface(player, button) || EnchantCrossbowBolts.enchant(player, button)) {
			return;
		}
		if(Emote.handle(player, button)) {
			return;
		}
		if(Skillcape.handle(player, button)) {
			return;
		}
		if(BowCarving.fletch(player, button)) {
			return;
		}
		if(Summoning.withdrawAll(player, button)) {
			return;
		}
		if(Smelting.smelt(player, button)) {
			return;
		}
		if(Glassblowing.blow(player, button)) {
			return;
		}
		if(Tanning.create(player, button)) {
			return;
		}
		if(DoughCreation.create(player, button)) {
			return;
		}
		if(Spinning.create(player, button)) {
			return;
		}
		if(PotClaying.create(player, button)) {
			return;
		}
		if(HideWorking.create(player, button)) {
			return;
		}
		if(LeatherWorking.create(player, button)) {
			return;
		}
		if(ExchangeSessionManager.get().buttonClickAction(player, button)) {
			return;
		}
		if(Dialogues.executeOptionListeners(player, button)) {
			return;
		}
		if(SkillData.sendEnterGoalLevel(player, button)) {
			return;
		}
		if(Slayer.clickButton(player, button)) {
			return;
		}
		if(LunarSpells.castButtonSpell(player, button)) {
			return;
		}
		//Bank 100-109
		if(button >= 100 && button <= 109) {
			player.getBank().setTab(button - 100);
		}
		switch(button) {
			case 55095:
				Item item = player.getInventory().get(player.getAttr().get("destroy_item_slot").getInt());
				player.getInventory().remove(item);
				
				player.getAttr().get("destroy_item_slot").set(-1);
				player.closeWidget();
				break;
			case 55096:
				player.closeWidget();
				break;
			case 195212:
				if(player.getClan().isPresent())
					if(player.getClan().get().getRank().getValue() >= player.getClan().get().getClan().getLowest().getValue())
						player.widget(-3);
					else
						player.getClan().get().sendMessage("You don't have the requirements to do that.");
				else
					player.out(new SendEnterName("Your clan chat name:", t -> () -> ClanManager.get().create(player, t)));
				break;
			case 83093:
				player.widget(15106);
				break;
			case 195209:
				if(player.getClan().isPresent())
					ClanManager.get().exit(player);
				else
					player.out(new SendEnterName("Enter the name of the chat you wish to join.", s -> () -> ClanManager.get().join(player, s)));
				break;
			
			case 59135:
				if(player.getViewingOrb() != null)
					player.getViewingOrb().move("Centre", 15239, player.getViewingOrb().getCentre());
				break;
			case 59136:
				if(player.getViewingOrb() != null)
					player.getViewingOrb().move("North-West", 15240, player.getViewingOrb().getNorthWest());
				break;
			case 59137:
				if(player.getViewingOrb() != null)
					player.getViewingOrb().move("North-East", 15241, player.getViewingOrb().getNorthEast());
				break;
			case 59138:
				if(player.getViewingOrb() != null)
					player.getViewingOrb().move("South-East", 15242, player.getViewingOrb().getSouthEast());
				break;
			case 59139:
				if(player.getViewingOrb() != null)
					player.getViewingOrb().move("South-West", 15243, player.getViewingOrb().getSouthWest());
				break;
			case 17111:
				if(player.getViewingOrb() != null) {
					player.getViewingOrb().close();
					player.setViewingOrb(null);
				}
				break;
			case 53152:
				CookingData cookingData = (CookingData) player.getAttr().get("cooking_data").get();
				if(cookingData != null) {
					Cooking cooking = new Cooking(player, (GameObject) player.getAttr().get("cooking_object").get(), cookingData, (Boolean) player.getAttr().get("cooking_usingStove").get(), 1);
					cooking.start();
				}
				break;
			case 53151:
				CookingData cookingData1 = (CookingData) player.getAttr().get("cooking_data").get();
				if(cookingData1 != null) {
					Cooking cooking = new Cooking(player, (GameObject) player.getAttr().get("cooking_object").get(), cookingData1, (Boolean) player.getAttr().get("cooking_usingStove").get(), 5);
					cooking.start();
				}
				break;
			case 53149:
				CookingData cookingData2 = (CookingData) player.getAttr().get("cooking_data").get();
				if(cookingData2 != null) {
					int amount = player.getInventory().computeAmountForId(cookingData2.getRawId());
					Cooking cooking = new Cooking(player, (GameObject) player.getAttr().get("cooking_object").get(), cookingData2, (Boolean) player.getAttr().get("cooking_usingStove").get(), amount);
					cooking.start();
				}
				break;
			case 100237:
			case 48176:
				Boolean acceptAid = (Boolean) player.getAttr().get("accept_aid").get();
				if(!acceptAid) {
					player.message("Accept aid has been turned on.");
					player.getAttr().get("accept_aid").set(true);
				} else {
					player.message("Accept aid has been turned off.");
					player.getAttr().get("accept_aid").set(false);
				}
				player.out(new SendConfig(427, !acceptAid ? 0 : 1));
				break;
			case 89061:
			case 93202:
				if(!player.isAutoRetaliate()) {
					player.setAutoRetaliate(true);
					player.message("Auto retaliate has been turned on!");
				} else {
					player.setAutoRetaliate(false);
					player.message("Auto retaliate has been turned off!");
				}
				break;
			case 9154:
				if(!MinigameHandler.execute(player, t -> t.canLogout(player)))
					break;
				if(!player.getCombat().hasPassed(10)) {
					player.message("You must wait " + (TimeUnit.MILLISECONDS.toSeconds(10_000 - player.getCombat().elapsedTime())) + " seconds after combat before logging out.");
					break;
				}
				if(player.getActivityManager().contains(ActivityManager.ActivityType.LOG_OUT)) {
					player.message("You can't log out right now.");
					break;
				}
				player.out(new SendLogout());
				break;
			case 153:
			case 152:
			case 74214:
				if(player.getMovementQueue().isRunning()) {
					player.getMovementQueue().setRunning(false);
				} else {
					if(player.getRunEnergy() <= 0) {
						break;
					}
					player.getMovementQueue().setRunning(true);
				}
				break;
			case 82018:
				player.getBank().depositeInventory();
				break;
			case 231047:
				player.getBank().depositeEquipment();
				break;
			case 231043:
				player.getBank().depositeFamiliar();
				break;
			case 231041:
				player.getAttr().get("withdraw_as_note").set(!(player.getAttr().get("withdraw_as_note").getBoolean()));
				player.out(new SendConfig(115, player.getAttr().get("withdraw_as_note").getBoolean() ? 1 : 0));
				break;
			case 231037:
				//player.getAttr().get("insert_item").set(!(player.getAttr().get("insert_item").getBoolean()));
				player.message("Temporary disabled feature.");
				//player.out(new SendConfig(116, player.getAttr().get("insert_item").getBoolean() ? 1 : 0));
				break;
			case 24017:
			case 7212:
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				break;
			//FIGHT TYPES
			case 1080: // staff
				player.getCombat().setFightType(FightType.STAFF_BASH);
				break;
			case 1079:
				player.getCombat().setFightType(FightType.STAFF_POUND);
				break;
			case 1078:
				player.getCombat().setFightType(FightType.STAFF_FOCUS);
				break;
			case 1177: // warhammer
				player.getCombat().setFightType(FightType.WARHAMMER_POUND);
				break;
			case 1176:
				player.getCombat().setFightType(FightType.WARHAMMER_PUMMEL);
				break;
			case 1175:
				player.getCombat().setFightType(FightType.WARHAMMER_BLOCK);
				break;
			case 3014: // scythe
				player.getCombat().setFightType(FightType.SCYTHE_REAP);
				break;
			case 3017:
				player.getCombat().setFightType(FightType.SCYTHE_CHOP);
				break;
			case 3016:
				player.getCombat().setFightType(FightType.SCYTHE_JAB);
				break;
			case 3015:
				player.getCombat().setFightType(FightType.SCYTHE_BLOCK);
				break;
			case 6168: // battle axe
				player.getCombat().setFightType(FightType.BATTLEAXE_CHOP);
				break;
			case 6171:
				player.getCombat().setFightType(FightType.BATTLEAXE_HACK);
				break;
			case 6170:
				player.getCombat().setFightType(FightType.BATTLEAXE_SMASH);
				break;
			case 6169:
				player.getCombat().setFightType(FightType.BATTLEAXE_BLOCK);
				break;
			case 14218: // mace
				player.getCombat().setFightType(FightType.MACE_POUND);
				break;
			case 14221:
				player.getCombat().setFightType(FightType.MACE_PUMMEL);
				break;
			case 14220:
				player.getCombat().setFightType(FightType.MACE_SPIKE);
				break;
			case 14219:
				player.getCombat().setFightType(FightType.MACE_BLOCK);
				break;
			case 18077: // spear
				player.getCombat().setFightType(FightType.SPEAR_LUNGE);
				break;
			case 18080:
				player.getCombat().setFightType(FightType.SPEAR_SWIPE);
				break;
			case 18079:
				player.getCombat().setFightType(FightType.SPEAR_POUND);
				break;
			case 18078:
				player.getCombat().setFightType(FightType.SPEAR_BLOCK);
				break;
			case 18106://2h sword
				player.getCombat().setFightType(FightType.TWOHANDEDSWORD_SLASH);
				break;
			case 18105:
				player.getCombat().setFightType(FightType.TWOHANDEDSWORD_SMASH);
				break;
			case 18104:
				player.getCombat().setFightType(FightType.TWOHANDEDSWORD_BLOCK);
				break;
			case 18103:
				player.getCombat().setFightType(FightType.TWOHANDEDSWORD_CHOP);
				break;
			case 15106:
				player.getCombat().setFightType(FightType.TWOHANDEDSWORD_SLASH);
				break;
			case 21200: // pickaxe
				player.getCombat().setFightType(FightType.PICKAXE_SPIKE);
				break;
			case 21203:
				player.getCombat().setFightType(FightType.PICKAXE_IMPALE);
				break;
			case 21202:
				player.getCombat().setFightType(FightType.PICKAXE_SMASH);
				break;
			case 21201:
				player.getCombat().setFightType(FightType.PICKAXE_BLOCK);
				break;
			case 30088: // claws
				player.getCombat().setFightType(FightType.CLAWS_CHOP);
				break;
			case 30091:
				player.getCombat().setFightType(FightType.CLAWS_SLASH);
				break;
			case 30090:
				player.getCombat().setFightType(FightType.CLAWS_LUNGE);
				break;
			case 30089:
				player.getCombat().setFightType(FightType.CLAWS_BLOCK);
				break;
			case 33018: // halberd
				player.getCombat().setFightType(FightType.HALBERD_JAB);
				break;
			case 33020:
				player.getCombat().setFightType(FightType.HALBERD_SWIPE);
				break;
			case 33016:
				player.getCombat().setFightType(FightType.HALBERD_FEND);
				break;
			case 22228: // unarmed
				player.getCombat().setFightType(FightType.UNARMED_PUNCH);
				break;
			case 22230:
				player.getCombat().setFightType(FightType.UNARMED_KICK);
				break;
			case 22229:
				player.getCombat().setFightType(FightType.UNARMED_BLOCK);
				break;
			case 48010: // whip
				player.getCombat().setFightType(FightType.WHIP_FLICK);
				break;
			case 48009:
				player.getCombat().setFightType(FightType.WHIP_LASH);
				break;
			case 48008:
				player.getCombat().setFightType(FightType.WHIP_DEFLECT);
				break;
			case 94014:
				player.getCombat().setFightType(FightType.SCORCH);
				break;
			case 94015:
				player.getCombat().setFightType(FightType.FLARE);
				break;
			case 94016:
				player.getCombat().setFightType(FightType.BLAZE);
				break;
			case 93251:
				if(player.getWeapon().equals(WeaponInterface.CHINCHOMPA)) {
					player.getCombat().setFightType(FightType.SHORT_FUSE);
				}
				break;
			case 93252:
				if(player.getWeapon().equals(WeaponInterface.CHINCHOMPA)) {
					player.getCombat().setFightType(FightType.MEDIUM_FUSE);
				}
				break;
			case 93253:
				if(player.getWeapon().equals(WeaponInterface.CHINCHOMPA)) {
					player.getCombat().setFightType(FightType.LONG_FUSE);
				}
				break;
			case 17102: // knife, thrownaxe, dart & javelin
				if(player.getWeapon() == WeaponInterface.KNIFE) {
					player.getCombat().setFightType(FightType.KNIFE_ACCURATE);
				} else if(player.getWeapon() == WeaponInterface.THROWNAXE) {
					player.getCombat().setFightType(FightType.THROWNAXE_ACCURATE);
				} else if(player.getWeapon() == WeaponInterface.DART) {
					player.getCombat().setFightType(FightType.DART_ACCURATE);
				} else if(player.getWeapon() == WeaponInterface.JAVELIN) {
					player.getCombat().setFightType(FightType.JAVELIN_ACCURATE);
				}
				break;
			case 17101:
				if(player.getWeapon() == WeaponInterface.KNIFE) {
					player.getCombat().setFightType(FightType.KNIFE_RAPID);
				} else if(player.getWeapon() == WeaponInterface.THROWNAXE) {
					player.getCombat().setFightType(FightType.THROWNAXE_RAPID);
				} else if(player.getWeapon() == WeaponInterface.DART) {
					player.getCombat().setFightType(FightType.DART_RAPID);
				} else if(player.getWeapon() == WeaponInterface.JAVELIN) {
					player.getCombat().setFightType(FightType.JAVELIN_RAPID);
				}
				break;
			case 17100:
				if(player.getWeapon() == WeaponInterface.KNIFE) {
					player.getCombat().setFightType(FightType.KNIFE_LONGRANGE);
				} else if(player.getWeapon() == WeaponInterface.THROWNAXE) {
					player.getCombat().setFightType(FightType.THROWNAXE_LONGRANGE);
				} else if(player.getWeapon() == WeaponInterface.DART) {
					player.getCombat().setFightType(FightType.DART_LONGRANGE);
				} else if(player.getWeapon() == WeaponInterface.JAVELIN) {
					player.getCombat().setFightType(FightType.JAVELIN_LONGRANGE);
				}
				break;
			case 6236: // shortbow & longbow & crossbow
				if(player.getWeapon() == WeaponInterface.SHORTBOW) {
					player.getCombat().setFightType(FightType.SHORTBOW_ACCURATE);
				} else if(player.getWeapon() == WeaponInterface.LONGBOW) {
					player.getCombat().setFightType(FightType.LONGBOW_ACCURATE);
				} else if(player.getWeapon() == WeaponInterface.CROSSBOW) {
					player.getCombat().setFightType(FightType.CROSSBOW_ACCURATE);
				} else if(player.getWeapon() == WeaponInterface.COMPOSITE_BOW) {
					player.getCombat().setFightType(FightType.LONGBOW_ACCURATE);
				}
				break;
			case 6235:
				if(player.getWeapon() == WeaponInterface.SHORTBOW) {
					player.getCombat().setFightType(FightType.SHORTBOW_RAPID);
				} else if(player.getWeapon() == WeaponInterface.LONGBOW) {
					player.getCombat().setFightType(FightType.LONGBOW_RAPID);
				} else if(player.getWeapon() == WeaponInterface.CROSSBOW) {
					player.getCombat().setFightType(FightType.CROSSBOW_RAPID);
				} else if(player.getWeapon() == WeaponInterface.COMPOSITE_BOW) {
					player.getCombat().setFightType(FightType.LONGBOW_RAPID);
				}
				break;
			case 6234:
				if(player.getWeapon() == WeaponInterface.SHORTBOW) {
					player.getCombat().setFightType(FightType.SHORTBOW_LONGRANGE);
				} else if(player.getWeapon() == WeaponInterface.LONGBOW) {
					player.getCombat().setFightType(FightType.LONGBOW_LONGRANGE);
				} else if(player.getWeapon() == WeaponInterface.CROSSBOW) {
					player.getCombat().setFightType(FightType.CROSSBOW_LONGRANGE);
				} else if(player.getWeapon() == WeaponInterface.COMPOSITE_BOW) {
					player.getCombat().setFightType(FightType.LONGBOW_LONGRANGE);
				}
				break;
			case 8234: // dagger & sword
				if(player.getWeapon() == WeaponInterface.DAGGER) {
					player.getCombat().setFightType(FightType.DAGGER_STAB);
				} else if(player.getWeapon() == WeaponInterface.SWORD) {
					player.getCombat().setFightType(FightType.SWORD_STAB);
				}
				break;
			case 8237:
				if(player.getWeapon() == WeaponInterface.DAGGER) {
					player.getCombat().setFightType(FightType.DAGGER_LUNGE);
				} else if(player.getWeapon() == WeaponInterface.SWORD) {
					player.getCombat().setFightType(FightType.SWORD_LUNGE);
				}
				break;
			case 8236:
				if(player.getWeapon() == WeaponInterface.DAGGER) {
					player.getCombat().setFightType(FightType.DAGGER_SLASH);
				} else if(player.getWeapon() == WeaponInterface.SWORD) {
					player.getCombat().setFightType(FightType.SWORD_SLASH);
				}
				break;
			case 8235:
				if(player.getWeapon() == WeaponInterface.DAGGER) {
					player.getCombat().setFightType(FightType.DAGGER_BLOCK);
				} else if(player.getWeapon() == WeaponInterface.SWORD) {
					player.getCombat().setFightType(FightType.SWORD_BLOCK);
				}
				break;
			case 9125: // scimitar & longsword
				if(player.getWeapon() == WeaponInterface.SCIMITAR) {
					player.getCombat().setFightType(FightType.SCIMITAR_CHOP);
				} else if(player.getWeapon() == WeaponInterface.LONGSWORD) {
					player.getCombat().setFightType(FightType.LONGSWORD_CHOP);
				}
				break;
			case 9128:
				if(player.getWeapon() == WeaponInterface.SCIMITAR) {
					player.getCombat().setFightType(FightType.SCIMITAR_SLASH);
				} else if(player.getWeapon() == WeaponInterface.LONGSWORD) {
					player.getCombat().setFightType(FightType.LONGSWORD_SLASH);
				}
				break;
			case 9127:
				if(player.getWeapon() == WeaponInterface.SCIMITAR) {
					player.getCombat().setFightType(FightType.SCIMITAR_LUNGE);
				} else if(player.getWeapon() == WeaponInterface.LONGSWORD) {
					player.getCombat().setFightType(FightType.LONGSWORD_LUNGE);
				}
				break;
			case 9126:
				if(player.getWeapon() == WeaponInterface.SCIMITAR) {
					player.getCombat().setFightType(FightType.SCIMITAR_BLOCK);
				} else if(player.getWeapon() == WeaponInterface.LONGSWORD) {
					player.getCombat().setFightType(FightType.LONGSWORD_BLOCK);
				}
				break;
			//AUTOCASTING
			case 51133:
			case 50139:
				player.setAutocastSpell(CombatSpell.SMOKE_RUSH);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51185:
			case 50187:
				player.setAutocastSpell(CombatSpell.SHADOW_RUSH);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51091:
			case 50101:
				player.setAutocastSpell(CombatSpell.BLOOD_RUSH);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 24018:
			case 50061:
				player.setAutocastSpell(CombatSpell.ICE_RUSH);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51159:
			case 50163:
				player.setAutocastSpell(CombatSpell.SMOKE_BURST);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51211:
			case 50211:
				player.setAutocastSpell(CombatSpell.SHADOW_BURST);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51111:
			case 50119:
				player.setAutocastSpell(CombatSpell.BLOOD_BURST);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51069:
			case 50081:
				player.setAutocastSpell(CombatSpell.ICE_BURST);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51146:
			case 50151:
				player.setAutocastSpell(CombatSpell.SMOKE_BLITZ);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51198:
			case 50199:
				player.setAutocastSpell(CombatSpell.SHADOW_BLITZ);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51102:
			case 50111:
				player.setAutocastSpell(CombatSpell.BLOOD_BLITZ);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51058:
			case 50071:
				player.setAutocastSpell(CombatSpell.ICE_BLITZ);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51172:
			case 50175:
				player.setAutocastSpell(CombatSpell.SMOKE_BARRAGE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51224:
			case 50223:
				player.setAutocastSpell(CombatSpell.SHADOW_BARRAGE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51122:
			case 50129:
				player.setAutocastSpell(CombatSpell.BLOOD_BARRAGE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 51080:
			case 50091:
				player.setAutocastSpell(CombatSpell.ICE_BARRAGE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7038:
			case 4128:
				player.setAutocastSpell(CombatSpell.WIND_STRIKE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7039:
			case 4130:
				player.setAutocastSpell(CombatSpell.WATER_STRIKE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7040:
			case 4132:
				player.setAutocastSpell(CombatSpell.EARTH_STRIKE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7041:
			case 4134:
				player.setAutocastSpell(CombatSpell.FIRE_STRIKE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7042:
			case 4136:
				player.setAutocastSpell(CombatSpell.WIND_BOLT);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7043:
			case 4139:
				player.setAutocastSpell(CombatSpell.WATER_BOLT);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7044:
			case 4142:
				player.setAutocastSpell(CombatSpell.EARTH_BOLT);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7045:
			case 4145:
				player.setAutocastSpell(CombatSpell.FIRE_BOLT);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7046:
			case 4148:
				player.setAutocastSpell(CombatSpell.WIND_BLAST);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7047:
			case 4151:
				player.setAutocastSpell(CombatSpell.WATER_BLAST);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7048:
			case 4153:
				player.setAutocastSpell(CombatSpell.EARTH_BLAST);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7049:
			case 4157:
				player.setAutocastSpell(CombatSpell.FIRE_BLAST);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7050:
			case 4159:
				player.setAutocastSpell(CombatSpell.WIND_WAVE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7051:
			case 4161:
				player.setAutocastSpell(CombatSpell.WATER_WAVE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7052:
			case 4164:
				player.setAutocastSpell(CombatSpell.EARTH_WAVE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 7053:
			case 4165:
				player.setAutocastSpell(CombatSpell.FIRE_WAVE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 4129:
				player.setAutocastSpell(CombatSpell.CONFUSE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 4133:
				player.setAutocastSpell(CombatSpell.WEAKEN);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 4137:
				player.setAutocastSpell(CombatSpell.CURSE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 6036:
				player.setAutocastSpell(CombatSpell.BIND);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 6003:
				player.setAutocastSpell(CombatSpell.IBAN_BLAST);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 47005:
				player.setAutocastSpell(CombatSpell.MAGIC_DART);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 4166:
				player.setAutocastSpell(CombatSpell.SARADOMIN_STRIKE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 4167:
				player.setAutocastSpell(CombatSpell.CLAWS_OF_GUTHIX);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 4168:
				player.setAutocastSpell(CombatSpell.FLAMES_OF_ZAMORAK);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 6006:
				player.setAutocastSpell(CombatSpell.VULNERABILITY);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 6007:
				player.setAutocastSpell(CombatSpell.ENFEEBLE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 6056:
				player.setAutocastSpell(CombatSpell.ENTANGLE);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 6026:
				player.setAutocastSpell(CombatSpell.STUN);
				TabInterface.ATTACK.sendInterface(player, player.getWeapon().getId());
				player.out(new SendConfig(108, 3));
				break;
			case 48147:
			case 48157:
			case 48167:
				player.message("Cannot autocast this.");
				break;
			case 26010:
				player.setAutocastSpell(null);
				player.out(new SendConfig(108, 0));
				break;
			case 94047:
				player.message("@red@No support for training defense with magic yet.");
			case 1093:
			case 1094:
			case 1097:
				if(player.autocasting) {
					player.setAutocastSpell(null);
					player.out(new SendConfig(108, 0));
				} else if(!player.autocasting) {
					Item staff = player.getEquipment().get(Equipment.WEAPON_SLOT);
					if(staff != null && staff.getId() == 4675) {
						if(!player.getSpellbook().equals(Spellbook.ANCIENT)) {
							player.message("You can only autocast ancient magics with this staff.");
							break;
						}
						
						TabInterface.ATTACK.sendInterface(player, 1689);
					} else {
						if(!player.getSpellbook().equals(Spellbook.NORMAL)) {
							player.message("You can only autocast standard magics with this staff.");
							break;
						}
						
						TabInterface.ATTACK.sendInterface(player, 1829);
					}
				}
				break;
			//SPECIALS
			case 29038:
			case 29063:
			case 29113:
			case 29188:
			case 29213:
			case 48023:
			case 7462:
			case 7512:
			case 12311:
			case 7562:
			case 7537:
			case 7788:
			case 7498:
			case 8481:
			case 7662:
			case 7667:
			case 7687:
			case 7587:
			case 7612:
			case 7623:
			case 7473:
			case 12322:
			case 29138:
			case 29163:
			case 29199:
			case 29074:
			case 33033:
			case 29238:
			case 30007:
			case 30108:
			case 48034:
			case 29049:
			case 30043:
			case 29124:
				if(player.getCombatSpecial() == null) {
					break;
				}
				
				if(!MinigameHandler.execute(player, m -> m.canUseSpecialAttacks(player, player.getCombatSpecial()))) {
					break;
				}
				
				if(player.isSpecialActivated()) {
					player.out(new SendConfig(301, 0));
					player.setSpecialActivated(false);
				} else {
					if (player.getSpecialPercentage().intValue() < player.getCombatSpecial().getAmount()) {
						player.message("You do not have enough special energy left!");
						break;
					}

					player.setSpecialActivated(true);
					player.out(new SendConfig(301, 1));
					player.getCombatSpecial().enable(player);
				}
				break;
		}
		player.getActivityManager().execute(ActivityManager.ActivityType.CLICK_BUTTON);
	}
}