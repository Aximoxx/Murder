package Aximox.murder.grade;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RankManager {

    private final Map<UUID, MGrades> playerRanks = new HashMap<>();

    /**
     * Récupère le rang d'un joueur
     */
    public MGrades getRank(UUID uuid) {
        return playerRanks.getOrDefault(uuid, MGrades.INVITEES); // Par défaut : Invité
    }

    /**
     * Définit le rang d'un joueur
     */
    public void setRank(UUID uuid, MGrades rank) {
        playerRanks.put(uuid, rank);
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