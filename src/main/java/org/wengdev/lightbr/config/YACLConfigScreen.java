package org.wengdev.lightbr.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.wengdev.lightbr.LightBR;

public class YACLConfigScreen extends YACLScreen {
    public YACLConfigScreen(Screen parent) {
        super(generateConfigScreen(), parent);
    }

    public static YetAnotherConfigLib generateConfigScreen() {
        final OptionGroup generalGroup = createGeneralGroup();
        final OptionGroup renderGroup = createDefaultSettingsGroup();

        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("lightbr.config.title"))
                .category(
                        ConfigCategory.createBuilder()
                                .name(Component.translatable("lightbr.config.category"))
                                .group(generalGroup)
                                .group(renderGroup)
                                .build()
                )
                .save(LightBR.config::saveAndReloadWorld)
                .build();
    }

    private static OptionGroup createGeneralGroup() {
        final OptionGroup.Builder generalGroup = OptionGroup.createBuilder();

        Component serverStatus = LightBR.isServerControlled()
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

        generalGroup.option(Option.<Boolean>createBuilder()
                .name(Component.translatable("lightbr.config.enabled.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.enabled.desc")))
                .binding(
                        false,
                        () -> LightBR.config.isEnabled,
                        newValue -> LightBR.config.isEnabled = newValue
                )
                .available(!LightBR.isEnabledServerControlled())
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        generalGroup.option(Option.<Integer>createBuilder()
                .name(Component.translatable("lightbr.config.chunk_xz_radius.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.chunk_xz_radius.desc")))
                .binding(
                        1,
                        () -> LightBR.config.chunkXZRadius,
                        newValue -> LightBR.config.chunkXZRadius = newValue
                )
                .available(!LightBR.isChunkXZServerControlled())
                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(0, 16)
                        .step(1))
                .build()
        );

        generalGroup.option(Option.<Integer>createBuilder()
                .name(Component.translatable("lightbr.config.chunk_y_radius.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.chunk_y_radius.desc")))
                .binding(
                        1,
                        () -> LightBR.config.chunkYRadius,
                        newValue -> LightBR.config.chunkYRadius = newValue
                )
                .available(!LightBR.isChunkYServerControlled())
                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(0, 16)
                        .step(1))
                .build()
        );

        return generalGroup.build();
    }

    private static OptionGroup createDefaultSettingsGroup() {
        final OptionGroup.Builder defaultSettingsGroup = OptionGroup.createBuilder();
        defaultSettingsGroup.name(Component.translatable("lightbr.config.render_group.name"));

        defaultSettingsGroup.option(Option.<Boolean>createBuilder()
                .name(Component.translatable("lightbr.config.render_water.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.render_water.desc")))
                .binding(
                        true,
                        () -> LightBR.config.renderAllWater,
                        newValue -> LightBR.config.renderAllWater = newValue
                )
                .available(!LightBR.isRenderAllWaterServerControlled())
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        defaultSettingsGroup.option(Option.<Boolean>createBuilder()
                .name(Component.translatable("lightbr.config.render_lava.name"))
                .description(OptionDescription.of(Component.translatable("lightbr.config.render_lava.desc")))
                .binding(
                        true,
                        () -> LightBR.config.renderAllLava,
                        newValue -> LightBR.config.renderAllLava = newValue
                )
                .available(!LightBR.isRenderAllLavaServerControlled())
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        return defaultSettingsGroup.build();
    }
}
