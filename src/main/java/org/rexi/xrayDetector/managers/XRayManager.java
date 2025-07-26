package org.rexi.xrayDetector.managers;

import org.rexi.xrayDetector.XrayDetector;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class XRayManager {

    XrayDetector plugin = XrayDetector.getInstance();

    int[] colorRGB = new int[]{240, 43, 20};

    public void sendAncientAlert(String playerName, int count) {
        String webhookUrl = plugin.getConfig().getString("discord_hook.ANCIENT_DEBRIS.url", null);
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            System.err.println(plugin.getMessage("webhook_error"));
            return;
        }
        String avatarUrl = plugin.getConfig().getString("discord_hook.ANCIENT_DEBRIS.avatar", "https://www.spigotmc.org/data/resource_icons/123/123517.jpg?1742847968");
        String username = plugin.getConfig().getString("discord_hook.ANCIENT_DEBRIS.username", "Xray Detector");
        String title = plugin.getConfig().getString("discord_hook.ANCIENT_DEBRIS.title", "Xray Detector Alert");

        setColorRGB("discord_hook.ANCIENT_DEBRIS.color_rgb", "240,43,20");
        String thumbnailUrl = getPlayerAvatar(playerName);

        String raw = plugin.getConfig().getString("discord_hook.message");
        String blockName = plugin.getConfig().getString("alert.blocks.ANCIENT_DEBRIS.name", "Ancient Debris");
        String msg = raw
                .replace("%player%", playerName)
                .replace("%count%", String.valueOf(count))
                .replace("%block%", blockName);
        send(msg, webhookUrl, avatarUrl, username, colorRGB, thumbnailUrl, title);
    }

    public void sendDiamondAlert(String playerName, int count) {
        String webhookUrl = plugin.getConfig().getString("discord_hook.DIAMOND_ORE.url", null);
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            System.err.println(plugin.getMessage("webhook_error"));
            return;
        }
        String avatarUrl = plugin.getConfig().getString("discord_hook.DIAMOND_ORE.avatar", "https://www.spigotmc.org/data/resource_icons/123/123517.jpg?1742847968");
        String username = plugin.getConfig().getString("discord_hook.DIAMOND_ORE.username", "Xray Detector");
        String title = plugin.getConfig().getString("discord_hook.DIAMOND_ORE.title", "Xray Detector Alert");

        setColorRGB("discord_hook.DIAMOND_ORE.color_rgb", "8,139,168");
        String thumbnailUrl = getPlayerAvatar(playerName);

        String raw = plugin.getConfig().getString("discord_hook.message");
        String blockName = plugin.getConfig().getString("alert.blocks.DIAMOND_ORE.name", "Diamond Ore");
        String msg = raw
                .replace("%player%", playerName)
                .replace("%count%", String.valueOf(count))
                .replace("%block%", blockName);
        send(msg, webhookUrl, avatarUrl, username, colorRGB, thumbnailUrl, title);
    }

    public void sendGoldAlert(String playerName, int count) {
        String webhookUrl = plugin.getConfig().getString("discord_hook.GOLD_ORE.url", null);
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            System.err.println(plugin.getMessage("webhook_error"));
            return;
        }
        String avatarUrl = plugin.getConfig().getString("discord_hook.GOLD_ORE.avatar", "https://www.spigotmc.org/data/resource_icons/123/123517.jpg?1742847968");
        String username = plugin.getConfig().getString("discord_hook.GOLD_ORE.username", "Xray Detector");
        String title = plugin.getConfig().getString("discord_hook.GOLD_ORE.title", "Xray Detector Alert");

        setColorRGB("discord_hook.GOLD_ORE.color_rgb", "194,189,4");
        String thumbnailUrl = getPlayerAvatar(playerName);

        String raw = plugin.getConfig().getString("discord_hook.message");
        String blockName = plugin.getConfig().getString("alert.blocks.GOLD_ORE.name", "Gold Ore");
        String msg = raw
                .replace("%player%", playerName)
                .replace("%count%", String.valueOf(count))
                .replace("%block%", blockName);
        send(msg, webhookUrl, avatarUrl, username, colorRGB, thumbnailUrl, title);
    }

    public void send(String content, String webhookUrl, String avatarUrl, String username, int[] colorRGB, String thumbnailUrl, String title) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            int color = (colorRGB[0] << 16) + (colorRGB[1] << 8) + colorRGB[2];

            String payload = """
            {
              "username": "%s",
              "avatar_url": "%s",
              "embeds": [{
                "title": "%s",
                "description": "%s",
                "color": %d,
                "thumbnail": {
                  "url": "%s"
                }
              }]
            }
            """.formatted(
                    escape(username),
                    escape(avatarUrl),
                    escape(title),
                    escape(content),
                    color,
                    escape(thumbnailUrl)
            );

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            connection.getResponseCode(); // fuerza la ejecución
        } catch (Exception e) {
            System.err.println(plugin.getMessage("webhook_error"));
            e.printStackTrace();
        }
    }

    private String escape(String s) {
        return s == null ? "" : s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("\t", "\\t");
    }

    public static String getUuidFromName(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return null;
            }

            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                String responseBody = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";

                int idKeyIndex = responseBody.indexOf("\"id\"");
                if (idKeyIndex == -1) return null;

                int colonIndex = responseBody.indexOf(":", idKeyIndex);
                if (colonIndex == -1) return null;

                int quoteStart = responseBody.indexOf("\"", colonIndex);
                if (quoteStart == -1) return null;

                int quoteEnd = responseBody.indexOf("\"", quoteStart + 1);
                if (quoteEnd == -1) return null;

                return responseBody.substring(quoteStart + 1, quoteEnd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPlayerAvatar(String playerName) {
        String uuid = getUuidFromName(playerName);
        String avatar = (uuid != null)
                ? "https://minotar.net/helm/" + uuid + "/64.png"
                : "https://i.pinimg.com/564x/54/f4/b5/54f4b55a59ff9ddf2a2655c7f35e4356.jpg";
        return avatar;
    }

    public void setColorRGB(String path, String fallback) {
        String color = plugin.getConfig().getString(path, null);
        if (color == null || color.isEmpty()) {
            color = fallback;
        }

        try {
            String[] parts = color.split(",");
            if (parts.length == 3) {
                colorRGB = new int[]{
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim()),
                        Integer.parseInt(parts[2].trim())
                };
            }
        } catch (Exception ignored) {}
    }

}
