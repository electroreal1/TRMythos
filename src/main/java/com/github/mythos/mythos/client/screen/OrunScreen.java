package com.github.mythos.mythos.client.screen;

import com.github.manasmods.manascore.api.client.gui.FontRenderHelper;
import com.github.manasmods.manascore.api.client.gui.widget.ImagePredicateButton;
import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.Skill.SkillType;
import com.github.manasmods.tensura.client.TensuraGUIHelper;
import com.github.mythos.mythos.menu.OrunMenu;
import com.github.mythos.mythos.networking.MythosNetwork;
import com.github.mythos.mythos.networking.play2server.RequestSkillCopyOrunmilaPacket;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.github.mythos.mythos.util.Cached;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
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
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrunScreen extends AbstractContainerScreen<OrunMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("tensura", "textures/gui/skill_creator/skill_creator.png");
    private static final ResourceLocation SKILL_BAR = new ResourceLocation("tensura", "textures/gui/skill_button.png");
    private static final ResourceLocation SCROLL_BAR = new ResourceLocation("tensura", "textures/gui/scroll_bar.png");
    private static final ResourceLocation GAIN_BUTTON = new ResourceLocation("tensura", "textures/gui/skill_creator/gain_button.png");
    public OrunMenu containerId;
    private Cached<List<ManasSkill>, String> filteredSkills;
    private Cached<List<ManasSkill>, String> copiedSkills;
    private ManasSkill selectedSkill = null;
    private boolean scrolling;
    private float scrollOffs;
    private int startIndex;
    private EditBox searchField;
    private final List<ManasSkill> skills;
    private final Player player;
    private final UUID targetUUID;

    public OrunScreen(OrunMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle.copy().withStyle(ChatFormatting.WHITE));
        this.imageWidth = 233;
        this.imageHeight = 140;
        this.player = pPlayerInventory.player;
        this.skills = ((OrunMenu)this.menu).getSkills().stream().map((skill) -> {
            return (ManasSkill)SkillAPI.getSkillRegistry().getValue(skill);
        }).toList();
        this.targetUUID = ((OrunMenu)this.menu).getTargetUUID();
    }

    public static final Logger LOG = LogManager.getLogger("Mythos");

    @Override
    protected void init() {
        LOG.info("OrunScreen.init() called. searchField = {}", this.searchField);
        LOG.info("skills size = {}", skills == null ? "null" : skills.size());
        super.init();
        this.scrollOffs = 0.0F;
        this.startIndex = 0;

        // Create search box
        this.searchField = new EditBox(this.font, this.getGuiLeft() + 19, this.getGuiTop() + 27, 79, 9, Component.empty());
        this.searchField.setBordered(false);
        this.searchField.setResponder(s -> {
            if (!s.isEmpty()) {
                this.scrollOffs = 0.0F;
                this.startIndex = 0;
            }
        });
        this.addRenderableWidget(this.searchField);

        // Create filtered skills cache
        this.filteredSkills = new Cached<List<ManasSkill>, String>(
                () -> {
                    List<ManasSkill> filteredSkillList = new ArrayList<>(List.copyOf(this.skills));
                    if (!this.searchField.getValue().isEmpty() && !this.searchField.getValue().isBlank()) {
                        String filterValue = this.searchField.getValue().toLowerCase();
                        filteredSkillList.removeIf(skill -> {
                            if (skill.getName() == null) return true;
                            return !skill.getName().getString().toLowerCase().contains(filterValue);
                        });
                    }
                    return filteredSkillList;
                },
                (com.github.mythos.mythos.util.Cached.CallbackInfo<String> info) -> {
                    if (info.lastCallbackReference == null || !info.lastCallbackReference.equals(this.searchField.getValue())) {
                        info.lastCallbackReference = this.searchField.getValue();
                        info.needsUpdate = true;
                    }
                    return info;
                }
        );

        int buttonX = this.getGuiLeft() + 162;
        int buttonY = this.getGuiTop() + 116;

        ImagePredicateButton gainSkillButton = new ImagePredicateButton(
                buttonX,
                buttonY,
                20,
                20,
                GAIN_BUTTON,
                (pButton) -> {
                    if (this.selectedSkill != null) {
                        MythosNetwork.sendToServer(new RequestSkillCopyOrunmilaPacket(SkillUtils.getSkillId(this.selectedSkill)));
                    }
                },
                (button, poseStack, x, y) -> {
                    this.renderTooltip(poseStack, Component.translatable("trmythos.skill.orunmila.analysis_copy"), x, y);
                },
                this.menu::check
        );
        this.addRenderableWidget(gainSkillButton);

    }

