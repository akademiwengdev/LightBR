package org.wengdev.lightbr.config;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.wengdev.lightbr.LightBR;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class YACLConfigScreen extends YACLScreen {
    public YACLConfigScreen(Screen parent) {
        super(generateConfigScreen(), parent);
    }

    public static YetAnotherConfigLib generateConfigScreen() {
        final OptionGroup generalGroup = createGeneralGroup();
        final OptionGroup renderGroup = createRenderGroup();

        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("LightBR Config"))
                .category(
                        ConfigCategory.createBuilder()
                                .name(Text.literal("LightBR"))
                                .group(generalGroup)
                                .group(renderGroup)
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
                        newValue -> LightBR.config.isEnabled = newValue
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
                        newValue -> LightBR.config.renderAllWater = newValue
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
                        newValue -> LightBR.config.renderAllLava = newValue
                )
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        generalGroup.option(Option.<Boolean>createBuilder()
                .name(Text.literal("Un-render Block Entities"))
                .description(OptionDescription.of(Text.literal("Whether block entities should be un-rendered or not.")))
                .binding(
                        true,
                        () -> LightBR.config.shouldUnrenderBlockEntities,
                        newValue -> LightBR.config.shouldUnrenderBlockEntities = newValue
                )
                .controller(TickBoxControllerBuilder::create)
                .build()
        );

        return generalGroup.build();
    }

    private static OptionGroup createRenderGroup() {
        final OptionGroup.Builder renderGroup = OptionGroup.createBuilder();
        renderGroup.name(Text.literal("Render Settings"));

        renderGroup.option(ButtonOption.createBuilder()
                .name(Text.literal("Renderable Block Entities"))
                .text(Text.literal("Open Selector"))
                .description(OptionDescription.of(Text.literal(
                        "Pick blocks with block entities to keep rendering when 'Un-render No-Collision Blocks' is enabled. "
                                + "Includes search and multi-select."
                )))
                .action((screen, option) -> {
                    List<String> options = getBlockEntityBackedBlockIds();
                    MinecraftClient.getInstance().setScreen(new SearchableMultiSelectScreen(
                            screen,
                            Text.literal("Renderable Block Entities"),
                            options,
                            LightBR.config.renderableBlockEntities,
                            () -> {
                                LightBR.config.saveAndReloadWorld();
                            }
                    ));
                })
                .build()
        );

        return renderGroup.build();
    }

    private static List<String> getBlockEntityBackedBlockIds() {
        List<String> ids = new ArrayList<>();

        for (Block block : Registries.BLOCK) {
            if (block instanceof BlockEntityProvider || block.getDefaultState().hasBlockEntity()) {
                ids.add(Registries.BLOCK.getId(block).toString());
            }
        }

        ids.sort(Comparator.naturalOrder());
        return ids;
    }

    private static List<String> getAllBlockIds() {
        List<String> ids = new ArrayList<>();

        for (Block block : Registries.BLOCK) {
            ids.add(Registries.BLOCK.getId(block).toString());
        }

        ids.sort(Comparator.naturalOrder());
        return ids;
    }
}
