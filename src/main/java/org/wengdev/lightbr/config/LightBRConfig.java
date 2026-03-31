package org.wengdev.lightbr.config;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LightBRConfig {
    private static final Path CONFIG_PATH = Path.of("config", "lightbr_config.json");

    @SerializedName("is_enabled")
    public boolean isEnabled = false;

    @SerializedName("render_water")
    public boolean renderAllWater = true;

    @SerializedName("render_lava")
    public boolean renderAllLava = true;

    @SerializedName("unrender_no_collision_blocks")
    public boolean unrenderNoCollisionBlocks = true;

    public void save() {
        try {
            final Gson gson = new Gson();
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
                return gson.fromJson(json, LightBRConfig.class);
            }
        } catch (IOException e) {
            System.out.println("Failed to load LightBR config: " + e.getMessage());
        }

        return new LightBRConfig();
    }
}
