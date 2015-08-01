package com.minecave.combatlog;

import com.minecave.combatlog.config.Settings;
import com.minecave.combatlog.npc.NPC;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class PlayerListener implements Listener {

    private CombatLog plugin;

    public PlayerListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if(e.isCancelled() || (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getCause() != EntityDamageEvent.DamageCause.PROJECTILE)) return;
        //If the Cause is Player vs Player
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            if(!plugin.getTagHandler().isTagEnabled(e.getEntity().getWorld())) return;
            Player p = (Player) e.getEntity();
            Player d = (Player) e.getDamager();
            plugin.getTagHandler().setTagged(p, plugin.getTagHandler().isTagged(p) ? null : Settings.Messages.DAMAGED_TAGGED.asFString(d));
            plugin.getTagHandler().setTagged(d, plugin.getTagHandler().isTagged(d) ? null : Settings.Messages.DAMAGER_TAGGED.asFString(p));
        //Else if the Cause if Player vs Zombie
        } else if(e.getEntity() instanceof Zombie && e.getDamager() instanceof Player) {
            NPC npc = plugin.getNPCHandler().fromZombie((Zombie) e.getEntity());
            if(npc != null) plugin.getNPCHandler().runTask(npc);
        //Else if the Cause is Player shooting Player
        } else if(e.getEntity() instanceof Player && e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
            if(e.getDamager() instanceof EnderPearl || !plugin.getTagHandler().isTagEnabled(e.getEntity().getWorld())) return;
            Player p = (Player) e.getEntity();
            Player d = (Player) ((Projectile) e.getDamager()).getShooter();
            plugin.getTagHandler().setTagged(p, plugin.getTagHandler().isTagged(p) ? null : Settings.Messages.DAMAGED_TAGGED.asFString(d));
            plugin.getTagHandler().setTagged(d, plugin.getTagHandler().isTagged(d) ? null : Settings.Messages.DAMAGER_TAGGED.asFString(p));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent e) {
        if(!(e.getEntity() instanceof Zombie)) return;
        NPC npc = plugin.getNPCHandler().fromZombie((Zombie) e.getEntity());
        if(npc != null) {
            e.getDrops().clear();
            e.setDroppedExp(Settings.DROP_EXPERIENCE.asBoolean() ? npc.getExperience() : 0);
            npc.dropInventory(e.getEntity().getLocation());
            plugin.getTagHandler().setCombatLogger(npc.getCombatLogger());
            plugin.getNPCHandler().despawnNPC(npc);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        //If the Player has an NPC and it's still spawned
        NPC npc = plugin.getNPCHandler().fromPlayer(e.getPlayer());
        if(npc != null) {
            e.getPlayer().teleport(npc.getZombie().getLocation());
            plugin.getNPCHandler().despawnNPC(npc);

        //Else if the Player has an NPC and it was killed
        } else if(plugin.getTagHandler().isCombatLogger(e.getPlayer())) {
            e.getPlayer().getInventory().clear();
            e.getPlayer().getInventory().setArmorContents(null);
            e.getPlayer().setTotalExperience(0);
            e.getPlayer().setHealth(0);
            plugin.getTagHandler().removeCombatLogger(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(plugin.getTagHandler().isTagged(e.getPlayer()))
            plugin.getNPCHandler().spawnNPC(e.getPlayer());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent e) {
        if(!Settings.BLOCK_TELEPORT.asBoolean() || e.getCause() != PlayerTeleportEvent.TeleportCause.COMMAND) return;
        if(plugin.getTagHandler().isTagged(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Settings.Messages.TELEPORT_BLOCKED.asString());
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if(!plugin.getTagHandler().isTagged(e.getPlayer())) return;
        if(Settings.BLOCKED_COMMANDS.asStringList().contains(e.getMessage().split(" ")[0])) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Settings.Messages.COMMAND_BLOCKED.asString());
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent e) {
        for(Entity ent : e.getChunk().getEntities())
            if(ent instanceof Zombie && plugin.getNPCHandler().fromZombie((Zombie) ent) != null) ent.remove();
    }
}
