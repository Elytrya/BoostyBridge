package onevnl.ru.elytrya.commands.subcommands;

import org.bukkit.command.CommandSender;

import onevnl.ru.elytrya.api.BoostyClient;
import onevnl.ru.elytrya.api.MessageManager;

public class ReloadSubCommand implements SubCommand {

    private final BoostyClient client;

    public ReloadSubCommand(BoostyClient client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "boosty.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        client.reload();

        MessageManager msg = client.getMessageManager();

        client.getAuthManager().checkAndRefreshToken().thenCompose(v -> 
            client.getBlogManager().getBlogStats()
        ).whenComplete((stats, ex) -> {
            if (ex != null || stats == null) {
                sender.sendMessage(msg.getMessage("reload_fail"));
                client.getPlugin().getLogger().severe("Failed to verify Boosty token after reload! Token might be invalid.");
            } else {
                sender.sendMessage(msg.getMessage("reload_success"));
                client.getPlugin().getLogger().info("Config and Boosty token successfully reloaded and verified!");
            }
        });
    }
}