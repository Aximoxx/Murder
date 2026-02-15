package Aximox.murder;

import Aximox.murder.grade.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MManager {
    private String murderName = "§c⚔️ Inconnu";
    private String lastMurderName = "§c⚔️ Inconnu";
    private final List<UUID>pls = new ArrayList<>();
    private final List<UUID>murderP = new ArrayList<>();
    private final List<UUID>innocent = new ArrayList<>();
    private final RankManager rankManager = new RankManager();
    private boolean started;

    /**
     *Cette méthode sert à gérer les personnes qui rejoignent la partie.
     **/
    public void onJoin(Player p){
        if (getPls().contains(p.getUniqueId())){
            p.sendMessage(getMurder() + "§eMalheureusement, vous êtes déjà dans la partie..");
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1f, 1f);
            return;
        }

        if (isStarted()){
            p.sendMessage(getMurder() + "§eUne partie est déjà en cours, attendez la prochaine !");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 1f, 1f);
        }

        for (UUID id : pls){
            Player pls = Bukkit.getPlayer(id);
            if (pls != null){
                pls.sendMessage(getMurder() + "§6" + p.getName() + "§e est prêt à en découdre !");
                pls.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.BLOCKS, 1f, 1f);
            }
        }

        getPls().add(p.getUniqueId());

        p.sendMessage(getMurder() + "§eÊtes vous prêt à MASTERMINDER tout le monde ?");
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
        //Todo: Le tp à la réu
    }

    /**
     *Cette méthode sert gérer les personnes qui quittent la partie.
     **/
    public void onQuit(Player p){
        if (!getPls().contains(p.getUniqueId())){
            p.sendMessage(getMurder() + "§eMalheureusement, vous n'êtes dans aucune partie..");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.BLOCKS, 1f, 1f);
            return;
        }

        p.sendMessage(getMurder() + "§eIl faut croire que vous n'êtes pas le MASTERMIND que l'on ma vendu..");
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, SoundCategory.BLOCKS, 1f, 1f);
        //Todo: Le tp au lobby

        getPls().remove(p.getUniqueId());

        for (UUID id : pls){
            Player pls = Bukkit.getPlayer(id);
            if (pls != null){
                pls.sendMessage(getMurder() + "§6" + p.getName() + "§e s'est trop fait manipulé(e) !");
                pls.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS, 1f, 1f);
            }
        }
    }

    /**
     *Cette méthode sert de logique de début.
     *Elle est appelée à chaque début de partie.
     **/
    public void onStart(Player p){
        if (!getPls().contains(p.getUniqueId()) || isStarted()) return;
        if (getPls().size() < 3){
            p.sendMessage(getMurder() + "§cIl faut au minimum 3 joueurs pour lancer une partie ! §7(§e" + getPls().size() + "§7/§63§7)");
            return;
        }

        setStarted(true);

        /*
         Timer pour fluidifier le déroulement de la partie
         */
        new BukkitRunnable() {
            private int timer = 5;

            @Override
            public void run() {
                for (UUID id : getPls()){
                    Player pls = Bukkit.getPlayer(id);
                    if (pls != null) {

                        if (timer <= 5 && timer >= 1){
                            pls.sendMessage(getMurder() + "§aLa partie démarre dans §e" + timer + "§as !");
                            pls.playSound(pls.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                        }

                        if (timer == 0){
                            pls.sendTitle("§a§lBonne chance !", "§7Vous en aurez besoin", 10, 30, 10);
                            pls.playSound(pls.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1f, 1f);

                            distributeRole(new ArrayList<>(getPls()));
                            //Todo: Les tps sur la map

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
     *Cette méthode sert de logique de fin de partie.
     *C'est elle qui est appelée à la fin de la game.
     **/
    public void onEnd(){
        if (!isStarted()) return;
        List<UUID> savePls = new ArrayList<>(getPls());

        new BukkitRunnable() {
            private int timer = 5;
            @Override
            public void run() {
                for (UUID id : getPls()){
                    Player pls = Bukkit.getPlayer(id);
                    if (pls != null) {

                        if (timer <= 5 && timer >= 1){
                            pls.sendMessage(getMurder() + "§aLa partie se termineras dans §e" + timer + "§as !");
                            pls.playSound(pls.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1f, 1f);
                        }

                        if (timer == 0){
                            //Todo: Les tps au lobby

                            //Reset la partie
                            setStarted(false);
                            reset();

                            //Remet tous les joueurs dans une autre partie
                            getPls().clear();
                            getPls().addAll(savePls);

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
     *Cette méthode sert à gérer les "kills" fait durant la partie.
     **/
    public void onKill(Player p){
        if (murderP.contains(p.getUniqueId())) {
            lastMurderName = p.getName();
            murderP.remove(p.getUniqueId());
        } else innocent.remove(p.getUniqueId());
        checkWin();
    }

    /**
    *Cette méthode sert à vérifier si la partie se termine ou pas.
    *Elle fonctionne comme ça : Si tous les innocents sont mort → Le murder gagne
                               Si Le Murder meurt → Les innocents gagnent.
    **/
    public void checkWin(){

        if (murderP.isEmpty()){
            for (UUID id : getPls()){
                Player p = Bukkit.getPlayer(id);
                if (p == null) continue;

                p.sendMessage(" ");
                p.sendMessage("§2§l━━━━━━━━━━━━━━━━━━━━━━━━");
                p.sendMessage("§a Les Innocent ont gagnés !");
                p.sendMessage("§7 Le murder était §4§l" + getLastMurderName());
                p.sendMessage("§2§l━━━━━━━━━━━━━━━━━━━━━━━━");
                p.sendMessage(" ");

                p.playSound(p.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, SoundCategory.BLOCKS, 1f, 1f);
                onEnd();
            }
        } else if (innocent.isEmpty()) {
            Player murderPlayer = Bukkit.getPlayer(murderP.getFirst());

            if (murderPlayer != null) {
                murderName = murderPlayer.getName();
            }

            for (UUID id : getPls()){
                Player p = Bukkit.getPlayer(id);
                if (p == null) continue;

                p.sendMessage(" ");
                p.sendMessage("§4§l━━━━━━━━━━━━━━━━━━━━━━━━");
                p.sendMessage("§c Le Murder a gagné(e) !");
                p.sendMessage("§7 Il s'agissait de §4§l" + murderName);
                p.sendMessage("§4§l━━━━━━━━━━━━━━━━━━━━━━━━");
                p.sendMessage(" ");

                p.playSound(p.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, SoundCategory.BLOCKS, 1f, 1f);
                onEnd();
            }
        }else {
            Bukkit.broadcastMessage(getMurder() + "§cCeci n'aurait pas dû se produire..");
        }
    }

    /**
     *Cette méthode sert à donner les rôles aux joueurs.
     **/
    public void distributeRole(List<UUID> pls){
        List<MRoles> roles = new ArrayList<>();
        roles.add(MRoles.MURDER);
        roles.add(MRoles.DETECTIVE);

        for (int i = 1; i < pls.size(); i++){
            roles.add(MRoles.Innocent);
        }

        Collections.shuffle(roles);

        for (int i = 0; i < pls.size(); i++) {
            UUID joueur = pls.get(i);
            MRoles role = roles.get(i);

            if (role == MRoles.MURDER){
                murderP.add(joueur);
            }else {
                innocent.add(joueur);
            }

            Player pl = Bukkit.getPlayer(joueur);
            if (pl != null){
                giveItems(pl, role);
                annonceR(joueur, role);
            }
        }
    }

    public void giveItems(Player p, MRoles role) {
        p.getInventory().clear();

        if (role == MRoles.MURDER) {
            ItemStack sword = new ItemStack(Material.IRON_SWORD);
            ItemMeta swordMeta = sword.getItemMeta();
            assert swordMeta != null;
            swordMeta.setDisplayName("§c§l⚔ Couteau du Murder");
            swordMeta.setUnbreakable(true);
            sword.setItemMeta(swordMeta);

            p.getInventory().setItem(0, sword);

        } else if (role == MRoles.DETECTIVE) {
            ItemStack bow = new ItemStack(Material.BOW);
            ItemMeta bowMeta = bow.getItemMeta();
            assert bowMeta != null;
            bowMeta.setUnbreakable(true);
            bowMeta.addEnchant(Enchantment.INFINITY, 1, true);
            bowMeta.setDisplayName("§a§l🔫 Pistolet de la Justice");
            bowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
            bow.setItemMeta(bowMeta);

            p.getInventory().setItem(0, bow);
            p.getInventory().setItem(9, new ItemStack(Material.ARROW));
        }

        p.updateInventory();
    }

    /**
     *Cette méthode sert à expliquer le rôle aux joueurs.
     **/
    public void annonceR(UUID p, MRoles roles){
        Player pls = Bukkit.getPlayer(p);
        assert pls != null;

        if (!getPls().contains(pls.getUniqueId()) || !isStarted()) return;

        pls.sendTitle(roles.getName(), roles.getDescription(), 10, 30, 10);
        pls.sendMessage("§c⚔️ Tu est " + roles.getName());
        pls.sendMessage("§c⚔️ §r" + roles.getDescription());
        pls.playSound(pls.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.BLOCKS, 0.5f, 1f);
    }

    /**
     * Cette méthode sert à remettre le jeu de 0.
     * **/
    public void reset(){
        pls.clear();
        murderP.clear();
        innocent.clear();
        started = false;
        lastMurderName = "§c⚔️ Inconnu";
    }

    //Guetter
    public List<UUID> getPls() {
        return pls;
    }
    public boolean isStarted() {
        return started;
    }
    public String getMurder() {
        return "§cMurder: ";
    }
    public List<UUID> getMurderP() {
        return murderP;
    }
    public RankManager getRankManager() {
        return rankManager;
    }
    public String getLastMurderName() {
        return lastMurderName;
    }

    //Setter
    public void setStarted(boolean started) {
        this.started = started;
    }
}
