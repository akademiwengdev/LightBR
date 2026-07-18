package org.wengdev.lightbr.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
//? if 1.21.11
//import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SearchableMultiSelectScreen extends Screen {
    private static final int PADDING = 20;
    private static final int SEARCH_HEIGHT = 20;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_HEIGHT = 18;

    private final Screen parent;
    private final List<String> allIds;
    private final Set<String> selectedIds;
    private final List<String> selectedTarget;
    private final Runnable onCloseCallback;

    private EditBox searchField;
    private List<String> filteredIds = new ArrayList<>();
    private int listTop;
    private int listBottom;
    private int listLeft;
    private int listRight;
    private int scrollOffset;

    public SearchableMultiSelectScreen(Screen parent, Component title, Collection<String> allIds, List<String> selectedTarget, Runnable onCloseCallback) {
        super(title);
        this.parent = parent;
        this.allIds = new ArrayList<>(allIds);
        this.allIds.sort(Comparator.naturalOrder());
        this.selectedTarget = selectedTarget;
        this.selectedIds = new LinkedHashSet<>(selectedTarget);
        this.onCloseCallback = onCloseCallback;
    }

    @Override
    protected void init() {
        int searchWidth = this.width - (PADDING * 2);
        this.searchField = new EditBox(this.font, PADDING, PADDING + 18, searchWidth, SEARCH_HEIGHT, Component.translatable("lightbr.select.search"));
        this.searchField.setMaxLength(256);
        this.searchField.setResponder(value -> {
            this.scrollOffset = 0;
            this.updateFilteredIds();
        });
        this.addRenderableWidget(this.searchField);
        this.setInitialFocus(this.searchField);

        int buttonY = this.height - PADDING - BUTTON_HEIGHT;
        int buttonWidth = 96;
        int buttonGap = 8;
        int center = this.width / 2;

        this.addRenderableWidget(Button.builder(Component.translatable("lightbr.select.select_all"), button -> this.selectedIds.addAll(this.filteredIds))
                .bounds(center - (buttonWidth * 2) - (buttonGap * 2), buttonY, buttonWidth, BUTTON_HEIGHT)
                .build());

        this.addRenderableWidget(Button.builder(Component.translatable("lightbr.select.clear"), button -> this.selectedIds.clear())
                .bounds(center - buttonWidth - buttonGap, buttonY, buttonWidth, BUTTON_HEIGHT)
                .build());

        this.addRenderableWidget(Button.builder(Component.translatable("lightbr.select.done"), button -> this.applyAndClose())
                .bounds(center + buttonGap, buttonY, buttonWidth, BUTTON_HEIGHT)
                .build());

        this.addRenderableWidget(Button.builder(Component.translatable("lightbr.select.cancel"), button -> this.onClose())
                .bounds(center + buttonWidth + (buttonGap * 2), buttonY, buttonWidth, BUTTON_HEIGHT)
                .build());

        this.listLeft = PADDING;
        this.listRight = this.width - PADDING;
        this.listTop = this.searchField.getY() + SEARCH_HEIGHT + 10;
        this.listBottom = buttonY - 10;

        this.updateFilteredIds();
    }

    private void updateFilteredIds() {
        String query = this.searchField == null ? "" : this.searchField.getValue().trim().toLowerCase(Locale.ROOT);
        this.filteredIds = new ArrayList<>();

        for (String id : this.allIds) {
            if (query.isEmpty() || id.toLowerCase(Locale.ROOT).contains(query)) {
                this.filteredIds.add(id);
            }
        }

        int maxOffset = Math.max(0, this.filteredIds.size() - this.getVisibleRowCount());
        this.scrollOffset = Math.clamp(this.scrollOffset, 0, maxOffset);
    }

    private int getVisibleRowCount() {
        int height = Math.max(0, this.listBottom - this.listTop);
        return Math.max(1, height / ROW_HEIGHT);
    }

    private void applyAndClose() {
        this.selectedTarget.clear();
        this.selectedTarget.addAll(this.selectedIds);
        Collections.sort(this.selectedTarget);
        this.onCloseCallback.run();
        this.onClose();
    }

    @Override
    public void onClose() {
        //? if 1.21.4
        assert this.minecraft != null;
        this.minecraft.setScreen(this.parent);
    }

    //? if 1.21.11 {
    /*@Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        Boolean result = mouseClickedInternal(event.x(), event.y(), event.button());
        if (result != null) return result;

        return super.mouseClicked(event, bl);
    }
    *///? } elif 1.21.4 {
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Boolean result = mouseClickedInternal(mouseX, mouseY, button);
        if (result != null) return result;
    
        return super.mouseClicked(mouseX, mouseY, button);
    }
    //? }

    private Boolean mouseClickedInternal(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= this.listLeft && mouseX <= this.listRight && mouseY >= this.listTop && mouseY <= this.listBottom) {
            int rowIndex = (int) ((mouseY - this.listTop) / ROW_HEIGHT);
            int entryIndex = this.scrollOffset + rowIndex;
            if (entryIndex >= 0 && entryIndex < this.filteredIds.size()) {
                String id = this.filteredIds.get(entryIndex);
                if (this.selectedIds.contains(id)) {
                    this.selectedIds.remove(id);
                } else {
                    this.selectedIds.add(id);
                }
                return true;
            }
        }

        return null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX >= this.listLeft && mouseX <= this.listRight && mouseY >= this.listTop && mouseY <= this.listBottom) {
            int maxOffset = Math.max(0, this.filteredIds.size() - this.getVisibleRowCount());
            if (verticalAmount > 0) {
                this.scrollOffset = Math.max(0, this.scrollOffset - 1);
            } else if (verticalAmount < 0) {
                this.scrollOffset = Math.min(maxOffset, this.scrollOffset + 1);
            }
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, PADDING, 0xFFFFFF);
        guiGraphics.drawString(this.font, Component.translatable("lightbr.select.selected_count", this.selectedIds.size(), this.allIds.size()), PADDING, this.searchField.getY() - 12, 0xAAAAAA, false);

        guiGraphics.fill(this.listLeft, this.listTop, this.listRight, this.listBottom, 0x66000000);

        int visibleRows = this.getVisibleRowCount();
        int end = Math.min(this.filteredIds.size(), this.scrollOffset + visibleRows);

        for (int i = this.scrollOffset; i < end; i++) {
            int drawIndex = i - this.scrollOffset;
            int rowY = this.listTop + drawIndex * ROW_HEIGHT;
            String id = this.filteredIds.get(i);
            boolean selected = this.selectedIds.contains(id);

            int rowColor = selected ? 0x553C8C3C : 0x33222222;
            guiGraphics.fill(this.listLeft + 1, rowY, this.listRight - 1, rowY + ROW_HEIGHT - 1, rowColor);

            String entryKey = selected ? "lightbr.select.entry_selected" : "lightbr.select.entry_unselected";
            guiGraphics.drawString(this.font, Component.translatable(entryKey, id), this.listLeft + 6, rowY + 5, 0xFFFFFF, false);
        }

        if (this.filteredIds.isEmpty()) {
            guiGraphics.drawCenteredString(this.font, Component.translatable("lightbr.select.no_matches"), this.width / 2, this.listTop + 8, 0xAAAAAA);
        }
    }
}

