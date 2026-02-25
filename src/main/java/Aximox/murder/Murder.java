package Aximox.murder;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Murder extends JavaPlugin {
    private static Murder instance;
    private MCommand mCommand;

    @Override
    public void onEnable() {
        instance = this;
        MManager mManager = new MManager();
        MListener mListener = new MListener(mManager);
        mCommand = new MCommand(mManager);

        Bukkit.getPluginManager().registerEvents(mListener, this);
        Objects.requireNonNull(getCommand("setrank")).setExecutor(mCommand);
        Objects.requireNonNull(getCommand("start")).setExecutor(mCommand);
        Objects.requireNonNull(getCommand("stop")).setExecutor(mCommand);
    }

    //Guetter
    public static Murder getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
    }
    public void setmCommand(MCommand mCommand) {
        this.mCommand = mCommand;
    }
}
