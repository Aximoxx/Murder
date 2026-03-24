package Aximox.murder;

import Aximox.murder.grade.MGrades;
import Aximox.murder.gui.GhostGUI;
import Aximox.murder.gui.VoteGUI;
import Aximox.murder.utils.ActionBar;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MListener implements Listener {
    private boolean cell = true;
    private boolean kill = true;
    private boolean charm = true;
    private boolean invis = true;
    private final MManager manager;
    private List<UUID> cooldown = new ArrayList<>();

    public MListener(MManager manager){
        this.manager = manager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        e.setJoinMessage(null);

        if (!p.isOp()) Murder.getInstance().getRankManager().setRank(p.getUniqueId(), MGrades.INVITEES);
        p.teleport(new Location(p.getWorld(), -47, 58, -278, 0f, 0f));

        manager.updateTabList(p);
        if (manager.getPls().contains(p.getUniqueId())) return;
        manager.onJoin(p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        e.setQuitMessage(null);

        if (!manager.getPls().contains(p.getUniqueId())) return;
        if (manager.getMurderP().contains(p.getUniqueId())){
            manager.onEnd();
        }

        manager.onQuit(p);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPveDamage(EntityDamageEvent e){
        e.setCancelled(true);
    }

    /**
     *La méthode qui s'occupe des rôles et de leurs intéractions avec leurs pouvoirs
     */
    @EventHandler
    public void onActivate(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!manager.getPls().contains(p.getUniqueId()) || !manager.isStarted()) return;
        if (e.getHand() != EquipmentSlot.HAND) return;


        Block block = e.getClickedBlock();
        if (block != null) {
            if (manager.isReunion()) return;

            switch (block.getType()) {

                case WARPED_SLAB:
                    if (manager.getRole(p) != MRoles.TRESOR) {
                        p.sendMessage("§cɴᴏɴ ! sᴇᴜʟ ʟᴇs ᴠɪʟᴀɪɴs ᴍᴇ́ᴄʜᴀɴᴛs ᴠᴏʟᴇᴜʀs ᴘᴇᴜᴠᴇɴᴛ ᴛᴏᴜᴄʜᴇʀ sᴇ ᴄᴏғғʀᴇ");
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 1f, 1f);
                        return;
                    }

                    new BukkitRunnable() {
                        private int timer = 5;

                        @Override
                        public void run() {

                            if (p.getLocation().distance(block.getLocation()) > 3) {
                                p.sendMessage("§cᴛᴜ ɴ'ᴇsᴛ ᴘᴀs ʟᴜғғʏ, ᴛᴏɴ ʙʀᴀs ɴᴇ s'ᴀʟʟᴏɴɢᴇ ᴘᴀs ᴇɴ ᴍᴇ̂ᴍᴇ ᴛᴇᴍᴘs ǫᴜᴇ ᴛᴏɪ !");
                                p.sendMessage("§7ʀᴀᴘᴘʀᴏᴄʜᴇ ᴛᴏɪ ᴜɴ ᴘᴇᴛɪᴛ ᴘᴇᴜ ᴘʟᴜs ᴇᴛ ʀᴇ́ᴇssᴀʏᴇ");
                                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 1f, 1f);
                                cancel();
                            }

                            if (timer == 5) {
                                p.sendMessage("§6ᴠᴏᴜs ᴇ̂ᴛᴇs ᴇɴᴛʀᴀɪɴ ᴅᴇ ᴄʀᴏᴄʜᴇᴛᴇʀ ʟᴇ ᴄᴏғғʀᴇ !");
                                p.playSound(p.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_EJECT_ITEM, SoundCategory.BLOCKS, 1f, 1f);
                            }

                            if (timer == 0) {
                                block.setType(Material.AIR);
                                p.sendMessage("§6ᴛᴜ ᴀs ᴘᴀʀғᴀɪᴛᴇᴍᴇɴᴛ ᴄʀᴏᴄʜᴇᴛᴇ́ ʟᴇ ᴄᴏғғʀᴇ !");
                                Bukkit.getWorld("world").spawnParticle(Particle.WITCH, block.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 50);
                                p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.BLOCKS, 1f, 1f);

                                cancel();
                            }

                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, (5 - timer) * 0.2f + 1f);

                            timer--;
                        }
                    }.runTaskTimer(Murder.getInstance(), 0, 20L);

                    break;

                case MOSS_CARPET:
                    if (manager.getRole(p) != MRoles.MATELOT){
                        p.sendMessage("§6Ce n'est pas à toi de la faire, mais au §bMatelot §6!");
                        p.sendMessage("§7Va le prévenir qu'il a loupé un endroit !");
                        return;
                    }

                    if (p.getInventory().getItemInMainHand().getType() != Material.PAPER){
                        p.sendMessage("§cTu as oublié ta serpillère");
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 1f, 1f);
                        return;
                    }

                    block.setType(Material.AIR);

                    int remainingMoss = 0;
                    for (Location moss : Murder.getInstance().getMoss()){
                        if (moss.getBlock().getType() == Material.MOSS_CARPET){
                            remainingMoss ++;
                        }
                    }

                    if (remainingMoss == 0){
                        p.sendMessage("§aBien Joué, tu as éliminé cette CRASSE !");
                        p.sendMessage("§aToutes les saletés ont été nettoyées !");
                        p.sendMessage("§7C'est le capitaine qui va être fier de toi !");

                        if (manager.getHasBuzzed().contains(p.getUniqueId())){
                            manager.getHasBuzzed().remove(p.getUniqueId());

                            p.sendMessage("§eBRAVO ! §aTu peux maintenant ré-utiliser le Buzzer !");
                            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.BLOCKS, 1f, 1f);
                        }
                        else{
                            manager.reward(p);
                        }
                    }
                    else {
                        p.sendMessage("§aBien Joué, tu as éliminé cette CRASSE, plus que §e" + remainingMoss);
                        p.sendMessage("c'est le capitaine qui va être fier de toi !");
                    }

                    remainingMoss = 0;

                    break;

                case GRANITE:
                    if (manager.getRole(p) != MRoles.CANONNIER){
                        p.sendMessage("§cCOMMENT ???? Tu est pas censé y touché");
                        return;
                    }

                    if (!isCustomItem(e.getItem(), Murder.getInstance().getCustomItems().canon())){
                        p.sendMessage("§6Met le canon dedans débile !");
                        return;
                    }

                    if (manager.getHasBuzzed().contains(p.getUniqueId())){
                        manager.getHasBuzzed().remove(p.getUniqueId());

                        p.sendMessage("§eBRAVO ! §aTu peux maintenant ré-utiliser le Buzzer !");
                        p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.BLOCKS, 1f, 1f);
                    }
                    else {
                        p.sendMessage("§aBravo ! Tu as charger le canon !");
                        manager.reward(p);
                    }

                    break;

                case MANGROVE_BUTTON:
                    if (manager.getHasBuzzed().contains(p.getUniqueId())) {
                        p.sendMessage("§aᴠᴏᴜs ᴀᴠᴇᴢ ᴅᴇ́ᴊᴀ̀ ᴜᴛɪʟɪsᴇ́ ʟᴇ ʙᴜᴢᴢᴇʀ !");
                        return;
                    }

                    manager.getHasBuzzed().add(p.getUniqueId());

                    for (UUID id : manager.getPls()){
                        Player pls = Bukkit.getPlayer(id);
                        if (pls != null) {
                            pls.sendMessage("§e" + p.getName() + " §6ᴀ ᴅᴇᴍᴀɴᴅᴇ́(ᴇ) ᴜɴᴇ ʀᴇ́ᴜɴɪᴏɴ ᴅ'ᴜʀɢᴇɴᴄᴇ");
                        }
                    }
                    manager.reuLogic();

                    break;

                case IRON_BLOCK:
                    if (manager.getRole(p) != MRoles.MECANO){
                        p.sendMessage("§cPAS TOUCHE ! Seul le mécano peut y toucher !");
                        return;
                    }

                    if (e.getClickedBlock().getType() != Material.LIME_CONCRETE_POWDER){
                        p.sendMessage("§cPourquoi tu veux essayer réparer quelque chose qui n'est pas cassé ??");
                        return;
                    }

                    if (e.getItem().getType() != Material.PAPER){
                        p.sendMessage("§cTu as oublié ta clef !");
                        return;
                    }

                    e.getClickedBlock().setType(Material.YELLOW_CONCRETE_POWDER);
                    p.sendMessage("§aBravo, tu as réparé une panne !");

                    //Todo: Réfléchir à comment les calculés ! peut être une liste


                default:
                    break;
            }

            if (block.getState() instanceof Sign sign){
                String line1 = sign.getLine(0);
                String line2 = sign.getLine(1);

                if (line1.equalsIgnoreCase("Clique ici pour")){
                    if (line2.equalsIgnoreCase("rejoindre le jeu")){
                        manager.onJoin(p);
                    }
                }
            }
        }



        /**
         * Écoute
         * des
         * Items
         */



        ItemStack item = e.getItem();
        if (item == null) return;

        e.setCancelled(true);

        if (e.getItem().getType().equals(Material.ECHO_SHARD)) {
            if (manager.isStarted()){
                new VoteGUI(p).open(p);
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
            }
        }

        if (manager.isReunion()) return;

        switch (item.getType()){

            case WITHER_ROSE:
                if (item.getType() != Material.WITHER_ROSE) return;
                new GhostGUI(p).open(p);
                break;

            case PAPER:
                switch (manager.getRole(p)) {

                    case SIRENE -> {
                        if (!isCharm()) return;

                        for (Entity entity : p.getWorld().getNearbyEntities(p.getLocation(), 5, 5, 5)) {
                            if (!(entity instanceof Player pls)) continue;
                            if (pls.getUniqueId().equals(p.getUniqueId())) continue;

                            if (!manager.getPls().contains(pls.getUniqueId())) continue;
                            pls.sendMessage("§dʟᴀ sɪʀᴇ̀ɴᴇ ᴠᴏᴜs ᴀ̀ ᴄʜᴀʀᴍᴇ́ᴇ !");
                            pls.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
                        }

                        setCharm(false);

                        new BukkitRunnable() {
                            private int timer = 30;

                            @Override
                            public void run() {
                                ActionBar.send(p, "§cᴄᴏᴏʟᴅᴏᴡɴ: §f" + timer);

                                if (timer == 0 || !manager.isStarted()) {
                                    setCharm(true);
                                    cancel();
                                }
                                timer--;
                            }
                        }.runTaskTimer(Murder.getInstance(), 0, 20L);
                    }

                }

            case AMETHYST_SHARD:
                if (isInvis()) {
                    new BukkitRunnable() {
                        private int timer = 40;

                        @Override
                        public void run() {
                            ActionBar.send(p, "§cᴄᴏᴏʟᴅᴏᴡɴ: §f" + timer);

                            if (timer == 40) {
                                Murder.getInstance().getRankManager().removeRank(p.getUniqueId());
                                p.sendMessage("§7ᴛᴜ ᴇsᴛ ɪɴᴠɪsɪʙʟᴇ ᴘᴏᴜʀ ʟᴇs 10 ᴘʀᴏᴄʜᴀɪɴᴇs sᴇᴄᴏɴᴅᴇs !");
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));

                                for (Player pls : Bukkit.getOnlinePlayers()) {
                                    pls.hidePlayer(Murder.getInstance(), p);
                                }

                                setInvis(false);
                            }

                            if (timer == 30) {

                                for (Player pls : Bukkit.getOnlinePlayers()) {
                                    pls.showPlayer(Murder.getInstance(), p);
                                }

                                p.sendMessage("§eᴛᴜ ᴇsᴛ ᴅᴇ ɴᴏᴜᴠᴇᴀᴜ ᴠɪsɪʙʟᴇ !");
                            }

                            if (timer == 0 || !manager.isStarted()) {
                                setInvis(true);
                                cancel();
                            }
                            timer--;
                        }
                    }.runTaskTimer(Murder.getInstance(), 0, 20L);
                }

                default:
                    break;
        }
    }

    /**
     *La méthode qui tue les gens en dehors de la map
     */
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!manager.isStarted() || !manager.getPls().contains(p.getUniqueId()) || manager.isReunion()) return;
        if (p.getGameMode() == GameMode.SPECTATOR) return;

        if (p.getLocation().getZ() >= 200 || p.getLocation().getZ() <= -50 ) {
            manager.onKill(p, p);
        }

        if (p.getLocation().getX() >= 90 || p.getLocation().getX() <= -40 ) {
            manager.onKill(p, p);
        }

        if (p.getLocation().getY() <= -20) {
            manager.onKill(p, p);
        }
    }

    /**
     *La méthode qui gère le rôle du Fantôme
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();


        if (p.getOpenInventory().getTitle().equalsIgnoreCase("§cᴘᴏᴜʀ ǫᴜɪ ᴠᴏᴛᴇʀᴀs-ᴛᴜ ?")){
            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getItemMeta() == null) return;

            SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
            Player target = Bukkit.getPlayer(meta.getOwningPlayer().getName());

            if (target == null) return;

            if (manager.getVotes().containsKey(p.getUniqueId())) return;

            manager.getVotes().put(p.getUniqueId(), target.getUniqueId());
            p.sendMessage("§aᴛᴜ ᴀs ᴠᴏᴛᴇ́(ᴇ) ᴘᴏᴜʀ §e" + target.getName());
            p.closeInventory();

            p.getInventory().setItem(4, new ItemStack(Material.AIR));

            int alivePlayers = manager.getPls().size() - manager.getDeath().size();
            if (manager.getVotes().size() >= alivePlayers) {
                manager.endVote();
            }
        }

        if (p.getOpenInventory().getTitle().equalsIgnoreCase("§8ᴊᴏᴜᴇᴜʀ ᴀ̀ ʜᴀɴᴛᴇʀ")){
            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getItemMeta() == null) return;

            SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
            Player target = Bukkit.getPlayer(meta.getOwningPlayer().getName());

            if (target == null) return;

            p.sendMessage("§8ᴛᴜ ᴠᴀs ʜᴀɴᴛᴇʀ §d" + target.getName());
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
            target.sendMessage("§8ᴛᴜ sᴜʙɪ ʟᴀ ᴍᴀʟᴇ́ᴅɪᴄᴛɪᴏɴ ᴅᴜ ғᴀɴᴛᴏ̂ᴍᴇ !");

            p.closeInventory();
            p.getInventory().clear();
            p.setInvisible(false);
            p.setGameMode(GameMode.SPECTATOR);
        }
    }

    /**
     *Les méthodes qui serviront pour les quêtes
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block placed = e.getBlockPlaced();
        Block against = e.getBlockAgainst();

        if (!manager.getPls().contains(p.getUniqueId()) || !manager.isStarted()) return;

        if (e.getBlockPlaced().getType() != Material.LEVER) return;
        if (e.getBlockAgainst().getType() != Material.IRON_BLOCK) return;

        BlockFace face = against.getFace(placed);
        if (face.equals(BlockFace.EAST) || face.equals(BlockFace.WEST)){

            if(p.getInventory().getItemInMainHand().getAmount() == 0){
                p.sendMessage("§aBravo ! Tu as réparé(e) tous les Jetskis !");

                manager.reward(p);
            }else {
                p.sendMessage("§aTu vois le nombre de levier dans ton inventaire, c'est le nombre de levier à placer");
                p.sendMessage("§7Allez au travail !");

                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent e){
        Player p = (Player) e.getWhoClicked();

        if (!manager.getPls().contains(p.getUniqueId()) || !manager.isStarted() || manager.isReunion()) return;

        ItemStack result = e.getRecipe() != null ? e.getRecipe().getResult() : null;

        if (isCustomItem(result, Murder.getInstance().getCustomItems().fruit())){
            if (manager.getRole(p) != MRoles.CUISINER){
                e.setCancelled(true);
                p.sendMessage("§cCOMMENT ???? Ta pas le BAC cuisine pourtant");
                return;
            }

            p.sendMessage("§aBravo ! Tu as réussi à préparer ton plat !");
            manager.reward(p);
        }

        if (isCustomItem(result, Murder.getInstance().getCustomItems().canon())){
            if (manager.getRole(p) != MRoles.CANONNIER) {
                e.setCancelled(true);
                p.sendMessage("§cCOMMENT ???? Seul le §8CANONNIER §ca le droit");
                return;
            }

            p.sendMessage("§aBravo ! Tu as trouvé comment faire un boulet, maintenant, va charger un canon !");
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1, 1);
        }
    }

    /**
     * La méthode pour report les corps
     */
    @EventHandler
    public void onReportCorps(PlayerInteractAtEntityEvent e){
        Player p = e.getPlayer();
        if (!(e.getRightClicked() instanceof ArmorStand as)) return;

        if (manager.isReunion()){
            return;
        }

       String customName = as.getCustomName();
       if (customName == null) return;

       if (customName.contains("§cIçi repose §6")){
           String deadPlayer = customName.replace("§c§l\uD83D\uDC80 §8| §cIçi repose §6", "");

           for (UUID id : manager.getPls()){
               Player pls = Bukkit.getPlayer(id);
               if (pls != null){
                   pls.sendMessage("§6" + p.getName() + " §eᴀ ᴛʀᴏᴜᴠᴇ́(ᴇ) ʟᴇ ᴄᴏʀᴘs ᴅᴇ §c" + deadPlayer);
                   pls.playSound(pls.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, SoundCategory.BLOCKS, 1f, 1f);
                }
           }
           manager.reuLogic();
       }
    }

    /**
     *La méthode qui gère entièrement le rôle Frontière
     */
    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player target)) return;

        Player p = e.getPlayer();

        if (!manager.getPls().contains(p.getUniqueId()) || !manager.isStarted() || manager.isReunion()) return;
        if (!manager.getRole(p).equals(MRoles.FRONTIERE)) return;
        if (p.getInventory().getItemInMainHand().getType() != Material.PAPER) return;
        if (!isCell()) return;

        e.setCancelled(true);

        setCell(false);
        p.sendMessage("§aᴘᴀʀғᴀɪᴛ ! ᴠᴏᴜs ᴀᴠᴇᴢ ᴀᴛᴛʀᴀᴘᴇ́ ᴜɴ ᴅᴇ́ʟɪɴǫᴜᴀɴᴛ !");

        target.teleport(new Location(target.getWorld(), 22, 17, 129, -180f, 0f));
        target.sendMessage("§cᴏʜ ɴᴏɴ ! ʟᴀ ᴘᴏʟɪᴄᴇ ᴅᴇs ғʀᴏɴᴛɪᴇ̀ʀᴇs ᴠᴏᴜs ᴀ ᴄʜᴏᴘᴘᴇ́ sᴀɴs ᴘᴀᴘɪᴇʀ ᴠᴀʟᴀʙʟᴇ !");
        target.sendMessage("§7ᴜɴ ᴘᴇᴛɪᴛ sᴇ́ᴊᴏᴜʀ ᴇɴ ᴢᴏɴᴢᴏɴ ᴠᴏᴜs ғᴇʀᴀ ᴘᴀs ᴅᴇ ᴍᴀʟ..");
        target.sendMessage(" ");

        new BukkitRunnable() {
            private int timer = 20;

            @Override
            public void run() {
                if (timer > 0) {
                    timer--;
                    ActionBar.send(target, "§cᴜɴᴇ ʀᴇ́ᴠɪsɪᴏɴ ᴅᴇ ᴠᴏᴛʀᴇ ɪᴅᴇɴᴛɪᴛᴇ́ ᴇsᴛ ᴇɴ ᴄᴏᴜʀs...");
                    return;
                }

                cancel();

                if (manager.getRole(target) != MRoles.CLANDESTIN) {
                    p.getInventory().clear();
                    p.sendMessage("§cᴠᴏᴜs ᴇ̂ᴛᴇs ᴜɴ ʙᴏɴ ᴀ̀ ʀɪᴇɴ.. ʟᴇ sᴜsᴘᴇᴄᴛ ᴇ́ᴛᴀɪᴛ ᴇɴ ʀᴇ̀ɢʟᴇ.");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1f, 1f);

                    target.sendMessage("§aᴀᴘʀᴇ̀s ʀᴇ́ᴠɪsɪᴏɴ ᴅᴇ ᴠᴏᴛʀᴇ ᴅᴏssɪᴇʀ, ᴛᴏᴜᴛ ᴇsᴛ ᴇɴ ʀᴇ̀ɢʟᴇ..");
                    target.sendMessage("§7ᴇxᴄᴜsᴇᴢ ɴᴏᴜs ᴘᴏᴜʀ ʟᴀ ɢᴇ̀ɴᴇ ᴏᴄᴄᴀsɪᴏɴɴᴇ́ᴇ");
                    target.sendMessage(" ");
                    target.playSound(target.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, SoundCategory.BLOCKS, 1f, 1f);
                    target.teleport(new Location(target.getWorld(), 22, 16, 81));
                    for (UUID id : manager.getPls()) {
                        Player pls = Bukkit.getPlayer(id);
                        if (pls != null) {
                            pls.sendMessage("");
                            pls.sendMessage("§6§l━━━━━━━━━━━━━━━━━━━━━━━━");
                            pls.sendMessage("§e" + p.getName() + " §aᴍᴇᴍʙʀᴇ ᴅᴇ ʟᴀ §9ᴘ§f.§fᴀ.§cғ");
                            pls.sendMessage("§cEST UN(E) RATÉ(E)");
                            pls.playSound(pls.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1f,1f);
                        }
                    }
                } else {
                    p.sendMessage("§aᴠᴏᴜs ᴀᴠᴇᴢ ᴄʜᴏᴘᴘᴇ́ ᴜɴ ɢʀᴏs ᴘᴏɪssᴏɴ ! ᴠᴏᴜs ᴀʟʟᴇᴢ ʀᴇᴄ̧ᴇᴠᴏɪʀ ᴜɴᴇ ᴘʀɪᴍᴇ !");
                    p.playSound(p.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, SoundCategory.BLOCKS, 1f, 1f);

                    target.sendMessage("§cᴀᴘʀᴇ̀s ᴇɴǫᴜᴇ̂ᴛᴇ, ɪʟ s'ᴀᴠᴇ̀ʀᴇ ǫᴜᴇ ᴠᴏᴜs ᴀᴠᴇᴢ ғʀᴀᴜᴅᴇ́ ᴠᴏᴛʀᴇ ᴘʟᴀᴄᴇ ᴅᴀɴs ᴄᴇ ʙᴀᴛᴇᴀᴜ");
                    target.sendMessage("§7ᴠᴏᴜs ᴇ̂ᴛᴇs ғɪɴɪᴛᴏ..");
                    target.playSound(target.getLocation(), Sound.ENTITY_CAT_HISS, SoundCategory.BLOCKS, 1f, 1f);
                    for (UUID id : manager.getPls()) {
                        Player pls = Bukkit.getPlayer(id);
                        if (pls != null) {
                            pls.sendMessage(" ");
                            pls.sendMessage("§2§l━━━━━━━━━━━━━━━━━━━━━━━━");
                            pls.sendMessage("§a ʟᴀ §9ᴘ§f.§fᴀ.§cғ ᴀ ɢᴀɢɴᴇ́ !");
                            pls.sendMessage("§7 ɪʟs ᴏɴᴛ ᴀʀʀᴇ̂ᴛᴇ́ ʟᴇ ᴄʟᴀɴᴅᴇsᴛɪɴ");
                            pls.sendMessage("§2§l━━━━━━━━━━━━━━━━━━━━━━━━");
                            pls.sendMessage(" ");
                        }
                    }
                    manager.onEnd();
                }
            }
        }.runTaskTimer(Murder.getInstance(), 0, 20L);
    }

    /**
     *La méthode qui gère les kills du Capitaine et du Pirate Fou
     */
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e){
        if (!(e.getEntity() instanceof Player victim)) return;
        if (!manager.getPls().contains(victim.getUniqueId())) return;

        Player attacker;

        if (e.getDamager() instanceof Player) {
            attacker = (Player) e.getDamager();

            Material hand = attacker.getInventory().getItemInMainHand().getType();

            if (hand != Material.IRON_SWORD) {
                e.setCancelled(true);
                return;
            }

            MRoles role = manager.getRole(attacker);
            if (!role.equals(MRoles.CAPITAINE) && !role.equals(MRoles.PIRATE_FOU)) {
                e.setCancelled(true);
                return;
            }
        } else {
            attacker = null;
        }

        if (attacker == null || !manager.getPls().contains(attacker.getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        if (manager.isReunion()){
            attacker.sendMessage("§cᴛᴜ ɴᴇ ᴘᴇᴜx ᴘᴀs ᴜᴛɪʟɪsᴇʀ ᴛᴏɴ ᴘᴏᴜᴠᴏɪʀ ᴘᴇɴᴅᴀɴᴛ ʟᴇs ʀᴇ́ᴜɴɪᴏɴs !");
            return;
        }

        e.setCancelled(true);

        if (Murder.getInstance().getManager().getRole(attacker).equals(MRoles.PIRATE_FOU)) {
            boolean wrongTarget = !Murder.getInstance().getManager().getRole(victim).equals(MRoles.CAPITAINE);

            manager.onKill(attacker, victim);

            if (wrongTarget) {
                manager.onKill(victim, attacker);
            }
        } else {
            if (!isKill()){
                new BukkitRunnable() {
                    private int timer = 15;

                    @Override
                    public void run() {

                    ActionBar.send(attacker, "§cMinute ! La folie meurtrière, c'est mal !");

                    if (timer == 0){
                        attacker.sendMessage("§aTu peux de-nouveau attacker un innocent !");
                        attacker.playSound(attacker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1f, 1f);
                        ActionBar.stop(attacker);
                        setKill(true);
                        cancel();
                    }

                    if (!manager.isStarted()){
                        ActionBar.stop(attacker);
                        setKill(true);
                        cancel();
                    }

                    timer--;
                    }
                }.runTaskTimer(Murder.getInstance(), 0, 20L);
            }else {
                setKill(false);
                manager.onKill(attacker, victim);
            }
        }
    }

    public void resetFlags() {
        this.cell = true;
        this.charm = true;
        this.invis = true;
        this.kill = true;
    }

    private boolean isCustomItem(ItemStack item, ItemStack expected) {
        return item != null && expected != null && item.isSimilar(expected);
    }

    // Getters
    public boolean isKill() {
        return kill;
    }
    public List<UUID> getCooldown() {
        return cooldown;
    }
    public boolean isCharm() {
        return charm;
    }
    public boolean isInvis() {
        return invis;
    }
    public boolean isCell() {
        return cell;
    }

    // Setters
    public void setInvis(boolean invis) {
        this.invis = invis;
    }
    public void setCharm(boolean charm) {
        this.charm = charm;
    }
    public void setCell(boolean cell) {
        this.cell = cell;
    }

    public void setKill(boolean kill) {
        this.kill = kill;
    }
}
