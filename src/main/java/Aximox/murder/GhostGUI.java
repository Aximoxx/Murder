package Aximox.murder;

import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GhostGUI extends FastInv {
    private List<UUID> alive = new ArrayList<>();

    public GhostGUI(Player p) {
        super(36, "§8Joueur à Hanter");

        for (Player pls : Bukkit.getOnlinePlayers()){
            if (Murder.getInstance().getManager().getInnocent().contains(pls.getUniqueId()) || Murder.getInstance().getManager().getDetective().contains(pls.getUniqueId())) {
                alive.add(pls.getUniqueId());
                alive.remove(p.getUniqueId());

                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                if (meta != null) {
                    meta.setOwner(pls.getName());
                    meta.setDisplayName(pls.getName());
                    item.setItemMeta(meta);
                }

                for (int i = 0; i < alive.size() ; i++){
                    setItem(i, item);
                }
            }
        }
    }
}
