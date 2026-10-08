package com.mcje.tracker.command;

import com.mcje.tracker.TrackerPlugin;
import com.mcje.tracker.config.CategorySettings;
import com.mcje.tracker.config.PlayerTrackSettings;
import com.mcje.tracker.tracking.TrackCategory;
import com.mcje.tracker.util.MaterialKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * /tracker 指令：三类追踪目标的开关、范围与增删，带 Tab 补全。
 */
public final class TrackerCommand implements CommandExecutor, TabCompleter {

    private static final List<String> CATEGORIES = List.of("item", "block", "player");
    private static final List<String> ACTIONS = List.of("add", "del", "on", "off");
    private static final List<String> RANGE_HINTS = List.of("5", "10", "15", "20", "30", "50");
    private static final int MIN_RANGE = 1;
    private static final int MAX_RANGE = 128;
    private static final int MAX_SUGGESTIONS = 50;

    private final TrackerPlugin plugin;

    public TrackerCommand(TrackerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("该指令只能由玩家执行。", NamedTextColor.RED));
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("mcjetracker.reload")) {
                player.sendMessage(Component.text("你没有权限执行该操作。", NamedTextColor.RED));
                return true;
            }
            plugin.reloadAll();
            player.sendMessage(Component.text("MCJE_Tracker 配置已重载。", NamedTextColor.GREEN));
            return true;
        }

        if (args.length < 2) {
            usage(player);
            return true;
        }

        TrackCategory category = TrackCategory.byKey(args[0]);
        if (category == null) {
            usage(player);
            return true;
        }

        PlayerTrackSettings settings = plugin.manager().settings(player);
        CategorySettings categorySettings = settings.get(category);
        String action = args[1].toLowerCase(Locale.ROOT);

        switch (action) {
            case "on" -> handleToggle(player, category, categorySettings, true, args);
            case "off" -> handleToggle(player, category, categorySettings, false, args);
            case "add" -> handleTarget(player, category, categorySettings, true, args);
            case "del" -> handleTarget(player, category, categorySettings, false, args);
            default -> usage(player);
        }
        return true;
    }

    private void handleToggle(Player player, TrackCategory category, CategorySettings settings,
                              boolean enable, String[] args) {
        if (enable && args.length >= 3) {
            int range;
            try {
                range = Integer.parseInt(args[2]);
            } catch (NumberFormatException exception) {
                player.sendMessage(Component.text("追踪格数必须是整数。", NamedTextColor.RED));
                return;
            }
            if (range < MIN_RANGE || range > MAX_RANGE) {
                player.sendMessage(Component.text("追踪格数需在 " + MIN_RANGE + "~" + MAX_RANGE + " 之间。", NamedTextColor.RED));
                return;
            }
            settings.setRange(range);
        }

        settings.setEnabled(enable);
        if (enable) {
            player.sendMessage(Component.text()
                    .append(Component.text("已开启 ", NamedTextColor.GREEN))
                    .append(Component.text(category.key(), NamedTextColor.YELLOW))
                    .append(Component.text(" 追踪，范围 " + settings.range() + " 格，当前目标数 "
                            + settings.targets().size() + "。", NamedTextColor.GRAY))
                    .build());
        } else {
            player.sendMessage(Component.text("已关闭 " + category.key() + " 追踪。", NamedTextColor.GRAY));
        }
    }

    private void handleTarget(Player player, TrackCategory category, CategorySettings settings,
                              boolean add, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Component.text("请填写目标，例如：/tracker " + category.key() + " "
                    + (add ? "add" : "del") + " " + exampleFor(category), NamedTextColor.RED));
            return;
        }

        String raw = args[2];
        String key = resolveKey(category, raw, add, player);
        if (key == null) {
            return;
        }

        if (add) {
            if (settings.addTarget(key)) {
                player.sendMessage(Component.text("已添加 " + category.key() + " 目标：" + display(category, key),
                        NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text("目标已存在：" + display(category, key), NamedTextColor.YELLOW));
            }
        } else {
            if (settings.removeTarget(key)) {
                player.sendMessage(Component.text("已移除 " + category.key() + " 目标：" + display(category, key),
                        NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text("目标不存在：" + display(category, key), NamedTextColor.YELLOW));
            }
        }
    }

    /**
     * 把用户输入解析成用于匹配的键；物品 / 方块会校验材质是否存在，失败返回 null 并提示。
     */
    private String resolveKey(TrackCategory category, String raw, boolean add, Player player) {
        if (category == TrackCategory.PLAYER) {
            return raw.toLowerCase(Locale.ROOT);
        }
        Material material = Material.matchMaterial(raw);
        if (add) {
            boolean valid = material != null && (category == TrackCategory.BLOCK ? material.isBlock() : material.isItem());
            if (!valid) {
                player.sendMessage(Component.text("未知的" + (category == TrackCategory.BLOCK ? "方块" : "物品")
                        + "：" + raw, NamedTextColor.RED));
                return null;
            }
            return MaterialKeys.keyOf(material);
        }
        // 删除时允许直接按归一化键匹配
        return material != null ? MaterialKeys.keyOf(material) : MaterialKeys.normalize(raw);
    }

    private String display(TrackCategory category, String key) {
        return category == TrackCategory.PLAYER ? key : MaterialKeys.shortKey(key);
    }

    private String exampleFor(TrackCategory category) {
        return switch (category) {
            case ITEM -> "diamond";
            case BLOCK -> "diamond_ore";
            case PLAYER -> "Steve";
        };
    }

    private void usage(Player player) {
        player.sendMessage(Component.text("用法：", NamedTextColor.GOLD));
        player.sendMessage(Component.text("/tracker <item|block|player> <on|off> [追踪格数 默认15]", NamedTextColor.GRAY));
        player.sendMessage(Component.text("/tracker <item|block|player> <add|del> <目标>", NamedTextColor.GRAY));
        player.sendMessage(Component.text("/tracker reload", NamedTextColor.GRAY));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return List.of();
        }
        if (args.length == 1) {
            List<String> options = new ArrayList<>(CATEGORIES);
            if (player.hasPermission("mcjetracker.reload")) {
                options.add("reload");
            }
            return filter(options, args[0]);
        }

        TrackCategory category = TrackCategory.byKey(args[0]);
        if (category == null) {
            return List.of();
        }
        if (args.length == 2) {
            return filter(ACTIONS, args[1]);
        }

        String action = args[1].toLowerCase(Locale.ROOT);
        if (args.length == 3) {
            if (action.equals("add") || action.equals("del")) {
                return suggestTargets(player, category, action.equals("del"), args[2]);
            }
            if (action.equals("on")) {
                return filter(RANGE_HINTS, args[2]);
            }
        }
        return List.of();
    }

    private List<String> suggestTargets(Player player, TrackCategory category, boolean existingOnly, String prefix) {
        if (category == TrackCategory.PLAYER) {
            List<String> names = new ArrayList<>();
            if (existingOnly) {
                names.addAll(plugin.manager().settings(player).get(category).targets());
            } else {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    names.add(online.getName());
                }
            }
            return filter(names, prefix);
        }

        if (existingOnly) {
            List<String> existing = new ArrayList<>();
            for (String key : plugin.manager().settings(player).get(category).targets()) {
                existing.add(MaterialKeys.shortKey(key));
            }
            return filter(existing, prefix);
        }

        boolean block = category == TrackCategory.BLOCK;
        String lowerPrefix = prefix.toLowerCase(Locale.ROOT);
        List<String> suggestions = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.isAir() || material.isLegacy()) {
                continue;
            }
            if (block ? !material.isBlock() : !material.isItem()) {
                continue;
            }
            String name = MaterialKeys.shortKey(MaterialKeys.keyOf(material));
            if (!name.startsWith(lowerPrefix)) {
                continue;
            }
            suggestions.add(name);
            if (suggestions.size() >= MAX_SUGGESTIONS) {
                break;
            }
        }
        return suggestions;
    }

    private List<String> filter(List<String> options, String prefix) {
        String lowerPrefix = prefix.toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase(Locale.ROOT).startsWith(lowerPrefix)) {
                result.add(option);
            }
        }
        return result;
    }
}