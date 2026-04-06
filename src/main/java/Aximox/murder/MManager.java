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
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class MManager {

    // ============================================================
    //  ÉTAT DE LA PARTIE
    // ============================================================

    private boolean started;
    private boolean reunion;
    private boolean isMuteOn;
    private String murderName     = "§c⚔️ Inconnu";
    private String lastMurderName = "§c⚔️ Inconnu";

    // ============================================================
    //  LISTES DE JOUEURS
    // ============================================================

    private final List<UUID> pls        = new ArrayList<>();
    private final List<UUID> death      = new ArrayList<>();
    private final List<UUID> avote      = new ArrayList<>();
    private final List<UUID> murderP    = new ArrayList<>();
    private final List<UUID> innocent   = new ArrayList<>();
    private final List<UUID> detective  = new ArrayList<>();
    private final List<UUID> hasBuzzed  = new ArrayList<>();

    // ============================================================
    //  VOTES
    // ============================================================

    private final HashMap<UUID, UUID> votes = new HashMap<>();

    // ============================================================
    //  RÔLES
    // ============================================================

    private final Map<UUID, MRoles> roleMap = new HashMap<>();

    private final List<MRoles> activeRoles = new ArrayList<>(Arrays.asList(
            MRoles.SIRENE,
            MRoles.TRESOR
    ));

    // ============================================================
    //  DIVERS
    // ============================================================

    private final RankManager rankManager              = new RankManager();
    private final Map<UUID, Set<UUID>> activeVigieHighlights = new HashMap<>();
    private final List<ArmorStand> morts               = new ArrayList<>();
    private BukkitTask vigieTask;

    // ============================================================
    //  GESTION DES JOUEURS (rejoindre / quitter)
    // ============================================================

    /** Gère les joueurs qui rejoignent la partie. */
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
            Player pl = Bukkit.getPlayer(id);
            if (pl != null) {
                pl.sendMessage(getMurder() + "§6" + p.getName() + "§e ᴇsᴛ ᴘʀᴇ̂ᴛ ᴀ̀ ᴇɴ ᴅᴇ́ᴄᴏᴜᴅʀᴇ !");
                pl.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.BLOCKS, 1f, 1f);
            }
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            updateTabList(online);
        }

        p.getInventory().clear();
        getPls().add(p.getUniqueId());
        p.sendMessage(getMurder() + "§eᴇ̂ᴛᴇs ᴠᴏᴜs ᴘʀᴇ̂ᴛ ᴀ̀ ᴍᴀsᴛᴇʀᴍɪɴᴅᴇʀ ᴛᴏᴜᴛ ʟᴇ ᴍᴏɴᴅᴇ ?");
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
    }

    /** Gère les joueurs qui quittent la partie. */
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
            Player pl = Bukkit.getPlayer(id);
            if (pl != null) {
                pl.sendMessage(getMurder() + "§6" + p.getName() + "§e s'ᴇsᴛ ᴛʀᴏᴘ ғᴀɪᴛ ᴍᴀɴɪᴘᴜʟᴇ́(ᴇ) !");
                pl.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS, 1f, 1f);
            }
        }
    }

    // ============================================================
    //  CYCLE DE VIE DE LA PARTIE (start / end / reset)
    // ============================================================

    /** Logique de début de partie. */
    public void onStart(Player p) {
        if (!getPls().contains(p.getUniqueId()) || isStarted()) return;
        if (getPls().size() < 2) {
            p.sendMessage(getMurder() + "§cɪʟ ғᴀᴜᴛ ᴀᴜ ᴍɪɴɪᴍᴜᴍ 3 ᴊᴏᴜᴇᴜʀs ᴘᴏᴜʀ ʟᴀɴᴄᴇʀ ᴜɴᴇ ᴘᴀʀᴛɪᴇ ! §7(§e" + getPls().size() + "§7/§63§7)");
            return;
        }

        new BukkitRunnable() {
            private int timer = 6;

            @Override
            public void run() {
                if (timer > 0) {
                    for (UUID id : getPls()) {
                        Player pl = Bukkit.getPlayer(id);
                        if (pl == null) continue;
                        pl.sendMessage(getMurder() + "§aʟᴀ ᴘᴀʀᴛɪᴇ ᴅᴇ́ᴍᴀʀʀᴇ ᴅᴀɴs §e" + timer + "§as !");
                        pl.playSound(pl.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                        if (timer == 1){
                            List<Location> spawns = Murder.getInstance().getSpawn();
                            Location rSpawns = spawns.get(new Random().nextInt(spawns.size()));
                            pl.teleport(Objects.requireNonNullElseGet(rSpawns, () -> new Location(pl.getWorld(), 22, 16, 81)));
                        }
                    }
                    timer--;
                    return;
                }

                setStarted(true);
                for (Team team : Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getTeams())
                    team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                distributeRole(new ArrayList<>(getPls()));
                spawnChest();
                spawnPanne();
                spawnMoss();
                cancel();
            }
        }.runTaskTimer(Murder.getInstance(), 0, 20);
    }

    /** Logique de fin de partie. */
    public void onEnd() {
        if (!isStarted()) return;
        setStarted(false);

        List<UUID> savePls = new ArrayList<>(getPls());

        new BukkitRunnable() {
            private int timer = 5;

            @Override
            public void run() {
                if (timer > 0) {
                    for (UUID id : savePls) {
                        Player pl = Bukkit.getPlayer(id);
                        if (pl == null) continue;
                        pl.sendMessage(getMurder() + "§aʟᴀ ᴘᴀʀᴛɪᴇ sᴇ ᴛᴇʀᴍɪɴᴇʀᴀ ᴅᴀɴs §e" + timer + "§as !");
                        pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1f, 1f);
                    }
                    timer--;
                    return;
                }

                for (Team team : Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getTeams())
                    team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);

                roleMap.forEach((uuid, role) -> {
                    Player player = Bukkit.getPlayer(uuid);
                    String nom = player != null ? player.getName() : "Joueur inconnu";
                    Bukkit.broadcastMessage("§e" + nom + " §6était " + role.getName());
                });

                for (UUID id : savePls) {
                    Player pl = Bukkit.getPlayer(id);
                    if (pl != null) {
                        pl.getInventory().clear();
                        pl.setGameMode(GameMode.SURVIVAL);
                        pl.teleport(new Location(Bukkit.getWorld("world"), -47, 58, -278, 0f, 0f));
                    }
                }
                reset();
                cancel();
            }
        }.runTaskTimer(Murder.getInstance(), 0, 20);
    }

    /** Remet le jeu à zéro. */
    public void reset() {
        resetMoss();
        resetPanne();
        resetChest();
        resetDeathAS();
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

    // ============================================================
    //  KILLS ET CONDITIONS DE VICTOIRE
    // ============================================================

    /** Gère les kills durant la partie. */
    public void onKill(Player p, Player v) {
        /**
        for (UUID id : getPls()) {
            Player pl = Bukkit.getPlayer(id);
            if (pl == null) continue;
            pl.sendMessage(" ");
            pl.sendMessage("§c§l━━━━━━━━━━━━⚔━━━━━━━━━━━━");
            pl.sendMessage("§e" + v.getName() + " §6ᴀ ᴇ́ᴛᴇ́ ᴀᴛᴛᴀǫᴜᴇ́(ᴇ) !");
            pl.sendMessage("§6ɪʟ ᴇ́ᴛᴀɪᴛ §e" + getRole(v).getName());
            pl.sendMessage("§c§l━━━━━━━━━━━━━━━━━━━━━━━━━");
            pl.sendMessage(" ");
            pl.playSound(pl.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS, 1f, 1f);
        }
         */

        if (murderP.contains(v.getUniqueId())) {
            lastMurderName = v.getName();
            murderP.remove(v.getUniqueId());
        } else {
            innocent.remove(v.getUniqueId());
            detective.remove(v.getUniqueId());
        }

        if (getRole(v) == MRoles.FANTOME) {
            applyFantomeDeath(v);
        } else {
            v.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(getMurder() + "§e ᴠᴏᴜs ᴀᴠᴇᴢ ᴇ́ʟɪᴍɪɴᴇ́(ᴇ) §6" + v.getName());
            morts.add(setCorps(v));
            death.add(v.getUniqueId());
            checkWin();
        }
    }

    /** Gère les kills lors d'une réunion. */
    public void onReuKill(Player v) {
        if (murderP.contains(v.getUniqueId())) {
            lastMurderName = v.getName();
            murderP.remove(v.getUniqueId());
        } else {
            innocent.remove(v.getUniqueId());
            detective.remove(v.getUniqueId());
        }

        if (getRole(v) == MRoles.FANTOME) {
            applyFantomeDeath(v);
        } else {
            roleMap.remove(v.getUniqueId());
            v.setGameMode(GameMode.SPECTATOR);
            death.add(v.getUniqueId());
            resetDeathAS();
            checkWin();
        }
    }

    /** Applique l'effet de mort du Fantôme (factorisé pour éviter la duplication). */
    private void applyFantomeDeath(Player v) {
        ItemStack haunt = new ItemStack(Material.WITHER_ROSE);
        ItemMeta meta = haunt.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§8Malice");
            meta.setLore(List.of(
                    "§7ʜᴀɴᴛᴇ ᴜɴ ᴊᴏᴜᴇᴜʀ ᴘᴏᴜʀ ʟᴇs 10 ᴘʀᴏᴄʜᴀɪɴᴇs sᴇᴄᴏɴᴅᴇs",
                    "§7ᴜsᴀɢᴇ §8|§f ᴄʟɪǫᴜᴇ ᴅʀᴏɪᴛ"
            ));
            meta.setEnchantmentGlintOverride(true);
            haunt.setItemMeta(meta);
        }
        v.setInvisible(true);
        v.setGameMode(GameMode.ADVENTURE);
        v.getInventory().addItem(haunt);
    }

    /** Vérifie si la partie doit se terminer. */
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
            if (murderPlayer != null) murderName = murderPlayer.getName();

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

    // ============================================================
    //  DISTRIBUTION DES RÔLES ET ITEMS
    // ============================================================

    /** Distribue les rôles aux joueurs. */
    public void distributeRole(List<UUID> pls) {
        List<MRoles> roleList = new ArrayList<>();
        roleList.add(MRoles.CAPITAINE);
        roleList.add(MRoles.PIRATE_FOU);

        List<MRoles> pool = new ArrayList<>(activeRoles);
        Bukkit.getLogger().info("Pool avant shuffle : " + pool);
        Collections.shuffle(pool);
        Bukkit.getLogger().info("RoleList finale : " + roleList);

        for (int i = 2; i < pls.size(); i++) {
            int extraIndex = i - 2;
            roleList.add(extraIndex < pool.size() ? pool.get(extraIndex) : MRoles.PASSAGER);
        }

        Collections.shuffle(roleList);

        for (int i = 0; i < pls.size(); i++) {
            UUID joueur  = pls.get(i);
            MRoles role  = roleList.get(i);

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

    /** Donne les items correspondant au rôle. */
    public void giveItems(Player p, MRoles role) {
        p.getInventory().clear();

        switch (role) {
            case CAPITAINE  -> p.getInventory().setItem(1, Murder.getInstance().getCustomItems().dagger());
            case CLANDESTIN -> p.getInventory().setItem(1, Murder.getInstance().getCustomItems().furtivite());
            case FRONTIERE  -> p.getInventory().setItem(1, Murder.getInstance().getCustomItems().prison());
            case SIRENE     -> p.getInventory().setItem(1, Murder.getInstance().getCustomItems().sirene());
            case PIRATE_FOU -> p.getInventory().setItem(1, Murder.getInstance().getCustomItems().sabre());
            case MATELOT    -> p.getInventory().setItem(1, Murder.getInstance().getCustomItems().bross());
            case MECANO     -> p.getInventory().setItem(1, Murder.getInstance().getCustomItems().wrench());
            case CANONNIER -> {
                p.getInventory().setItem(8,  new ItemStack(Material.IRON_BLOCK, 2));
                p.getInventory().setItem(23, new ItemStack(Material.GUNPOWDER, 1));
                p.getInventory().setItem(12, new ItemStack(Material.NETHERITE_INGOT, 6));
            }
            case CUISINER -> {
                p.getInventory().setItem(9,  new ItemStack(Material.CHORUS_FLOWER, 1));
                p.getInventory().setItem(18, new ItemStack(Material.CHORUS_FRUIT, 7));
                p.getInventory().setChestplate(new ItemStack(Material.STICK, 1));
            }
            default -> {}
        }

        p.updateInventory();
    }

    /** Annonce le rôle au joueur. */
    public void annonceR(UUID p, MRoles roles) {
        Player pl = Bukkit.getPlayer(p);
        assert pl != null;
        if (!getPls().contains(pl.getUniqueId())) return;

        pl.sendMessage(" ");
        pl.sendMessage("§c⚔ §eTu est " + roles.getName());
        pl.sendMessage("§c⚔ §r" + roles.getDescription());
        pl.sendMessage(" ");
        pl.sendTitle("§7Tu est " + roles.getName(), roles.getDescription(), 10, 30, 10);
        pl.playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.BLOCKS, 0.5f, 1f);
    }

    // ============================================================
    //  RÉUNION ET VOTES
    // ============================================================

    /** Lance la logique de réunion. */
    public void reuLogic() {
        new BukkitRunnable() {
            private int timer = 61;

            @Override
            public void run() {
                if (timer == 61) setReunion(true);

                if (timer == 60) {
                    for (UUID id : getPls()) {
                        Player pl = Bukkit.getPlayer(id);
                        if (pl != null) {
                            pl.getInventory().setItem(4, new ItemBuilder(Material.ECHO_SHARD)
                                    .name("§9ᴄʜᴏɪsɪ ᴜɴᴇ ᴘᴇʀsᴏɴɴᴇ ᴘᴏᴜʀ ǫᴜɪ ᴠᴏᴛᴇʀ").build());
                            pl.teleport(new Location(pl.getWorld(), 27, -24, 64));
                        }
                    }
                }

                if (timer == 3) {
                    for (UUID id : getPls()) {
                        Player pl = Bukkit.getPlayer(id);
                        if (pl != null) {
                            pl.sendMessage("§a ʟᴇs ʀᴇ́sᴜʟᴛᴀᴛs ᴠᴏᴜs sᴇʀᴏɴᴛ ᴛʀᴀɴsᴍɪs ᴅ'ɪᴄ̧ɪ ᴘᴇᴜ !");
                            pl.playSound(pl.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                        }
                    }
                }

                if (timer == 0) { endVote(); cancel(); return; }

                for (UUID id : getPls()) {
                    Player pl = Bukkit.getPlayer(id);
                    if (pl != null) {
                        if (isReunion()) ActionBar.send(pl, "§6ᴛᴇᴍᴘs ʀᴇsᴛᴀɴᴛ: §f" + timer);
                        else { cancel(); return; }
                    }
                }

                timer--;
            }
        }.runTaskTimer(Murder.getInstance(), 0, 20L);
    }

    /** Termine le vote et applique le résultat. */
    public void endVote() {
        if (!isReunion()) return;

        UUID mostVoted = getVotes().values().stream()
                .max(Comparator.comparingInt(uuid -> Collections.frequency(getVotes().values(), uuid)))
                .orElse(null);

        for (UUID uuid : getPls()) {
            Player pl = Bukkit.getPlayer(uuid);
            if (pl == null) continue;

            pl.getInventory().setItem(4, new ItemStack(Material.AIR));
            pl.teleport(new Location(pl.getWorld(), 22, 16, 81));
            pl.playSound(pl.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);

            pl.sendMessage(" ");
            pl.sendMessage("§c§l━━━━━━━━━━━━━━━━━━━━━━━━");
            if (mostVoted == null || getVotes().isEmpty()) {
                pl.sendMessage("§cᴘᴇʀsᴏɴɴᴇ ɴ'ᴀ ᴠᴏᴛᴇ́, ᴘᴇʀsᴏɴɴᴇ ɴ'ᴇsᴛ ᴇ́ʟɪᴍɪɴᴇ́ !");
            } else {
                Player mostVotedPlayer = Bukkit.getPlayer(mostVoted);
                pl.sendMessage(mostVotedPlayer != null
                        ? "§e" + mostVotedPlayer.getName() + " §6ᴀ̀ ᴇ́ᴛᴇ́ ᴇ́ʟɪᴍɪɴᴇ́(ᴇ) ɪssᴜ ᴅᴇ ᴄᴇ ᴄᴏɴsᴇɪʟ !"
                        : "§c ᴇʀʀᴇᴜʀ : ʟᴇ ᴊᴏᴜᴇᴜʀ ɴ'ᴇsᴛ ᴘʟᴜs ʟᴀ̀");
            }
            pl.sendMessage("§c§l━━━━━━━━━━━━━━━━━━━━━━━━");
            pl.sendMessage(" ");
        }

        if (mostVoted != null && !getVotes().isEmpty()) {
            Player mostVotedPlayer = Bukkit.getPlayer(mostVoted);
            if (mostVotedPlayer != null) onReuKill(mostVotedPlayer);
        }

        getVotes().clear();
        getAvote().clear();
        setReunion(false);
    }

    // ============================================================
    //  RÔLE CLANDESTIN (récompense)
    // ============================================================

    public void reward(Player p) {
        p.getInventory().clear();

        for (Player pl : Bukkit.getOnlinePlayers()) {
            pl.hidePlayer(Murder.getInstance(), p);
        }

        new BukkitRunnable() {
            private int timer = 30;

            @Override
            public void run() {
                if (timer == 0 || isReunion()) {
                    p.sendMessage("§cVotre invisibilitée est terminée");
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        pl.showPlayer(Murder.getInstance(), p);
                    }
                    cancel();
                    return;
                }

                ActionBar.send(p, "§aVous êtes invisible pendant encore: §e" + timer + "§a secondes !");
                timer--;
            }
        }.runTaskTimer(Murder.getInstance(), 0, 20L);

        p.sendMessage("§eBRAVO ! §aTu est complètement invisible pour les 30 prochaines secondes !");
        p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.BLOCKS, 1f, 1f);
    }

    // ============================================================
    //  RÔLE MECANO (réparation)
    // ============================================================

    public void onRepair(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 10, false, false, false));

        new BukkitRunnable() {
            private int timer = 5;

            @Override
            public void run() {
                if (timer == 0) { cancel(); return; }
                timer--;
            }
        }.runTaskTimer(Murder.getInstance(), 0, 20L);
    }

    // ============================================================
    //  CONFIGURATION DES BLOCS SPÉCIAUX
    // ============================================================

    public void setChest(Player p) {
        if (!p.getWorld().equals(Bukkit.getWorld("world"))) return;
        int chest = Murder.getInstance().getConfig().getInt("murder.chestCount", 0) + 1;
        Murder.getInstance().getConfig().set("murder.chestCount", chest);
        Murder.getInstance().getConfig().set("murder.chest" + chest, p.getLocation());
        Murder.getInstance().saveConfig();
        p.sendMessage("§aLe coffre §b#" + chest + " §aà bien été ajouté en §bx: §f" + p.getLocation().getBlockX() + "§f, §by: §f" + p.getLocation().getBlockY() + "§f, §bz: §f" + p.getLocation().getBlockZ());
    }

    public void setMoss(Player p) {
        if (!p.getWorld().equals(Bukkit.getWorld("world"))) return;
        int moss = Murder.getInstance().getConfig().getInt("murder.mossCount", 0) + 1;
        Murder.getInstance().getConfig().set("murder.mossCount", moss);
        Murder.getInstance().getConfig().set("murder.moss" + moss, p.getLocation());
        Murder.getInstance().saveConfig();
        p.sendMessage("§aLa saletée §b#" + moss + " §aà bien été ajouté en §bx: §f" + p.getLocation().getBlockX() + "§f, §by: §f" + p.getLocation().getBlockY() + "§f, §bz: §f" + p.getLocation().getBlockZ());
    }

    public void setPanne(Player p) {
        if (!p.getWorld().equals(Bukkit.getWorld("world"))) return;
        int panne = Murder.getInstance().getConfig().getInt("murder.panneCount", 0) + 1;
        Murder.getInstance().getConfig().set("murder.panneCount", panne);
        Murder.getInstance().getConfig().set("murder.panne" + panne, p.getLocation());
        Murder.getInstance().saveConfig();
        p.sendMessage("§aLa panne §b#" + panne + " §aà bien été ajouté en §bx: §f" + p.getLocation().getBlockX() + "§f, §by: §f" + p.getLocation().getBlockY() + "§f, §bz: §f" + p.getLocation().getBlockZ());
    }

    public void setSpawn(Player p) {
        if (!p.getWorld().equals(Bukkit.getWorld("world"))) return;
        int spawn = Murder.getInstance().getConfig().getInt("murder.spawnCount", 0) + 1;
        Murder.getInstance().getConfig().set("murder.spawnCount", spawn);
        Murder.getInstance().getConfig().set("murder.spawn" + spawn, p.getLocation());
        Murder.getInstance().saveConfig();
        p.sendMessage("§aLe point de spawn §b#" + spawn + " §aà bien été ajouté en §bx: §f" + p.getLocation().getBlockX() + "§f, §by: §f" + p.getLocation().getBlockY() + "§f, §bz: §f" + p.getLocation().getBlockZ());
    }

    // ============================================================
    //  ARMOR STANDS
    // ============================================================

    public void setBuzzer(Player p) {
        ArmorStand as = p.getWorld().spawn(p.getLocation(), ArmorStand.class);
        as.setVisible(false);
        as.setGravity(false);
        as.setSmall(true);
        as.setCustomName("§c§lʙᴜᴢᴢᴇʀ ᴅ'ᴜʀɢᴇɴᴄᴇ");
        as.setCustomNameVisible(true);
    }

    public void setRejoin(Player p) {
        ArmorStand as = p.getWorld().spawn(p.getLocation(), ArmorStand.class);
        as.setVisible(false);
        as.setGravity(false);
        as.setSmall(true);
        as.setCustomName("§a§lʀᴇᴊᴏɪɴᴅʀᴇ ʟᴀ ᴘᴀʀᴛɪᴇ");
        as.setCustomNameVisible(true);
    }

    public ArmorStand setCorps(Player p) {
        ArmorStand as = p.getWorld().spawn(p.getLocation(), ArmorStand.class);
        as.setVisible(false);
        as.setGravity(false);
        as.setCustomName("§c§l\uD83D\uDC80 §8| §cIçi repose §6" + p.getName());
        as.setCustomNameVisible(true);
        return as;
    }

    // ============================================================
    //  SPAWN DES ÉLÉMENTS DE JEU
    // ============================================================

    public void spawnChest() {
        new BukkitRunnable() {
            private int timer = 90;

            @Override
            public void run() {
                if (!isStarted()) { Bukkit.getLogger().info("§cLe spawn des coffres a été interrompu"); cancel(); return; }

                if (timer == 30 || timer == 60 || timer == 90) {
                    List<Location> chest = Murder.getInstance().getChests();
                    if (chest.isEmpty()) { Bukkit.getLogger().warning("Aucun coffre n'est configuré pour le Murder."); cancel(); return; }
                    Location randomChest = chest.get(new Random().nextInt(chest.size()));
                    randomChest.setYaw(0);
                    Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(randomChest).setType(Material.WARPED_SLAB);
                    Bukkit.broadcastMessage("§aᴜɴ ᴄᴏғғʀᴇ ᴠɪᴇɴᴛ ᴅ'ᴀᴘᴘᴀʀᴀɪ̂ᴛʀᴇ !");
                    Bukkit.getOnlinePlayers().forEach(pl -> pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1f, 1f));
                }

                if (timer == 0) { cancel(); return; }
                timer--;
            }
        }.runTaskTimer(Murder.getInstance(), 0, 20L);
    }

    public void spawnMoss() {
        new BukkitRunnable() {
            private int timer = 6;

            @Override
            public void run() {
                if (timer >= 1) {
                    List<Location> moss = Murder.getInstance().getMoss();
                    if (moss.isEmpty()) { Bukkit.getLogger().warning("Aucune mousse n'est configurée pour le Murder."); cancel(); return; }
                    Objects.requireNonNull(Bukkit.getWorld("world"))
                            .getBlockAt(moss.get(new Random().nextInt(moss.size())))
                            .setType(Material.MOSS_CARPET);
                }
                if (timer == 0) { Bukkit.getLogger().info("§aLe spawn des moss s'est bien passé"); cancel(); return; }
                timer--;
            }
        }.runTaskTimer(Murder.getInstance(), 0, 5L);
    }

    public void spawnPanne() {
        new BukkitRunnable() {
            private int timer = 6;

            @Override
            public void run() {
                if (timer >= 1) {
                    List<Location> panne = Murder.getInstance().getPanne();
                    if (panne.isEmpty()) { Bukkit.getLogger().warning("Aucune panne n'est configurée pour le Murder."); cancel(); return; }
                    Objects.requireNonNull(Bukkit.getWorld("world"))
                            .getBlockAt(panne.get(new Random().nextInt(panne.size())))
                            .setType(Material.LIME_CONCRETE_POWDER);
                }
                if (timer == 0) { Bukkit.getLogger().info("§aLe spawn des pannes s'est bien passé"); cancel(); return; }
                timer--;
            }
        }.runTaskTimer(Murder.getInstance(), 0, 5L);
    }

    // ============================================================
    //  RESET DES BLOCS
    // ============================================================

    public void resetChest() {
        for (Location chest : Murder.getInstance().getChests()) {
            if (Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(chest).getType() == Material.WARPED_SLAB)
                Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(chest).setType(Material.AIR);
        }
    }

    public void resetMoss() {
        for (Location moss : Murder.getInstance().getMoss()) {
            if (Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(moss).getType() == Material.MOSS_CARPET)
                Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(moss).setType(Material.AIR);
        }
    }

    public void resetPanne() {
        for (Location panne : Murder.getInstance().getPanne()) {
            if (Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(panne).getType() == Material.LIME_CONCRETE_POWDER)
                Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(panne).setType(Material.YELLOW_CONCRETE_POWDER);
        }
    }

    public void resetDeathAS() {
        for (ArmorStand mort : morts) mort.remove();
        morts.clear();
    }

    // ============================================================
    //  TAB LIST
    // ============================================================

    public void updateTabList(Player p) {
        MGrades rank    = Murder.getInstance().getRankManager().getRank(p.getUniqueId());
        String teamName = String.format("%02d_%s", 10 - rank.getPower(), rank.name());

        for (Player online : Bukkit.getOnlinePlayers()) {
            Scoreboard sb = online.getScoreboard();

            // Retirer le joueur de toutes les anciennes teams
            for (Team t : sb.getTeams()) {
                t.removeEntry(p.getName());
            }

            Team team = sb.getTeam(teamName);
            if (team == null) team = sb.registerNewTeam(teamName);
            team.setPrefix(rank.getPrefix());
            team.addEntry(p.getName());
        }

        p.setPlayerListName(rank.getPrefix() + p.getName());
    }

    // ============================================================
    //  VOICE CHAT
    // ============================================================

    public void setMuteOn(boolean mute) {
        this.isMuteOn = mute;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Murder.getInstance().getRank(player).hasPower(MGrades.HOST)) continue;

            PermissionAttachment attachment = player.addAttachment(Murder.getInstance());

            attachment.setPermission("voicechat.speak", !mute);
            attachment.setPermission("voicechat.listen", !mute);
        }
    }


    // ============================================================
    //  GETTERS
    // ============================================================

    public boolean isStarted()              { return started; }
    public boolean isReunion()              { return reunion; }
    public String getMurder()               { return "§cMurder: "; }
    public String getLastMurderName()       { return lastMurderName; }
    public List<UUID> getPls()              { return pls; }
    public List<UUID> getDeath()            { return death; }
    public List<UUID> getAvote()            { return avote; }
    public List<UUID> getMurderP()          { return murderP; }
    public List<UUID> getInnocent()         { return innocent; }
    public List<UUID> getDetective()        { return detective; }
    public List<UUID> getHasBuzzed()        { return hasBuzzed; }
    public List<MRoles> getActiveRoles()    { return activeRoles; }
    public HashMap<UUID, UUID> getVotes()   { return votes; }
    public Map<UUID, MRoles> getRoleMap()   { return roleMap; }
    public RankManager getRankManager()     { return rankManager; }
    public boolean isMuteOn()               { return isMuteOn; }
    public MRoles getRole(Player p)         { return roleMap.getOrDefault(p.getUniqueId(), MRoles.PASSAGER); }

    // ============================================================
    //  SETTERS
    // ============================================================

    public void setStarted(boolean started)             { this.started = started; }
    public void setReunion(boolean reunion)             { this.reunion = reunion; }
    public void setLastMurderName(String lastMurderName){ this.lastMurderName = lastMurderName; }
}