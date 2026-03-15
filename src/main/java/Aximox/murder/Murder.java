package Aximox.murder;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Murder extends JavaPlugin {
    private MManager manager;
    private MCommand mCommand;
    private static Murder instance;

    @Override
    public void onEnable() {
        instance = this;

        manager = new MManager();
        MListener mListener = new MListener(manager);
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
    }

    public MManager getManager() {
        return manager;
    }
    public MCommand getmCommand() {
        return mCommand;
    }
    public void setmCommand(MCommand mCommand) {
        this.mCommand = mCommand;
    }
}
