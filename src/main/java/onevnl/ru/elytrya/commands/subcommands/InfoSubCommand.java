package onevnl.ru.elytrya.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import onevnl.ru.elytrya.api.BoostyClient;
import onevnl.ru.elytrya.api.BoostyUser;

public class InfoSubCommand implements SubCommand {

    private final BoostyClient client;

    public InfoSubCommand(BoostyClient client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только для игроков.");
            return;
        }

        BoostyUser user = client.getDatabase().getAllUsers().stream()
                .filter(u -> u.uuid().equals(player.getUniqueId()))
                .findFirst().orElse(null);

        if (user != null) {
            player.sendMessage(client.getMessageManager().getMessage("info_linked")
                    .replace("%boosty%", user.boostyName())
                    .replace("%level%", user.levelName()));
        } else {
            player.sendMessage(client.getMessageManager().getMessage("info_not_linked"));
        }
    }
}