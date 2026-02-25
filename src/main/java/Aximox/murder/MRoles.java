package Aximox.murder;

public enum MRoles {
    MURDER("§cMurder", "§7Ta mission est de §ctuer tout le monde"),
    DETECTIVE("§bDetective", "§7Ta mission est de §btrouver §7et de §btuer le §cMurder§7 !"),
    Innocent("§aInnocent", "§7Juste survie sah..");

    private final String name;
    private final String description;

    MRoles(String name, String description){
        this.name = name;
        this.description = description;
    }

    //Guetter
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
}
