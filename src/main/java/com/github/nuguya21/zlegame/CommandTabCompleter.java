package com.github.nuguya21.zlegame;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandTabCompleter implements TabCompleter {

    private static final String[] typingpractice = { "start", "stop"};
    private static final String[] deathpoint = { "teleport", "delete"};

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        if (command.getName().equals("타자연습")) {
            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], Arrays.asList(typingpractice), completions);
            }
        }
        else if (command.getName().equals("deathpoint")) {
            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], Arrays.asList(deathpoint), completions);
            }
        }
        Collections.sort(completions);
        return completions;
    }
}
