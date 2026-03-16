package Aximox.murder.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActionBar {
    private static Map<UUID, BukkitTask> tasks = new HashMap();

    public static void send(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public static void start(Plugin plugin, final Player player, final String message) {
        stop(player);
        BukkitTask task = (new BukkitRunnable() {
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                } else {
                   ActionBar.send(player, message);
                }
            }
        }).runTaskTimer(plugin, 0L, 20L);
        tasks.put(player.getUniqueId(), task);
    }

    public static void stop(Player player) {
        BukkitTask task = tasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }

    }
}

