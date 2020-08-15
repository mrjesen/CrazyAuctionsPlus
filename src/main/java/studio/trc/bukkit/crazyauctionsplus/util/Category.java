package studio.trc.bukkit.crazyauctionsplus.util;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import studio.trc.bukkit.crazyauctionsplus.util.FileManager.Files;
import studio.trc.bukkit.crazyauctionsplus.util.FileManager.ProtectedConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Category {

    public static final List<Category> collection = new ArrayList();

    private final String name;
    private final List<Material> items;
    private final List<ItemMeta> itemMeta;
    private final String displayName;
    private final boolean whitelist;

    /**
     * @param name Name of the Shop Type.
     */
    private Category(String name, List<Material> items) {
        this.name = name;
        this.items = items;
        itemMeta = new ArrayList();
        displayName = Files.CATEGORY.getFile().getString("Category." + name + ".Display-Name");
        whitelist = Files.CATEGORY.getFile().getBoolean("Category." + name + ".Whitelist");
    }

    private Category(String name, List<Material> items, List<ItemMeta> itemMeta) {
        this.name = name;
        this.items = items;
        this.itemMeta = itemMeta;
        displayName = Files.CATEGORY.getFile().getString("Category." + name + ".Display-Name");
        whitelist = Files.CATEGORY.getFile().getBoolean("Category." + name + ".Whitelist");
    }

    /**
     * Get Category's instance.
     *
     * @param moduleName Module name in Category.yml.
     * @return
     */
    public static Category getModule(String moduleName) {
        if (moduleName == null) return null;
        List<Material> materialList = new ArrayList();
        List<ItemMeta> metaList = new ArrayList();
        ProtectedConfiguration cat = Files.CATEGORY.getFile();
        if (cat.get("Category") != null) {
            if (cat.get("Category." + moduleName) == null) {
                return null;
            }
            if (cat.get("Category." + moduleName + ".Items") != null) {
                for (String name : cat.getStringList("Category." + moduleName + ".Items")) {
                    try {
                        Material m = Material.matchMaterial(name);
                        materialList.add(m);
                    } catch (Exception ex) {
                        PluginControl.printStackTrace(ex);
                    }
                }
            }
            if (cat.get("Category." + moduleName + ".Modules") != null) {
                for (String modulesname : cat.getStringList("Category." + moduleName + ".Modules")) {
                    if (getModule(modulesname) == null) continue;
                    Category category = getModule(modulesname);
                    materialList.addAll(category.getItems());
                }
            }
            if (cat.get("Category." + moduleName + ".Item-Collection") != null) {
                for (String items : cat.getStringList("Category." + moduleName + ".Item-Collection")) {
                    try {
                        long uid = Long.valueOf(items);
                        ItemCollection ic = ItemCollection.getItemCollection(uid);
                        if (ic != null) metaList.add(ic.getItem().getItemMeta());
                    } catch (NumberFormatException ex) {
                        ItemCollection ic = ItemCollection.getItemCollection(items);
                        if (ic != null) metaList.add(ic.getItem().getItemMeta());
                        PluginControl.printStackTrace(ex);
                    }
                }
            }
            if (cat.getBoolean("Category." + moduleName + ".Reflection-boolean.Enabled")) {
                for (String methods : cat.getStringList("Category." + moduleName + ".Reflection-boolean.Methods")) {
                    Method method;
                    try {
                        method = Material.class.getMethod(methods);
                    } catch (NoSuchMethodException | SecurityException ex) {
                        PluginControl.printStackTrace(ex);
                        continue;
                    }
                    for (Material materials : Material.values()) {
                        try {
                            boolean value = (boolean) method.invoke(materials);
                            if (value) {
                                materialList.add(materials);
                            }
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            PluginControl.printStackTrace(ex);
                        }
                    }
                }
            }
            if (cat.getBoolean("Category." + moduleName + ".Whitelist")) {
                return new Category(moduleName, materialList, metaList);
            } else {
                List<Material> newList = new ArrayList();
                for (Material m : Material.values()) {
                    if (!materialList.contains(m)) {
                        newList.add(m);
                    }
                }
                return new Category(moduleName, newList, metaList);
            }
        } else {
            return null;
        }
    }

    public static List<String> getModuleNameList() {
        List<String> list = new ArrayList();
        ProtectedConfiguration cat = Files.CATEGORY.getFile();
        if (cat.get("Category") != null) {
            for (String name : cat.getConfigurationSection("Category").getKeys(false)) {
                list.add(name);
            }
            return list;
        } else {
            return list;
        }
    }

    public static Category getDefaultCategory() {
        ProtectedConfiguration cat = Files.CATEGORY.getFile();
        if (cat.get("Default-Category") != null) {
            return getModule(cat.getString("Default-Category"));
        }
        return null;
    }

    public static List<Category> getCategoryModules() {
        List<Category> list = new ArrayList();
        for (String name : getModuleNameList()) {
            Category module = getModule(name);
            if (module != null) {
                list.add(getModule(name));
            }
        }
        return list;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<Material> getItems() {
        return items;
    }

    public List<ItemMeta> getAllItemMeta() {
        return itemMeta;
    }

    public boolean isWhitelist() {
        return whitelist;
    }

    @Override
    public String toString() {
        return "[Category] -> [Name:" + name + ", DisplayName:" + (displayName != null ? displayName : "") + "]";
    }
}