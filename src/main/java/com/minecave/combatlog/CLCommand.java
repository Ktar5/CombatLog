package com.minecave.combatlog;

import com.minecave.combatlog.config.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CLCommand implements CommandExecutor {

	private CombatLog plugin;

	public CLCommand(CombatLog plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You cannot run this command from the Console!");
				return true;
			}
			if(!plugin.getTagHandler().isTagged((Player) sender)) sender.sendMessage(Settings.Messages.NOT_TAGGED.asString());
			else sender.sendMessage(Settings.Messages.TAGGED_TIME.asString().replace("%time%", String.valueOf(plugin.getTagHandler().getRemainingTime((Player) sender))));

		} else if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			if(!sender.isOp() && !sender.hasPermission("combatlog.admin")) sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			else {
				plugin.getConfigHandler().reloadConfig("config");
				sender.sendMessage(ChatColor.GREEN + "CombatLog Configuration reloaded!");
			}
		}
		return true;
	}
}
