package Aximox.murder;

import Aximox.murder.grade.MGrades;
import Aximox.murder.gui.CompoGUI;
import Aximox.murder.utils.ActionBar;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class MCommand implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(@NotNull CommandSender s, @NotNull Command c, @NotNull String label, String[] args) {
        if (!(s instanceof Player p)) {
            s.sendMessage(Murder.getInstance().getManager() + "§cSeuls les joueurs peuvent entrer des commandes");
            return true;
        }

        if (c.getName().equalsIgnoreCase("doc")){
            TextComponent docs = new TextComponent("§eClique ici pour accéder au doc !");
            docs.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://broken.creatyc.com:2027/"));

            p.spigot().sendMessage(docs);
            return true;
        }

        if (c.getName().equalsIgnoreCase("feed")){
            p.setFoodLevel(20);
            p.sendMessage("§aTu est maintenant calé(e) !");
            return true;
        }

        if (!p.isOp()){
            p.sendMessage("§cTu n'as pas le bon rôle, bozo le clown");
            return true;
        }

        if (c.getName().equalsIgnoreCase("setrank")) {
            if (args.length < 2) {
                p.sendMessage("§cUsage : /setrank <joueur> <grade>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage("§cCe joueur n'est pas connecté.");
                return true;
            }

            MGrades newRank = MGrades.getByName(args[1]);

            Murder.getInstance().getRankManager().setRank(target.getUniqueId(), newRank);

            p.sendMessage("§a§lSUCCÈS §8| §fLe grade de §e" + target.getName() + " §fest maintenant " + newRank.getPrefix());
            target.sendMessage("§a§lINFO §8| §fTon grade a été mis à jour : " + newRank.getPrefix());

            Murder.getInstance().getRankManager().loadRank(target);
            Murder.getInstance().getManager().updateTabList(target);

            return true;
        }

        else if (c.getName().equalsIgnoreCase("murder")) {
            if (args.length < 1) {
                p.sendMessage("pas d'arguments");
                return true;
            }

            switch (args[0]) {

                case "help":
                    sendHelp(p);
                    break;

                case "mute":
                    Murder.getInstance().getManager().setMuteOn(!Murder.getInstance().getManager().isMuteOn());

                    if (Murder.getInstance().getManager().isMuteOn()) {
                        for (Player pls : Bukkit.getOnlinePlayers()) {
                            if (!Murder.getInstance().getRank(pls).hasPower(MGrades.HOST)) {
                                Murder.getListeMutes().add(pls.getUniqueId());
                                ActionBar.start(Murder.getInstance(), pls, "§cVous êtes §4§lMUETS §c!");
                            }
                        }
                        p.sendTitle("§6Une annonce est entrain de se faire", "§eÉcouter attentivement", 10, 30, 10);
                    } else {
                        Murder.getListeMutes().clear();
                        Bukkit.getOnlinePlayers().forEach(pls -> {
                            ActionBar.stop(pls);
                            pls.sendMessage("§aVous avez retrouvé l'usage de la parole.");
                        });
                    }
                    break;

                case "start":
                    Murder.getInstance().getManager().onStart(p);
                    break;

                case "stop":
                    Murder.getInstance().getManager().onEnd();
                    break;

                case "setchest":
                    Murder.getInstance().getManager().setChest(p);
                    break;

                case "setMoss":
                    Murder.getInstance().getManager().setMoss(p);
                    break;

                case "setPanne":
                    Murder.getInstance().getManager().setPanne(p);
                    break;

                case "setbuzz":
                    Murder.getInstance().getManager().setBuzzer(p);
                    break;

                case "setrejoin":
                    Murder.getInstance().getManager().setRejoin(p);
                    break;

                case "setspawn":
                    Murder.getInstance().getManager().setSpawn(p);
                    break;

                case "reveal":
                    for (Player viewer : Bukkit.getOnlinePlayers()) {
                        viewer.showPlayer(Murder.getInstance(), p);
                    }
                    p.sendMessage("§aTu es de nouveau visible !");
                    break;

                case "clearAS":
                    Murder.getInstance().getManager().resetDeathAS();
                    break;

                case "compo":
                    if (Murder.getInstance().getManager().isStarted()){
                        p.sendMessage("§cTu ne peux pas changer la compo pendant la partie !");
                        return true;
                    }

                    new CompoGUI(p).open(p);
                    break;

                case "give":
                    if (args.length < 2) {
                        p.sendMessage("pas d'arguments");
                        return true;
                    }

                    switch (args[1]) {
                        case "capitaine":
                            p.getInventory().addItem(Murder.getInstance().getCustomItems().dagger());
                            break;

                        case "pirate":
                            p.getInventory().setItem(0, Murder.getInstance().getCustomItems().sabre());
                            break;

                        case "sirene":
                            p.getInventory().addItem(Murder.getInstance().getCustomItems().sirene());
                            break;

                        case "fruit":
                            p.getInventory().addItem(Murder.getInstance().getCustomItems().fruit());
                            break;

                        case "canon":
                            p.getInventory().addItem(Murder.getInstance().getCustomItems().canon());
                            break;

                        case "sifflet":
                            p.getInventory().addItem(Murder.getInstance().getCustomItems().prison());
                            break;

                        case "bross":
                            p.getInventory().addItem(Murder.getInstance().getCustomItems().bross());
                            break;

                        case "wrench":
                            p.getInventory().addItem(Murder.getInstance().getCustomItems().wrench());
                            break;

                        default:
                            sendItemHelp(p);
                            break;
                    }

                    break;

                default:
                    sendHelp(p);
                    break;

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
                List<String> subs = Arrays.asList("help", "start", "stop", "setchest", "setbuzz", "setspawn", "clearAS", "compo", "give");
                return subs.stream()
                        .filter(sub -> sub.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
                List<String> items = Arrays.asList("capitaine", "pirate", "sirene", "fruit", "canon", "sifflet", "bross");
                return items.stream()
                        .filter(item -> item.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    private void sendHelp(Player p){
        p.sendMessage("§6§l=======§c+§6§l=======");
        p.sendMessage(" ");
        p.sendMessage("§b/murder §fstart");
        p.sendMessage("§7Cette commande sert à §adémarrer §7une partie !");
        p.sendMessage(" ");
        p.sendMessage("§b/murder §fstop");
        p.sendMessage("§7Cette commande sert à §4arrêter §7une partie !");
        p.sendMessage(" ");
        p.sendMessage("§b/murder §fsetchest");
        p.sendMessage("§7Cette commande sert à définir l'emplacement d'un §6coffre §7!");
        p.sendMessage(" ");
        p.sendMessage("§b/murder §fsetmoss");
        p.sendMessage("§7Cette commande sert à définir l'emplacement d'une §asaleté §7!");
        p.sendMessage(" ");
        p.sendMessage("§b/murder §fsetpanne");
        p.sendMessage("§7Cette commande sert à définir l'emplacement d'une §cpanne §7!");
        p.sendMessage(" ");
        p.sendMessage("§b/murder §fsetbuzz");
        p.sendMessage("§7Cette commande sert à définir l'emplacement du §cBuzzer §7!");
        p.sendMessage(" ");
        p.sendMessage("§b/murder §fclearas");
        p.sendMessage("§7Cette commande sert à §csupprimer §7les hologrames des corps !");
        p.sendMessage(" ");
        p.sendMessage("§b/murder §fcompo");                                          // <--
        p.sendMessage("§7Cette commande sert à §9modifier §7la composition des rôles !"); // <--
    }

    private void sendItemHelp(Player p){
        p.sendMessage("§6§l=======§c+§6§l=======");
        p.sendMessage(" ");
        p.sendMessage("§b/murder give §fcapitaine");
        p.sendMessage("§7Cette commande sert à give l'item du §cCapitaine !");
        p.sendMessage(" ");
        p.sendMessage("§b/murder give §fpirate");
        p.sendMessage("§7Cette commande sert à give l'item du §6Pirate !");
        p.sendMessage(" ");
        p.sendMessage("§b/murder give §fsirene");
        p.sendMessage("§7Cette commande sert à give l'item de la §dSirène §7!");
        p.sendMessage(" ");
        p.sendMessage("§b/murder give §fbross");
        p.sendMessage("§7Cette commande sert à give l'item du §fMatelot §7!");
        p.sendMessage(" ");
        p.sendMessage("§b/murder give §ffruit");
        p.sendMessage("§7Cette commande sert à give l'item du §cCuisiner §7!");
        p.sendMessage(" ");
        p.sendMessage("§b/murder give §fsifflet");
        p.sendMessage("§7Cette commande sert à give l'item de la §1P§f.A.§cF §7!");
        p.sendMessage(" ");
        p.sendMessage("§b/murder give §fcanon");
        p.sendMessage("§7Cette commande sert à give l'item du §cCanonnier !");
    }


}
