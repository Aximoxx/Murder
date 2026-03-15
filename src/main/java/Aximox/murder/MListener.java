package Aximox.murder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
    private final MManager manager;
    private List<UUID> cooldown = new ArrayList<>();

    public MListener(MManager manager){
        this.manager = manager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        e.setJoinMessage(null);

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

        } else if (manager.getRole(p).equals(MRoles.SIRENE)) {
            if (item.getType() == Material.SUNFLOWER) {


                for (UUID id : manager.getPls()) {
                    Player pls = Bukkit.getPlayer(id);
                    if (pls == null) continue;

                    if (p.getWorld().getNearbyEntities(p.getLocation(), 5, 5, 5).contains(pls)) {
                        pls.sendMessage("§dLa Sirène vous à charmée !");
                        pls.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
                    }
                    // Todo: un cooldown

                }
            }

        } else if (manager.getRole(p).equals(MRoles.CLANDESTIN)) {
            if (item.getType() != Material.AMETHYST_SHARD) return;

            new BukkitRunnable() {
                private int timer = 5;

                @Override
                public void run() {
                    if (timer == 5) {
                        p.sendMessage("§7Tu est invisible pour les 5 prochaines secondes !");
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, 1));
                        p.setInvisible(true);
                    }
                    if (timer == 0) {
                        p.setInvisible(false);
                        p.sendMessage("§eTu est de nouveau visible !");
                        cancel();
                    }
                    timer--;
                }
            }.runTaskTimer(Murder.getInstance(), 0, 20L);
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
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
            target.sendMessage("§8Tu subi la malédiction du Fantôme !");
            p.closeInventory();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e){
        e.setCancelled(true);
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

    public List<UUID> getCooldown() {
        return cooldown;
    }
}
