package com.minecave.combatlog.npc;

import com.minecave.combatlog.CombatLog;
import com.minecave.combatlog.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class NPCHandler {

	private CombatLog plugin;

	private Map<NPC, BukkitTask> npcTasks = new HashMap<NPC, BukkitTask>();

	public NPCHandler (CombatLog plugin) {
		this.plugin = plugin;
	}

	public NPC fromPlayer(Player player) {
		for(NPC npc : npcTasks.keySet())
			if(npc.getCombatLogger().equals(player.getUniqueId())) return npc;
		return null;
	}

	public NPC fromZombie(Zombie zombie) {
		for(NPC npc : npcTasks.keySet())
			if(npc.getZombie().getUniqueId().equals(zombie.getUniqueId())) return npc;
		return null;
	}

	public void spawnNPC(Player player) {
		runTask(new NPC(player));
	}

	public void despawnNPC(NPC npc) {
		if(npc.getZombie() != null && !npc.getZombie().isDead())
			npc.getZombie().remove();
		if(npcTasks.containsKey(npc)) {
			npcTasks.get(npc).cancel();
			npcTasks.remove(npc);
		}
	}

	public void runTask(final NPC npc) {
		if(npcTasks.containsKey(npc)) npcTasks.get(npc).cancel();
		npcTasks.put(npc, Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				despawnNPC(npc);
			}
		}, Settings.NPC_DESPAWN_DELAY.asInt() * 20));
	}

	public static Zombie zombieNPC(Player player) {
		Zombie npc = player.getWorld().spawn(player.getLocation(), Zombie.class);
		npc.setBaby(false);
		npc.setVillager(false);
		npc.setCustomName(player.getName());
		npc.setCustomNameVisible(true);
		npc.setRemoveWhenFarAway(true);
		npc.getEquipment().setArmorContents(player.getInventory().getArmorContents());
		npc.getEquipment().setHelmet(playerSkull(player));
		npc.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
		if(npc.isInsideVehicle()) npc.getVehicle().eject(); //If for some reason the Zombie spawns on an Entity
		return npc;
	}

	private static ItemStack playerSkull(Player player) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(player.getName());
		skull.setItemMeta(meta);
		return skull;
	}
}
