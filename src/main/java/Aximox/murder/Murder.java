package Aximox.murder;

import Aximox.murder.grade.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Murder extends JavaPlugin {
    private MManager manager;
    private MCommand mCommand;
    private MListener mListener;
    private static Murder instance;
    private RankManager rankManager;

    @Override
    public void onEnable() {
        instance = this;

        rankManager = new RankManager();
        getRankManager().loadRanks();

        manager = new MManager();
        mListener = new MListener(manager);
        mCommand = new MCommand(manager);

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

    public MManager getManager() {
        return manager;
    }
    public MCommand getmCommand() {
        return mCommand;
    }
    public MListener getmListener() {
        return mListener;
    }
    public RankManager getRankManager() {
        return rankManager;
    }
    public void setmCommand(MCommand mCommand) {
        this.mCommand = mCommand;
    }
}
