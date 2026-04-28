package org.wengdev.lightbr.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import org.wengdev.lightbr.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class LightBRConfig {
    private static final Path CONFIG_PATH = Path.of("config", "lightbr_config.json");

    static final List<String> DEFAULT_RENDERABLE_BLOCK_ENTITIES = Utils.getBlockEntityBackedBlockIds();

    @SerializedName("is_enabled")
    public boolean isEnabled = false;

    @SerializedName("render_water")
    public boolean renderAllWater = true;

    @SerializedName("render_lava")
    public boolean renderAllLava = true;

    @SerializedName("should_unrender_block_entities")
    public boolean shouldUnrenderBlockEntities = true;

    @SerializedName("renderable_block_entities")
    public List<String> renderableBlockEntities = new ArrayList<>(DEFAULT_RENDERABLE_BLOCK_ENTITIES);

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
                return config;
            }
        } catch (IOException e) {
            System.out.println("Failed to load LightBR config: " + e.getMessage());
        }

        return new LightBRConfig();
    }
}
