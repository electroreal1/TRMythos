//package com.github.mythos.mythos.client.screen;
//
//import com.github.manasmods.manascore.api.client.gui.FontRenderHelper;
//import com.github.manasmods.manascore.api.client.gui.widget.ImagePredicateButton;
//import com.github.manasmods.manascore.api.skills.ManasSkill;
//import com.github.manasmods.manascore.api.skills.SkillAPI;
//import com.github.manasmods.tensura.ability.SkillUtils;
//import com.github.manasmods.tensura.ability.skill.Skill;
//import com.github.manasmods.tensura.ability.skill.Skill.SkillType;
//import com.github.manasmods.tensura.client.TensuraGUIHelper;
//import com.github.mythos.mythos.menu.OrunMenu;
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.blaze3d.vertex.PoseStack;
//import io.github.Memoires.trmysticism.network.MysticismNetwork;
//import io.github.Memoires.trmysticism.network.play2server.RequestSkillTrueCopyPacket;
//import io.github.Memoires.trmysticism.util.Cached;
//import java.awt.Color;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import javax.annotation.Nullable;
//import net.minecraft.ChatFormatting;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.client.gui.components.EditBox;
//import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//import net.minecraft.client.renderer.GameRenderer;
//import net.minecraft.client.resources.sounds.SimpleSoundInstance;
//import net.minecraft.network.chat.Component;
//import net.minecraft.network.chat.MutableComponent;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.util.Mth;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//
//public class OrunScreen extends AbstractContainerScreen<OrunMenu> {
//    private static final ResourceLocation BACKGROUND = new ResourceLocation("tensura", "textures/gui/skill_creator/skill_creator.png");
//    private static final ResourceLocation SKILL_BAR = new ResourceLocation("tensura", "textures/gui/skill_button.png");
//    private static final ResourceLocation SCROLL_BAR = new ResourceLocation("tensura", "textures/gui/scroll_bar.png");
//    private static final ResourceLocation GAIN_BUTTON = new ResourceLocation("tensura", "textures/gui/skill_creator/gain_button.png");
//
//    private final Player player;
//  //  private final List<ManasSkill> skills;
//
//    private Cached<List<ManasSkill>, String> filteredSkills;
//    private ManasSkill selectedSkill = null;
//    private boolean scrolling;
//    private float scrollOffs;
//    private int startIndex;
//    private EditBox searchField;
//    private String lastSearchValue = "";
//
//
//    public OrunScreen(OrunMenu menu, Inventory playerInventory, Component title) {
//        super(menu, playerInventory, title.copy().withStyle(ChatFormatting.WHITE));
//        this.imageWidth = 233;
//        this.imageHeight = 140;
//        this.player = playerInventory.player;
////        this.skills = menu.getSkills().stream()
////                .map(skill -> (ManasSkill) SkillAPI.getSkillRegistry().getValue(skill))
////             .toList();
//    }
//
//    @Override
//    protected void init() {
//        super.init();
//        this.scrollOffs = 0.0F;
//        this.startIndex = 0;
//
//        this.searchField = new EditBox(this.font, this.getGuiLeft() + 19, this.getGuiTop() + 27, 79, 9, Component.empty());
//        this.searchField.setBordered(false);
//        this.searchField.setResponder(s -> {
//            if (!s.isEmpty()) {
//                this.scrollOffs = 0.0F;
//                this.startIndex = 0;
//            }
//        });
//        this.addRenderableWidget(this.searchField);
//
//        this.filteredSkills = new Cached<>(
//                () -> {
//               //     List<ManasSkill> filteredSkillList = new ArrayList<>(this.skills);
//                    if (!this.searchField.getValue().isEmpty()) {
//                        String filterValue = this.searchField.getValue().toLowerCase();
//                     //   filteredSkillList.removeIf(skill -> skill.getName() == null || !skill.getName().getString().toLowerCase().contains(filterValue));
//                    }
//                 //   return filteredSkillList;
//               // },
//                info -> {
//                    if (!lastSearchValue.equals(this.searchField.getValue())) {
//                        lastSearchValue = this.searchField.getValue();  // no shadowing
//                        info.needsUpdate = true;
//                    }
//                    return info;
//                }
//        );
//
//
//        int buttonX = this.getGuiLeft() + 162;
//        int buttonY = this.getGuiTop() + 116;
//
//        Button.OnPress pressAction = btn -> {
//            if (this.selectedSkill != null) {
//                MysticismNetwork.sendToServer(new RequestSkillTrueCopyPacket(SkillUtils.getSkillId(this.selectedSkill)));
//            }
//        };
//
//        Button.OnTooltip tooltipAction = (button, poseStack, x, y) ->
//                this.renderTooltip(poseStack, Component.translatable("trmythos.ski.orunmila.read_record_analysis"), x, y);
//
//     //   ImagePredicateButton gainSkillButton = new ImagePredicateButton(buttonX, buttonY, 20, 20, GAIN_BUTTON, pressAction, tooltipAction, this.menu::check);
//     //   this.addRenderableWidget(gainSkillButton);
//    }
//
//    @Override
//    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
//        this.renderBackground(poseStack);
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//        RenderSystem.setShaderTexture(0, BACKGROUND);
//
//        int x = (this.width - this.imageWidth) / 2;
//        int y = (this.height - this.imageHeight) / 2;
//        this.blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
//
//        // Mode text
//        Component modeText = Component.translatable("trmysticism.skill.mode.viciel.true_copy");
//        TensuraGUIHelper.renderScaledCenteredXText(this.font, poseStack, modeText, this.getGuiLeft() + 3, this.getGuiTop() + 7, 112, 11, Color.LIGHT_GRAY, false);
//
//        // Scrollbar
//        int k = (int)(78.0F * this.scrollOffs);
//        RenderSystem.setShaderTexture(0, SCROLL_BAR);
//        blit(poseStack, this.leftPos + 98, this.topPos + 43 + k, 0f, 13f, 10, 13, 10, 26);
//
//        List<ManasSkill> filtered = this.filteredSkills.getValue();
//        int lastVisible = Math.min(this.startIndex + 7, filtered.size());
//        this.renderButtons(poseStack, mouseX, mouseY, lastVisible, filtered);
//
//        // Selected skill preview
//        if (this.selectedSkill != null) {
//            ResourceLocation icon = this.selectedSkill.getSkillIcon();
//            if (icon != null) {
//                RenderSystem.setShaderTexture(0, icon);
//                blit(poseStack, this.getGuiLeft() + 162, this.getGuiTop() + 9, 0f, 0f, 20, 20, 20, 20);
//            }
//            FontRenderHelper.renderScaledTextInArea(poseStack, this.font, this.selectedSkill.getSkillDescription(), this.getGuiLeft() + 125f, this.getGuiTop() + 46f, 94f, 66f, Color.LIGHT_GRAY);
//        }
//    }
//
//    private void renderButtons(PoseStack poseStack, int mouseX, int mouseY, int lastVisibleIndex, List<ManasSkill> list) {
//        for (int i = this.startIndex; i < lastVisibleIndex && i < list.size(); i++) {
//            int x = this.getGuiLeft() + 6;
//            int y = this.getGuiTop() + 43 + (i - this.startIndex) * 13;
//            int offset = 0;
//            boolean hovering = mouseX >= x && mouseY >= y && mouseX < x + 89 && mouseY < y + 13;
//            if (hovering) offset = 13;
//
//            RenderSystem.setShaderTexture(0, SKILL_BAR);
//            blit(poseStack, x, y, 0f, offset, 89, 13, 89, 26);
//
//            ManasSkill skill = list.get(i);
//            SkillType type = skill instanceof Skill s ? s.getType() : SkillType.COMMON;
//            MutableComponent name = skillName(skill).withStyle(type.getChatFormatting());
//            TensuraGUIHelper.renderScaledShadowText(poseStack, this.font, TensuraGUIHelper.shortenTextComponent(name, 14), x + 5f, y + 3f, 85f, 13f, Color.WHITE.getRGB(), 2f, 0.01f);
//
//            if (hovering) this.renderTooltip(poseStack, name, mouseX, mouseY);
//        }
//    }
//
//    private MutableComponent skillName(@Nullable ManasSkill skill) {
//        return skill != null && skill.getName() != null ? skill.getName() : Component.translatable("tensura.race.selection.skills.empty");
//    }
//
//    private boolean isScrollBarActive() {
//        return this.filteredSkills.getValue().size() > 7;
//    }
//
//    private int getOffscreenRows() {
//        return this.filteredSkills.getValue().size() - 7;
//    }
//}
