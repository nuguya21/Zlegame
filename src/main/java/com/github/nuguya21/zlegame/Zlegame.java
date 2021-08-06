package com.github.nuguya21.zlegame;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class Zlegame extends JavaPlugin {

    public static Inventory menu;
    public static HashMap<UUID, Inventory> aimtest = new HashMap<>();
    public static HashMap<UUID, Inventory> setting = new HashMap<>();
    public static int num = -1;
    public static Location[] zilpoongcham = new Location[1000];
    public static String prefix = "[ZleGame]";
    public static Player main = null;
    public static String sentence = null;
    public static int round = 10;
    public static boolean typingpractice = false;

    public void itemset(String display, Material ID, int data, int stack, List<String> lore, int loc, Inventory inventory) {
        ItemStack item = new MaterialData(ID, (byte) data).toItemStack(stack);
        ItemMeta items = item.getItemMeta();
        items.setDisplayName(display);
        items.setLore(lore);
        item.setItemMeta(items);
        inventory.setItem(loc, item);
    }

    @Override
    public void onEnable() {
        Objects.requireNonNull(this.getCommand("타자연습")).setTabCompleter(new CommandTabCompleter());
        Objects.requireNonNull(this.getCommand("deathpoint")).setTabCompleter(new CommandTabCompleter());
        Objects.requireNonNull(this.getCommand("zilpoongcham")).setTabCompleter(new CommandTabCompleter());
        Objects.requireNonNull(this.getCommand("aimtest")).setTabCompleter(new CommandTabCompleter());
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        menu = Bukkit.createInventory(null, 45, ChatColor.DARK_GRAY + "메뉴");
    }

    @Override
    public void onDisable() {}

    public static int countdown = 10;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equals("aimtest")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                if (!Zlegame.aimtest.containsKey(uuid)) {
                    Zlegame.aimtest.put(uuid, Bukkit.createInventory(player, 45, ChatColor.DARK_GRAY + "에임 테스트"));
                }
                if (!Events.aimtestpower.containsKey(uuid)) {
                    Events.aimtestpower.put(uuid, false);
                }
                if (!Events.aimtestpower.get(uuid)) {
                    aimtest.get(uuid).clear();
                    itemset(ChatColor.GREEN + "시작", Material.SPECTRAL_ARROW, 1, 1, Collections.singletonList(ChatColor.GRAY + "우클릭으로 시작하세요!"), 21, aimtest.get(uuid));
                    itemset(ChatColor.RED + "개발 중", Material.BARRIER, 1, 1, null, 23, aimtest.get(uuid));
                    player.openInventory(aimtest.get(uuid));
                } else {
                    Events.aimtestpower.put(uuid, false);
                    sender.sendMessage(ChatColor.RED + "게임 실행 중에 오류가 발생했습니다. ");
                }
            } else return false;
        } else if (command.getName().equals("zilpoongcham")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Location location = player.getLocation();
                num++;
                zilpoongcham[num] = location;
                sender.sendMessage(ChatColor.GREEN + "시작 지점 지정 환료");
            } else return false;
        } else if (command.getName().equals("deathpoint")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                if (args.length == 0) {
                    if (Events.deathitem.containsKey(uuid)) {
                        if (Bukkit.getEntity(Events.deathitem.get(uuid).getUniqueId()) != null) {
                            player.teleport(Events.deathitem.get(uuid));
                        }
                    }
                } else if (args.length == 1) {
                    if (args[0].equals("tp") || args[0].equals("teleport")) {
                        if (Events.deathitem.containsKey(uuid)) {
                            if (Bukkit.getEntity(Events.deathitem.get(uuid).getUniqueId()) != null) {
                                player.teleport(Events.deathitem.get(uuid));
                                Events.deathitem.get(uuid).getWorld().spawnParticle(Particle.END_ROD, Events.deathitem.get(uuid).getLocation(), 10, 1, 1, 1, 0);
                                player.playSound(Events.deathitem.get(uuid).getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                                Events.deathitem.get(uuid).remove();
                                Events.deathitem.remove(uuid);
                                Events.timelimit.get(uuid).cancel();
                                Events.timelimit.remove(uuid);
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_RED + "당신의 데스포인트가 사라졌거나 없습니다. ");
                        }
                    } else if (args[0].equals("delete") || args[0].equals("dt")) {
                        if (Events.deathitem.containsKey(uuid)) {
                            if (Bukkit.getEntity(Events.deathitem.get(uuid).getUniqueId()) != null) {
                                Events.deathitem.get(uuid).getWorld().spawnParticle(Particle.WHITE_ASH, Events.deathitem.get(uuid).getLocation(), 10, 1, 1, 1, 1);
                                Events.deathitem.get(uuid).remove();
                                Events.deathitem.remove(uuid);
                                Events.timelimit.get(uuid).cancel();
                                Events.timelimit.remove(uuid);
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_RED + "당신의 데스포인트가 사라졌거나 없습니다. ");
                        }
                    }
                }
            }
            return true;
        } else if (command.getName().equals("타자연습")) {
            BukkitRunnable game = new BukkitRunnable() {
                int count = 0;
                int playernum = 0;
                Player waiting = null;

                @Override
                public void run() {
                    if (typingpractice) {
                        if (Bukkit.getOnlinePlayers().size() > 1) {
                            if (round != 0) {
                                if (main == null) {
                                    ArrayList<Player> allplayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                                    int random = new Random().nextInt(allplayers.size());
                                    count += 1;
                                    Player picked = allplayers.get(random);
                                    if (count < 20) {
                                        String name = allplayers.get(playernum).getName();
                                        for (Player all : Bukkit.getOnlinePlayers()) {
                                            all.sendTitle(name, "", 0, 3, 0);
                                            all.getWorld().playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1);
                                        }
                                        if (playernum < ((allplayers.size()) - 1)) {
                                            playernum += 1;
                                        } else playernum = 0;
                                    } else if (count < 30) {
                                        if (count == 20) {
                                            waiting = picked;
                                            for (Player all : Bukkit.getOnlinePlayers()) {
                                                all.getWorld().playSound(all.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
                                            }
                                        }
                                        if (count % 2 == 0) {
                                            for (Player all : Bukkit.getOnlinePlayers()) {
                                                all.sendTitle(ChatColor.AQUA + waiting.getName(), "", 0, 3, 0);
                                            }
                                        } else {
                                            for (Player all : Bukkit.getOnlinePlayers()) {
                                                all.sendTitle(ChatColor.DARK_GRAY + waiting.getName(), "", 0, 3, 0);
                                            }
                                        }
                                    } else if (count < 40) {
                                        main = waiting;
                                        waiting = null;
                                        for (Player all : Bukkit.getOnlinePlayers()) {
                                            all.sendTitle(ChatColor.GOLD + main.getName(), "", 0, 3, 0);
                                        }
                                        count = 0;
                                    }
                                } else if (sentence == null) {
                                    if (countdown != 10) {
                                        countdown = 10;
                                    }
                                    for (Player all : Bukkit.getOnlinePlayers()) {
                                        all.sendTitle(ChatColor.GOLD + main.getName(), "문장을 입력하세요!", 0, 3, 0);
                                    }
                                } else {
                                    if (countdown != 0) {
                                        count += 1;
                                        if (count >= 20) {
                                            countdown -= 1;
                                            count = 0;
                                        }
                                        for (Player all : Bukkit.getOnlinePlayers()) {
                                            all.sendTitle(ChatColor.GOLD + "" + countdown, sentence, 0, 3, 0);
                                        }
                                    } else {
                                        for (Player all : Bukkit.getOnlinePlayers()) {
                                            all.sendTitle(ChatColor.DARK_RED + "게임 오버!", "", 0, 3, 0);
                                        }
                                        count += 1;
                                        if (count >= 30) {
                                            round -= 1;
                                            main = null;
                                            sentence = null;
                                            count = 1;
                                            waiting = null;
                                        }
                                    }
                                }
                            } else {
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    all.resetTitle();
                                    all.sendTitle(ChatColor.GOLD + "The End", ChatColor.GOLD + "게임 끝!");
                                    all.getWorld().playSound(all.getLocation(), Sound.BLOCK_CHEST_LOCKED, 100, 0);
                                }
                                count = 0;
                                countdown = 10;
                                typingpractice = false;
                                sentence = null;
                                main = null;
                                cancel();
                            }
                        } else {
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                all.sendMessage(ChatColor.RED + "해당 게임이 종료되었습니다. " + ChatColor.GRAY + "플레이어 부족");
                                all.resetTitle();
                            }
                            countdown = 10;
                            count = 0;
                            typingpractice = false;
                            main = null;
                            sentence = null;
                            cancel();
                        }
                    } else {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            all.sendMessage(ChatColor.RED + "해당 게임이 종료되었습니다. " + ChatColor.GRAY + "명령어" + ChatColor.DARK_GRAY + "[/타자연습 stop]");
                            all.resetTitle();
                        }
                        countdown = 10;
                        count = 0;
                        typingpractice = false;
                        main = null;
                        sentence = null;
                        cancel();
                    }
                }
            };
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "/타자연습 <start|stop>");
                } else if (args.length == 1) {
                    if (args[0].equals("start")) {
                        if (!typingpractice) {
                            if (Bukkit.getOnlinePlayers().size() > 1) {
                                typingpractice = true;
                                round = 10;
                                game.runTaskTimer(this, 1L, 1L);
                            } else
                                player.sendMessage(ChatColor.RED + "해당 게임을 하기 위해선 플레이어가 최소 2명 이상 있어야합니다. " + ChatColor.GRAY + "\n현재 플레이어 수(" + Bukkit.getOnlinePlayers().size() + ")");
                        } else player.sendMessage(ChatColor.RED + "이미 게임이 실행 중입니다. ");
                    } else if (args[0].equals("stop")) {
                        if (typingpractice) {
                            typingpractice = false;
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                all.sendMessage(ChatColor.AQUA + "[타자 연습]이(가) 성공적으로 종료되었습니다. ");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "해당 게임이 진행되고 있지않습니다. ");
                        }
                    } else player.sendMessage(ChatColor.RED + "/타자연습 <start|stop>");
                }
            } else return false;
        }
        return true;
    }
}

