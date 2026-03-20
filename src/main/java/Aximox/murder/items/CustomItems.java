package Aximox.murder.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CustomItems {

    /**
     * LES ITEMS QUI SONT DESTINEE AU QUÊTES
     */

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

    /**
     * LES ITEMS QUI SONT DESTINEE AUX LES RÔLES
     */

    /**
     * Item pour le rôle P.A.F (Permet de mettre en prison un joueur)
     */
    public ItemStack prison(){
        ItemStack prison = new ItemStack(Material.IRON_BARS);
        ItemMeta prisonMeta = prison.getItemMeta();
        if (prisonMeta != null) {
            prisonMeta.setDisplayName("§fPrison");
            prisonMeta.setEnchantmentGlintOverride(true);
            prisonMeta.setLore(List.of("§7ᴄᴇᴛ ɪᴛᴇᴍ ᴛᴇ ᴘᴇʀᴍᴇᴛ ᴅᴇ ᴘʀɪᴠᴇʀ ʟᴀ ʟɪʙᴇʀᴛᴇ́ ᴅ'ᴀᴜᴛʀᴜɪ", "§7ᴜsᴀɢᴇ §8| §fᴄʟɪǫᴜᴇ ᴅʀᴏɪᴛ"));
            prison.setItemMeta(prisonMeta);
        }
        return prison;
    }

    /**
     * Item pour le rôle Sirène (Permet de mettre slowness au joueur proche)
     */
    public ItemStack sirene(){
        ItemStack voice = new ItemStack(Material.SUNFLOWER);
        ItemMeta vmeta = voice.getItemMeta();
        if (vmeta != null) {
            vmeta.setDisplayName("§dChant de la Sirène");
            vmeta.setEnchantmentGlintOverride(true);
            vmeta.setLore(List.of("§7ᴍᴇᴛ sʟᴏᴡɴᴇss ᴀ̀ ᴛᴏᴜᴛ ʟᴇs ᴊᴏᴜᴇᴜʀs ᴅᴀɴs ᴜɴ ʀᴀʏᴏɴ ᴅᴇ 5 ʙʟᴏᴄᴋs ᴘᴇɴᴅᴀɴᴛ 10 sᴇᴄᴏɴᴅᴇs", "§7ᴜsᴀɢᴇ §8| §fᴄʟɪǫᴜᴇ ᴅʀᴏɪᴛ"));
            vmeta.setCustomModelData(3);
            voice.setItemMeta(vmeta);
        }
        return voice;
    }

    /**
     * Item pour le rôle Clandestin (Permet de se mettre invisible)
     */
    public ItemStack furtivite(){
        ItemStack invis = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta invisMeta = invis.getItemMeta();
        if (invisMeta != null) {
            invisMeta.setDisplayName("§fɪɴᴠɪsɪʙɪʟɪᴛᴇ́");
            invisMeta.setLore(List.of("§7ᴜɴ ᴄʟɪǫᴜᴇ ᴅʀᴏɪᴛ, ᴇᴛ §ғᴘᴏᴜғ"));
            invisMeta.setEnchantmentGlintOverride(true);
            invis.setItemMeta(invisMeta);
        }
        return invis;
    }

    /**
     * Item pour le rôle Pirate Fou (Permet de kill un joueur)
     */
    public ItemStack sabre(){
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        if (swordMeta != null) {
            swordMeta.setUnbreakable(true);
            swordMeta.setCustomModelData(19);
            swordMeta.setEnchantmentGlintOverride(true);
            swordMeta.setDisplayName("§a§l⚔ sᴀʙʀᴇ ᴅᴜ ᴘɪʀᴀᴛᴇ");
            sword.setItemMeta(swordMeta);
        }
        return sword;
    }

    /**
     * Item pour le rôle Capitaine (Le couteau pour kill les joueurs)
     */
    public ItemStack dagger(){
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        if (swordMeta != null) {
            swordMeta.setDisplayName("§c§l🔪 ᴘᴏɪɢɴᴀʀᴅ ᴅᴜ ᴄᴀᴘɪᴛᴀɪɴᴇ");
            swordMeta.setUnbreakable(true);
            swordMeta.setCustomModelData(18);
            sword.setItemMeta(swordMeta);
        }
        return sword;
    }
}