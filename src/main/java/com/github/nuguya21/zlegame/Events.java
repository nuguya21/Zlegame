package com.github.nuguya21.zlegame;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class Events implements Listener {
    Plugin plugin = Zlegame.getPlugin(Zlegame.class);

    HashMap<UUID, Boolean> chat = new HashMap<>();
    HashMap<UUID, Integer> score = new HashMap<>();
    HashMap<UUID, Boolean> swordevent = new HashMap<>();
    HashMap<UUID, Boolean> deatheffect = new HashMap<>();
    HashMap<UUID, Timer> ready2 = new HashMap<>();
    HashMap<UUID, Timer> ready1 = new HashMap<>();
    HashMap<UUID, Timer> start = new HashMap<>();
    HashMap<UUID, Timer> timer = new HashMap<>();
    HashMap<UUID, Boolean> blockbreak = new HashMap<>();
    HashMap<UUID, Integer> zilpoongchammod = new HashMap<>();
    HashMap<UUID, Boolean> nether_star_shuriken = new HashMap<>();
    HashMap<UUID, Boolean> damage_particle = new HashMap<>();
    HashMap<UUID, Boolean> player_can_see = new HashMap<>();
    public static HashMap<UUID, Boolean> aimtestpower = new HashMap<>();

    public void itemset(String display, Material ID, int data, int stack, List<String> lore, int loc, Inventory inventory) {
        ItemStack item = new MaterialData(ID, (byte) data).toItemStack(stack);
        ItemMeta items = item.getItemMeta();
        items.setDisplayName(display);
        items.setLore(lore);
        item.setItemMeta(items);
        inventory.setItem(loc, item);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!Zlegame.aimtest.containsKey(uuid)) {
            Zlegame.aimtest.put(uuid, Bukkit.createInventory(player, 45, ChatColor.DARK_GRAY + "에임 테스트"));
        }
        if (!Zlegame.setting.containsKey(uuid)) {
            Zlegame.setting.put(uuid, Bukkit.createInventory(player, 45, ChatColor.DARK_GRAY + "설정"));
        }
    }

    HashMap<UUID, Timer> cooltime = new HashMap<>();
    HashMap<UUID, Boolean> chatevent = new HashMap<>();

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        UUID uuid = player.getUniqueId();
        if (!chatevent.containsKey(uuid)) {
            chatevent.put(uuid, false);
        }
        if (Zlegame.typingpractice) {
            if (Zlegame.main != null) {
                if (Bukkit.getOnlinePlayers().size() > 1) {
                    if (player == Zlegame.main) {
                        if (Zlegame.sentence == null) {
                            if (message.length() <= 16) {
                                Zlegame.sentence = message;
                                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1);
                            } else {
                                player.sendMessage(ChatColor.RED + "제시어는 16글자 이하까지 가능합니다. " + ChatColor.GRAY + "\n해당 제시어의 글자 수(" + message.length() + ")");
                            }
                        }
                    } else if (Zlegame.sentence != null) {
                        if (Zlegame.countdown != 0) {
                            if (message.equals(Zlegame.sentence)) {
                                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
                                Zlegame.sentence = null;
                                Zlegame.main = player;
                                Zlegame.round -= 1;
                            }
                        }
                    }
                }
            }
        } else if (chatevent.get(uuid)) {
            if (!chat.containsKey(uuid)) {
                chat.put(uuid, false);
            } else if (chat.get(uuid)) {
                Random random = new Random();
                chat.put(uuid, false);
                if (!cooltime.containsKey(uuid)) {
                    cooltime.put(uuid, new Timer());
                }
                cooltime.get(uuid).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        chat.put(uuid, true);
                    }
                }, 10000);
                if (message.equals("자폭")) {
                    player.getWorld().createExplosion(player.getLocation(), 4, true, false, player);
                } else if (message.equals("발사")) {
                    Fireball fb = player.launchProjectile(Fireball.class);
                    fb.setVelocity(player.getLocation().getDirection().multiply(4));
                } else switch (random.nextInt(2)) {
                    case 0:
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 3);
                        player.getWorld().spawnParticle(Particle.NOTE, player.getLocation(), 10, 1, 1, 1, 1);
                        break;
                    case 1:
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        player.getWorld().spawnParticle(Particle.COMPOSTER, player.getLocation(), 10, 1, 1, 1, 0);
                        player.giveExp(15);
                        break;
                }
            }
        }
    }

    HashMap<UUID, Timer> resetvelocity = new HashMap<>();

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Entity targetEntity = event.getRightClicked();
        if (targetEntity instanceof LivingEntity) {
            LivingEntity targetLivingEntity = (LivingEntity) targetEntity;
            if (!swordevent.containsKey(uuid)) {
                swordevent.put(uuid, false);
            }
            if (swordevent.get(uuid)) {
                if (!resetvelocity.containsKey(uuid)) {
                    resetvelocity.put(uuid, new Timer());
                }
                if (!zilpoongchammod.containsKey(uuid)) {
                    zilpoongchammod.put(uuid, 1);
                }
                if (player.isSneaking()) {
                    if (!(targetEntity instanceof Player)) {
                        targetEntity.setCustomName("Dinnerbone");
                    }
                    event.setCancelled(true);
                } else if (player.isSprinting()) {
                    if (player.getInventory().getItemInMainHand().getType().equals(Material.IRON_SWORD)) {
                        player.swingMainHand();
                        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.1, 0.1, 0.1, 0.1);
                        player.damage(2, player);
                        if (zilpoongchammod.get(uuid) == 1) {
                            player.setVelocity(player.getLocation().getDirection().multiply(75));
                            resetvelocity.get(uuid).schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    player.setVelocity(new Vector().multiply(0));
                                }
                            }, 50);
                        } else if (zilpoongchammod.get(uuid) == 2) {
                            player.setVelocity(player.getLocation().getDirection().multiply(2));
                        }
                        targetLivingEntity.damage(6, player);
                        targetLivingEntity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, targetLivingEntity.getLocation().getX(), (targetLivingEntity.getLocation().getY()) + 1, targetLivingEntity.getLocation().getZ(), 5, 0.5, 0.5, 0.5, 1);
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 3);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        Inventory inventory = event.getClickedInventory();
        ClickType click = event.getClick();
        Random random = new Random();
        int randomslot = random.nextInt(45);
        if (Zlegame.aimtest.containsValue(inventory)) {//에임 테스트
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
                event.setCancelled(false);
                if (aimtestpower.get(uuid)) {
                    if (score.get(uuid) >= 2) {
                        score.put(uuid, score.get(uuid) - 2);
                    }
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 2);
                }
            } else if (click.isShiftClick()) {
                event.setCancelled(true);
            } else {
                event.setCancelled(true);
                switch (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())) {
                    case "시작":
                        Objects.requireNonNull(event.getClickedInventory()).clear();
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        itemset(ChatColor.DARK_RED + "3", Material.BARRIER, 1, 1, null, 22, event.getClickedInventory());
                        if (!ready2.containsKey(uuid)) {
                            ready2.put(uuid, new Timer());
                        }
                        ready2.get(uuid).schedule(new TimerTask() {
                            @Override
                            public void run() {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                                itemset(ChatColor.RED + "2", Material.BARRIER, 1, 1, null, 22, event.getClickedInventory());
                                if (!ready1.containsKey(uuid)) {
                                    ready1.put(uuid, new Timer());
                                }
                                ready1.get(uuid).schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                                        itemset(ChatColor.YELLOW + "1", Material.BARRIER, 1, 1, null, 22, event.getClickedInventory());
                                        if (!start.containsKey(uuid)) {
                                            start.put(uuid, new Timer());
                                        }
                                        start.get(uuid).schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                                aimtestpower.put(uuid, true);
                                                score.put(uuid, 0);
                                                event.getClickedInventory().clear();
                                                itemset(ChatColor.YELLOW + "+1", Material.EMERALD, 1, 1, null, randomslot, event.getClickedInventory());
                                                if (!timer.containsKey(uuid)) {
                                                    timer.put(uuid, new Timer());
                                                }
                                                timer.get(uuid).schedule(new TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        if (aimtestpower.get(uuid)) {
                                                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                                            event.getClickedInventory().clear();
                                                            aimtestpower.put(uuid, false);
                                                            itemset(ChatColor.GRAY + "결과", Material.BOOK, 1, 1, Collections.singletonList(ChatColor.GREEN + score.get(uuid).toString() + "점"), 22, event.getClickedInventory());
                                                            itemset(ChatColor.GREEN + "메뉴", Material.EMERALD, -1, 1, Arrays.asList(ChatColor.WHITE + "[Zlegame] 플러그인의 메뉴입니다"), 40, event.getClickedInventory());
                                                            itemset(ChatColor.GREEN + "뒤로가기", Material.ARROW, -1, 1, Arrays.asList(ChatColor.WHITE + "메뉴(으)로 돌아가기"), 39, event.getClickedInventory());
                                                            itemset(ChatColor.GREEN + "다시하기", Material.SPECTRAL_ARROW, -1, 1, Arrays.asList(ChatColor.YELLOW + "[AimTest] " + ChatColor.WHITE + "다시하기"), 41, event.getClickedInventory());
                                                        }
                                                    }
                                                }, 10000);
                                            }
                                        }, 1000);
                                    }
                                }, 1000);
                            }
                        }, 1000);
                        break;
                    case "개발 중":
                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                        player.sendTitle(ChatColor.DARK_RED + "개발 중입니다", "");
                        break;
                    case "+1":
                        if (aimtestpower.get(uuid)) {
                            score.put(uuid, score.get(uuid) + 1);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 3);
                            Objects.requireNonNull(event.getClickedInventory()).clear();
                            itemset(ChatColor.YELLOW + "+1", Material.EMERALD, 1, 1, null, randomslot, event.getClickedInventory());
                        }
                        break;
                    case "결과":
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                        player.closeInventory();
                        Objects.requireNonNull(event.getClickedInventory()).clear();
                        break;
                    case "뒤로가기":
                    case "메뉴":
                        player.openInventory(Zlegame.menu);
                        break;
                    case "다시하기":
                        if (!Zlegame.aimtest.containsKey(uuid)) {
                            Zlegame.aimtest.put(uuid, Bukkit.createInventory(player, 45, ChatColor.DARK_GRAY + "에임 테스트"));
                        }
                        if (!aimtestpower.containsKey(uuid)) {
                            aimtestpower.put(uuid, false);
                        }
                        if (!aimtestpower.get(uuid)) {
                            Zlegame.aimtest.get(uuid).clear();
                            itemset(ChatColor.GREEN + "시작", Material.SPECTRAL_ARROW, 1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 시작하세요!"), 21, Zlegame.aimtest.get(uuid));
                            itemset(ChatColor.DARK_RED + "개발 중", Material.BARRIER, 1, 1, null, 23, Zlegame.aimtest.get(uuid));
                            itemset(ChatColor.GREEN + "메뉴", Material.EMERALD, -1, 1, Arrays.asList(ChatColor.WHITE + "[Zlegame] 플러그인의 메뉴입니다"), 40, Zlegame.aimtest.get(uuid));
                            itemset(ChatColor.GREEN + "뒤로가기", Material.ARROW, -1, 1, Arrays.asList(ChatColor.WHITE + "메뉴(으)로 돌아가기"), 39, Zlegame.aimtest.get(uuid));
                            player.openInventory(Zlegame.aimtest.get(uuid));
                        } else {
                            aimtestpower.put(uuid, false);
                            timer.get(uuid).cancel();
                            timer.put(uuid, new Timer());
                            player.sendMessage(Zlegame.prefix + ChatColor.YELLOW + "[AimTest] " + ChatColor.DARK_RED + "게임 실행 중에 오류가 발생했습니다. ");
                        }
                }
            }
        } else if (inventory == Zlegame.menu) {
            if (event.getCurrentItem() == null || !Objects.requireNonNull(event.getCurrentItem()).hasItemMeta()) {
                event.setCancelled(false);
            } else if (click.isShiftClick()) {
                event.setCancelled(true);
            } else {
                event.setCancelled(true);
                switch (ChatColor.stripColor(Objects.requireNonNull(event.getCurrentItem()).getItemMeta().getDisplayName())) {
                    case "에임 테스트":
                        if (!Zlegame.aimtest.containsKey(uuid)) {
                            Zlegame.aimtest.put(uuid, Bukkit.createInventory(player, 45, ChatColor.DARK_GRAY + "에임 테스트"));
                        }
                        if (!aimtestpower.containsKey(uuid)) {
                            aimtestpower.put(uuid, false);
                        }
                        if (!aimtestpower.get(uuid)) {
                            Zlegame.aimtest.get(uuid).clear();
                            itemset(ChatColor.GREEN + "시작", Material.SPECTRAL_ARROW, 1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 시작하세요!"), 21, Zlegame.aimtest.get(uuid));
                            itemset(ChatColor.DARK_RED + "개발 중", Material.BARRIER, 1, 1, null, 23, Zlegame.aimtest.get(uuid));
                            itemset(ChatColor.GREEN + "메뉴", Material.EMERALD, -1, 1, Arrays.asList(ChatColor.WHITE + "[Zlegame] 플러그인의 메뉴입니다"), 40, Zlegame.aimtest.get(uuid));
                            itemset(ChatColor.GREEN + "뒤로가기", Material.ARROW, -1, 1, Arrays.asList(ChatColor.WHITE + "메뉴(으)로 돌아가기"), 39, Zlegame.aimtest.get(uuid));
                            player.openInventory(Zlegame.aimtest.get(uuid));
                        } else {
                            aimtestpower.put(uuid, false);
                            timer.get(uuid).cancel();
                            timer.put(uuid, new Timer());
                            player.sendMessage(Zlegame.prefix + ChatColor.YELLOW + "[AimTest] " + ChatColor.DARK_RED + "게임 실행 중에 오류가 발생했습니다. ");
                        }
                        break;
                    case "질풍참 파쿠르":
                        player.closeInventory();
                        if (Zlegame.num != -1) {
                            player.playSound(Zlegame.zilpoongcham[Zlegame.num], Sound.ENTITY_ENDER_EYE_DEATH, 1, 1);
                            player.teleport(Zlegame.zilpoongcham[Zlegame.num]);
                            player.sendMessage(ChatColor.GREEN + "입장!");
                        } else {
                            player.sendMessage(ChatColor.RED + "시작 지점을 지정해주세요. ");
                        }
                        break;
                    case "설정":
                        if (!Zlegame.setting.containsKey(uuid)) {
                            Zlegame.setting.put(uuid, Bukkit.createInventory(player, 45, ChatColor.DARK_GRAY + "설정"));
                        } else Zlegame.setting.get(uuid).clear();
                        if (!chatevent.containsKey(uuid)) {
                            chatevent.put(uuid, false);
                        }
                        if (!swordevent.containsKey(uuid)) {
                            swordevent.put(uuid, false);
                        }
                        if (!deatheffect.containsKey(uuid)) {
                            deatheffect.put(uuid, false);
                        }
                        if (!blockbreak.containsKey(uuid)) {
                            blockbreak.put(uuid, false);
                        }
                        if (chatevent.get(uuid)) {
                            itemset(ChatColor.GREEN + "채팅 이벤트", Material.YELLOW_CARPET, 1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 11, Zlegame.setting.get(uuid));
                        } else
                            itemset(ChatColor.DARK_GREEN + "채팅 이벤트", Material.GRAY_CARPET, 1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 11, Zlegame.setting.get(uuid));
                        if (swordevent.get(uuid)) {
                            itemset(ChatColor.GREEN + "칼 이벤트", Material.IRON_SWORD, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 15, Zlegame.setting.get(uuid));
                        } else
                            itemset(ChatColor.DARK_GREEN + "칼 이벤트", Material.IRON_SWORD, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 15, Zlegame.setting.get(uuid));
                        if (deatheffect.get(uuid)) {
                            itemset(ChatColor.GREEN + "데스 포인트", Material.CRYING_OBSIDIAN, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 29, Zlegame.setting.get(uuid));
                        } else
                            itemset(ChatColor.DARK_GREEN + "데스 포인트", Material.CRYING_OBSIDIAN, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 29, Zlegame.setting.get(uuid));
                        if (blockbreak.get(uuid)) {
                            itemset(ChatColor.GREEN + "깔끔한 채집", Material.DIAMOND_PICKAXE, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 33, Zlegame.setting.get(uuid));
                        } else
                            itemset(ChatColor.DARK_GREEN + "깔끔한 채집", Material.DIAMOND_PICKAXE, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 33, Zlegame.setting.get(uuid));
                        itemset(ChatColor.GREEN + "메뉴", Material.EMERALD, -1, 1, Arrays.asList(ChatColor.GRAY + "[Zlegame] 플러그인의 메뉴입니다"), 40, Zlegame.setting.get(uuid));
                        itemset(ChatColor.GREEN + "뒤로가기", Material.ARROW, -1, 1, Arrays.asList(ChatColor.GRAY + "메뉴(으)로 돌아가기"), 39, Zlegame.setting.get(uuid));
                        itemset(ChatColor.GREEN + "다음 페이지", Material.ARROW, -1, 1, Arrays.asList(ChatColor.YELLOW + "페이지 2"), 41, Zlegame.setting.get(uuid));
                        player.openInventory(Zlegame.setting.get(uuid));
                        break;
                }
            }
        } else if (Zlegame.setting.containsValue(inventory)) {
            if (event.getCurrentItem() == null || !Objects.requireNonNull(event.getCurrentItem()).hasItemMeta()) {
                event.setCancelled(false);
            } else if (click.isShiftClick()) {
                event.setCancelled(true);
            } else {
                event.setCancelled(true);
                switch (ChatColor.stripColor(Objects.requireNonNull(event.getCurrentItem()).getItemMeta().getDisplayName())) {
                    case "채팅 이벤트":
                        if (chatevent.get(uuid)) {
                            chatevent.put(uuid, false);
                            itemset(ChatColor.DARK_GREEN + "채팅 이벤트", Material.GRAY_CARPET, 1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 11, Objects.requireNonNull(event.getClickedInventory()));
                        } else {
                            chatevent.put(uuid, true);
                            itemset(ChatColor.GREEN + "채팅 이벤트", Material.YELLOW_CARPET, 1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 11, Objects.requireNonNull(event.getClickedInventory()));
                        }
                        break;
                    case "칼 이벤트":
                        if (swordevent.get(uuid)) {
                            swordevent.put(uuid, false);
                            itemset(ChatColor.DARK_GREEN + "칼 이벤트", Material.IRON_SWORD, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 15, Zlegame.setting.get(uuid));
                        } else {
                            swordevent.put(uuid, true);
                            itemset(ChatColor.GREEN + "칼 이벤트", Material.IRON_SWORD, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 15, Zlegame.setting.get(uuid));
                        }
                        break;
                    case "데스 포인트":
                        if (deatheffect.get(uuid)) {
                            deatheffect.put(uuid, false);
                            itemset(ChatColor.DARK_GREEN + "데스 포인트", Material.CRYING_OBSIDIAN, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 29, Zlegame.setting.get(uuid));
                        } else {
                            deatheffect.put(uuid, true);
                            itemset(ChatColor.GREEN + "데스 포인트", Material.CRYING_OBSIDIAN, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 29, Zlegame.setting.get(uuid));
                        }
                        break;
                    case "깔끔한 채집":
                        if (blockbreak.get(uuid)) {
                            blockbreak.put(uuid, false);
                            itemset(ChatColor.DARK_GREEN + "깔끔한 채집", Material.DIAMOND_PICKAXE, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 33, Zlegame.setting.get(uuid));
                        } else {
                            blockbreak.put(uuid, true);
                            itemset(ChatColor.GREEN + "깔끔한 채집", Material.DIAMOND_PICKAXE, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 33, Zlegame.setting.get(uuid));
                        }
                        break;
                    case "네더의 별 수리검":
                        if (nether_star_shuriken.get(uuid)) {
                            nether_star_shuriken.put(uuid, false);
                            itemset(ChatColor.DARK_GREEN + "네더의 별 수리검", Material.NETHER_STAR, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 11, Zlegame.setting.get(uuid));
                        } else {
                            nether_star_shuriken.put(uuid, true);
                            itemset(ChatColor.GREEN + "네더의 별 수리검", Material.NETHER_STAR, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 11, Zlegame.setting.get(uuid));
                        }
                        break;
                    case "블럭 보호":
                        if (blockprotect.get(uuid)) {
                            blockprotect.put(uuid, false);
                            itemset(ChatColor.DARK_GREEN + "블럭 보호", Material.BEDROCK, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 15, Zlegame.setting.get(uuid));
                        } else {
                            blockprotect.put(uuid, true);
                            itemset(ChatColor.GREEN + "블럭 보호", Material.BEDROCK, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 15, Zlegame.setting.get(uuid));
                        }
                        break;
                    case "플레이어 보기":
                        if (player_can_see.get(uuid)) {
                            player_can_see.put(uuid, false);
                            itemset(ChatColor.DARK_GREEN + "플레이어 보기", Material.LIME_DYE, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 29, Zlegame.setting.get(uuid));
                            for (Player a : Bukkit.getOnlinePlayers()) {
                                player.hidePlayer(a);
                            }
                        } else {
                            player_can_see.put(uuid, true);
                            itemset(ChatColor.GREEN + "플레이어 보기", Material.GRAY_DYE, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 29, Zlegame.setting.get(uuid));
                            for (Player a : Bukkit.getOnlinePlayers()) {
                                player.canSee(a);
                            }
                        }
                        break;
                    case "데미지 표시":
                        if (damage_particle.get(uuid)) {
                            damage_particle.put(uuid, false);
                            itemset(ChatColor.DARK_GREEN + "데미지 표시", Material.GRAY_CARPET, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 33, Zlegame.setting.get(uuid));
                        } else {
                            damage_particle.put(uuid, true);
                            itemset(ChatColor.GREEN + "데미지 표시", Material.LIME_CARPET, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 33, Zlegame.setting.get(uuid));
                        }
                        break;
                    case "메뉴":
                    case "뒤로가기":
                        player.openInventory(Zlegame.menu);
                        break;
                    case "다음 페이지":
                        Zlegame.setting.get(uuid).clear();
                        if (!nether_star_shuriken.containsKey(uuid)) {
                            nether_star_shuriken.put(uuid, false);
                        }
                        if (!blockprotect.containsKey(uuid)) {
                            blockprotect.put(uuid, false);
                        }
                        if (!player_can_see.containsKey(uuid)) {
                            player_can_see.put(uuid, false);
                        }
                        if (!damage_particle.containsKey(uuid)) {
                            damage_particle.put(uuid, false);
                        }
                        if (nether_star_shuriken.get(uuid)) {
                            itemset(ChatColor.GREEN + "네더의 별 수리검", Material.NETHER_STAR, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 11, Zlegame.setting.get(uuid));
                        } else
                            itemset(ChatColor.DARK_GREEN + "네더의 별 수리검", Material.NETHER_STAR, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 11, Zlegame.setting.get(uuid));
                        if (blockprotect.get(uuid)) {
                            itemset(ChatColor.GREEN + "블럭 보호", Material.BEDROCK, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 15, Zlegame.setting.get(uuid));
                        } else
                            itemset(ChatColor.DARK_GREEN + "블럭 보호", Material.BEDROCK, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 15, Zlegame.setting.get(uuid));
                        if (player_can_see.get(uuid)) {
                            itemset(ChatColor.GREEN + "플레이어 보기", Material.LIME_DYE, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 29, Zlegame.setting.get(uuid));
                        } else
                            itemset(ChatColor.DARK_GREEN + "플레이어 보기", Material.GRAY_DYE, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 29, Zlegame.setting.get(uuid));
                        if (damage_particle.get(uuid)) {
                            itemset(ChatColor.GREEN + "데미지 표시", Material.LIME_CARPET, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 33, Zlegame.setting.get(uuid));
                        } else
                            itemset(ChatColor.DARK_GREEN + "데미지 표시", Material.GRAY_CARPET, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 33, Zlegame.setting.get(uuid));
                        itemset(ChatColor.GREEN + "메뉴", Material.EMERALD, -1, 1, Arrays.asList(ChatColor.GRAY + "[Zlegame] 플러그인의 메뉴입니다"), 40, Zlegame.setting.get(uuid));
                        itemset(ChatColor.GREEN + "이전 페이지", Material.ARROW, -1, 1, Arrays.asList(ChatColor.YELLOW + "페이지 1"), 39, Zlegame.setting.get(uuid));
                        break;
                    case "이전 페이지":
                        if (!Zlegame.setting.containsKey(uuid)) {
                            Zlegame.setting.put(uuid, Bukkit.createInventory(player, 45, ChatColor.DARK_GRAY + "설정"));
                        } else Zlegame.setting.get(uuid).clear();
                        if (!chatevent.containsKey(uuid)) {
                            chatevent.put(uuid, false);
                        }
                        if (!swordevent.containsKey(uuid)) {
                            swordevent.put(uuid, false);
                        }
                        if (!deatheffect.containsKey(uuid)) {
                            deatheffect.put(uuid, false);
                        }
                        if (!blockbreak.containsKey(uuid)) {
                            blockbreak.put(uuid, false);
                        }
                        if (chatevent.get(uuid)) {
                            itemset(ChatColor.GREEN + "채팅 이벤트", Material.YELLOW_CARPET, 1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 11, Zlegame.setting.get(uuid));
                        } else
                            itemset(ChatColor.DARK_GREEN + "채팅 이벤트", Material.GRAY_CARPET, 1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 11, Zlegame.setting.get(uuid));
                        if (swordevent.get(uuid)) {
                            itemset(ChatColor.GREEN + "칼 이벤트", Material.IRON_SWORD, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 15, Zlegame.setting.get(uuid));
                        } else
                            itemset(ChatColor.DARK_GREEN + "칼 이벤트", Material.IRON_SWORD, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 15, Zlegame.setting.get(uuid));
                        if (deatheffect.get(uuid)) {
                            itemset(ChatColor.GREEN + "데스 포인트", Material.CRYING_OBSIDIAN, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 29, Zlegame.setting.get(uuid));
                        } else
                            itemset(ChatColor.DARK_GREEN + "데스 포인트", Material.CRYING_OBSIDIAN, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 29, Zlegame.setting.get(uuid));
                        if (blockbreak.get(uuid)) {
                            itemset(ChatColor.GREEN + "깔끔한 채집", Material.DIAMOND_PICKAXE, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 비활성화"), 33, Zlegame.setting.get(uuid));
                        } else
                            itemset(ChatColor.DARK_GREEN + "깔끔한 채집", Material.DIAMOND_PICKAXE, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 활성화"), 33, Zlegame.setting.get(uuid));
                        itemset(ChatColor.GREEN + "메뉴", Material.EMERALD, -1, 1, Arrays.asList(ChatColor.GRAY + "[Zlegame] 플러그인의 메뉴입니다"), 40, Zlegame.setting.get(uuid));
                        itemset(ChatColor.GREEN + "뒤로가기", Material.ARROW, -1, 1, Arrays.asList(ChatColor.GRAY + "메뉴(으)로 돌아가기"), 39, Zlegame.setting.get(uuid));
                        itemset(ChatColor.GREEN + "다음 페이지", Material.ARROW, -1, 1, Arrays.asList(ChatColor.YELLOW + "페이지 2"), 41, Zlegame.setting.get(uuid));
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        UUID uuid = player.getUniqueId();
        if (inventory.equals(Zlegame.aimtest.get(uuid))) {
            if (aimtestpower.get(uuid)) {
                player.sendTitle(ChatColor.DARK_RED + "게임 중단", "");
                aimtestpower.put(uuid, false);
                inventory.clear();
                if (!timer.containsKey(uuid)) {
                    timer.put(uuid, new Timer());
                }
                timer.get(uuid).cancel();
                timer.put(uuid, new Timer());
            } else {
                if (!ready2.containsKey(uuid)) {
                    ready2.put(uuid, new Timer());
                }
                ready2.get(uuid).cancel();
                ready2.put(uuid, new Timer());
                if (!ready1.containsKey(uuid)) {
                    ready1.put(uuid, new Timer());
                }
                ready1.get(uuid).cancel();
                ready1.put(uuid, new Timer());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        UUID uuid = player.getUniqueId();
        if (action.equals(Action.RIGHT_CLICK_AIR)) {
            if (player.isSneaking()) {
                if (!player.isSprinting()) {
                    if (player.getInventory().getItemInMainHand().getType().equals(Material.EMERALD)) {
                        player.openInventory(Zlegame.menu);
                        Zlegame.menu.clear();
                        itemset(ChatColor.GRAY + "설정", Material.COMMAND_BLOCK, 1, 1, null, 44, Zlegame.menu);
                        itemset(ChatColor.YELLOW + "에임 테스트", Material.SPECTRAL_ARROW, 1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 입장"), 10, Zlegame.menu);
                        itemset(ChatColor.WHITE + "질풍참 파쿠르", Material.IRON_SWORD, -1, 1, Arrays.asList(ChatColor.GRAY + "우클릭으로 입장"), 11, Zlegame.menu);
                        itemset(ChatColor.GREEN + "메뉴", Material.EMERALD, -1, 1, Arrays.asList(ChatColor.WHITE + "[Zlegame] 플러그인의 메뉴입니다"), 40, Zlegame.menu);
                    } else if (player.getInventory().getItemInMainHand().getType().equals(Material.IRON_SWORD)) {
                        if (!swordevent.containsKey(uuid)) {
                            swordevent.put(uuid, false);
                        }
                        if (swordevent.get(uuid)) {
                            if (!zilpoongchammod.containsKey(uuid)) {
                                zilpoongchammod.put(uuid, 1);
                            }
                            if (zilpoongchammod.get(uuid) < 2) {
                                zilpoongchammod.put(uuid, zilpoongchammod.get(uuid) + 1);
                            } else zilpoongchammod.put(uuid, 1);
                            if (zilpoongchammod.get(uuid) == 1) {
                                player.sendActionBar(ChatColor.GREEN + "질풍참 모드 : 안날라감");
                            } else if (zilpoongchammod.get(uuid) == 2) {
                                player.sendActionBar(ChatColor.GREEN + "질풍참 모드 : 날라감");
                            }
                        }
                    }
                }
            }
            if (player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_SWORD)) {
                if (!swordevent.containsKey(uuid)) {
                    swordevent.put(uuid, false);
                }
                if (swordevent.get(uuid)) {
                    Fireball fb = player.launchProjectile(Fireball.class);
                    fb.setVelocity(player.getLocation().getDirection().multiply(2));
                }
            }
            if (!nether_star_shuriken.containsKey(uuid)) {
                nether_star_shuriken.put(uuid, false);
            }
            if (nether_star_shuriken.get(uuid)) {
                if (player.getInventory().getItemInMainHand().getType().equals(Material.NETHER_STAR)) {
                    ArmorStand bullet = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
                    Snowball snowball_bullet = player.launchProjectile(Snowball.class, player.getLocation().getDirection().multiply(3));
                    Location snowballloc = snowball_bullet.getLocation();
                    snowball_bullet.setSilent(true);
                    snowball_bullet.setItem(new ItemStack(Material.STONE_BUTTON));
                    snowball_bullet.setInvulnerable(true);
                    bullet.setItem(EquipmentSlot.HEAD, new ItemStack(Material.NETHER_STAR));
                    bullet.setMarker(false);
                    bullet.setInvisible(true);
                    bullet.setSilent(true);
                    bullet.setCanMove(true);
                    bullet.setGravity(true);
                    bullet.setHeadPose(new EulerAngle(90, 0, 0));
                    bullet.setBasePlate(false);
                    bullet.setSmall(true);
                    Location loc = snowballloc.add(0,-0.6,-0.6);
                    bullet.teleport(loc);
                    BukkitRunnable bullet_spin = new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (!snowball_bullet.isDead()) {
                                if (snowball_bullet.getLocation().getBlock().getType().isEmpty()) {
                                    bullet.setHeadPose(bullet.getHeadPose().add(0, 0, 10));
                                    Location snowballloc = snowball_bullet.getLocation();
                                    Location loc = snowballloc.add(0,-0.6,-0.6);
                                    bullet.teleport(loc);
                                } else {
                                    snowball_bullet.remove();
                                    bullet.remove();
                                    cancel();
                                }
                            } else bullet.remove();
                        }
                    };
                    bullet_spin.runTaskTimer(plugin, 1, 1);
                }
            }
        }
    }

    public static HashMap<UUID, Timer> timelimit = new HashMap<>();
    public static HashMap<UUID, Item> deathitem = new HashMap<>();

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();
        if (!deatheffect.containsKey(uuid)) {
            deatheffect.put(uuid, false);
        }
        if (deatheffect.get(uuid)) {
            if (!deathitem.containsKey(uuid)) {
                if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                    deathitem.put(uuid, player.getWorld().dropItem(player.getLocation(), player.getInventory().getItemInMainHand()));
                } else
                    deathitem.put(uuid, player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.BARRIER)));
                deathitem.get(uuid).setOwner(uuid);
                deathitem.get(uuid).setThrower(uuid);
                deathitem.get(uuid).setCanPlayerPickup(false);
                deathitem.get(uuid).setWillAge(false);
                deathitem.get(uuid).setCustomName(player.getName() + "님의 데스포인트");
                deathitem.get(uuid).setCustomNameVisible(true);
                if (timelimit.containsKey(uuid)) {
                    timelimit.get(uuid).cancel();
                    timelimit.remove(uuid);
                }
            } else if (Bukkit.getEntity(deathitem.get(uuid).getUniqueId()) == null) {
                deathitem.put(uuid, player.getWorld().dropItem(player.getLocation(), player.getInventory().getItemInMainHand()));
                deathitem.get(uuid).setOwner(uuid);
                deathitem.get(uuid).setThrower(uuid);
                deathitem.get(uuid).setCanPlayerPickup(false);
                deathitem.get(uuid).setWillAge(false);
                deathitem.get(uuid).setCustomName(player.getName() + "님의 데스포인트");
                deathitem.get(uuid).setCustomNameVisible(true);
                if (timelimit.containsKey(uuid)) {
                    timelimit.get(uuid).cancel();
                    timelimit.remove(uuid);
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!deatheffect.containsKey(uuid)) {
            deatheffect.put(uuid, false);
        }
        if (deatheffect.get(uuid)) {
            if (deathitem.containsKey(uuid)) {
                if (Bukkit.getEntity(deathitem.get(uuid).getUniqueId()) != null) {
                    if (!timelimit.containsKey(uuid)) {
                        TextComponent teleport = new TextComponent();
                        teleport.setText("[예]");
                        teleport.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                        teleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpoint tp"));
                        teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/deathpoint teleport").create()));
                        TextComponent delete = new TextComponent();
                        delete.setText("[아니요]");
                        delete.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                        delete.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deathpoint dt"));
                        delete.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/deathpoint delete").create()));
                        player.sendMessage("-------------------------\n\n" + ChatColor.YELLOW + "당신의 데스포인트로 가시겠습니까?" + ChatColor.RESET + "\n(10초 뒤에 사라집니다)\n\n-------------------------");
                        player.sendMessage(teleport);
                        player.sendMessage(delete);
                        timelimit.put(uuid, new Timer());
                        timelimit.get(uuid).schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (deathitem.containsKey(uuid)) {
                                    if (Bukkit.getEntity(deathitem.get(uuid).getUniqueId()) != null) {
                                        deathitem.get(uuid).remove();
                                        timelimit.remove(uuid);
                                        player.sendMessage(ChatColor.DARK_RED + "당신의 데스포인트가 사라졌습니다. ");
                                    }
                                    deathitem.remove(uuid);
                                }
                            }
                        }, 10000);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        UUID uuid = damager.getUniqueId();
        Entity entity = event.getEntity();
        double damage = event.getDamage();
        if (damager instanceof Snowball && entity instanceof LivingEntity) {
            if (((Snowball) damager).getItem().getType().equals(Material.STONE_BUTTON)) {
                ((LivingEntity) entity).damage(3, damager);
            }
        }
        if (damager instanceof Player) {
            Player player = (Player) damager;
            if (!damage_particle.containsKey(uuid)) {
                damage_particle.put(uuid, false);
            }
            if (damage_particle.get(uuid)) {
                ArmorStand effect = entity.getWorld().spawn(entity.getLocation().add(0, 1, 0), ArmorStand.class);
                HashMap<ArmorStand, Timer> timer = new HashMap<>();
                timer.put(effect, new Timer());
                effect.setSilent(true);
                effect.setInvisible(true);
                effect.setBasePlate(false);
                effect.setInvulnerable(true);
                effect.setCustomName(ChatColor.DARK_RED + "" + damage);
                effect.setCustomNameVisible(true);
                effect.setCanMove(true);
                effect.setMarker(true);
                timer.get(effect).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        effect.remove();
                    }
                }, 1000);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        UUID uuid = player.getUniqueId();
        Collection<ItemStack> dropitems = block.getDrops(player.getInventory().getItemInMainHand(), player);
        for (Player a : Bukkit.getOnlinePlayers()) {
            UUID auuid = a.getUniqueId();
            if (!userblock.containsKey(uuid)) {
                Collection<Block> blocks = new ArrayList<>();
                userblock.put(uuid, blocks);
            }
            if (!userblock.containsKey(auuid)) {
                Collection<Block> blocks = new ArrayList<>();
                userblock.put(auuid, blocks);
            }
            if (userblock.get(auuid).contains(block)) {
                if (userblock.get(uuid).contains(block)) {
                    userblock.get(uuid).remove(block);
                    if (!blockbreak.containsKey(uuid)) {
                        blockbreak.put(uuid, false);
                    }
                    if (blockbreak.get(uuid)) {
                        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                            for (ItemStack items : dropitems) {
                                player.getInventory().addItem(items);
                                break;
                            }
                            player.giveExp(event.getExpToDrop());
                            event.setDropItems(false);
                            event.setExpToDrop(0);
                        }
                    }
                }
                else {
                    Player blockown = Bukkit.getPlayer(auuid);
                    if (blockown != null) {
                        String name = blockown.getName();
                        player.sendMessage(ChatColor.RED + name + "님의 블럭입니다. ");
                        event.setCancelled(true);
                    }
                }
            }else {
                if (!blockbreak.containsKey(uuid)) {
                    blockbreak.put(uuid, false);
                }
                if (blockbreak.get(uuid)) {
                    if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                        for (ItemStack items : dropitems) {
                            player.getInventory().addItem(items);
                            break;
                        }
                        player.giveExp(event.getExpToDrop());
                        event.setDropItems(false);
                        event.setExpToDrop(0);
                    }
                }
            }
            break;
        }
        for (OfflinePlayer o : Bukkit.getOfflinePlayers()) {
            UUID ouuid = o.getUniqueId();
            if (userblock.containsKey(ouuid)) {
                if (userblock.get(ouuid).contains(block)) {
                    if (!userblock.get(uuid).contains(block)) {
                        OfflinePlayer blockown = Bukkit.getOfflinePlayer(ouuid);
                        String name = blockown.getName();
                        player.sendMessage(ChatColor.RED + name + "님의 블럭입니다. ");
                        event.setCancelled(true);
                    }
                }
            }
            break;
        }

    }

    public static HashMap<UUID, Collection<Block>> userblock = new HashMap<>();
    public static HashMap<UUID, Boolean> blockprotect = new HashMap<>();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Block block = event.getBlock();
        if (!blockprotect.containsKey(uuid)) {
            blockprotect.put(uuid, false);
        }
        if (blockprotect.get(uuid)) {
            if (!userblock.containsKey(uuid)) {
                Collection<Block> blocks = new ArrayList<>();
                blocks.add(block);
                userblock.put(uuid, blocks);
            }
            Collection<Block> blocks = userblock.get(uuid);
            blocks.add(block);
            userblock.put(uuid, blocks);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        List<Block> eventBlocks = event.getBlocks();
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Block block : eventBlocks) {
                UUID uuid = player.getUniqueId();
                if (userblock.containsKey(uuid)) {
                    if (userblock.get(uuid).contains(block)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            for (Block block : eventBlocks) {
                UUID offuuid = offlinePlayer.getUniqueId();
                if (userblock.containsKey(offuuid)) {
                    if (userblock.get(offuuid).contains(block)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        List<Block> eventBlocks = event.getBlocks();
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Block block : eventBlocks) {
                UUID uuid = player.getUniqueId();
                if (userblock.containsKey(uuid)) {
                    if (userblock.get(uuid).contains(block)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            for (Block block : eventBlocks) {
                UUID offuuid = offlinePlayer.getUniqueId();
                if (userblock.containsKey(offuuid)) {
                    if (userblock.get(offuuid).contains(block)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
