package com.github.mythos.mythos.client.screen;

import com.github.manasmods.manascore.api.client.gui.FontRenderHelper;
import com.github.manasmods.manascore.api.client.gui.widget.ImagePredicateButton;
import com.github.manasmods.tensura.client.TensuraGUIHelper;
import com.github.mythos.mythos.menu.SoundSwapperMenu;
import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.networking.play2server.SoundSwapPacket;
import com.github.mythos.mythos.util.Cached;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SoundSwapperScreen extends AbstractContainerScreen<SoundSwapperMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("tensura", "textures/gui/skill_creator/skill_creator.png");
    private static final ResourceLocation SKILL_BAR = new ResourceLocation("tensura", "textures/gui/skill_button.png");
    private static final ResourceLocation SCROLL_BAR = new ResourceLocation("tensura", "textures/gui/scroll_bar.png");
    private static final ResourceLocation APPLY_BUTTON = new ResourceLocation("tensura", "textures/gui/skill_creator/gain_button.png");
    private static final ResourceLocation REMOVE_BUTTON = new ResourceLocation("minecraft", "textures/gui/sprites/container/beacon/cancel.png");
    public SoundSwapperScreen(SoundSwapperMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 233;
        this.imageHeight = 140;
        this.allSounds = new ArrayList<>(ForgeRegistries.SOUND_EVENTS.getValues());
    }
    private int imageWidth = 233;
    private int imageHeight = 140;
    private int leftPos, topPos;

    private Cached<List<SoundEvent>, String> filteredSounds;
    private SoundEvent selectedSound = null;
    private boolean scrolling;
    private float scrollOffs;
    private int startIndex;

    private EditBox searchField;
    private EditBox replacementField;
    private final List<SoundEvent> allSounds;

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.searchField = new EditBox(this.font, this.leftPos + 19, this.topPos + 27, 79, 9, Component.empty());
        this.searchField.setBordered(false);
        this.searchField.setResponder(s -> { this.scrollOffs = 0.0F; this.startIndex = 0; });
        this.addRenderableWidget(this.searchField);

        this.replacementField = new EditBox(this.font, this.leftPos + 125, this.topPos + 90, 94, 12, Component.literal("Replacement ID"));
        this.addRenderableWidget(this.replacementField);

        this.filteredSounds = new Cached<>(() -> {
            String filter = this.searchField.getValue().toLowerCase();
            return allSounds.stream()
                    .filter(s -> s.getLocation().toString().contains(filter))
                    .toList();
        }, info -> {
            info.needsUpdate = info.lastCallbackReference == null || !info.lastCallbackReference.equals(this.searchField.getValue());
            info.lastCallbackReference = this.searchField.getValue();
            return info;
        });

        this.addRenderableWidget(new ImagePredicateButton(this.leftPos + 155, this.topPos + 116, 20, 20, APPLY_BUTTON, b -> {
            if (this.selectedSound != null && !this.replacementField.getValue().isEmpty()) {
                MythosNetwork.sendToServer(new SoundSwapPacket(this.selectedSound.getLocation().toString(), this.replacementField.getValue(), false));
                this.onClose();
            }
        }, (b, p, x, y) -> this.renderTooltip(p, Component.literal("Apply Global Swap"), x, y), () -> true));

        this.addRenderableWidget(new ImagePredicateButton(this.leftPos + 185, this.topPos + 116, 20, 20, REMOVE_BUTTON, b -> {
            if (this.selectedSound != null) {
                MythosNetwork.sendToServer(new SoundSwapPacket(this.selectedSound.getLocation().toString(), "", true));
                this.onClose();
            }
        }, (b, p, x, y) -> this.renderTooltip(p, Component.literal("Remove This Swap"), x, y), () -> true));
    }

    @Override
    public void render(@NotNull PoseStack p, int mx, int my, float pt) {
        this.renderBackground(p);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        this.blit(p, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int k = (int)(78.0F * this.scrollOffs);
        RenderSystem.setShaderTexture(0, SCROLL_BAR);
        blit(p, this.leftPos + 98, this.topPos + 43 + k, 0, isScrollBarActive() ? 13 : 0, 10, 13, 10, 26);

        List<SoundEvent> list = this.filteredSounds.getValue();
        int end = Math.min(this.startIndex + 7, list.size());
        for (int i = this.startIndex; i < end; i++) {
            int bx = this.leftPos + 6, by = this.topPos + 43 + (i - this.startIndex) * 13;
            boolean h = mx >= bx && my >= by && mx < bx + 89 && my < by + 13;
            RenderSystem.setShaderTexture(0, SKILL_BAR);
            blit(p, bx, by, 0, h ? 13 : 0, 89, 13, 89, 26);

            String name = list.get(i).getLocation().getPath();
            TensuraGUIHelper.renderScaledShadowText(p, this.font, Component.literal(name), bx + 5, by + 3, 80, 10, 0xFFFFFF, 0.8F, 0.01F);
        }

        if (this.selectedSound != null) {
            String path = this.selectedSound.getLocation().toString();
            FontRenderHelper.renderScaledTextInArea(p, this.font, Component.literal("Targeting:\n§b" + path), this.leftPos + 125, this.topPos + 46, 94, 40, Color.WHITE);
        }

        super.render(p, mx, my, pt);
    }

    @Override
    protected void renderBg(@NotNull PoseStack p, float pt, int mx, int my) {
        this.renderBackground(p);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        this.blit(p, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int k = (int)(78.0F * this.scrollOffs);
        RenderSystem.setShaderTexture(0, SCROLL_BAR);
        blit(p, this.leftPos + 98, this.topPos + 43 + k, 0, this.isScrollBarActive() ? 13 : 0, 10, 13, 10, 26);

        List<SoundEvent> list = this.filteredSounds.getValue();
        int end = Math.min(this.startIndex + 7, list.size());
        for (int i = this.startIndex; i < end; i++) {
            int bx = this.leftPos + 6, by = this.topPos + 43 + (i - this.startIndex) * 13;
            boolean h = mx >= bx && my >= by && mx < bx + 89 && my < by + 13;

            RenderSystem.setShaderTexture(0, SKILL_BAR);
            blit(p, bx, by, 0, h ? 13 : 0, 89, 13, 89, 26);

            SoundEvent s = list.get(i);
            String displayName = s.getLocation().getPath();
            TensuraGUIHelper.renderScaledShadowText(p, this.font, Component.literal(displayName), bx + 5, by + 3, 80, 10, 0xFFFFFF, 0.8F, 0.01F);
        }

        if (this.selectedSound != null) {
            Component title = Component.literal("Selected Sound:").withStyle(ChatFormatting.AQUA);
            Component path = Component.literal(this.selectedSound.getLocation().toString()).withStyle(ChatFormatting.GRAY);

            this.font.draw(p, title, this.leftPos + 125, this.topPos + 46, 0xFFFFFF);
            FontRenderHelper.renderScaledTextInArea(p, this.font, path, this.leftPos + 125, this.topPos + 58, 94, 30, Color.WHITE);

            this.font.draw(p, Component.literal("Replace with:").withStyle(ChatFormatting.GOLD), this.leftPos + 125, this.topPos + 80, 0xFFFFFF);
        } else {
            FontRenderHelper.renderScaledTextInArea(p, this.font, Component.literal("Select a sound from the list to weave its frequency."), this.leftPos + 125, this.topPos + 46, 94, 66, Color.LIGHT_GRAY);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int b) {
        List<SoundEvent> list = this.filteredSounds.getValue();
        int end = Math.min(this.startIndex + 7, list.size());
        for (int i = this.startIndex; i < end; i++) {
            int bx = this.leftPos + 6, by = this.topPos + 43 + (i - this.startIndex) * 13;
            if (mx >= bx && my >= by && mx < bx + 89 && my < by + 13) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.selectedSound = list.get(i);
                return true;
            }
        }
        if (mx >= this.leftPos + 98 && mx < this.leftPos + 109 && my >= this.topPos + 43 && my < this.topPos + 135) this.scrolling = true;
        return super.mouseClicked(mx, my, b);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int b, double dx, double dy) {
        if (this.scrolling && isScrollBarActive()) {
            this.scrollOffs = Mth.clamp((float)((my - (this.topPos + 43) - 6.5) / 78.0), 0.0F, 1.0F);
            this.startIndex = (int)(this.scrollOffs * (filteredSounds.getValue().size() - 7) + 0.5);
            return true;
        }
        return super.mouseDragged(mx, my, b, dx, dy);
    }

    private boolean isScrollBarActive() { return this.filteredSounds.getValue().size() > 7; }
    @Override public boolean isPauseScreen() { return false; }
}