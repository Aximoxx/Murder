package Aximox.murder;

import Aximox.murder.grade.MGrades;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MCommand implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command c, @NotNull String label, String[] args) {
        if (!(s instanceof Player p)) {
            s.sendMessage(Murder.getInstance().getManager() + "§cSeuls les joueurs peuvent entrer des commandes");
            return true;
        }

        if (Murder.getInstance().getRankManager().getRank(p.getUniqueId()).getPower() < 3 || !p.isOp()){
            p.sendMessage("§cTu n'as pas le bon rôle, bozo le clown");
            return true;
        }

        if (c.getName().equalsIgnoreCase("setrank")) {
            MGrades playerRank = Murder.getInstance().getManager().getRankManager().getRank(p.getUniqueId());

            if (args.length < 2) {
                p.sendMessage(Murder.getInstance().getManager().getMurder() + "§cUtilisation: /setrank <joueur> <rang>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage(Murder.getInstance().getManager().getMurder() + "§cCe joueur n'existe pas ou n'est pas en ligne.");
                return true;
            }

            MGrades newRank = MGrades.getByName(args[1]);
            if (newRank == null) {
                p.sendMessage(Murder.getInstance().getManager().getMurder() + "§cCe rang n'existe pas !");
                return true;
            }

            if (newRank.getPower() >= playerRank.getPower() && !p.isOp()) {
                p.sendMessage(Murder.getInstance().getManager().getMurder() + "§cVous ne pouvez pas donner un rang supérieur au vôtre !");
                return true;
            }

            Murder.getInstance().getManager().getRankManager().setRank(target.getUniqueId(), newRank);

            p.sendMessage("§a§lSUCCÈS §8| §fLe statut de §e" + target.getName() + " §fest maintenant " + newRank.getPrefix());
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1f, 1f);

            target.sendMessage("§a§lINFO §8| §fTon statut a été mis à jour : " + newRank.getPrefix());
            target.playSound(target.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, SoundCategory.BLOCKS, 1f, 1f);

            for (Player pls : Bukkit.getOnlinePlayers()) Murder.getInstance().getManager().updateTabList(pls);
            return true;
        }

        else if (c.getName().equalsIgnoreCase("murder")) {
            if (args.length < 1){
                p.sendMessage("pas d'arguments");
                return true;
            }


            if (args[0].equalsIgnoreCase("help")){
                sendHelp(p);
                return true;
            }else if (args[0].equalsIgnoreCase("start")) {
                Murder.getInstance().getManager().onStart(p);
                return true;
            }else if (args[0].equalsIgnoreCase("stop")) {
                Murder.getInstance().getManager().onEnd();
                return true;
            } else if (args[0].equalsIgnoreCase("setchest")) {
                Murder.getInstance().getManager().setChest(p);
                return true;
            } else if (args[0].equalsIgnoreCase("setbuzz")) {
                Murder.getInstance().getManager().setBuzzer(p);
                return true;
            } else if (args[0].equalsIgnoreCase("clearAS")) {
                Murder.getInstance().getManager().resetDeahtAS();
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command c, @NotNull String alias, String[] args) {
        if (!(s instanceof Player p)) return Collections.emptyList();

        if (Murder.getInstance().getRankManager().getRank(p.getUniqueId()).getPower() < 3 && !p.isOp())
            return Collections.emptyList();

        if (c.getName().equalsIgnoreCase("setrank")) {
            if (args.length == 1) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (args.length == 2) {
                return Arrays.stream(MGrades.values())
                        .map(MGrades::name)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        else if (c.getName().equalsIgnoreCase("murder")) {
            if (args.length == 1) {
                List<String> subs = Arrays.asList("help", "start", "stop", "setchest", "setbuzz", "clearAS");
                return subs.stream()
                        .filter(sub -> sub.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }
    
    private void sendHelp(Player p){
        p.sendMessage("§6§l=======§c+§6§l=======");
        p.sendMessage(" ");
        p.sendMessage("§b/epreuve §fstart");
        p.sendMessage("§7Cette commande sert à §adémarrer §7une partie !");
        p.sendMessage(" ");
        p.sendMessage("§b/epreuve §fstop");
        p.sendMessage("§7Cette commande sert à §4arrêter §7une partie !");
        p.sendMessage(" ");
        p.sendMessage("§b/epreuve §fsetchest");
        p.sendMessage("§7Cette commande sert à définir l'emplacement d'un §6coffre §7!");
        p.sendMessage(" ");
        p.sendMessage("§b/epreuve §fsetbuzz");
        p.sendMessage("§7Cette commande sert à définir l'emplacement du §cBuzzer §7!");
        p.sendMessage(" ");
        p.sendMessage("§b/epreuve §fclearas");
        p.sendMessage("§7Cette commande sert à §csupprimer §7les hologrames des corps !");
    }
}
