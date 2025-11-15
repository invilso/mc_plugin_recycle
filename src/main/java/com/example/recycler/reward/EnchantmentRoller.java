package com.example.recycler.reward;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class EnchantmentRoller {
    private final RecyclerSettings settings;
    private final ItemTierService itemTierService;
    private final WeightedSelector weightedSelector;
    private final ThreadLocalRandom random;

    public EnchantmentRoller(RecyclerSettings settings,
                             ItemTierService itemTierService,
                             WeightedSelector weightedSelector,
                             ThreadLocalRandom random) {
        this.settings = settings;
        this.itemTierService = itemTierService;
        this.weightedSelector = weightedSelector;
        this.random = random;
    }

    @SuppressWarnings("deprecation")
    public void tryApplyEnchantments(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return;
        }
        if (random.nextDouble() >= settings.getEnchantChance()) {
            return;
        }

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }

        List<Enchantment> compatible = Arrays.stream(Enchantment.values())
                .filter(enchantment -> enchantment != null)
                .filter(enchantment -> !enchantment.isCursed())
                .filter(enchantment -> enchantment.canEnchantItem(stack))
                .toList();

        if (compatible.isEmpty()) {
            return;
        }

        List<Enchantment> shuffled = new ArrayList<>(compatible);
        Collections.shuffle(shuffled, random);

        int maxAvailable = shuffled.size();
        int desired = rollEnchantmentCount(stack.getType(), maxAvailable);
        desired = Math.max(1, Math.min(desired, maxAvailable));

        for (int i = 0; i < desired; i++) {
            Enchantment enchantment = shuffled.get(i);
            int minLevel = Math.max(1, enchantment.getStartLevel());
            int maxLevel = Math.max(minLevel, enchantment.getMaxLevel());
            int level = (maxLevel == minLevel) ? maxLevel : random.nextInt(minLevel, maxLevel + 1);
            meta.addEnchant(enchantment, level, true);
        }
        stack.setItemMeta(meta);
    }

    int rollEnchantmentCount(Material material, int availableEnchantments) {
        Map<Integer, Double> weights = settings.getEnchantCountWeights();
        List<WeightedSelector.WeightedChoice<Integer>> options = new ArrayList<>();
        double divisor = settings.getDivisorFor(itemTierService.resolve(material));
        for (Map.Entry<Integer, Double> entry : weights.entrySet()) {
            int requested = entry.getKey();
            if (requested > availableEnchantments) {
                continue;
            }
            double weight = entry.getValue();
            if (requested >= 3) {
                weight /= divisor;
            }
            if (weight > 0) {
                options.add(new WeightedSelector.WeightedChoice<>(requested, weight));
            }
        }
        if (options.isEmpty()) {
            return Math.min(availableEnchantments, 1);
        }
        Integer picked = weightedSelector.pick(options);
        return picked == null ? 1 : picked;
    }
}
