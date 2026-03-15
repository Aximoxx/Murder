package Aximox.murder;

import Aximox.murder.grade.RankManager;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MManager {
    private boolean started;
    private String murderName = "§c⚔️ Inconnu";
    private String lastMurderName = "§c⚔️ Inconnu";
    private final List<UUID> pls = new ArrayList<>();
    private final List<UUID> murderP = new ArrayList<>();
    private final List<UUID> innocent = new ArrayList<>();
    private final List<UUID> detective = new ArrayList<>();
    private final Map<UUID, MRoles> roleMap = new HashMap<>();
    private final RankManager rankManager = new RankManager();

    /**
     * Cette méthode sert à gérer les personnes qui rejoignent la partie.
     **/
    public void onJoin(Player p) {
        if (getPls().contains(p.getUniqueId())) {
            p.sendMessage(getMurder() + "§eMalheureusement, vous êtes déjà dans la partie..");
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1f, 1f);
            return;
        }

        if (isStarted()) {
            p.sendMessage(getMurder() + "§eUne partie est déjà en cours, attendez la prochaine !");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 1f, 1f);
        }

        for (UUID id : pls) {
            Player pls = Bukkit.getPlayer(id);
            if (pls != null) {
                pls.sendMessage(getMurder() + "§6" + p.getName() + "§e est prêt à en découdre !");
                pls.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.BLOCKS, 1f, 1f);
            }
        }

        getPls().add(p.getUniqueId());

        p.getInventory().clear();
        p.sendMessage(getMurder() + "§eÊtes vous prêt à MASTERMINDER tout le monde ?");
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
    }

    /**
     * Cette méthode sert gérer les personnes qui quittent la partie.
     **/
    public void onQuit(Player p) {
        if (!getPls().contains(p.getUniqueId())) {
            p.sendMessage(getMurder() + "§eMalheureusement, vous n'êtes dans aucune partie..");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 1f, 1f);
            return;
        }

        p.sendMessage(getMurder() + "§eIl faut croire que vous n'êtes pas le MASTERMIND que l'on ma vendu..");
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, SoundCategory.BLOCKS, 1f, 1f);

        getPls().remove(p.getUniqueId());

        for (UUID id : pls) {
            Player pls = Bukkit.getPlayer(id);
            if (pls != null) {
                pls.sendMessage(getMurder() + "§6" + p.getName() + "§e s'est trop fait manipulé(e) !");
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
            p.sendMessage(getMurder() + "§cIl faut au minimum 3 joueurs pour lancer une partie ! §7(§e" + getPls().size() + "§7/§63§7)");
            return;
        }

        setStarted(true);

        new BukkitRunnable() {
            private int timer = 5;

            @Override
            public void run() {
                for (UUID id : getPls()) {
                    Player pls = Bukkit.getPlayer(id);
                    if (pls != null) {
                        if (timer <= 5 && timer >= 1) {
                            pls.sendMessage(getMurder() + "§aLa partie démarre dans §e" + timer + "§as !");
                            pls.playSound(pls.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                        }

                        if (timer == 0) {
                            pls.sendTitle("§a§lBonne chance !", "§7Vous en aurez besoin", 10, 30, 10);
                            pls.playSound(pls.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1f, 1f);

                            distributeRole(new ArrayList<>(getPls()));
                            cancel();
                            return;
                        }
                    }
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
                if (timer >= 1) {
                    for (UUID id : getPls()) {
                        Player pls = Bukkit.getPlayer(id);
                        if (pls != null) {
                            pls.sendMessage(getMurder() + "§aLa partie se terminera dans §e" + timer + "§as !");
                            pls.playSound(pls.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1f, 1f);
                        }
                    }
                    timer--;
                    return;
                }

                for (UUID id : savePls) {
                    Player pls = Bukkit.getPlayer(id);
                    if (pls != null) {
                        pls.getInventory().clear();
                        pls.setGameMode(GameMode.SURVIVAL);
                        pls.teleport(new Location(Bukkit.getWorld("world"), 0, 100, 0));
                    }
                }

                reset();
                cancel();
                getPls().clear();
                setStarted(false);
                getPls().addAll(savePls);
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
            pls.sendMessage("§e" + v.getName() + " §6a été attaqué(e) !");
            pls.sendMessage("§6Il était §e" + getRole(v).getName());
            pls.sendMessage("§c§l━━━━━━━━━━━━━━━━━━━━━━━━━");
            pls.sendMessage(" ");

            pls.playSound(pls.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS, 1f, 1f);
        }

        if (getRole(v) == MRoles.FANTOME) {
            ItemStack haunt = new ItemStack(Material.WITHER_ROSE);
            ItemMeta meta = haunt.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§8Malice");
                meta.setLore(List.of("§7Hante un joueur pour les 10 prochaines secondes", "§7Usage §8| §fClique droit"));
                meta.setEnchantmentGlintOverride(true);
                haunt.setItemMeta(meta);
            }
            v.getInventory().addItem(haunt);
        }

        if (murderP.contains(v.getUniqueId())) {
            lastMurderName = v.getName();
            murderP.remove(v.getUniqueId());
        } else {
            innocent.remove(v.getUniqueId());
            detective.remove(v.getUniqueId());
        }

        roleMap.remove(v.getUniqueId());
        v.setGameMode(GameMode.SPECTATOR);
        p.sendMessage(getMurder() + "§eVous avez éliminé(e) §6" + v.getName());
        checkWin();
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
                p.sendMessage("§a Les Innocents ont gagnés !");
                p.sendMessage("§7 Le murder était §4§l" + getLastMurderName());
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
                p.sendMessage("§c Le Murder a gagné(e) !");
                p.sendMessage("§7 Il s'agissait de §4§l" + murderName);
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
        roleList.add(MRoles.PIRATE_FOU);

        for (int i = 2; i < pls.size(); i++) {
            if (i == 2) {
                roleList.add(MRoles.SIRENE);
            } else if (i == 3) {
                roleList.add(MRoles.CLANDESTIN);
            } else if (i == 4) {
                roleList.add(MRoles.TRESOR);
            } else if (i == 5) {
                roleList.add(MRoles.FANTOME);
            } else if (i == 6) {
                roleList.add(MRoles.VIGIE);
            } else {
                roleList.add(MRoles.Innocent);
            }
        }

        Collections.shuffle(roleList);

        for (int i = 0; i < pls.size(); i++) {
            UUID joueur = pls.get(i);
            MRoles role = roleList.get(i);

            roleMap.put(joueur, role); // ← stocke le vrai rôle

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

    /**
     * Cette méthode sert à distribuer les items aux rôles.
     **/
    public void giveItems(Player p, MRoles role) {
        p.getInventory().clear();

        if (role == MRoles.CAPITAINE) {
            ItemStack sword = new ItemStack(Material.IRON_SWORD);
            ItemMeta swordMeta = sword.getItemMeta();
            assert swordMeta != null;
            swordMeta.setDisplayName("§c§l🔪 Poignard du Capitaine");
            swordMeta.setUnbreakable(true);
            sword.setItemMeta(swordMeta);
            p.getInventory().setItem(0, sword);

        } else if (role == MRoles.PIRATE_FOU) {
            ItemStack bow = new ItemStack(Material.WOODEN_SWORD);
            ItemMeta bowMeta = bow.getItemMeta();
            assert bowMeta != null;
            bowMeta.setUnbreakable(true);
            bowMeta.addEnchant(Enchantment.INFINITY, 1, true);
            bowMeta.setDisplayName("§a§l⚔ Sabre du Pirate");
            bowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
            bow.setItemMeta(bowMeta);
            p.getInventory().setItem(0, bow);
            p.getInventory().setItem(9, new ItemStack(Material.ARROW));

        } else if (role == MRoles.CLANDESTIN) {
            ItemStack invis = new ItemStack(Material.AMETHYST_SHARD);
            ItemMeta invisMeta = invis.getItemMeta();
            if (invisMeta != null) {
                invisMeta.setDisplayName("§fInvisibilité");
                invisMeta.setLore(List.of("§7Un clique droit, et §fPOUF"));
                invisMeta.setEnchantmentGlintOverride(true);
                invis.setItemMeta(invisMeta);
            }
            p.getInventory().addItem(invis);

        } else if (role == MRoles.SIRENE) {
            ItemStack voice = new ItemStack(Material.SUNFLOWER);
            ItemMeta vmeta = voice.getItemMeta();
            if (vmeta != null) {
                vmeta.setDisplayName("§dChant de la Sirène");
                vmeta.setEnchantmentGlintOverride(true);
                vmeta.setLore(List.of("§7Met slowness à tout les joueurs dans un rayon de 5 blocks pendant 10 secondes", "§7Usage §8| §fClique droit"));
                vmeta.setCustomModelData(3);
                voice.setItemMeta(vmeta);
            }
            p.getInventory().addItem(voice);
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

        pls.sendTitle("§7Tu est" + roles.getName(), roles.getDescription(), 10, 30, 10);
        pls.playSound(pls.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.BLOCKS, 0.5f, 1f);
    }

    /**
     * Cette méthode sert à récupérer le vrai rôle d'un joueur.
     **/
    public MRoles getRole(Player p) {
        return roleMap.getOrDefault(p.getUniqueId(), MRoles.Innocent);
    }

    /**
     * Cette méthode sert à remettre le jeu de 0.
     **/
    public void reset() {
        pls.clear();
        murderP.clear();
        innocent.clear();
        detective.clear();
        roleMap.clear();
        started = false;
        lastMurderName = "§c⚔️ Inconnu";
    }

    // Getters
    public List<UUID> getPls() { return pls; }
    public boolean isStarted() { return started; }
    public String getMurder() { return "§cMurder: "; }
    public List<UUID> getMurderP() { return murderP; }
    public List<UUID> getInnocent() { return innocent; }
    public List<UUID> getDetective() { return detective; }
    public RankManager getRankManager() { return rankManager; }
    public String getLastMurderName() { return lastMurderName; }

    // Setters
    public void setStarted(boolean started) { this.started = started; }
}