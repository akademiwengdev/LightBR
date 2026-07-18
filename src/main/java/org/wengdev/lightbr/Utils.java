package org.wengdev.lightbr;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Utils {
    public static List<String> getBlockEntityBackedBlockIds() {
        List<String> ids = new ArrayList<>();

        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof EntityBlock || block.defaultBlockState().hasBlockEntity()) {
                ids.add(BuiltInRegistries.BLOCK.getKey(block).toString());
            }
        }

        ids.sort(Comparator.naturalOrder());
        return ids;
    }
}
