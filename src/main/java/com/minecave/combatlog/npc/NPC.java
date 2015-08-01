package com.minecave.combatlog.npc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class NPC {

    private UUID combatLogger;
    private Zombie npcEntity;
    private ItemStack[] invContents;
    private ItemStack[] armorContents;
    private int experience;

    public NPC(Player player) {
        this.combatLogger = player.getUniqueId();
        this.npcEntity = NPCHandler.zombieNPC(player);
        this.invContents = player.getInventory().getContents();
        this.armorContents = player.getInventory().getArmorContents();
        this.experience = player.getTotalExperience();
    }

    public UUID getCombatLogger() {
        return combatLogger;
    }

    public Zombie getZombie() {
        return npcEntity;
    }

    public ItemStack[] getInvContents() {
        return invContents;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

    public int getExperience() {
        return experience;
    }

    public void dropInventory(Location loc) {
        for(ItemStack i : getInvContents())
            if(i != null && i.getType() != Material.AIR) loc.getWorld().dropItem(loc, i);
        for(ItemStack a : getArmorContents())
            if(a != null && a.getType() != Material.AIR) loc.getWorld().dropItem(loc, a);
    }
}
