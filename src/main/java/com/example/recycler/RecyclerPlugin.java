package com.example.recycler;

import com.example.recycler.command.RecycleCommand;
import com.example.recycler.inventory.RecyclerInventoryHolder;
import com.example.recycler.listener.RecycleListener;
import com.example.recycler.locale.Messages;
import com.example.recycler.locale.TranslationService;
import com.example.recycler.reward.EnchantmentRoller;
import com.example.recycler.reward.ItemTierService;
import com.example.recycler.reward.RecyclerSettings;
import com.example.recycler.reward.RewardPools;
import com.example.recycler.reward.RewardSelector;
import com.example.recycler.reward.WeightedSelector;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

public final class RecyclerPlugin extends JavaPlugin {
    private static final long COOLDOWN_MILLIS = TimeUnit.MINUTES.toMillis(5);

    private final ConcurrentMap<UUID, ConcurrentMap<Material, Long>> cooldowns = new ConcurrentHashMap<>();
    private Messages messages;

    @Override
    public void onEnable() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        RecyclerSettings settings = RecyclerSettings.defaults();
        ItemTierService itemTierService = new ItemTierService();
        RewardPools rewardPools = new RewardPools(settings);
        WeightedSelector weightedSelector = new WeightedSelector(random);
        RewardSelector rewardSelector = new RewardSelector(settings, itemTierService, weightedSelector);
        EnchantmentRoller enchantmentRoller = new EnchantmentRoller(settings, itemTierService, weightedSelector, random);
        messages = new Messages(this, "en_us", List.of(
            "en_us",
            "uk_ua",
            "ru_ru",
            "be_by",
            "pl_pl",
            "et_ee",
            "de_de"
        ));
        TranslationService translationService = new TranslationService();

        RecycleListener listener = new RecycleListener(this, random, settings, rewardPools, rewardSelector, enchantmentRoller, messages, translationService);
        getServer().getPluginManager().registerEvents(listener, this);
        Objects.requireNonNull(getCommand("recycle"), "recycle command not registered").setExecutor(new RecycleCommand(this, messages));
        getLogger().info("Recycler is enabled");
    }

    @Override
    public void onDisable() {
        cooldowns.clear();
    }

    public Inventory createRecyclerInventory(Locale locale) {
        RecyclerInventoryHolder holder = new RecyclerInventoryHolder();
        Inventory inventory = Bukkit.createInventory(holder, 9, messages.guiTitle(locale));
        holder.setInventory(inventory);
        return inventory;
    }

    public Messages getMessages() {
        return messages;
    }

    public boolean hasCooldown(UUID playerId, Material material) {
        return getRemainingCooldown(playerId, material) > 0;
    }

    public long getRemainingCooldown(UUID playerId, Material material) {
        ConcurrentMap<Material, Long> playerCooldowns = cooldowns.get(playerId);
        if (playerCooldowns == null) {
            return 0L;
        }

        Long expiration = playerCooldowns.get(material);
        if (expiration == null) {
            return 0L;
        }

        long remaining = expiration - System.currentTimeMillis();
        if (remaining <= 0) {
            playerCooldowns.remove(material);
            if (playerCooldowns.isEmpty()) {
                cooldowns.remove(playerId);
            }
            return 0L;
        }

        return remaining;
    }

    public void startCooldown(UUID playerId, Material material) {
        cooldowns.computeIfAbsent(playerId, id -> new ConcurrentHashMap<>())
                .put(material, System.currentTimeMillis() + COOLDOWN_MILLIS);
    }
}
