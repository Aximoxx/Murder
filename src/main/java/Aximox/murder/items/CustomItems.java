package Aximox.murder.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CustomItems {

    public ItemStack lever (){
        ItemStack lever = new ItemStack(Material.LEVER);
        ItemMeta meta = lever.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§fGuidon");
            meta.setLore(List.of("§7Peut seulement être poser sur un block de Fer"));
            lever.setItemMeta(meta);
        }
        return lever;
    }
}
