package Aximox.murder;

import Aximox.murder.utils.ActionBar;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MListener implements Listener {
    private boolean cell = true;
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

        p.teleport(new Location(p.getWorld(), -47, 58, -278, 0f, 0f));
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

    @EventHandler
    public void onActivate(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!manager.getPls().contains(p.getUniqueId()) || !manager.isStarted()) return;

        ItemStack item = e.getItem();
        if (item == null) return;

        e.setCancelled(true);

        if (manager.getRole(p).equals(MRoles.FANTOME)) {
            if (item.getType() != Material.WITHER_ROSE) return;
            new GhostGUI(p).open(p);

        }
        else if (manager.getRole(p).equals(MRoles.SIRENE)) {
            if (item.getType() == Material.SUNFLOWER) {
                if (!isCharm()) return;

                for (UUID id : manager.getPls()) {
                    Player pls = Bukkit.getPlayer(id);
                    if (pls == null) continue;

                    if (isCharm()){
                        if (p.getWorld().getNearbyEntities(p.getLocation(), 5, 5, 5).stream()
                                .filter(entity -> !entity.getUniqueId().equals(p.getUniqueId())).toList().contains(pls)) {
                            pls.sendMessage("§dLa Sirène vous à charmée !");
                            pls.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
                        }
                        setCharm(false);
                    }
                }

                new BukkitRunnable() {
                    private int timer = 30;

                    @Override
                    public void run() {
                        ActionBar.send(p, "§cCooldown: §f" + timer);

                        if (timer == 0){
                            setCharm(true);
                            cancel();
                        }
                        timer--;
                    }
                }.runTaskTimer(Murder.getInstance(), 0, 20L);
            }
        }
        else if (manager.getRole(p).equals(MRoles.CLANDESTIN)) {
            if (item.getType() != Material.AMETHYST_SHARD) return;

            if (isInvis()){
                new BukkitRunnable() {
                    private int timer = 40;

                    @Override
                    public void run() {
                        ActionBar.send(p, "§cCooldown: §f" + timer);

                        if (timer == 40) {
                            p.sendMessage("§7Tu est invisible pour les 10 prochaines secondes !");
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
                            p.setInvisible(true);
                            setInvis(false);
                        }

                        if (timer == 30) {
                            p.setInvisible(false);
                            p.sendMessage("§eTu est de nouveau visible !");
                        }

                        if (timer == 0){
                            setInvis(true);
                            cancel();
                        }
                        timer--;
                    }
                }.runTaskTimer(Murder.getInstance(), 0, 20L);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();

        if (p.getOpenInventory().getTitle().equalsIgnoreCase("§8Joueur à Hanter")){
            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getItemMeta() == null) return;

            SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
            Player target = Bukkit.getPlayer(meta.getOwningPlayer().getName());

            if (target == null) return;

            p.sendMessage("§8Tu vas hanter §d" + target.getName());
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
            target.sendMessage("§8Tu subi la malédiction du Fantôme !");

            p.closeInventory();
            p.getInventory().clear();
            p.setInvisible(false);
            p.setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player target)) return;

        Player p = e.getPlayer();

        if (!manager.getPls().contains(p.getUniqueId()) || !manager.isStarted()) return;
        if (!manager.getRole(p).equals(MRoles.FRONTIERE)) return;
        if (p.getInventory().getItemInMainHand().getType() != Material.IRON_BARS) return;
        if (!isCell()) return;

        e.setCancelled(true);

        setCell(false);
        p.sendMessage("§aPARFAIT ! Vous avez attrapé un délinquant !");

        target.teleport(new Location(target.getWorld(), 22, 17, 129, -180f, 0f));
        target.sendMessage("§cOH NON ! La police des frontières vous a choppé sans papier valable !");
        target.sendMessage("§7Un petit séjour en zonzon vous fera pas de mal..");
        target.sendMessage(" ");

        new BukkitRunnable() {
            private int timer = 20;

            @Override
            public void run() {
                if (timer > 0) {
                    timer--;
                    ActionBar.send(target, "§cUne révision de votre identité est en cours...");
                    return;
                }

                cancel();

                if (manager.getRole(target) != MRoles.CLANDESTIN) {
                    p.getInventory().clear();
                    p.sendMessage("§cVous êtes un bon à rien.. Le suspect était en règle.");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1f, 1f);

                    target.sendMessage("§aAprès révision de votre dossier, tout est en règle..");
                    target.sendMessage("§7Excusez nous pour la gène occasionnée");
                    target.sendMessage(" ");
                    target.playSound(target.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, SoundCategory.BLOCKS, 1f, 1f);
                    target.teleport(new Location(target.getWorld(), 22, 16, 81));
                    for (UUID id : manager.getPls()) {
                        Player pls = Bukkit.getPlayer(id);
                        if (pls != null) {
                            pls.sendMessage("");
                            pls.sendMessage("§6§l━━━━━━━━━━━━━━━━━━━━━━━━");
                            pls.sendMessage("§e" + p.getName() + " §aMembre de la §9P.A.F");
                            pls.sendMessage("§cEST UN(E) RATÉ(E)");
                            pls.playSound(pls.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1f,1f);
                        }
                    }
                } else {
                    p.sendMessage("§aVOUS AVEZ CHOPPÉ UN GROS POISSON ! Vous allez reçevoir une prime !");
                    p.playSound(p.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, SoundCategory.BLOCKS, 1f, 1f);

                    target.sendMessage("§cAprès enquête, il s'avère que vous avez fraudé votre place dans ce bateau");
                    target.sendMessage("§7Vous êtes finito..");
                    target.playSound(target.getLocation(), Sound.ENTITY_CAT_HISS, SoundCategory.BLOCKS, 1f, 1f);
                    for (UUID id : manager.getPls()) {
                        Player pls = Bukkit.getPlayer(id);
                        if (pls != null) {
                            pls.sendMessage(" ");
                            pls.sendMessage("§2§l━━━━━━━━━━━━━━━━━━━━━━━━");
                            pls.sendMessage("§a La P.A.F a gagné !");
                            pls.sendMessage("§7 Ils ont arrêté le clandestin");
                            pls.sendMessage("§2§l━━━━━━━━━━━━━━━━━━━━━━━━");
                            pls.sendMessage(" ");
                        }
                    }
                    manager.onEnd();
                }
            }
        }.runTaskTimer(Murder.getInstance(), 0, 20L);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e){
        if (!(e.getEntity() instanceof Player victim)) return;
        if (!manager.getPls().contains(victim.getUniqueId())) return;

        Player attacker = null;

        if (e.getDamager() instanceof Player) {
            attacker = (Player) e.getDamager();

            Material hand = attacker.getInventory().getItemInMainHand().getType();

            // Empêche de tuer sans épée
            if (hand != Material.IRON_SWORD && hand != Material.WOODEN_SWORD) {
                e.setCancelled(true);
                return;
            }

            // Seul le Capitaine ou le Pirate Fou peut tuer
            MRoles role = manager.getRole(attacker);
            if (!role.equals(MRoles.CAPITAINE) && !role.equals(MRoles.PIRATE_FOU)) {
                e.setCancelled(true);
                return;
            }
        }

        if (attacker == null || !manager.getPls().contains(attacker.getUniqueId())) {
            e.setCancelled(true);
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
            manager.onKill(attacker, victim);
        }
    }

    public void resetFlags() {
        this.cell = true;
        this.charm = true;
        this.invis = true;
    }

    // Getters
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
}
