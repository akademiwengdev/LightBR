package org.wengdev.lightbr.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class LightBRConfig {
    private static final Path CONFIG_PATH = Path.of("config", "lightbr_config.json");

    static final List<String> DEFAULT_RENDERABLE_BLOCK_ENTITIES = List.of(
            "minecraft:chest",
            "minecraft:trapped_chest",
            "minecraft:ender_chest",
            "minecraft:enchanting_table",
            "minecraft:skull",
            "minecraft:brewing_stand",
            "minecraft:shulker_box",
            "minecraft:white_shulker_box",
            "minecraft:orange_shulker_box",
            "minecraft:magenta_shulker_box",
            "minecraft:light_blue_shulker_box",
            "minecraft:yellow_shulker_box",
            "minecraft:lime_shulker_box",
            "minecraft:pink_shulker_box",
            "minecraft:gray_shulker_box",
            "minecraft:light_gray_shulker_box",
            "minecraft:cyan_shulker_box",
            "minecraft:purple_shulker_box",
            "minecraft:blue_shulker_box",
            "minecraft:brown_shulker_box",
            "minecraft:green_shulker_box",
            "minecraft:red_shulker_box",
            "minecraft:black_shulker_box",
            "minecraft:furnace",
            "minecraft:blast_furnace",
            "minecraft:smoker",
            "minecraft:white_bed",
            "minecraft:orange_bed",
            "minecraft:magenta_bed",
            "minecraft:light_blue_bed",
            "minecraft:yellow_bed",
            "minecraft:lime_bed",
            "minecraft:pink_bed",
            "minecraft:gray_bed",
            "minecraft:light_gray_bed",
            "minecraft:cyan_bed",
            "minecraft:purple_bed",
            "minecraft:blue_bed",
            "minecraft:brown_bed",
            "minecraft:green_bed",
            "minecraft:red_bed",
            "minecraft:black_bed"
    );

    static final List<String> DEFAULT_BLOCKS_ONLY_SELECTED_BLOCKS = List.of();

    @SerializedName("is_enabled")
    public boolean isEnabled = false;

    @SerializedName("render_water")
    public boolean renderAllWater = true;

    @SerializedName("render_lava")
    public boolean renderAllLava = true;

    @SerializedName("should_unrender_block_entities")
    public boolean shouldUnrenderBlockEntities = true;

    @SerializedName("render_mode")
    public RenderMode renderMode = RenderMode.NEARBY_CHUNKS;

    @SerializedName("renderable_block_entities")
    public List<String> renderableBlockEntities = new ArrayList<>(DEFAULT_RENDERABLE_BLOCK_ENTITIES);

    @SerializedName("blocks_only_selected_blocks")
    public List<String> blocksOnlySelectedBlocks = new ArrayList<>(DEFAULT_BLOCKS_ONLY_SELECTED_BLOCKS);

    public void save() {
        try {
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            final String json = gson.toJson(this);

            Files.writeString(CONFIG_PATH, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Failed to save LightBR config: " + e.getMessage());
        }
    }

    public void saveAndReloadWorld() {
        save();

        MinecraftClient.getInstance().worldRenderer.reload();
    }

    public static LightBRConfig load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                final String json = Files.readString(CONFIG_PATH);
                final Gson gson = new Gson();
                LightBRConfig config = gson.fromJson(json, LightBRConfig.class);
                if (config.renderableBlockEntities == null) {
                    config.renderableBlockEntities = new ArrayList<>(DEFAULT_RENDERABLE_BLOCK_ENTITIES);
                }
                if (config.blocksOnlySelectedBlocks == null) {
                    config.blocksOnlySelectedBlocks = new ArrayList<>(DEFAULT_BLOCKS_ONLY_SELECTED_BLOCKS);
                }
                if (config.renderMode == null) {
                    config.renderMode = RenderMode.NEARBY_CHUNKS;
                }
                return config;
            }
        } catch (IOException e) {
            System.out.println("Failed to load LightBR config: " + e.getMessage());
        }

        return new LightBRConfig();
    }
}
