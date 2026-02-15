package Aximox.murder.grade;

public enum MGrades {

    HOST("§7[§6Host§7] ", 3),
    INVITEES("§7[§cInvité(e)s§7] ", 1);

    private final String prefix;
    private final int power;

    MGrades(String prefix, int power){
        this.prefix = prefix;
        this.power = power;
    }

    // Getters
    public String getPrefix() {
        return prefix;
    }
    public int getPower() {
        return power;
    }

    public static MGrades getByName(String name) {
        for (MGrades rank : values()) {
            if (rank.name().equalsIgnoreCase(name)) {
                return rank;
            }
        }
        return INVITEES;
    }
}