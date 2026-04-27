package onevnl.ru.elytrya.commands.subcommands;

import org.bukkit.command.CommandSender;

public interface SubCommand {
    String getName();
    String getPermission();
    void execute(CommandSender sender, String[] args);
    
    default boolean hasPermission(CommandSender sender) {
        String perm = getPermission();
        return perm == null || sender.hasPermission(perm);
    }
}