package lol.maltest.buildpractice.methods.menu;

import de.tr7zw.nbtapi.NBTItem;
import lol.maltest.buildpractice.BuildPractice;
import lol.maltest.buildpractice.arena.Arena;
import lol.maltest.buildpractice.arena.ArenaManager;
import lol.maltest.buildpractice.listeners.BlockListener;
import lol.maltest.buildpractice.methods.PaginatedMenu;
import lol.maltest.buildpractice.methods.Timer;
import lol.maltest.buildpractice.utils.ChatUtil;
import lol.maltest.buildpractice.utils.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ArenaChoose extends PaginatedMenu {


    private BuildPractice plugin;
    private ArenaManager arenaManager;
    private Timer timer;

    public ArenaChoose(PlayerMenuUtility playerMenuUtility, BuildPractice plugin) {
        super(playerMenuUtility);
        this.plugin = plugin;
        this.arenaManager = plugin.getArenaManager();
        this.timer = plugin.getTimerClass();
    }

    public ArenaChoose(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return ChatUtil.clr("&cSelect an Arena");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        List<Arena> arenas = new ArrayList<>(arenaManager.getArenaList());

        NBTItem nbti = new NBTItem(e.getCurrentItem());
        Player player = (Player) e.getWhoClicked();
        if(e.getClickedInventory().getName().equals(ChatUtil.clr("&cSelect an Arena"))) {
            if (e.getCurrentItem().getType().equals(Material.DIRT)) {
                if (nbti.hasNBTData()) {
                    System.out.println("keys " + nbti.getKeys());
                    String owner = nbti.getString("player");
                    String arenaName = e.getCurrentItem().getItemMeta().getDisplayName().replace("§cArena ", "");
//                    if(arenaManager.getArena("name").getPlayers() == 1) {
//
//                    }
                    Arena arena = arenaManager.getArena(arenaName);
                    if (arena.getPlayers().size() == 1) {
                        player.sendMessage(ChatUtil.clr("&cThat arena is now taken sorry!"));
                        player.closeInventory();
                    } else {
                        if(arenaManager.getArena(player) == null) {
                            arena.addPlayer(player);
                            player.teleport(arena.getLocation());
                            player.sendMessage(ChatUtil.clr("&7You are now apart of &cArena " + arenaName));
                            return;
                        }
                        arenaManager.getArena(player).removePlayer(player);
                        arena.addPlayer(player);
                        player.teleport(arena.getLocation());
                        if(timer.getTime(player) != 0) {
                            timer.stopTimer(player);
                        }
                        if(BlockListener.blocks.get(player.getUniqueId()) != null) {
                            for(Block block : BlockListener.blocks.get(player.getUniqueId())) {
                                block.setType(Material.AIR);
                            }
                        }
                        player.sendMessage(ChatUtil.clr("&7You are now apart of &cArena " + arenaName));
                    }
                }
            } else if (e.getCurrentItem().getType().equals(Material.BARRIER)) {

                //close inventory
                player.closeInventory();

            }else if(e.getCurrentItem().getType().equals(Material.WOOD_BUTTON)){
                if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")){
                    if (page == 0){
                        player.sendMessage(ChatUtil.clr("&7You are already on the &cfirst &7page."));
                    }else{
                        page = page - 1;
                        super.open();
                    }
                }else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")){
                    if (!((index + 1) >= arenas.size())){
                        page = page + 1;
                        super.open();
                    }else{
                        player.sendMessage(ChatUtil.clr("&7You are on the &claste &7page."));
                    }
                }
            }
            e.setCancelled(true);
        }
    }

    @Override
    public void setMenuItems() {

        addMenuBorder();

        List<Arena> arenas = new ArrayList<>(arenaManager.getArenaList());

        if (!arenas.isEmpty()) {
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                System.out.println(arenas.size());
                System.out.println(index);
                if (index >= arenas.size()) break;
                if (arenas.get(index) != null) {
                    if (arenas.get(index).getPlayers().size() == 0) {
                        ItemStack playerItem = new ItemStack(Material.DIRT, 1);
                        ItemMeta playerMeta = playerItem.getItemMeta();
                        ArrayList<String> lore = new ArrayList<>();
                        if (arenas.get(index).getPlayers().size() == 1) {
//                            lore.add(ChatUtil.clr("&aClick to request access!"));
//                            lore.add(ChatUtil.clr("&7Owner: &c" + arenas.get(index).getPlayers().toString()));
                        } else {
                            lore.add(ChatUtil.clr("&aClick to go to arena " + arenas.get(index).getName()));
                        }

                            playerMeta.setDisplayName(ChatColor.RED + "Arena " + arenas.get(index).getName());
                            NBTItem nbti = new NBTItem(playerItem);
                            nbti.setString("name", arenas.get(index).getName());
                            nbti.setString("player", arenas.get(index).getPlayers().toString());
                            System.out.println(arenas.get(index).getPlayers().toString());
                            playerMeta.setLore(lore);
                            playerItem.setItemMeta(playerMeta);

                            inventory.addItem(playerItem);
                    }
                }
            }

        }
    }
}
