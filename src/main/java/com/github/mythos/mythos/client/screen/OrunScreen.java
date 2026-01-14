package com.github.mythos.mythos.client.screen;

import com.github.manasmods.manascore.api.client.gui.FontRenderHelper;
import com.github.manasmods.manascore.api.client.gui.widget.ImagePredicateButton;
import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.client.TensuraGUIHelper;
import com.github.mythos.mythos.menu.OrunMenu;
import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.networking.play2server.SkillCopyPacket;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrunScreen extends AbstractContainerScreen<OrunMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("tensura", "textures/gui/skill_creator/skill_creator.png");
    private static final ResourceLocation SKILL_BAR = new ResourceLocation("tensura", "textures/gui/skill_button.png");
    private static final ResourceLocation SCROLL_BAR = new ResourceLocation("tensura", "textures/gui/scroll_bar.png");
    private static final ResourceLocation GAIN_BUTTON = new ResourceLocation("tensura", "textures/gui/skill_creator/gain_button.png");
    private Cached<List<ManasSkill>, String> filteredSkills;
    private ManasSkill selectedSkill = null;
    private boolean scrolling;
    private float scrollOffs;
    private int startIndex;
    private EditBox searchField;
    private final List<ManasSkill> skills;
    private final UUID targetUUID;

    public OrunScreen(OrunMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle.copy().withStyle(ChatFormatting.WHITE));
        this.imageWidth = 233;
        this.imageHeight = 140;
        this.skills = pMenu.getSkills().stream().map(loc -> {
            Object obj = SkillAPI.getSkillRegistry().getValue(loc);
            return (obj instanceof ManasSkill s) ? s : null;
        }).filter(java.util.Objects::nonNull).toList();
        this.targetUUID = pMenu.getTargetUUID();
    }

    @Override
    protected void init() {
        super.init();
        this.scrollOffs = 0.0F;
        this.startIndex = 0;
        this.searchField = new EditBox(this.font, this.leftPos + 19, this.topPos + 27, 79, 9, Component.empty());
        this.searchField.setBordered(false);
        this.searchField.setResponder(s -> {
            this.scrollOffs = 0.0F;
            this.startIndex = 0;
        });
        this.addRenderableWidget(this.searchField);
        this.filteredSkills = new Cached<>(() -> {
            List<ManasSkill> list = new ArrayList<>(this.skills);
            String filter = this.searchField.getValue().toLowerCase();
            if (!filter.isEmpty()) list.removeIf(s -> s.getName() == null || !s.getName().getString().toLowerCase().contains(filter));
            return list;
        }, info -> {
            if (info.lastCallbackReference == null || !info.lastCallbackReference.equals(this.searchField.getValue())) {
                info.lastCallbackReference = this.searchField.getValue();
                info.needsUpdate = true;
            }
            return info;
        });
        this.addRenderableWidget(new ImagePredicateButton(this.leftPos + 162, this.topPos + 116, 20, 20, GAIN_BUTTON, b -> {
            if (this.selectedSkill != null) {
                MythosNetwork.sendToServer(new SkillCopyPacket(SkillUtils.getSkillId(this.selectedSkill), this.targetUUID));
                this.onClose();
            }
        }, (b, p, x, y) -> this.renderTooltip(p, Component.literal("Copy"), x, y), this.menu::check));
    }

    @Override
    protected void renderBg(PoseStack p, float pt, int mx, int my) {
        this.renderBackground(p);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        this.blit(p, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        int k = (int)(78.0F * this.scrollOffs);
        RenderSystem.setShaderTexture(0, SCROLL_BAR);
        this.blit(p, this.leftPos + 98, this.topPos + 43 + k, 0, this.isScrollBarActive() ? 13 : 0, 10, 13, 10, 26);
        List<ManasSkill> list = this.filteredSkills.getValue();
        int end = Math.min(this.startIndex + 7, list.size());
        for (int i = this.startIndex; i < end; i++) {
            int bx = this.leftPos + 6, by = this.topPos + 43 + (i - this.startIndex) * 13;
            boolean h = mx >= bx && my >= by && mx < bx + 89 && my < by + 13;
            RenderSystem.setShaderTexture(0, SKILL_BAR);
            this.blit(p, bx, by, 0, h ? 13 : 0, 89, 13, 89, 26);
            ManasSkill s = list.get(i);
            ChatFormatting c = (s instanceof Skill ts) ? ts.getType().getChatFormatting() : ChatFormatting.WHITE;
            TensuraGUIHelper.renderScaledShadowText(p, this.font, TensuraGUIHelper.shortenTextComponent(this.skillName(s).withStyle(c), 14), bx + 5, by + 3, 80, 10, 0xFFFFFF, 1.0F, 0.01F);
        }
        if (this.selectedSkill != null) {
            ResourceLocation icon = this.selectedSkill.getSkillIcon();
            if (icon != null) {
                RenderSystem.setShaderTexture(0, icon);
                this.blit(p, this.leftPos + 162, this.topPos + 9, 0, 0, 20, 20, 20, 20);
            }
            FontRenderHelper.renderScaledTextInArea(p, this.font, this.selectedSkill.getSkillDescription(), this.leftPos + 125, this.topPos + 46, 94, 66, Color.LIGHT_GRAY);
        }
    }

    @Override
    public void render(PoseStack p, int mx, int my, float pt) {
        super.render(p, mx, my, pt);
        this.renderTooltip(p, mx, my);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int b) {
        this.scrolling = false;
        List<ManasSkill> list = this.filteredSkills.getValue();
        int end = Math.min(this.startIndex + 7, list.size());
        for (int i = this.startIndex; i < end; i++) {
            int bx = this.leftPos + 6, by = this.topPos + 43 + (i - this.startIndex) * 13;
            if (mx >= bx && my >= by && mx < bx + 89 && my < by + 13) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.selectedSkill = list.get(i);
                return true;
            }
        }
        if (mx >= this.leftPos + 98 && mx < this.leftPos + 109 && my >= this.topPos + 43 && my < this.topPos + 135) this.scrolling = true;
        return super.mouseClicked(mx, my, b);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int b, double dx, double dy) {
        if (this.scrolling && this.isScrollBarActive()) {
            this.scrollOffs = Mth.clamp((float)((my - (this.topPos + 43) - 6.5) / 78.0), 0.0F, 1.0F);
            this.startIndex = (int)(this.scrollOffs * this.getOffscreenRows() + 0.5);
            return true;
        }
        return super.mouseDragged(mx, my, b, dx, dy);
    }

    private MutableComponent skillName(@Nullable ManasSkill s) { return s != null ? s.getName() : Component.translatable("tensura.race.selection.skills.empty"); }
    private boolean isScrollBarActive() { return this.filteredSkills.getValue().size() > 7; }
    private int getOffscreenRows() { return this.filteredSkills.getValue().size() - 7; }
    @Override protected void renderLabels(PoseStack p, int mx, int my) {}
}