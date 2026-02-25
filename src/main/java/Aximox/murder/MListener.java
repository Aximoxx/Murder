package Aximox.murder;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;

public class MListener implements Listener {
    private final MManager manager;

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
    public void onPickup(EntityPickupItemEvent e) {
        Player p = (Player) e.getEntity();
        Material type = e.getItem().getItemStack().getType();
        if (manager.getRole(p).equals(MRoles.MURDER)){
            p.sendMessage("§cTu ne peux pas récupérer l'§a§lArc de la Justice");
            e.setCancelled(true);
            return;
        }

        if (type != Material.BOW && type != Material.ARROW) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPveDamage(EntityDamageEvent e){
        e.setCancelled(true);
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
            // Empêche la possibilité de kill à la main
            if (attacker.getInventory().getItemInMainHand().getType() != Material.IRON_SWORD) {
                e.setCancelled(true);
                return;
            }

            // Seul le Murder peut tuer à l'épée
            if (!manager.getRole(attacker).equals(MRoles.MURDER)){
                return;
            }
        } else if (e.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player) {
            attacker = (Player) arrow.getShooter();
            if (manager.getRole(attacker).equals(MRoles.MURDER)){
                return;
            }
        }

        if (attacker == null || !manager.getPls().contains(attacker.getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
        manager.onKill(attacker, victim);
    }
}
