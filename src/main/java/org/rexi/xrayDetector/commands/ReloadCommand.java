package org.rexi.xrayDetector.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.rexi.xrayDetector.XrayDetector;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("xraydetector.reload")) {
            sender.sendMessage(XrayDetector.getInstance().getMessage("no_permission"));
            return false;
        }

        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(XrayDetector.getInstance().getMessage("usage"));
            return false;
        }

        XrayDetector.getInstance().reloadConfig();

        sender.sendMessage(XrayDetector.getInstance().getMessage("config_reloaded"));
        return true;
    }
}
