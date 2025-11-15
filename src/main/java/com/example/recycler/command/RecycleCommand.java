package com.example.recycler.command;

import com.example.recycler.RecyclerPlugin;
import com.example.recycler.locale.Messages;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


public class RecycleCommand implements CommandExecutor {
    private final RecyclerPlugin plugin;
    private final Messages messages;

    public RecycleCommand(RecyclerPlugin plugin, Messages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.component(null, "command.only-players", NamedTextColor.RED));
            return true;
        }

        Inventory recycler = plugin.createRecyclerInventory(player.locale());
        player.openInventory(recycler);
        player.sendMessage(messages.component(player.locale(), "command.opened", NamedTextColor.GREEN));
        return true;
    }

}
