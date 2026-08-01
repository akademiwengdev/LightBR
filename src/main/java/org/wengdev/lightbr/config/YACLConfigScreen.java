package org.wengdev.lightbr.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.wengdev.lightbr.LightBR;
import org.wengdev.lightbr.RenderContext;
import org.wengdev.lightbr.RenderContextManager;
import org.wengdev.lightbr.ServerControlManager;

public class YACLConfigScreen extends YACLScreen {
    public YACLConfigScreen(Screen parent) {
        super(generateConfigScreen(), parent);
    }

    public static YetAnotherConfigLib generateConfigScreen() {
        final OptionGroup generalGroup = createGeneralGroup();
        final OptionGroup renderGroup = createDefaultSettingsGroup();
        final OptionGroup appliedValuesGroup = createAppliedValuesGroup();

        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("lightbr.config.title"))
                .category(
                        ConfigCategory.createBuilder()
                                .name(Component.translatable("lightbr.config.category"))
                                .group(generalGroup)
                                .group(renderGroup)
                                .group(appliedValuesGroup)
                                .build()
                )
                .save(LightBR.config::saveAndReloadAll)
                .build();
    }

    private static OptionGroup createGeneralGroup() {
        final OptionGroup.Builder generalGroup = OptionGroup.createBuilder();

        generalGroup.option(Option.<Boolean>createBuilder()
                .name(Component.translatable("lightbr.config.enabled.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.enabled.desc")))
                .binding(
                        false,
                        () -> LightBR.config.isEnabled,
                        newValue -> LightBR.config.isEnabled = newValue
                )
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        Component serverStatus = ServerControlManager.isServerControlled()
                ? Component.translatable("lightbr.config.server_controlled.active")
                : Component.translatable("lightbr.config.server_controlled.inactive");

        generalGroup.option(ButtonOption.createBuilder()
                .name(Component.translatable("lightbr.config.server_controlled.name"))
                .text(serverStatus)
                .description(OptionDescription.of(Component.translatable("lightbr.config.server_controlled.desc")))
                .action((screen, option) -> {
                })
                .build()
        );

        return generalGroup.build();
    }

    private static OptionGroup createDefaultSettingsGroup() {
        final OptionGroup.Builder defaultSettingsGroup = OptionGroup.createBuilder();
        defaultSettingsGroup.name(Component.translatable("lightbr.config.render_group.name"));

        defaultSettingsGroup.option(Option.<Integer>createBuilder()
                .name(Component.translatable("lightbr.config.chunk_xz_radius.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.chunk_xz_radius.desc")))
                .binding(
                        1,
                        () -> LightBR.config.chunkXZRadius,
                        newValue -> LightBR.config.chunkXZRadius = newValue
                )
                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(0, 16)
                        .step(1))
                .build()
        );

        defaultSettingsGroup.option(Option.<Integer>createBuilder()
                .name(Component.translatable("lightbr.config.chunk_y_radius.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.chunk_y_radius.desc")))
                .binding(
                        1,
                        () -> LightBR.config.chunkYRadius,
                        newValue -> LightBR.config.chunkYRadius = newValue
                )
                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(0, 16)
                        .step(1))
                .build()
        );

        defaultSettingsGroup.option(Option.<Boolean>createBuilder()
                .name(Component.translatable("lightbr.config.render_all_water.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.render_all_water.desc")))
                .binding(
                        true,
                        () -> LightBR.config.renderAllWater,
                        newValue -> LightBR.config.renderAllWater = newValue
                )
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        defaultSettingsGroup.option(Option.<Boolean>createBuilder()
                .name(Component.translatable("lightbr.config.render_all_lava.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.render_all_lava.desc")))
                .binding(
                        true,
                        () -> LightBR.config.renderAllLava,
                        newValue -> LightBR.config.renderAllLava = newValue
                )
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        return defaultSettingsGroup.build();
    }

    private static OptionGroup createAppliedValuesGroup() {
        final OptionGroup.Builder appliedValuesGroup = OptionGroup.createBuilder();
        appliedValuesGroup.name(Component.translatable("lightbr.config.applied_values_group.name"));
        appliedValuesGroup.description(OptionDescription.of(Component.translatable("lightbr.config.applied_values_group.desc")));
        appliedValuesGroup.collapsed(true);

        RenderContext renderContext = RenderContextManager.get();

        appliedValuesGroup.option(ButtonOption.createBuilder()
                .name(Component.translatable("lightbr.config.applied.enabled.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.applied.enabled.desc")))
                .text(appliedValueText(ServerControlManager.isIsEnabledServerControlled(), appliedValueBooleanText(renderContext.isEnabled)))
                .available(false)
                .action((screen, option) -> {})
                .build()
        );

        appliedValuesGroup.option(ButtonOption.createBuilder()
                .name(Component.translatable("lightbr.config.applied.chunk_xz_radius.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.applied.chunk_xz_radius.desc")))
                .text(appliedValueText(ServerControlManager.isChunkXZServerControlled(), renderContext.chunkXZRadius))
                .available(false)
                .action((screen, option) -> {})
                .build()
        );

        appliedValuesGroup.option(ButtonOption.createBuilder()
                .name(Component.translatable("lightbr.config.applied.chunk_y_radius.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.applied.chunk_y_radius.desc")))
                .text(appliedValueText(ServerControlManager.isChunkXZServerControlled(), renderContext.chunkYRadius))
                .available(false)
                .action((screen, option) -> {})
                .build()
        );

        appliedValuesGroup.option(ButtonOption.createBuilder()
                .name(Component.translatable("lightbr.config.applied.render_all_water.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.applied.render_all_water.desc")))
                .text(appliedValueText(ServerControlManager.isChunkXZServerControlled(), appliedValueBooleanText(renderContext.renderAllWater)))
                .available(false)
                .action((screen, option) -> {})
                .build()
        );

        appliedValuesGroup.option(ButtonOption.createBuilder()
                .name(Component.translatable("lightbr.config.applied.render_all_lava.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.applied.render_all_lava.desc")))
                .text(appliedValueText(ServerControlManager.isChunkXZServerControlled(), appliedValueBooleanText(renderContext.renderAllLava)))
                .available(false)
                .action((screen, option) -> {})
                .build()
        );

        Component regionCountString = Component.translatable("lightbr.config.applied.always_render_regions.regions", renderContext.alwaysRenderRegions.size());

        appliedValuesGroup.option(ButtonOption.createBuilder()
                .name(Component.translatable("lightbr.config.applied.always_render_regions.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.applied.always_render_regions.desc")))
                .text(appliedValueText(ServerControlManager.isChunkXZServerControlled(), regionCountString))
                .available(false)
                .action((screen, option) -> {})
                .build()
        );

        return appliedValuesGroup.build();
    }

    private static Component appliedValueBooleanText(boolean bool) {
        return bool
                ? Component.translatable("lightbr.config.applied.true")
                : Component.translatable("lightbr.config.applied.false");
    }

    private static Component appliedValueText(boolean isFromServer, Object object) {
        if (isFromServer) {
            return Component.translatable("lightbr.config.applied.format.server", object);
        }

        return Component.translatable("lightbr.config.applied.format.default", object);
    }
}
