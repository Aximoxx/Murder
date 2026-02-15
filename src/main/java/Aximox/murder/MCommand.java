package Aximox.murder;

import Aximox.murder.grade.MGrades;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MCommand implements CommandExecutor {
    private final MManager manager;

    public MCommand(MManager manager){
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
        if (!(s instanceof Player p)) {
            s.sendMessage(manager.getMurder() + "§cSeuls les joueurs peuvent entrer des commandes");
            return true;
        }

        if (c.getName().equalsIgnoreCase("setrank")) {
            MGrades playerRank = manager.getRankManager().getRank(p.getUniqueId());

            if (!p.isOp() && playerRank.getPower() < 3) {
                p.sendMessage(manager.getMurder() + "§cVous n'avez pas le rang requis.");
                return true;
            }

            if (args.length < 2) {
                p.sendMessage(manager.getMurder() + "§cUtilisation: /setrank <joueur> <rang>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage(manager.getMurder() + "§cCe joueur n'existe pas ou n'est pas en ligne.");
                return true;
            }

            MGrades newRank = MGrades.getByName(args[1]);
            if (newRank == null) {
                p.sendMessage(manager.getMurder() + "§cCe rang n'existe pas !");
                return true;
            }

            if (newRank.getPower() >= playerRank.getPower() && !p.isOp()) {
                p.sendMessage(manager.getMurder() + "§cVous ne pouvez pas donner un rang supérieur au vôtre !");
                return true;
            }

            manager.getRankManager().setRank(target.getUniqueId(), newRank);

            p.sendMessage("§a§lSUCCÈS §8| §fLe statut de §e" + target.getName() + " §fest maintenant " + newRank.getPrefix());
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1f, 1f);

            target.sendMessage("§a§lINFO §8| §fTon statut a été mis à jour : " + newRank.getPrefix());
            target.playSound(target.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, SoundCategory.BLOCKS, 1f, 1f);
            return true;
        }

        else if (c.getName().equalsIgnoreCase("start")) {
            if (manager.getRankManager().getRank(p.getUniqueId()).getPower() < 3){
                p.sendMessage(manager.getMurder() + "§cVous n'avez pas la bonne classe pour effectuer cela !");
                return true;
            }

            manager.onStart(p);
        }

        else if (c.getName().equalsIgnoreCase("stop")) {
            if (manager.getRankManager().getRank(p.getUniqueId()).getPower() < 3){
                p.sendMessage(manager.getMurder() + "§cVous n'avez pas la bonne classe pour effectuer cela !");
                return true;
            }

            manager.onEnd();
        }

        return false;
    }
}
