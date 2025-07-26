package org.rexi.xrayDetector.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.rexi.xrayDetector.XrayDetector;
import org.rexi.xrayDetector.managers.XRayManager;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class XrayDetectorListener implements Listener {

    private final XRayManager XRayManager;
    private final Map<UUID, Integer> ancientDebrisCount = new HashMap<>();
    private final Map<UUID, Integer> goldCount = new HashMap<>();
    private final Map<UUID, Integer> diamondCount = new HashMap<>();
    private final Map<UUID, Long> lastMineTime = new HashMap<>();

    XrayDetector plugin = XrayDetector.getInstance();

    public XrayDetectorListener(XRayManager XRayManager) {
        this.XRayManager = XRayManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("xraydetector.bypass")) {
            return;
        }

        UUID playerId = player.getUniqueId();
        Material blockType = event.getBlock().getType();
        long currentTime = System.currentTimeMillis();

        lastMineTime.putIfAbsent(playerId, currentTime);

        int resettime = plugin.getConfig().getInt("alert.cooldown", 60);

        int resettimeformatted = resettime*1000;

        if (currentTime - lastMineTime.get(playerId) > resettimeformatted) {
            ancientDebrisCount.put(playerId, 0);
            goldCount.put(playerId, 0);
            diamondCount.put(playerId, 0);
            lastMineTime.put(playerId, currentTime);
        }

        switch (blockType) {
            case ANCIENT_DEBRIS:
                ancientDebrisCount.put(playerId, ancientDebrisCount.getOrDefault(playerId, 0) + 1);
                int count_ancient = plugin.getConfig().getInt("alert.blocks.ANCIENT_DEBRIS.count", 3);
                if (ancientDebrisCount.get(playerId) >= count_ancient) {
                    String blockname = plugin.getConfig().getString("alert.blocks.ANCIENT_DEBRIS.name");
                    ancientDebrisCount.put(playerId, 0);
                    if (plugin.getConfig().getBoolean("alert.blocks.ANCIENT_DEBRIS.enabled") &&
                            plugin.getConfig().getBoolean("alert.enabled")) {
                        notifyStaff(player, blockname, count_ancient);
                    }
                    if (plugin.getConfig().getBoolean("discord_hook.ANCIENT_DEBRIS.enabled") &&
                            plugin.getConfig().getBoolean("discord_hook.enabled")) {
                        XRayManager.sendAncientAlert(player.getName(), count_ancient, resettime);
                    }
                }
                break;
            case GOLD_ORE:
                goldCount.put(playerId, goldCount.getOrDefault(playerId, 0) + 1);
                int count_gold = plugin.getConfig().getInt("alert.blocks.GOLD_ORE.count", 3);
                if (goldCount.get(playerId) >= count_gold) {
                    String blockname = plugin.getConfig().getString("alert.blocks.GOLD_ORE.name");
                    goldCount.put(playerId, 0);
                    if (plugin.getConfig().getBoolean("alert.blocks.GOLD_ORE.enabled") &&
                            plugin.getConfig().getBoolean("alert.enabled")) {
                        notifyStaff(player, blockname, count_gold);
                    }
                    if (plugin.getConfig().getBoolean("discord_hook.GOLD_ORE.enabled") &&
                            plugin.getConfig().getBoolean("discord_hook.enabled")) {
                        XRayManager.sendGoldAlert(player.getName(), count_gold, resettime);
                    }
                }
                break;
            case DIAMOND_ORE:
                diamondCount.put(playerId, diamondCount.getOrDefault(playerId, 0) + 1);
                int count_diamond = plugin.getConfig().getInt("alert.blocks.DIAMOND_ORE.count", 3);
                if (diamondCount.get(playerId) >= count_diamond) {
                    String blockname = plugin.getConfig().getString("alert.blocks.DIAMOND_ORE.name");
                    diamondCount.put(playerId, 0);
                    if (plugin.getConfig().getBoolean("alert.blocks.DIAMOND_ORE.enabled") &&
                            plugin.getConfig().getBoolean("alert.enabled")) {
                        notifyStaff(player, blockname, count_diamond);
                    }
                    if (plugin.getConfig().getBoolean("discord_hook.DIAMOND_ORE.enabled") &&
                            plugin.getConfig().getBoolean("discord_hook.enabled")) {
                        XRayManager.sendDiamondAlert(player.getName(), count_diamond, resettime);
                    }
                }
                break;
        }
    }

    private void notifyStaff(Player player, String blockType, int count) {
        Component message = plugin.getMessage("alert",
                "%player%", player.getName(),
                "%block%", blockType,
                "%count%", String.valueOf(count));
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("xraydetector.staff")) {
                onlinePlayer.sendMessage(message);
            }
        }
    }
}