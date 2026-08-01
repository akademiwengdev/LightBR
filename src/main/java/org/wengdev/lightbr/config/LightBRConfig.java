package org.wengdev.lightbr.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;
import org.wengdev.lightbr.LightBR;
import org.wengdev.lightbr.RenderContextManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LightBRConfig {
    private static final Path CONFIG_PATH = Path.of("config", "lightbr_config.json");

    @SerializedName("is_enabled")
    public boolean isEnabled = false;

    @SerializedName("chunk_xz_radius")
    public int chunkXZRadius = 1;

    @SerializedName("chunk_y_radius")
    public int chunkYRadius = 1;

    @SerializedName("render_water")
    public boolean renderAllWater = true;

    @SerializedName("render_lava")
    public boolean renderAllLava = true;

    public void save() {
        try {
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            final String json = gson.toJson(this);

            Files.writeString(CONFIG_PATH, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LightBR.LOGGER.error("Failed to save LightBR config", e);
        }
    }

    public void saveAndReloadAll() {
        save();

        RenderContextManager.reloadContextAndClearCache();
        Minecraft.getInstance().levelRenderer.allChanged();
    }

    public void saveAndReloadWorldOnly() {
        save();
        Minecraft.getInstance().levelRenderer.allChanged();
    }

    public static LightBRConfig load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                final String json = Files.readString(CONFIG_PATH);
                final Gson gson = new Gson();
                return gson.fromJson(json, LightBRConfig.class);
            }
        } catch (IOException e) {
            LightBR.LOGGER.error("Failed to load LightBR config", e);
        }

        return new LightBRConfig();
    }
}
