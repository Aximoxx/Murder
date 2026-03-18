package Aximox.murder;

import Aximox.murder.grade.RankManager;
import Aximox.murder.items.CustomItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Murder extends JavaPlugin {
    private MManager manager;
    private MCommand mCommand;
    private MListener mListener;
    private static Murder instance;
    private RankManager rankManager;
    private CustomItems customItems;

    @Override
    public void onEnable() {
        instance = this;

        rankManager = new RankManager();
        getRankManager().loadRanks();

        manager = new MManager();
        mListener = new MListener(manager);
        mCommand = new MCommand();

        Bukkit.getPluginManager().registerEvents(mListener, this);
        Objects.requireNonNull(getCommand("setrank")).setExecutor(mCommand);
        Objects.requireNonNull(getCommand("murder")).setExecutor(mCommand);
    }

    //Guetter
    public static Murder getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        getRankManager().saveAllRanks();
    }

    public List<Location> getChests() {
        List<Location> chests = new ArrayList<>();
        int chestCount = Murder.getInstance().getConfig().getInt("murder.chestCount", 0);

        for (int i = 1; i <= chestCount; i++) {
            Location loc = (Location) Murder.getInstance().getConfig().get("murder.chest" + i);
            if (loc != null) chests.add(loc);
        }

        return chests;
    }

    public MManager getManager() {
        return manager;
    }
    public MCommand getmCommand() {
        return mCommand;
    }
    public MListener getmListener() {
        return mListener;
    }
    public CustomItems getCustomItems() {
        return customItems;
    }
    public RankManager getRankManager() {
        return rankManager;
    }
    public void setmCommand(MCommand mCommand) {
        this.mCommand = mCommand;
    }
}
