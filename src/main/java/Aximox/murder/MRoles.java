package Aximox.murder;

public enum MRoles {

    // Un seul Méchant
    CAPITAINE("§cLe Capitaine", "§7Ta mission est de §ctuer tout le monde"),

    //Rôle Solo
    TRESOR("§6Chasseur de Trésor", "§7Ton objectifs est de trouver 3 trésors pour gagner la partie"),
    FRONTIERE("§9Police des Frontières", "§7Tu peux enfermer un joueur pendant 20 secondes, si il est Clandestin, tu gagnes !"),

    // Les rôles que le méchant doit tuer
    PIRATE_FOU("§dPirate fou", "§7Tu as la possibilité d'éliminer un joueur de ton choix, si il est gentil, tu capoute !"),
    SIRENE("§dSirène", "§7Tu peux ralentir les joueurs que tu croise avec ton chant"),
    FANTOME("§8Fantome du Naufrage", "§7Tu peux hanter quelqu'un après ta mort."),
    CLANDESTIN("§7Passager Clandestin", "§7Tu as la possibilité de te cacher tout les 40 secondes"),

    // Les rôles qui ont des quêtes
    CANONNIER("§8Canonnier", "§7Tu doit fabriquer un boulet et le mettre dans le canon !"),
    MECANO("§7Mécano", "§7Tu dois réparer les moteurs dans la salle des Moteurs !"),
    MATELOT("§bMatelot", "§7Tu doit nettoyer le pont du bateau !"),
    CUISINER("§6Cuisiner", "§7Tu doit faire à manger !"),

    // Les rôles qui ne servent à rien
    PASSAGER("§aPassager", "§7Juste survie sah..");

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
