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
import onevnl.ru.elytrya.api.managers.MessageManager;
import onevnl.ru.elytrya.commands.subcommands.AdminSubCommand;
import onevnl.ru.elytrya.commands.subcommands.InfoSubCommand;
import onevnl.ru.elytrya.commands.subcommands.LinkSubCommand;
import onevnl.ru.elytrya.commands.subcommands.ReloadSubCommand;
import onevnl.ru.elytrya.commands.subcommands.SubCommand;
import onevnl.ru.elytrya.models.BoostyUser;


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

    private void registerSubCommand(SubCommand sub) {
        subCommands.put(sub.getName(), sub);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        MessageManager msg = client.getMessageManager();
        SubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub != null && (sub.getPermission() == null || sender.hasPermission(sub.getPermission()))) {
            sub.execute(sender, args);
        } else {
            sender.sendMessage(msg.getMessage("error_not_found"));

        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
            List<String> userHelp = client.getMessageManager().getMessageList("help_menu");
            if (userHelp != null) {
                for (String line : userHelp) {
                    sender.sendMessage(line);
                }
            }

            if (sender.hasPermission("boosty.admin")) {
                List<String> adminHelp = client.getMessageManager().getMessageList("help_menu_admin");
                if (adminHelp != null) {
                    for (String line : adminHelp) {
                        sender.sendMessage(line);
                    }
                }
            }
        }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            subCommands.keySet().stream().filter(s -> s.startsWith(input)).forEach(completions::add);
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("admin") && sender.hasPermission("boosty.admin")) {
            handleAdminTab(completions, args);
        }
        return completions;
    }

    private void handleAdminTab(List<String> list, String[] args) {
        if (args.length == 2) {
            String input = args[1].toLowerCase();
            for (String s : new String[]{"unlink", "info", "forcelink","forcesync"}) {
                if (s.startsWith(input)) {
                    list.add(s);
                }
            }
        } else if (args.length == 3) {
            handlePlayerTab(list, args);
        }
    }

    private void handlePlayerTab(List<String> list, String[] args) {
        String sub = args[1].toLowerCase();
        String input = args[2].toLowerCase();
        if (sub.equals("unlink") || sub.equals("info")) {
            client.getDatabase().getAllUsers().stream()
                .map(BoostyUser::playerName)
                .filter(n -> n.toLowerCase().startsWith(input))
                .forEach(list::add);
        } else if (sub.equals("forcelink")) {
            Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(n -> n.toLowerCase().startsWith(input))
                .forEach(list::add);
        }
    }
}