package org.wengdev.lightbr;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Utils {
    public static List<String> getBlockEntityBackedBlockIds() {
        List<String> ids = new ArrayList<>();

        for (Block block : Registries.BLOCK) {
            if (block instanceof BlockEntityProvider || block.getDefaultState().hasBlockEntity()) {
                ids.add(Registries.BLOCK.getId(block).toString());
            }
        }

        ids.sort(Comparator.naturalOrder());
        return ids;
    }
}
