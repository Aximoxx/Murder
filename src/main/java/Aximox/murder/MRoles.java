package Aximox.murder;

public enum MRoles {
    MURDER("§cMurder", "§7Tu est le §cMurder§7 ! Ta mission est de tuer tout le monde"),
    DETECTIVE("§bDetective", "§7Tu est le §bDétective§7 ! Ta mission est de trouver et de tuer le §cMurder§7 !"),
    Innocent("§aInnocent", "§7Tu est §aInnocent§7 ! Survie..");

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
