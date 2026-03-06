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
        MessageManager msg = client.getMessageManager();
        client.reload();

        client.getAuthManager().checkAndRefreshToken()
            .thenAccept(v -> sender.sendMessage(msg.getMessage("reload_success")))
            .exceptionally(ex -> {
                sender.sendMessage(msg.getMessage("reload_fail"));
                return null;
            });
    }
}