package com.github.mythos.mythos.registry;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.function.Supplier;

public class MythosRenderTypes extends RenderType {
    public MythosRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType mythosDome(Supplier<ShaderInstance> shader) {
        return RenderType.create("mythos_dome",
                DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.TRIANGLES, 256, false, true,
                RenderType.CompositeState.builder()
                        .setShaderState(new ShaderStateShard(shader))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .createCompositeState(false));
    }
}
