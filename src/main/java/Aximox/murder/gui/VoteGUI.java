package Aximox.murder.gui;

import Aximox.murder.Murder;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class VoteGUI extends FastInv {

    public VoteGUI(Player p) {
        super(36, "§cᴘᴏᴜʀ ǫᴜɪ ᴠᴏᴛᴇʀᴀs-ᴛᴜ ?");

        List<Player> alive = new ArrayList<>();

        for (Player pls : Bukkit.getOnlinePlayers()) {
            if (!Murder.getInstance().getManager().getPls().contains(pls.getUniqueId())) continue;
            if (Murder.getInstance().getManager().getDeath().contains(pls.getUniqueId())) continue;
            if (pls.getUniqueId().equals(p.getUniqueId())) continue;

            alive.add(pls);
        }

        for (int i = 0; i < alive.size(); i++) {
            Player pls = alive.get(i);

            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer((OfflinePlayer) pls.getPlayerProfile());
                meta.setDisplayName("§e" + pls.getName());
                item.setItemMeta(meta);
            }

            setItem(i, item);
        }
    }
}