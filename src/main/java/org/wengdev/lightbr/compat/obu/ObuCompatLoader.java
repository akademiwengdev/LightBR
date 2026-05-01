package org.wengdev.lightbr.compat.obu;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ObuCompatLoader {
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?.*");
    private static final ObuCompat NOOP = new ObuCompat() {
        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public boolean isBlockSlippery(String blockId) {
            return false;
        }
    };

    private static ObuCompat instance;

    private ObuCompatLoader() {
    }

    public static ObuCompat get() {
        if (instance == null) {
            instance = loadCompat();
        }
        return instance;
    }

    private static ObuCompat loadCompat() {
        if (!FabricLoader.getInstance().isModLoaded("openboatutils")) {
            return NOOP;
        }

        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer("openboatutils");
        String version = container.map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("0.0.0");

        if (isAtLeast(version, 0, 5)) {
            return instantiate("org.wengdev.lightbr.compat.obu.latest.LatestObuCompat");
        }

        return instantiate("org.wengdev.lightbr.compat.obu.legacy.LegacyObuCompat");
    }

    private static boolean isAtLeast(String version, int major, int minor) {
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

    private static ObuCompat instantiate(String className) {
        try {
            Class<?> type = Class.forName(className, true, ObuCompatLoader.class.getClassLoader());
            return (ObuCompat) type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException | ClassCastException e) {
            return NOOP;
        }
    }
}

