package com.minecave.combatlog.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public enum Settings {

    TAG_EXPIRE_DELAY,
    NPC_DESPAWN_DELAY,
    DISABLED_WORLDS,
    BLOCKED_COMMANDS,
    BLOCK_TELEPORT,
    DROP_EXPERIENCE;

    private static YamlConfiguration c = ConfigHandler.getConfig("config");

    public boolean asBoolean() {
        return c.getBoolean(name());
    }

    public int asInt() {
        return c.getInt(name());
    }

    public List<String> asStringList() {
        return c.getStringList(name());
    }

    public enum Messages {

        COMMAND_BLOCKED,
        TELEPORT_BLOCKED,
        DAMAGED_TAGGED,
        DAMAGER_TAGGED,
        NOT_TAGGED,
        TAGGED_TIME;

        public String asString() {
            return ChatColor.translateAlternateColorCodes('&', c.getString("messages." + name()));
        }

        public String asFString(Player player) {
            return ChatColor.translateAlternateColorCodes('&', c.getString("messages." + name()).replace("%player%", player.getName()).replace("%time%", String.valueOf(TAG_EXPIRE_DELAY.asInt())));
        }
    }
}
