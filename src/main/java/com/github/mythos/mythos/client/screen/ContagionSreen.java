package com.github.mythos.mythos.client.screen;

import com.github.manasmods.tensura.client.TensuraGUIHelper;
import com.github.mythos.mythos.config.MythosContagionConfig;
import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.networking.play2server.MutationPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContagionSreen extends Screen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("tensura", "textures/gui/skill_creator/skill_creator.png");
    private static final ResourceLocation SKILL_BAR = new ResourceLocation("tensura", "textures/gui/skill_button.png");
    private static final ResourceLocation SCROLL_BAR = new ResourceLocation("tensura", "textures/gui/scroll_bar.png");

    private final int biomatter;
    private final Map<String, Integer> currentLevels;
    private final List<String> pathKeys;

    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private final int displayCount = 7;

    protected int leftPos;
    protected int topPos;
    protected int imageWidth = 233;
    protected int imageHeight = 140;

    public ContagionSreen(int biomatter, Map<String, Integer> levels) {
        super(Component.literal("Viral Mutation"));
        this.biomatter = biomatter;
        this.currentLevels = levels;
        this.pathKeys = new ArrayList<>(levels.keySet());

        System.out.println("Client Screen Debug: Received " + pathKeys.size() + " paths.");
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.updateButtons();
    }

    private void updateButtons() {
        this.clearWidgets();

        try {
            int end = Math.min(startIndex + displayCount, pathKeys.size());
            for (int i = startIndex; i < end; i++) {
                String path = pathKeys.get(i);
                int level = currentLevels.get(path);

                var maxConfig = MythosContagionConfig.MAX_LEVELS.get(path);
                if (maxConfig == null) continue;

                int max = maxConfig.get();
                int cost = 1000 + (level * 500);
                boolean isMaxed = level >= max;

                int buttonX = this.leftPos + 6;
                int buttonY = this.topPos + 43 + (i - startIndex) * 13;

                Button evolveButton = new Button(buttonX, buttonY, 89, 13,
                        Component.literal(path.toUpperCase() + " [" + level + "/" + max + "]"),
                        (btn) -> {
                            MythosNetwork.sendToServer(new MutationPacket(path));
                            this.onClose();
                        },
                        (btn, pose, mX, mY) -> {
                            List<Component> tooltip = new ArrayList<>();
                            tooltip.add(Component.literal("§7Evolution Cost: §a" + cost + " Biomatter"));
                            if (isMaxed) tooltip.add(Component.literal("§6Maximum Level."));
                            this.renderComponentTooltip(pose, tooltip, mX, mY);
                        }
                ) {
                    @Override
                    public void renderButton(PoseStack pose, int mX, int mY, float pt) {
                        this.active = !isMaxed && biomatter >= cost;
                        RenderSystem.setShader(GameRenderer::getPositionTexShader);
                        RenderSystem.setShaderTexture(0, SKILL_BAR);

                        int v = this.isHoveredOrFocused() ? 13 : 0;
                        blit(pose, this.x, this.y, 0, v, this.width, this.height, 89, 26);

                        TensuraGUIHelper.renderScaledShadowText(pose, Minecraft.getInstance().font,
                                this.getMessage(), this.x + 5, this.y + 3, 80, 10,
                                this.active ? 0xFFFFFF : 0xAAAAAA, 1.0F, 0.01F);
                    }
                };

                this.addRenderableWidget(evolveButton);
            }
        } catch (IllegalStateException e) {

        }
    }

    @Override
    public void render(PoseStack pose, int mx, int my, float pt) {
        this.renderBackground(pose);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        this.blit(pose, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        TensuraGUIHelper.renderScaledCenteredXText(
                this.font, pose, Component.literal("Viral Mutation"), this.leftPos, this.topPos + 10, this.imageWidth, 20, Color.GREEN, true);

        TensuraGUIHelper.renderCenteredXText(
                this.font, pose, Component.literal("BIO: " + biomatter), this.leftPos + 10, this.topPos + 27, 89, Color.WHITE, false);

        if (this.isScrollBarActive()) {
            int k = (int)(78.0F * this.scrollOffs);
            RenderSystem.setShaderTexture(0, SCROLL_BAR);
            this.blit(pose, this.leftPos + 98, this.topPos + 43 + k, 0, this.scrolling ? 13 : 0, 10, 13, 10, 26);
        }

        super.render(pose, mx, my, pt);
        for (net.minecraft.client.gui.components.Widget widget : this.renderables) {
            if (widget instanceof GuiEventListener listener && listener.isMouseOver(mx, my)) {
                if (widget instanceof Button btn) {
                    btn.renderToolTip(pose, mx, my);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int b) {
        this.scrolling = false;
        if (mx >= this.leftPos + 98 && mx < this.leftPos + 109 && my >= this.topPos + 43 && my < this.topPos + 135) {
            this.scrolling = true;
            return true;
        }
        return super.mouseClicked(mx, my, b);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int b, double dx, double dy) {
        if (this.scrolling && this.isScrollBarActive()) {
            this.scrollOffs = Mth.clamp((float)((my - (this.topPos + 43) - 6.5) / 78.0), 0.0F, 1.0F);
            int newStartIndex = (int)(this.scrollOffs * this.getOffscreenRows() + 0.5);
            if (newStartIndex != this.startIndex) {
                this.startIndex = newStartIndex;
                this.updateButtons();
            }
            return true;
        }
        return super.mouseDragged(mx, my, b, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        if (this.isScrollBarActive()) {
            int newStartIndex = Mth.clamp(this.startIndex - (int) Math.signum(delta), 0, this.getOffscreenRows());
            if (newStartIndex != this.startIndex) {
                this.startIndex = newStartIndex;
                this.scrollOffs = (float) this.startIndex / (float) this.getOffscreenRows();
                this.updateButtons();
                return true;
            }
        }
        return super.mouseScrolled(mx, my, delta);
    }

    private boolean isScrollBarActive() { return this.pathKeys.size() > displayCount; }
    private int getOffscreenRows() { return Math.max(0, this.pathKeys.size() - displayCount); }
}