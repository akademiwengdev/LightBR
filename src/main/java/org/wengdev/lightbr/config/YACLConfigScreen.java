package org.wengdev.lightbr.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.wengdev.lightbr.LightBR;

public class YACLConfigScreen extends YACLScreen {
    public YACLConfigScreen(Screen parent) {
        super(generateConfigScreen(), parent);
    }

    public static YetAnotherConfigLib generateConfigScreen() {
        final OptionGroup generalGroup = createGeneralGroup();

        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("LightBR Config"))
                .category(
                        ConfigCategory.createBuilder()
                                .name(Text.literal("LightBR"))
                                .group(generalGroup)
                                .build()
                )
                .save(LightBR.config::saveAndReloadWorld)
                .build();
    }

    private static OptionGroup createGeneralGroup() {
        final OptionGroup.Builder generalGroup = OptionGroup.createBuilder();

        generalGroup.option(Option.<Boolean>createBuilder()
                .name(Text.literal("Enabled"))
                .description(OptionDescription.of(Text.literal("Whether LightBR is enabled or not.")))
                .binding(
                        false,
                        () -> LightBR.config.isEnabled,
                        newValue -> {
                            LightBR.config.isEnabled = newValue;
                        }
                )
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        generalGroup.option(Option.<Boolean>createBuilder()
                .name(Text.literal("Render Water"))
                .description(OptionDescription.of(Text.literal("Whether all water should be rendered or not.")))
                .binding(
                        true,
                        () -> LightBR.config.renderAllWater,
                        newValue -> {
                            LightBR.config.renderAllWater = newValue;
                        }
                )
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        generalGroup.option(Option.<Boolean>createBuilder()
                .name(Text.literal("Render Lava"))
                .description(OptionDescription.of(Text.literal("Whether all lava should be rendered or not.")))
                .binding(
                        true,
                        () -> LightBR.config.renderAllLava,
                        newValue -> {
                            LightBR.config.renderAllLava = newValue;
                        }
                )
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        generalGroup.option(Option.<Boolean>createBuilder()
                .name(Text.literal("Un-render No-Collision Blocks"))
                .description(OptionDescription.of(Text.literal("Whether blocks without a collision shape should be un-rendered or not.")))
                .binding(
                        true,
                        () -> LightBR.config.unrenderNoCollisionBlocks,
                        newValue -> {
                            LightBR.config.unrenderNoCollisionBlocks = newValue;
                        }
                )
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        return generalGroup.build();
    }
}
