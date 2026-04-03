package Aximox.murder.gui;

import Aximox.murder.MRoles;
import Aximox.murder.Murder;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class CompoGUI extends FastInv {

    public CompoGUI(Player p) {
        super(27, "§8Composition des rôles");

        List<MRoles> extras = Arrays.asList(
                MRoles.SIRENE, MRoles.MECANO,
                MRoles.TRESOR, MRoles.CLANDESTIN, MRoles.FRONTIERE,
                MRoles.CANONNIER, MRoles.CUISINER, MRoles.MATELOT, MRoles.FANTOME
        );

        int slot = 10;
        for (MRoles role : extras) {
            boolean active = Murder.getInstance().getManager().getActiveRoles().contains(role);

            ItemStack item = new ItemStack(active ? Material.LIME_DYE : Material.GRAY_DYE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(role.getName());
                meta.setLore(Arrays.asList(
                        role.getDescription(),
                        "",
                        active ? "§a✔ Activé — clic pour désactiver" : "§c✘ Désactivé — clic pour activer"
                ));
                item.setItemMeta(meta);
            }
            setItem(slot++, item);
        }
    }
}
