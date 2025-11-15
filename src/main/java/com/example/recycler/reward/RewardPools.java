package com.example.recycler.reward;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public final class RewardPools {
    private final List<Material> blockRewards;
    private final List<Material> itemRewards;

    public RewardPools(RecyclerSettings settings) {
        this.blockRewards = Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .filter(material -> material != Material.AIR)
                .filter(material -> material.getMaxStackSize() > 0)
                .filter(material -> !settings.getExcludedMaterials().contains(material))
                .toList();

        this.itemRewards = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .filter(material -> !material.isBlock())
                .filter(material -> material != Material.AIR)
                .filter(material -> !settings.getExcludedMaterials().contains(material))
                .toList();
    }

    public List<Material> getBlockRewards() {
        return blockRewards;
    }

    public List<Material> getItemRewards() {
        return itemRewards;
    }
}