//        OrunMenu orunMenu = (OrunMenu)this.menu;
//
//        this.searchField = new EditBox(this.font, this.getGuiLeft() + 19, this.getGuiTop(){} + 27, 79, 9, Component.empty()); //orunMenu::check
//        ImagePredicateButton gainSkillButton = new ImagePredicateButton(var10002, var10003, 20, 20, var10006, var10007, var10008);
//        this.addRenderableWidget(gainSkillButton);


    @Override
    protected void renderBg(PoseStack poseStack, float pPartialTick, int pX, int pY) {
        LOG.info("OrunScreen.renderBg() called scrollsOffs={} startIndex={}", this.scrollOffs, this.startIndex);
        LOG.info("skills size = {}", skills == null ? "null" : skills.size());
        fill(poseStack, this.searchField.x, this.searchField.y, this.searchField.x + this.searchField.getWidth(), this.searchField.y + this.searchField.getHeight(), 0x80FF0000);
        this.renderBackground(poseStack);

        // Draw background
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Draw title text
        TensuraGUIHelper.renderScaledCenteredXText(
                this.font,
                poseStack,
                Component.translatable("trmythos.skill.orunmila.analysis_copy"),
                this.getGuiLeft() + 3,
                this.getGuiTop() + 7,
                112,
                11,
                Color.LIGHT_GRAY,
                false
        );

        // Draw scroll bar
        int k = (int)(78.0F * this.scrollOffs);
        RenderSystem.setShaderTexture(0, SCROLL_BAR);
        blit(poseStack, this.leftPos + 98, this.topPos + 43 + k, 0.0F, this.isScrollBarActive() ? 13.0F : 0.0F, 10, 13, 10, 26);

        // Render skill buttons
        List<ManasSkill> filteredSkills = this.filteredSkills.getValue();
        int lastVisibleElementIndex = Math.min(this.startIndex + 7, filteredSkills.size());
        this.renderButtons(poseStack, pX, pY, lastVisibleElementIndex, filteredSkills);

        // Render selected skill info
        if (this.selectedSkill != null) {
            ResourceLocation location = this.selectedSkill.getSkillIcon();
            if (location != null) {
                RenderSystem.setShaderTexture(0, location);
                blit(poseStack, this.getGuiLeft() + 162, this.getGuiTop() + 9, 0.0F, 0.0F, 20, 20, 20, 20);
            }

            Component description = this.selectedSkill.getSkillDescription();
            FontRenderHelper.renderScaledTextInArea(
                    poseStack,
                    this.font,
                    description,
                    (float)(this.getGuiLeft() + 125),
                    (float)(this.getGuiTop() + 46),
                    94.0F,
                    66.0F,
                    Color.LIGHT_GRAY
            );

            boolean hovering = pX > this.getGuiLeft() + 158 && pX < this.getGuiLeft() + 185
                    && pY > this.getGuiTop() + 5 && pY < this.getGuiTop() + 32;

            if (hovering && this.selectedSkill.getName() != null) {
                this.renderTooltip(poseStack, this.selectedSkill.getName(), pX, pY);
            }
        }
    }

    private void renderButtons(PoseStack pPoseStack, int pMouseX, int pMouseY, int pLastVisibleElementIndex, List<ManasSkill> list) {
        for(int i = this.startIndex; i < pLastVisibleElementIndex && i < list.size(); ++i) {
            int x = this.getGuiLeft() + 6;
            int y = this.getGuiTop() + 43 + (i - this.startIndex) * 13;
            int offset = 0;
            boolean hovering = pMouseX >= x && pMouseY >= y && pMouseX < x + 89 && pMouseY < y + 13;
            if (hovering) {
                offset = 13;
            }

            RenderSystem.setShaderTexture(0, SKILL_BAR);
            blit(pPoseStack, x, y, 0.0F, (float)offset, 89, 13, 89, 26);
            ManasSkill manasSkill = (ManasSkill)list.get(i);
            Skill.SkillType skillType = SkillType.COMMON;
            if (manasSkill instanceof Skill skill) {
                skillType = skill.getType();
            }

            MutableComponent name = this.skillName(manasSkill).withStyle(skillType.getChatFormatting());
            TensuraGUIHelper.renderScaledShadowText(pPoseStack, this.font, TensuraGUIHelper.shortenTextComponent(name, 14), (float)(this.getGuiLeft() + 11), (float)(this.getGuiTop() + 46 + (i - this.startIndex) * 13), 85.0F, 13.0F, Color.WHITE.getRGB(), 2.0F, 0.01F);
            if (hovering) {
                this.renderTooltip(pPoseStack, name, pMouseX, pMouseY);
            }
        }

    }

    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {}

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    protected void renderTooltip(PoseStack pPoseStack, int pX, int pY) {
        super.renderTooltip(pPoseStack, pX, pY);
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.searchField.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        } else if (this.searchField.isFocused() && this.searchField.isVisible() && pKeyCode != 256) {
            return true;
        } else {
            return this.minecraft != null && this.minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode)) || super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
    }

    public boolean mouseClicked(double pX, double pY, int pButton) {
        this.scrolling = false;
        List<ManasSkill> skills = (List)this.filteredSkills.getValue();
        int lastDisplayedIndex = Math.min(this.startIndex + 7, skills.size());

        for(int i = this.startIndex; i < lastDisplayedIndex; ++i) {
            int x = this.getGuiLeft() + 6;
            int y = this.getGuiTop() + 43 + (i - this.startIndex) * 13;
            if (skills.size() <= i) {
                break;
            }

            if (pX >= (double)x && pY >= (double)y && pX < (double)(x + 89) && pY < (double)(y + 13)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.selectedSkill = (ManasSkill)skills.get(i);
                return true;
            }
        }

        if (pX >= (double)(this.getGuiLeft() + 98) && pX < (double)(this.getGuiLeft() + 109) && pY >= (double)(this.getGuiTop() + 43) && pY < (double)(this.getGuiTop() + 135)) {
            this.scrolling = true;
        }

        return super.mouseClicked(pX, pY, pButton);
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (this.isScrollBarActive()) {
            int i = this.getOffscreenRows();
            float f = (float)pDelta / (float)i;
            this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
            this.startIndex = Math.max(0, Math.min(this.startIndex - (int)pDelta, ((List)this.filteredSkills.getValue()).size() - 7));
        }

        return true;
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.scrolling && this.isScrollBarActive()) {
            int i = this.getGuiTop() + 43;
            int j = i + 91;
            this.scrollOffs = (float)((pMouseY - (double)i - 6.5) / (double)((float)(j - i) - 13.0F));
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOffs * (float)this.getOffscreenRows()) + 0.5);
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    private MutableComponent skillName(@Nullable ManasSkill skill) {
        return skill != null && skill.getName() != null ? skill.getName() : Component.translatable("tensura.race.selection.skills.empty");
    }

    private boolean isScrollBarActive() {
        return ((List)this.filteredSkills.getValue()).size() > 7;
    }

    private int getOffscreenRows() {
        return ((List)this.filteredSkills.getValue()).size() - 7;
    }
}
