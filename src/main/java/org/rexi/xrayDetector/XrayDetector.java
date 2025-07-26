package org.rexi.xrayDetector;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.rexi.xrayDetector.commands.ReloadCommand;
import org.rexi.xrayDetector.listeners.XrayDetectorListener;
import org.rexi.xrayDetector.managers.XRayManager;

public final class XrayDetector extends JavaPlugin {

    private static XrayDetector instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getCommand("xraydetector").setExecutor(new ReloadCommand());
        getServer().getPluginManager().registerEvents(new XrayDetectorListener(new XRayManager()), this);
        Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&aXrayDetector has been enabled!"));
        Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&bThank you for using Rexi666 plugins!"));
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cXrayDetector has been disabled!"));
        Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&bThank you for using Rexi666 plugins!"));
    }

    public static XrayDetector getInstance() {
        return instance;
    }

    public Component getMessage(String key) {
        String location = getConfig().getString("messages." + key, "&cMessage not found: " + key);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(location);
    }

    public Component getMessage(String key, String... replacements) {
        String location = getConfig().getString("messages." + key, "&cMessage not found: " + key);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            location = location.replace(replacements[i], replacements[i + 1]);
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(location);
    }
}
