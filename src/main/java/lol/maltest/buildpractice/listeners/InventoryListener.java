package lol.maltest.buildpractice.listeners;

import lol.maltest.buildpractice.BuildPractice;
import lol.maltest.buildpractice.methods.Menu;
import lol.maltest.buildpractice.methods.menu.ArenaChoose;
import lol.maltest.buildpractice.utils.ChatUtil;
import lol.maltest.buildpractice.utils.GameInfo;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener {

    private BuildPractice plugin;
    public InventoryListener(BuildPractice plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void rightClickItem(PlayerInteractEvent e) {
        if(e.getItem() == null || e.getItem().getType().equals(Material.AIR)) return;
        if (e.getItem().getType().equals(Material.EYE_OF_ENDER)) {
            if(e.getItem().getItemMeta().getDisplayName().equals(ChatUtil.clr("&cChange Arena"))) {
                if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    new ArenaChoose(BuildPractice.getPlayerMenuUtility(e.getPlayer()), plugin).open();
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void invClickEvent(InventoryClickEvent e) {
        if(!e.getWhoClicked().hasPermission(GameInfo.STAFF_PERMNODE.getValue())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if(!e.getWhoClicked().hasPermission(GameInfo.STAFF_PERMNODE.getValue())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent e) {
        if(e.getPlayer().hasPermission(GameInfo.STAFF_PERMNODE.getValue())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){

        InventoryHolder holder = e.getInventory().getHolder();
        //If the inventoryholder of the inventory clicked on
        // is an instance of Menu, then gg. The reason that
        // an InventoryHolder can be a Menu is because our Menu
        // class implements InventoryHolder!!
        if (holder instanceof Menu) {
            e.setCancelled(true); //prevent them from fucking with the inventory
            if (e.getCurrentItem() == null) { //deal with null exceptions
                return;
            }
            //Since we know our inventoryholder is a menu, get the Menu Object representing
            // the menu we clicked on
            Menu menu = (Menu) holder;
            //Call the handleMenu object which takes the event and processes it
            menu.handleMenu(e);
        }

    }

}


