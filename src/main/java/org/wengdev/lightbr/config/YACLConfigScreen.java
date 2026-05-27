package org.wengdev.lightbr.config;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.wengdev.lightbr.LightBR;
import org.wengdev.lightbr.Utils;

import java.util.List;

public class YACLConfigScreen extends YACLScreen {
    public YACLConfigScreen(Screen parent) {
        super(generateConfigScreen(), parent);
    }

    public static YetAnotherConfigLib generateConfigScreen() {
        final OptionGroup generalGroup = createGeneralGroup();
        final OptionGroup renderGroup = createDefaultSettingsGroup();

        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("lightbr.config.title"))
                .category(
                        ConfigCategory.createBuilder()
                                .name(Text.translatable("lightbr.config.category"))
                                .group(generalGroup)
                                .group(renderGroup)
                                .build()
                )
                .save(LightBR.config::saveAndReloadWorld)
                .build();
    }

    private static OptionGroup createGeneralGroup() {
        final OptionGroup.Builder generalGroup = OptionGroup.createBuilder();

        Text serverStatus = LightBR.isServerControlled()
                ? Text.translatable("lightbr.config.server_controlled.active")
                : Text.translatable("lightbr.config.server_controlled.inactive");

        generalGroup.option(ButtonOption.createBuilder()
                .name(Text.translatable("lightbr.config.server_controlled.name"))
                .text(serverStatus)
                .description(OptionDescription.of(Text.translatable("lightbr.config.server_controlled.desc")))
                .action((screen, option) -> {
                })
                .build()
        );

        generalGroup.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("lightbr.config.enabled.name"))
                .description(OptionDescription.of(Text.translatable("lightbr.config.enabled.desc")))
                .binding(
                        false,
                        () -> LightBR.config.isEnabled,
                        newValue -> LightBR.config.isEnabled = newValue
                )
                .available(!LightBR.isEnabledServerControlled())
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        return generalGroup.build();
    }

    private static OptionGroup createDefaultSettingsGroup() {
        final OptionGroup.Builder defaultSettingsGroup = OptionGroup.createBuilder();
        defaultSettingsGroup.name(Text.translatable("lightbr.config.render_group.name"));

        defaultSettingsGroup.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("lightbr.config.render_water.name"))
                .description(OptionDescription.of(Text.translatable("lightbr.config.render_water.desc")))
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
                .name(Text.translatable("lightbr.config.render_lava.name"))
                .description(OptionDescription.of(Text.translatable("lightbr.config.render_lava.desc")))
                .binding(
                        true,
                        () -> LightBR.config.renderAllLava,
                        newValue -> LightBR.config.renderAllLava = newValue
                )
                .available(!LightBR.isRenderAllLavaServerControlled())
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        defaultSettingsGroup.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("lightbr.config.unrender_block_entities.name"))
                .description(OptionDescription.of(Text.translatable("lightbr.config.unrender_block_entities.desc")))
                .binding(
                        false,
                        () -> LightBR.config.shouldUnrenderBlockEntities,
                        newValue -> LightBR.config.shouldUnrenderBlockEntities = newValue
                )
                .available(!LightBR.isUnrenderBlockEntitiesServerControlled())
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        defaultSettingsGroup.option(ButtonOption.createBuilder()
                .name(Text.translatable("lightbr.config.renderable_block_entities.name"))
                .text(Text.translatable("lightbr.config.renderable_block_entities.open_selector"))
                .description(OptionDescription.of(Text.translatable(
                        "lightbr.config.renderable_block_entities.desc"
                )))
                .available(!LightBR.isRenderableBlockEntitiesServerControlled())
                .action((screen, option) -> {
                    List<String> options = Utils.getBlockEntityBackedBlockIds();
                    MinecraftClient.getInstance().setScreen(new SearchableMultiSelectScreen(
                            screen,
                            Text.translatable("lightbr.config.renderable_block_entities.title"),
                            options,
                            LightBR.config.renderableBlockEntities,
                            () -> {
                                LightBR.config.saveAndReloadWorld();
                            }
                    ));
                })
                .build()
        );

        return defaultSettingsGroup.build();
    }
}
