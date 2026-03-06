package onevnl.ru.elytrya.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import onevnl.ru.elytrya.api.BoostyClient;
import onevnl.ru.elytrya.api.MessageManager;
import onevnl.ru.elytrya.api.PendingLink;

public class ChatListener implements Listener {

    private final BoostyClient client;

    public ChatListener(BoostyClient client) {
        this.client = client;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (!client.getPendingLinks().containsKey(player.getUniqueId())) {
            return;
        }

        event.setCancelled(true);
        event.getRecipients().clear();
        
        PendingLink pending = client.getPendingLinks().remove(player.getUniqueId());
        
        String rawInput = event.getMessage();
        String input = ChatColor.stripColor(rawInput).trim();
        
        MessageManager msg = client.getMessageManager();

        client.debug("Player " + player.getName() + " entered email for verification.");
        client.debug("-> Expected: '" + pending.email() + "'");
        client.debug("-> Received (raw): '" + rawInput + "'");
        client.debug("-> Received (stripped): '" + input + "'");

        client.getPlugin().getServer().getScheduler().runTask(client.getPlugin(), () -> {
            if (input.equalsIgnoreCase(pending.email())) {
                client.debug("Email match successful!");
                client.getDatabase().saveLink(player.getUniqueId(), player.getName(), pending.boostyName(), pending.levelName());
                player.sendMessage(msg.getMessage("link_success").replace("%name%", pending.boostyName()));
                msg.broadcastCongratulation(player.getName(), pending.levelName());
                executeRewards(player, pending.boostyName(), pending.levelName());
            } else {
                client.debug("Email mismatch! Verification failed.");
                player.sendMessage(msg.getMessage("link_email_fail"));
            }
        });
        
        event.setMessage("");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        client.getPendingLinks().remove(event.getPlayer().getUniqueId());
    }

    private void executeRewards(Player player, String boostyName, String levelName) {
        List<String> commands = client.getPlugin().getConfig().getStringList("rewards." + levelName + ".give");
        if (commands != null && !commands.isEmpty()) {
            for (String cmd : commands) {
                String finalCmd = cmd.replace("%player%", player.getName()).replace("%boosty_name%", boostyName);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
                client.debug("Executed reward command: " + finalCmd);
            }
        } else {
            client.debug("No rewards found for level: " + levelName);
        }
    }
}