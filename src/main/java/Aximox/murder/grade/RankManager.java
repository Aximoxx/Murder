package Aximox.murder.grade;

import Aximox.murder.Murder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RankManager {

    private final Map<UUID, MGrades> playerRanks = new HashMap<>();

    /**
     * Récupère le rang d'un joueur
     */
    public MGrades getRank(UUID uuid) {
        return playerRanks.getOrDefault(uuid, MGrades.INVITEES);
    }

    public void loadRanks() {
        File file = new File(Murder.getInstance().getDataFolder(), "ranks.yml");
        if (!file.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            MGrades rank = MGrades.valueOf(config.getString(key));
            playerRanks.put(uuid, rank);
        }
    }

    public void setRank(UUID uuid, MGrades rank) {
        playerRanks.put(uuid, rank);

        try {
            File file = new File(Murder.getInstance().getDataFolder(), "ranks.yml");
            if (!file.exists()) {
                Murder.getInstance().getDataFolder().mkdirs();
                file.createNewFile();
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set(uuid.toString(), rank.toString());
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAllRanks() {
        try {
            File file = new File(Murder.getInstance().getDataFolder(), "ranks.yml");
            if (!file.exists()) {
                Murder.getInstance().getDataFolder().mkdirs();
                file.createNewFile();
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            for (Map.Entry<UUID, MGrades> entry : playerRanks.entrySet()) {
                config.set(entry.getKey().toString(), entry.getValue().toString());
            }

            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprime le rang d'un joueur (retour au défaut)
     */
    public void removeRank(UUID uuid) {
        playerRanks.remove(uuid);
    }

    /**
     * Vérifie si un joueur a un rang minimum requis
     */
    public boolean hasMinimumPower(UUID uuid, int minPower) {
        return getRank(uuid).getPower() >= minPower;
    }
}