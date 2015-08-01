package com.minecave.combatlog;

import com.minecave.combatlog.config.ConfigHandler;
import com.minecave.combatlog.config.Settings;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class TagHandler {

    private CombatLog plugin;

    private YamlConfiguration c = ConfigHandler.getConfig("loggers");
    private List<UUID> combatLoggers = new ArrayList<UUID>();
    private Map<UUID, Long> taggedPlayers = new HashMap<UUID, Long>();

    public TagHandler (CombatLog plugin) {
        this.plugin = plugin;
    }

    public void loadCombatLoggers() {
        for(String uuid : c.getStringList("combatLoggers"))
            combatLoggers.add(UUID.fromString(uuid));
        plugin.getLogger().info("Loaded " + combatLoggers.size() + " CombatLoggers!");
    }

    public void saveCombatLoggers() {
        List<String> output = new ArrayList<String>();
        for(UUID uuid : combatLoggers)
            output.add(uuid.toString());
        c.set("combatLoggers", output);
        plugin.getLogger().info("Saved " + combatLoggers.size() + " CombatLoggers!");
    }

    public boolean isCombatLogger(Player player) {
        return combatLoggers.contains(player.getUniqueId());
    }

    public void setCombatLogger(UUID player) {
        combatLoggers.add(player);
    }

    public void removeCombatLogger(UUID player) {
        combatLoggers.remove(player);
    }

    public boolean isTagEnabled(World world) {
        return Settings.TAG_EXPIRE_DELAY.asInt() != -1 && !Settings.DISABLED_WORLDS.asStringList().contains(world.getName());
    }

    public boolean isTagged(Player player) {
        return taggedPlayers.containsKey(player.getUniqueId()) && (System.currentTimeMillis() - taggedPlayers.get(player.getUniqueId()) < Settings.TAG_EXPIRE_DELAY.asInt() * 1000);
    }

    public void setTagged(Player player, String message) {
        if(player.hasPermission("combatlog.ignore")) return;
        taggedPlayers.put(player.getUniqueId(), System.currentTimeMillis());
        if(message != null) player.sendMessage(message);
    }

    public int getRemainingTime(Player player) {
        return (int) (Settings.TAG_EXPIRE_DELAY.asInt() - (System.currentTimeMillis() - taggedPlayers.get(player.getUniqueId())) / 1000);
    }
}
