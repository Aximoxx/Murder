package Aximox.murder;

import Aximox.murder.grade.MGrades;
import Aximox.murder.grade.RankManager;
import Aximox.murder.utils.ActionBar;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class MManager {
    private boolean started;
    private boolean reunion;
    private String murderName = "§c⚔️ Inconnu";
    private String lastMurderName = "§c⚔️ Inconnu";
    private final List<UUID> pls = new ArrayList<>();
    private final List<UUID> death = new ArrayList<>();
    private final List<UUID> avote = new ArrayList<>();
    private HashMap<UUID, UUID> votes = new HashMap<>();
    private final List<UUID> murderP = new ArrayList<>();
    private final List<UUID> innocent = new ArrayList<>();
    private final List<UUID> hasBuzzed = new ArrayList<>();
    private final List<UUID> detective = new ArrayList<>();
    private final List<ArmorStand> morts = new ArrayList<>();
    private final Map<UUID, MRoles> roleMap = new HashMap<>();
    private final RankManager rankManager = new RankManager();

    /**
     * Cette méthode sert à gérer les personnes qui rejoignent la partie.
     **/
    public void onJoin(Player p) {
        if (getPls().contains(p.getUniqueId())) {
            p.sendMessage(getMurder() + "§eᴍᴀʟʜᴇᴜʀᴇᴜsᴇᴍᴇɴᴛ, ᴠᴏᴜs ᴇ̂ᴛᴇs ᴅᴇ́ᴊᴀ̀ ᴅᴀɴs ʟᴀ ᴘᴀʀᴛɪᴇ..");
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1f, 1f);
            return;
        }

        if (isStarted()) {
            p.sendMessage(getMurder() + "§eᴜɴᴇ ᴘᴀʀᴛɪᴇ ᴇsᴛ ᴅᴇ́ᴊᴀ̀ ᴇɴ ᴄᴏᴜʀs, ᴀᴛᴛᴇɴᴅᴇᴢ ʟᴀ ᴘʀᴏᴄʜᴀɪɴᴇ !");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 1f, 1f);
            return;
        }

        for (UUID id : pls) {
            Player pls = Bukkit.getPlayer(id);
            if (pls != null) {
                pls.sendMessage(getMurder() + "§6" + p.getName() + "§e ᴇsᴛ ᴘʀᴇ̂ᴛ ᴀ̀ ᴇɴ ᴅᴇ́ᴄᴏᴜᴅʀᴇ !");
                pls.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.BLOCKS, 1f, 1f);
            }
        }

        getPls().add(p.getUniqueId());

        p.getInventory().clear();
        p.sendMessage(getMurder() + "§eᴇ̂ᴛᴇs ᴠᴏᴜs ᴘʀᴇ̂ᴛ ᴀ̀ ᴍᴀsᴛᴇʀᴍɪɴᴅᴇʀ ᴛᴏᴜᴛ ʟᴇ ᴍᴏɴᴅᴇ ?");
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
    }

    /**
     * Cette méthode sert gérer les personnes qui quittent la partie.
     **/
    public void onQuit(Player p) {
        if (!getPls().contains(p.getUniqueId())) {
            p.sendMessage(getMurder() + "§eᴍᴀʟʜᴇᴜʀᴇᴜsᴇᴍᴇɴᴛ, ᴠᴏᴜs ɴ'ᴇ̂ᴛᴇs ᴅᴀɴs ᴀᴜᴄᴜɴᴇ ᴘᴀʀᴛɪᴇ..");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 1f, 1f);
            return;
        }

        p.sendMessage(getMurder() + "§eɪʟ ғᴀᴜᴛ ᴄʀᴏɪʀᴇ ǫᴜᴇ ᴠᴏᴜs ɴ'ᴇ̂ᴛᴇs ᴘᴀs ʟᴇ ᴍᴀsᴛᴇʀᴍɪɴᴅ ǫᴜᴇ ʟ'ᴏɴ ᴍᴀ ᴠᴇɴᴅᴜ..");
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, SoundCategory.BLOCKS, 1f, 1f);

        getPls().remove(p.getUniqueId());

        for (UUID id : pls) {
            Player pls = Bukkit.getPlayer(id);
            if (pls != null) {
                pls.sendMessage(getMurder() + "§6" + p.getName() + "§e s'ᴇsᴛ ᴛʀᴏᴘ ғᴀɪᴛ ᴍᴀɴɪᴘᴜʟᴇ́(ᴇ) !");
                pls.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS, 1f, 1f);
            }
        }
    }

    /**
     * Cette méthode sert de logique de début.
     * Elle est appelée à chaque début de partie.
     **/
    public void onStart(Player p) {
        if (!getPls().contains(p.getUniqueId()) || isStarted()) return;
        if (getPls().size() < 3) {
            p.sendMessage(getMurder() + "§cɪʟ ғᴀᴜᴛ ᴀᴜ ᴍɪɴɪᴍᴜᴍ 3 ᴊᴏᴜᴇᴜʀs ᴘᴏᴜʀ ʟᴀɴᴄᴇʀ ᴜɴᴇ ᴘᴀʀᴛɪᴇ ! §7(§e" + getPls().size() + "§7/§63§7)");
            return;
        }

        new BukkitRunnable() {
            private int timer = 6;

            @Override
            public void run() {
                if (timer <= 5 && timer >= 0) {
                    for (UUID id : getPls()) {
                        Player pls = Bukkit.getPlayer(id);
                        if (pls == null) continue;
                        pls.sendMessage(getMurder() + "§aʟᴀ ᴘᴀʀᴛɪᴇ ᴅᴇ́ᴍᴀʀʀᴇ ᴅᴀɴs §e" + timer + "§as !");
                        pls.playSound(pls.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                    }
                }

                if (timer == 1){
                    for (UUID id : getPls()) {
                        Player pls = Bukkit.getPlayer(id);
                        if (pls == null) continue;
                        pls.teleport(new Location(pls.getWorld(), 22, 16, 81));
                    }
                }

                if (timer == 0){
                    distributeRole(new ArrayList<>(getPls()));
                    spawnChest();
                    setStarted(true);
                    cancel();
                }
                timer--;
            }
        }.runTaskTimer(Murder.getInstance(), 0, 20);
    }

    /**
     * Cette méthode sert de logique de fin de partie.
     **/
    public void onEnd() {
        if (!isStarted()) return;
        List<UUID> savePls = new ArrayList<>(getPls());

        new BukkitRunnable() {
            private int timer = 5;

            @Override
            public void run() {
                if (timer > 0) {
                    for (UUID id : savePls) {
                        Player pls = Bukkit.getPlayer(id);
                        if (pls == null) continue;
                        pls.sendMessage(getMurder() + "§aʟᴀ ᴘᴀʀᴛɪᴇ sᴇ ᴛᴇʀᴍɪɴᴇʀᴀ ᴅᴀɴs §e" + timer + "§as !");
                        pls.playSound(pls.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1f, 1f);
                    }
                    timer--;
                    return;
                }

                // timer == 0
                for (UUID id : savePls) {
                    Player pls = Bukkit.getPlayer(id);
                    if (pls == null) continue;
                    pls.getInventory().clear();
                    pls.setGameMode(GameMode.SURVIVAL);
                    pls.teleport(new Location(Bukkit.getWorld("world"), -47, 58, -278, 0f, 0f));
                }

                reset();
                cancel();
            }
        }.runTaskTimer(Murder.getInstance(), 0, 20);
    }

    /**
     * Cette méthode sert à gérer les "kills" fait durant la partie.
     **/
    public void onKill(Player p, Player v) {
        for (UUID id : getPls()) {
            Player pls = Bukkit.getPlayer(id);
            if (pls == null) continue;

            pls.sendMessage(" ");
            pls.sendMessage("§c§l━━━━━━━━━━━━⚔━━━━━━━━━━━━");
            pls.sendMessage("§e" + v.getName() + " §6ᴀ ᴇ́ᴛᴇ́ ᴀᴛᴛᴀǫᴜᴇ́(ᴇ) !");
            pls.sendMessage("§6ɪʟ ᴇ́ᴛᴀɪᴛ §e" + getRole(v).getName());
            pls.sendMessage("§c§l━━━━━━━━━━━━━━━━━━━━━━━━━");
            pls.sendMessage(" ");

            pls.playSound(pls.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS, 1f, 1f);
        }

        if (murderP.contains(v.getUniqueId())) {
            lastMurderName = v.getName();
            murderP.remove(v.getUniqueId());
        } else {
            innocent.remove(v.getUniqueId());
            detective.remove(v.getUniqueId());
        }

        if (getRole(v) == MRoles.FANTOME) {
            ItemStack haunt = new ItemStack(Material.WITHER_ROSE);
            ItemMeta meta = haunt.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§8Malice");
                meta.setLore(List.of("§7ʜᴀɴᴛᴇ ᴜɴ ᴊᴏᴜᴇᴜʀ ᴘᴏᴜʀ ʟᴇs 10 ᴘʀᴏᴄʜᴀɪɴᴇs sᴇᴄᴏɴᴅᴇs", "§7ᴜsᴀɢᴇ §8| §fᴄʟɪǫᴜᴇ ᴅʀᴏɪᴛ"));
                meta.setEnchantmentGlintOverride(true);
                haunt.setItemMeta(meta);
            }
            v.setInvisible(true);
            v.setGameMode(GameMode.ADVENTURE);
            v.getInventory().addItem(haunt);
        }else {
            roleMap.remove(v.getUniqueId());
            v.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(getMurder() + "§eᴠᴏᴜs ᴀᴠᴇᴢ ᴇ́ʟɪᴍɪɴᴇ́(ᴇ) §6" + v.getName());

            morts.add(setCorps(v));
            death.add(v.getUniqueId());
            checkWin();
        }
    }

    /**
     * Cette méthode sert à vérifier si la partie se termine ou pas.
     **/
    public void checkWin() {
        if (murderP.isEmpty()) {
            for (UUID id : getPls()) {
                Player p = Bukkit.getPlayer(id);
                if (p == null) continue;

                p.sendMessage(" ");
                p.sendMessage("§2§l━━━━━━━━━━━━━━━━━━━━━━━━");
                p.sendMessage("§a ʟᴇs ɪɴɴᴏᴄᴇɴᴛs ᴏɴᴛ ɢᴀɢɴᴇ́s !");
                p.sendMessage("§7 ʟᴇ ᴍᴜʀᴅᴇʀ ᴇ́ᴛᴀɪᴛ §4§l" + getLastMurderName());
                p.sendMessage("§2§l━━━━━━━━━━━━━━━━━━━━━━━━");
                p.sendMessage(" ");

                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.BLOCKS, 1f, 1f);
            }
            onEnd();
        } else if (innocent.isEmpty()) {
            Player murderPlayer = Bukkit.getPlayer(murderP.getFirst());

            if (murderPlayer != null) {
                murderName = murderPlayer.getName();
            }

            for (UUID id : getPls()) {
                Player p = Bukkit.getPlayer(id);
                if (p == null) continue;

                p.sendMessage(" ");
                p.sendMessage("§4§l━━━━━━━━━━━━━━━━━━━━━━━━");
                p.sendMessage("§c ʟᴇ ᴍᴜʀᴅᴇʀ ᴀ ɢᴀɢɴᴇ́(ᴇ) !");
                p.sendMessage("§7 ɪʟ s'ᴀɢɪssᴀɪᴛ ᴅᴇ §4§l" + murderName);
                p.sendMessage("§4§l━━━━━━━━━━━━━━━━━━━━━━━━");
                p.sendMessage(" ");

                p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, SoundCategory.BLOCKS, 1f, 1f);
            }
            onEnd();
        }
    }

    /**
     * Cette méthode sert à donner les rôles aux joueurs.
     **/
    public void distributeRole(List<UUID> pls) {
        List<MRoles> roleList = new ArrayList<>();
        roleList.add(MRoles.CAPITAINE);
        roleList.add(MRoles.CLANDESTIN);

        for (int i = 2; i < pls.size(); i++) {
            if (i == 6) {
                roleList.add(MRoles.SIRENE);
            } else if (i == 3) {
                roleList.add(MRoles.CLANDESTIN);
            } else if (i == 4) {
                roleList.add(MRoles.TRESOR);
            } else if (i == 5) {
                roleList.add(MRoles.FANTOME);
            } else if (i == 2) {
                roleList.add(MRoles.FRONTIERE);
            } else {
                roleList.add(MRoles.PASSAGER);
            }
        }

        Collections.shuffle(roleList);

        for (int i = 0; i < pls.size(); i++) {
            UUID joueur = pls.get(i);
            MRoles role = roleList.get(i);

            roleMap.put(joueur, role);

            if (role == MRoles.CAPITAINE) {
                murderP.add(joueur);
            } else if (role == MRoles.PIRATE_FOU) {
                detective.add(joueur);
                innocent.add(joueur);
            } else {
                innocent.add(joueur);
            }

            Player pl = Bukkit.getPlayer(joueur);
            if (pl != null) {
                giveItems(pl, role);
                annonceR(joueur, role);
            }
        }
    }

    public void spawnChest(){
        new BukkitRunnable() {
            private int timer = 90;
            @Override
            public void run() {

                if (!isStarted()){
                    Bukkit.getLogger().info("§cLe spawn des coffres à été intérrompue");
                    cancel();
                }

                if (timer == 30 || timer == 60 || timer == 90){
                    List<Location> chest = Murder.getInstance().getChests();
                    Location randomChest = chest.get(new Random().nextInt(chest.size()));
                    randomChest.setYaw(0);

                    Bukkit.getWorld("world").getBlockAt(randomChest).setType(Material.WARPED_SLAB);
                    Bukkit.broadcastMessage("§aᴜɴ ᴄᴏғғʀᴇ ᴠɪᴇɴᴛ ᴅ'ᴀᴘᴘᴀʀᴀɪ̂ᴛʀᴇ !");
                    Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1f, 1f));
                }

                if (timer == 0){
                    cancel();
                }

                timer--;
            }
        }.runTaskTimer(Murder.getInstance(), 0, 20L);
    }

    /**
     * Cette méthode sert à distribuer les items aux rôles.
     **/
    public void giveItems(Player p, MRoles role) {
        p.getInventory().clear();

        if (role == MRoles.CAPITAINE) {
            ItemStack sword = new ItemStack(Material.IRON_SWORD);
            ItemMeta swordMeta = sword.getItemMeta();
            if (swordMeta != null) {
                swordMeta.setDisplayName("§c§l🔪 ᴘᴏɪɢɴᴀʀᴅ ᴅᴜ ᴄᴀᴘɪᴛᴀɪɴᴇ");
                swordMeta.setUnbreakable(true);
                swordMeta.setCustomModelData(18);
                sword.setItemMeta(swordMeta);
            }
            p.getInventory().setItem(1, sword);

        } else if (role == MRoles.PIRATE_FOU) {
            ItemStack sword = new ItemStack(Material.IRON_SWORD);
            ItemMeta swordMeta = sword.getItemMeta();
            if (swordMeta != null) {
                swordMeta.setUnbreakable(true);
                swordMeta.setCustomModelData(19);
                swordMeta.setEnchantmentGlintOverride(true);
                swordMeta.setDisplayName("§a§l⚔ sᴀʙʀᴇ ᴅᴜ ᴘɪʀᴀᴛᴇ");
                sword.setItemMeta(swordMeta);
            }

            p.getInventory().setItem(0, sword);
        } else if (role == MRoles.CLANDESTIN) {
            ItemStack invis = new ItemStack(Material.AMETHYST_SHARD);
            ItemMeta invisMeta = invis.getItemMeta();
            if (invisMeta != null) {
                invisMeta.setDisplayName("§fɪɴᴠɪsɪʙɪʟɪᴛᴇ́");
                invisMeta.setLore(List.of("§7ᴜɴ ᴄʟɪǫᴜᴇ ᴅʀᴏɪᴛ, ᴇᴛ §ғᴘᴏᴜғ"));
                invisMeta.setEnchantmentGlintOverride(true);
                invis.setItemMeta(invisMeta);
            }
            p.getInventory().setItem(1, invis);

        } else if (role == MRoles.SIRENE) {
            ItemStack voice = new ItemStack(Material.SUNFLOWER);
            ItemMeta vmeta = voice.getItemMeta();
            if (vmeta != null) {
                vmeta.setDisplayName("§dChant de la Sirène");
                vmeta.setEnchantmentGlintOverride(true);
                vmeta.setLore(List.of("§7ᴍᴇᴛ sʟᴏᴡɴᴇss ᴀ̀ ᴛᴏᴜᴛ ʟᴇs ᴊᴏᴜᴇᴜʀs ᴅᴀɴs ᴜɴ ʀᴀʏᴏɴ ᴅᴇ 5 ʙʟᴏᴄᴋs ᴘᴇɴᴅᴀɴᴛ 10 sᴇᴄᴏɴᴅᴇs", "§7ᴜsᴀɢᴇ §8| §fᴄʟɪǫᴜᴇ ᴅʀᴏɪᴛ"));
                vmeta.setCustomModelData(3);
                voice.setItemMeta(vmeta);
            }
            p.getInventory().addItem(voice);
        } else if (role == MRoles.FRONTIERE) {
            ItemStack prison = new ItemStack(Material.IRON_BARS);
            ItemMeta prisonMeta = prison.getItemMeta();
            if (prisonMeta != null) {
                prisonMeta.setDisplayName("§fPrison");
                prisonMeta.setEnchantmentGlintOverride(true);
                prisonMeta.setLore(List.of("§7ᴄᴇᴛ ɪᴛᴇᴍ ᴛᴇ ᴘᴇʀᴍᴇᴛ ᴅᴇ ᴘʀɪᴠᴇʀ ʟᴀ ʟɪʙᴇʀᴛᴇ́ ᴅ'ᴀᴜᴛʀᴜɪ", "§7ᴜsᴀɢᴇ §8| §fᴄʟɪǫᴜᴇ ᴅʀᴏɪᴛ"));
                prison.setItemMeta(prisonMeta);
            }
            p.getInventory().setItem(1, prison);
        }

        p.updateInventory();
    }

    /**
     * Cette méthode sert à expliquer le rôle aux joueurs.
     **/
    public void annonceR(UUID p, MRoles roles) {
        Player pls = Bukkit.getPlayer(p);
        assert pls != null;

        if (!getPls().contains(pls.getUniqueId()) || !isStarted()) return;

        pls.sendMessage(" ");
        pls.sendMessage("§c⚔ §eTu est " + roles.getName());
        pls.sendMessage("§c⚔ §r" + roles.getDescription());
        pls.sendMessage(" ");

        pls.sendTitle("§7Tu est " + roles.getName(), roles.getDescription(), 10, 30, 10);
        pls.playSound(pls.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.BLOCKS, 0.5f, 1f);
    }

    public void setChest(Player p){
        if (!p.getWorld().equals(Bukkit.getWorld("world"))) return;

        int chest = Murder.getInstance().getConfig().getInt("murder.chestCount", 0) + 1;

        Murder.getInstance().getConfig().set("murder.chestCount", chest);
        Murder.getInstance().getConfig().set("murder.chest" + chest, p.getLocation());
        Murder.getInstance().saveConfig();

        p.sendMessage("§aLe coffre §b#" + chest + " §aà bien été ajouté au coordonée §bx: §f" + p.getLocation().getX() + "§f, §by: §f" + p.getLocation().getY() + "§f, §bz: §f" + p.getLocation().getZ());
    }

    public void setBuzzer(Player p){
        ArmorStand as = p.getWorld().spawn(p.getLocation(), ArmorStand.class);
        as.setVisible(false);
        as.setGravity(false);
        as.setCustomName("§c§lʙᴜᴢᴢᴇʀ ᴅ'ᴜʀɢᴇɴᴄᴇ");
        as.setCustomNameVisible(true);
    }

    public ArmorStand setCorps(Player p){
        ArmorStand as = p.getWorld().spawn(p.getLocation(), ArmorStand.class);

        as.setVisible(false);
        as.setGravity(false);
        as.setCustomName("§c§l\uD83D\uDC80 §8| §cIçi repose §6" + p.getName());
        as.setCustomNameVisible(true);
        return as;
    }

    public void reuLogic(Player p){
        new BukkitRunnable() {
            private int timer = 61;

            @Override
            public void run() {

                if (timer == 61){
                    setReunion(true);
                }

                if (timer == 60){
                    for (UUID id : getPls()){
                        Player pls = Bukkit.getPlayer(id);
                        if (pls != null){

                            pls.getInventory().setItem(4, new ItemBuilder(Material.ECHO_SHARD).name("§9ᴄʜᴏɪsɪ ᴜɴᴇ ᴘᴇʀsᴏɴɴᴇ ᴘᴏᴜʀ ǫᴜɪ ᴠᴏᴛᴇʀ").build());
                            pls.teleport(new Location(pls.getWorld(), 27, -24, 64));
                        }
                    }
                }

                if (timer == 3){
                    for (UUID id : getPls()){
                        Player pls = Bukkit.getPlayer(id);
                        if (pls != null){
                            pls.teleport(new Location(pls.getWorld(), 22, 16, 81));
                            pls.sendMessage("§aʟᴇs ʀᴇ́sᴜʟᴛᴀᴛs ᴠᴏᴜs sᴇʀᴏɴᴛ ᴛʀᴀɴsᴍɪs ᴅ'ɪᴄ̧ɪ ᴘᴇᴜ !");
                            pls.playSound(pls.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                        }
                    }
                }

                if (timer == 0) {
                    endVote();
                    cancel();
                    return;
                }

                for (UUID id : getPls()){
                    Player pls = Bukkit.getPlayer(id);
                    if (pls != null){
                        if (isReunion()){
                            ActionBar.send(pls, "§6ᴛᴇᴍᴘs ʀᴇsᴛᴀɴᴛ: §f" + timer);
                        }else {
                            cancel();
                            return;
                        }
                    }
                }

                timer--;
            }
        }.runTaskTimer(Murder.getInstance(), 0, 20L);
    }

    public void endVote() {
        if (!isReunion()) return;

        UUID mostVoted = getVotes().values().stream()
                .max(Comparator.comparingInt(uuid -> Collections.frequency(getVotes().values(), uuid)))
                .orElse(null);

        for (UUID uuid : getPls()) {
            Player pls = Bukkit.getPlayer(uuid);
            if (pls == null) continue;

            pls.getInventory().setItem(4, new ItemStack(Material.AIR));

            if (mostVoted == null || getVotes().isEmpty()) {
                pls.sendMessage(" ");
                pls.sendMessage("§c§l━━━━━━━━━━━━━━━━━━━━━━━━");
                pls.sendMessage("§cᴘᴇʀsᴏɴɴᴇ ɴ'ᴀ ᴠᴏᴛᴇ́, ᴘᴇʀsᴏɴɴᴇ ɴ'ᴇsᴛ ᴇ́ʟɪᴍɪɴᴇ́ !");
                pls.sendMessage("§c§l━━━━━━━━━━━━━━━━━━━━━━━━");
                pls.sendMessage(" ");
            } else {
                Player mostVotedPlayer = Bukkit.getPlayer(mostVoted);
                pls.sendMessage(" ");
                pls.sendMessage("§c§l━━━━━━━━━━━━━━━━━━━━━━━━");
                pls.sendMessage(mostVotedPlayer != null
                        ? "§e" + mostVotedPlayer.getName() + " §6ᴀ̀ ᴇ́ᴛᴇ́ ᴇ́ʟɪᴍɪɴᴇ́(ᴇ) ʟᴏʀs ᴅᴇ ᴄᴇ ᴄᴏɴsᴇɪʟ !"
                        : "§cᴇʀʀᴇᴜʀ : ʟᴇ ᴊᴏᴜᴇᴜʀ ɴ'ᴇsᴛ ᴘʟᴜs ʟᴀ̀");
                pls.sendMessage("§c§l━━━━━━━━━━━━━━━━━━━━━━━━");
                pls.sendMessage(" ");
            }
        }

        if (mostVoted != null && !getVotes().isEmpty()) {
            Player mostVotedPlayer = Bukkit.getPlayer(mostVoted);
            if (mostVotedPlayer != null) {
                onKill(mostVotedPlayer, mostVotedPlayer);
            }
        }

        getVotes().clear();
        getAvote().clear();
        setReunion(false);
    }

    /**
     * Cette méthode sert à récupérer le vrai rôle d'un joueur.
     **/
    public MRoles getRole(Player p) {
        return roleMap.getOrDefault(p.getUniqueId(), MRoles.PASSAGER);
    }

    public void updateTabList(Player p) {
        MGrades rank = Murder.getInstance().getRankManager().getRank(p.getUniqueId());
        String teamName = String.format("%02d_%s", 10 - rank.getPower(), rank.name());

        Team team = p.getScoreboard().getTeam(teamName);
        if (team == null) team = p.getScoreboard().registerNewTeam(teamName);

        team.setPrefix(rank.getPrefix());
        team.addEntry(p.getName());
        p.setPlayerListName(rank.getPrefix() + p.getName());
    }

    /**
     * Cette méthode sert à remettre le jeu de 0.
     **/
    public void reset() {
        resetChest();
        resetDeahtAS();
        setReunion(false);
        setStarted(false);
        getDeath().clear();
        getRoleMap().clear();
        getMurderP().clear();
        getInnocent().clear();
        getDetective().clear();
        getHasBuzzed().clear();
        setLastMurderName("§c⚔️ Inconnu");
        Murder.getInstance().getmListener().resetFlags();
    }

    public void resetChest() {
        for (Location chest : Murder.getInstance().getChests()) {
            if (Bukkit.getWorld("world").getBlockAt(chest).getType() == Material.WARPED_SLAB) {
                Bukkit.getWorld("world").getBlockAt(chest).setType(Material.AIR);
            }
        }
    }

    public void resetDeahtAS(){
        for (ArmorStand mort : morts){
            mort.remove();
        }
        morts.clear();
    }

    // Getters
    public boolean isReunion() {
        return reunion;
    }
    public List<UUID> getDeath() {
        return death;
    }
    public List<UUID> getPls() { return pls; }
    public HashMap<UUID, UUID> getVotes() {
        return votes;
    }
    public List<UUID> getAvote() {
        return avote;
    }
    public boolean isStarted() { return started; }
    public String getMurder() { return "§cMurder: "; }
    public List<UUID> getMurderP() { return murderP; }
    public List<UUID> getInnocent() { return innocent; }
    public List<UUID> getHasBuzzed() {
        return hasBuzzed;
    }
    public List<UUID> getDetective() { return detective; }
    public Map<UUID, MRoles> getRoleMap() {
        return roleMap;
    }
    public RankManager getRankManager() { return rankManager; }
    public String getLastMurderName() { return lastMurderName; }
    // Setters

    public void setStarted(boolean started) { this.started = started; }
    public void setReunion(boolean reunion) {
        this.reunion = reunion;
    }

    public void setLastMurderName(String lastMurderName) {
        this.lastMurderName = lastMurderName;
    }
}