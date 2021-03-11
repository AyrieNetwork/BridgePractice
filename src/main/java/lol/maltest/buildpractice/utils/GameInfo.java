package lol.maltest.buildpractice.utils;

public enum GameInfo {
    
    GAME_NAME("Bridging Practice"),
    GAME_DESCRIPTION1("&7This &cpractice mode &7is perfect for learning all types "),
    GAME_DESCRIPTION2("&7of &cbridging &7and is made to &csatisfy &7our players."),
    GAME_CREDITS("maltest"),

    STAFF_PREFIX("&c&lSTAFF &c» &8"),
    STAFF_PERMNODE("ayrie.staff"),
    STAFF_NOPERMISSION("&7yall rocking with no &cperms &7?!");

    private String value;

    private GameInfo(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
