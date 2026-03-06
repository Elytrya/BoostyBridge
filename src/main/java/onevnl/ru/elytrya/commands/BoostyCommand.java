package onevnl.ru.elytrya.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import onevnl.ru.elytrya.api.BoostyClient;
import onevnl.ru.elytrya.api.BoostyUser;
import onevnl.ru.elytrya.commands.subcommands.AdminSubCommand;
import onevnl.ru.elytrya.commands.subcommands.InfoSubCommand;
import onevnl.ru.elytrya.commands.subcommands.LinkSubCommand;
import onevnl.ru.elytrya.commands.subcommands.ReloadSubCommand;
import onevnl.ru.elytrya.commands.subcommands.SubCommand;

public class BoostyCommand implements CommandExecutor, TabCompleter {

    private final BoostyClient client;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public BoostyCommand(BoostyClient client) {
        this.client = client;
        registerSubCommand(new ReloadSubCommand(client));
        registerSubCommand(new LinkSubCommand(client));
        registerSubCommand(new InfoSubCommand(client));
        registerSubCommand(new AdminSubCommand(client));
    }

    private void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    private void sendHelp(CommandSender sender) {
        for (String line : client.getMessageManager().getMessageList("help_menu")) {
            sender.sendMessage(line);
        }
        if (sender.hasPermission("boosty.admin")) {
            for (String line : client.getMessageManager().getMessageList("help_menu_admin")) {
                sender.sendMessage(line);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());

        if (subCommand == null) {
            sendHelp(sender);
            return true;
        }

        if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(client.getMessageManager().getMessage("no_permission"));
            return true;
        }

        subCommand.execute(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if ("link".startsWith(args[0].toLowerCase())) completions.add("link");
            if ("info".startsWith(args[0].toLowerCase())) completions.add("info");
            if (sender.hasPermission("boosty.admin")) {
                if ("admin".startsWith(args[0].toLowerCase())) completions.add("admin");
                if ("reload".startsWith(args[0].toLowerCase())) completions.add("reload");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("admin") && sender.hasPermission("boosty.admin")) {
            if ("unlink".startsWith(args[1].toLowerCase())) completions.add("unlink");
            if ("info".startsWith(args[1].toLowerCase())) completions.add("info");
            if ("forcelink".startsWith(args[1].toLowerCase())) completions.add("forcelink");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("admin") && (args[1].equalsIgnoreCase("unlink") || args[1].equalsIgnoreCase("info")) && sender.hasPermission("boosty.admin")) {
            for (BoostyUser user : client.getDatabase().getAllUsers()) {
                if (user.playerName().toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(user.playerName());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("forcelink") && sender.hasPermission("boosty.admin")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(p.getName());
                }
            }
        }

        return completions;
    }
}