package org.wengdev.lightbr.plugin;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LightBRMixinPlugin implements IMixinConfigPlugin {
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?.*");

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("SodiumBlockRendererMixin")) {
            return FabricLoader.getInstance().isModLoaded("sodium");
        }

        if (mixinClassName.endsWith("OBULegacyMixin")) {
            return isObuLoaded() && !isObuAtLeast(0, 5);
        }

        if (mixinClassName.endsWith("OBULatestMixin") || mixinClassName.endsWith("MutableContextMixin")) {
            return isObuLoaded() && isObuAtLeast(0, 5);
        }

        return true;
    }

    private static boolean isObuLoaded() {
        return FabricLoader.getInstance().isModLoaded("openboatutils");
    }

    private static boolean isObuAtLeast(int major, int minor) {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer("openboatutils");
        String version = container.map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("0.0.0");
        Matcher matcher = VERSION_PATTERN.matcher(version);
        if (!matcher.matches()) {
            return false;
        }

        int vMajor = parseInt(matcher.group(1));
        int vMinor = parseInt(matcher.group(2));

        return vMajor > major || (vMajor == major && vMinor >= minor);
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
