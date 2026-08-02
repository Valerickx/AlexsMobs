package com.github.alexthe666.alexsmobs.client.gui;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.inventory.MenuTransmutationTable;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class GUITransmutationTable extends AbstractContainerScreen<MenuTransmutationTable> implements net.minecraft.client.gui.screens.inventory.MenuAccess<MenuTransmutationTable> {
    public static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/gui/transmutation_table.png");
    private int tickCount = 0;
    private ButtonTransmute transmuteBtn1;
    private ButtonTransmute transmuteBtn2;
    private ButtonTransmute transmuteBtn3;

    public GUITransmutationTable(MenuTransmutationTable menu, Inventory inventory, Component name) {
        super(menu, inventory, name);
    }

    @Override
    protected void init() {
        super.init();
        int i = this.leftPos;
        int j = this.topPos;
        this.addRenderableWidget(transmuteBtn1 = new ButtonTransmute(this, i + 30, j + 16, (button) -> {
            this.menu.clickMenuButton(minecraft.player, 0);
        }));
        this.addRenderableWidget(transmuteBtn2 = new ButtonTransmute(this, i + 30, j + 35, (button) -> {
            this.menu.clickMenuButton(minecraft.player, 1);
        }));
        this.addRenderableWidget(transmuteBtn3 = new ButtonTransmute(this, i + 30, j + 54, (button) -> {
            this.menu.clickMenuButton(minecraft.player, 2);
        }));
        transmuteBtn1.visible = false;
        transmuteBtn2.visible = false;
        transmuteBtn3.visible = false;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int i = this.leftPos;
        int j = this.topPos;
        guiGraphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        this.renderItemsTransmute(guiGraphics, mouseX, mouseY);
        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
    }

    protected void containerTick() {
        tickCount++;
        boolean thingIn = !this.menu.getSlot(0).getItem().isEmpty();
        transmuteBtn1.visible = !AlexsMobs.PROXY.getDisplayTransmuteResult(0).isEmpty() && thingIn;
        transmuteBtn2.visible = !AlexsMobs.PROXY.getDisplayTransmuteResult(1).isEmpty() && thingIn;
        transmuteBtn3.visible = !AlexsMobs.PROXY.getDisplayTransmuteResult(2).isEmpty() && thingIn;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        super.extractLabels(guiGraphics, mouseX, mouseY);
    }

    protected void renderItemsTransmute(GuiGraphicsExtractor guiGraphics, int x, int y) {
        int i = this.leftPos;
        int j = this.topPos;
        if (!this.menu.getSlot(0).getItem().isEmpty()) {
            guiGraphics.item(AlexsMobs.PROXY.getDisplayTransmuteResult(0), i + 31, j + 17);
            guiGraphics.item(AlexsMobs.PROXY.getDisplayTransmuteResult(1), i + 31, j + 36);
            guiGraphics.item(AlexsMobs.PROXY.getDisplayTransmuteResult(2), i + 31, j + 55);
        }
    }

}



