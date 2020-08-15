package studio.trc.bukkit.crazyauctionsplus.utils.enums;

public enum ShopType {
    
    SELL("Sell"), BUY("Buy"), BID("Bid"), ANY("Any");
    
    private final String name;
    
    /**
     * @param name name of the Shop Type.
     */
    private ShopType(String name) {
        this.name = name;
    }
    
    /**
     * @param name name of the Type you want.
     * @return Returns the Type as a Enum.
     */
    public static ShopType getFromName(String name) {
        for (ShopType type : ShopType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * @return Returns the type name as a string.
     */
    public String getName() {
        return name;
    }
    
}