package com.example.recycler.listener;

import com.example.recycler.RecyclerPlugin;
import com.example.recycler.inventory.RecyclerInventoryHolder;
import com.example.recycler.locale.Messages;
import com.example.recycler.reward.EnchantmentRoller;
import com.example.recycler.reward.RecyclerSettings;
import com.example.recycler.reward.RewardPools;
import com.example.recycler.reward.RewardSelector;
import com.example.recycler.locale.TranslationService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class RecycleListener implements Listener {
    private final RecyclerPlugin plugin;
    private final ThreadLocalRandom random;
    private final RecyclerSettings settings;
    private final RewardPools rewardPools;
    private final RewardSelector rewardSelector;
    private final EnchantmentRoller enchantmentRoller;
    private final Messages messages;
    private final TranslationService translationService;

    public RecycleListener(RecyclerPlugin plugin,
                           ThreadLocalRandom random,
                           RecyclerSettings settings,
                           RewardPools rewardPools,
                           RewardSelector rewardSelector,
                           EnchantmentRoller enchantmentRoller,
                           Messages messages,
                           TranslationService translationService) {
        this.plugin = plugin;
        this.random = random;
        this.settings = settings;
        this.rewardPools = rewardPools;
        this.rewardSelector = rewardSelector;
        this.enchantmentRoller = enchantmentRoller;
        this.messages = messages;
        this.translationService = translationService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!isRecyclerInventory(event.getView())) {
            return;
        }

        Inventory inventory = event.getInventory();
        if (event.getRawSlot() >= inventory.getSize()) {
            return;
        }

        if (event.getSlot() != 0) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> processInput(player, inventory));
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!isRecyclerInventory(event.getView())) {
            return;
        }

        Inventory inventory = event.getInventory();
        for (int slot : event.getRawSlots()) {
            if (slot < inventory.getSize() && slot != 0) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!isRecyclerInventory(event.getView())) {
            return;
        }

        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        ItemStack input = inventory.getItem(0);
        if (input == null || input.getType() == Material.AIR) {
            return;
        }

        inventory.setItem(0, null);
        giveOrDrop(player, input.clone());
    }

    private void processInput(Player player, Inventory inventory) {
        if (!isPlayerViewingInventory(player, inventory)) {
            return;
        }

        ItemStack input = inventory.getItem(0);
        if (input == null || input.getType() == Material.AIR) {
            return;
        }

        UUID playerId = player.getUniqueId();
        Locale locale = player.locale();

        if (plugin.hasCooldown(playerId, input.getType())) {
            long remaining = plugin.getRemainingCooldown(playerId, input.getType());
            Map<String, Component> componentPlaceholders = Map.of(
                    "item", translationService.material(input.getType())
            );
            Map<String, String> stringPlaceholders = Map.of(
                    "time", formatCooldown(remaining)
            );
            rejectInput(player, inventory, input,
                    messages.component(locale, "recycler.cooldown-input", NamedTextColor.RED, componentPlaceholders, stringPlaceholders));
            return;
        }

        if (isFullStackRewardEligible(input)) {
            handleFullStackReward(player, inventory, input, playerId, locale);
        } else {
            handleBlockReward(player, inventory, input, playerId, locale);
        }
    }

    private boolean isPlayerViewingInventory(Player player, Inventory inventory) {
        Inventory top = player.getOpenInventory().getTopInventory();
        return top != null && top.equals(inventory);
    }

    private void handleBlockReward(Player player, Inventory inventory, ItemStack input, UUID playerId, Locale locale) {
        ItemStack reward = createRandomBlockReward(playerId, input);
        if (reward == null) {
            rejectInput(player, inventory, input,
                    messages.component(locale, "recycler.no-block", NamedTextColor.RED));
            return;
        }

        if (!isRewardReady(player, inventory, input, reward, playerId, locale)) {
            return;
        }

        inventory.setItem(0, null);
        giveOrDrop(player, reward);
        plugin.startCooldown(playerId, reward.getType());
        Map<String, Component> componentPlaceholders = Map.of(
            "input", translationService.material(input.getType()),
            "reward", translationService.material(reward.getType())
        );
        Map<String, String> stringPlaceholders = Map.of(
            "inputAmount", String.valueOf(input.getAmount()),
            "rewardAmount", String.valueOf(reward.getAmount())
        );
        player.sendMessage(messages.component(locale, "recycler.block-success", NamedTextColor.GREEN, componentPlaceholders, stringPlaceholders));
    }

    private void handleFullStackReward(Player player, Inventory inventory, ItemStack input, UUID playerId, Locale locale) {
        ItemStack reward = createRandomItemReward(playerId, input);
        if (reward == null) {
            rejectInput(player, inventory, input,
                    messages.component(locale, "recycler.no-item", NamedTextColor.RED));
            return;
        }

        if (!isRewardReady(player, inventory, input, reward, playerId, locale)) {
            return;
        }

        inventory.setItem(0, null);
        giveOrDrop(player, reward);
        plugin.startCooldown(playerId, reward.getType());
        Map<String, Component> componentPlaceholders = Map.of(
            "reward", translationService.material(reward.getType())
        );
        Map<String, String> stringPlaceholders = Map.of(
            "amount", String.valueOf(reward.getAmount())
        );
        player.sendMessage(messages.component(locale, "recycler.stack-success", NamedTextColor.GOLD, componentPlaceholders, stringPlaceholders));
    }

    private boolean isRewardReady(Player player, Inventory inventory, ItemStack input, ItemStack reward, UUID playerId, Locale locale) {
        Material rewardMaterial = reward.getType();
        long remaining = plugin.getRemainingCooldown(playerId, rewardMaterial);
        if (remaining > 0) {
            Map<String, Component> componentPlaceholders = Map.of(
                    "item", translationService.material(rewardMaterial)
            );
            Map<String, String> stringPlaceholders = Map.of(
                    "time", formatCooldown(remaining)
            );
            rejectInput(player, inventory, input,
                    messages.component(locale, "recycler.cooldown-reward", NamedTextColor.RED, componentPlaceholders, stringPlaceholders));
            return false;
        }
        return true;
    }

    private ItemStack createRandomBlockReward(UUID playerId, ItemStack stack) {
        Material rewardMaterial = rewardSelector.pickRewardMaterial(
            stack.getType(),
            rewardPools.getBlockRewards(),
            false,
            material -> plugin.hasCooldown(playerId, material));
        if (rewardMaterial == null) {
            return null;
        }

        int maxStackSize = rewardMaterial.getMaxStackSize();
        int amount = Math.min(stack.getAmount(), maxStackSize);
        amount = Math.max(1, amount);
        return new ItemStack(rewardMaterial, amount);
    }

    private ItemStack createRandomItemReward(UUID playerId, ItemStack stack) {
        Material rewardMaterial = rewardSelector.pickRewardMaterial(
                stack.getType(),
                rewardPools.getItemRewards(),
                true,
                material -> plugin.hasCooldown(playerId, material));
        if (rewardMaterial == null) {
            return null;
        }

        ItemStack reward = new ItemStack(rewardMaterial, 1);
        if (rewardMaterial.getMaxStackSize() > 1 && random.nextDouble() < settings.getDoubleRewardChance()) {
            reward.setAmount(2);
        }

        enchantmentRoller.tryApplyEnchantments(reward);
        return reward;
    }

    private boolean isFullStackRewardEligible(ItemStack stack) {
        int maxStack = stack.getMaxStackSize();
        return maxStack >= settings.getFullStackMin() && stack.getAmount() == maxStack;
    }

    private void giveOrDrop(Player player, ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return;
        }

        Map<Integer, ItemStack> leftover = player.getInventory().addItem(stack);
        if (!leftover.isEmpty()) {
            leftover.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
        }
    }

    private boolean isRecyclerInventory(InventoryView view) {
        if (view == null) {
            return false;
        }
        Inventory top = view.getTopInventory();
        return top != null && top.getHolder() instanceof RecyclerInventoryHolder;
    }

    private void rejectInput(Player player, Inventory inventory, ItemStack input, Component message) {
        inventory.setItem(0, null);
        giveOrDrop(player, input.clone());
        player.sendMessage(message);
    }

    private String formatCooldown(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes);
        if (minutes > 0) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " and " + seconds + " second" + (seconds == 1 ? "" : "s");
        }
        return seconds + " second" + (seconds == 1 ? "" : "s");
    }
}
