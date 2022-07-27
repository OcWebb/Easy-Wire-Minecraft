package com.webb.easywiring.common.render;

import java.util.OptionalDouble;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class CustomRenderTypes extends RenderType 
{

    public CustomRenderTypes(String name, VertexFormat format, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable runnablePre, Runnable runnablePost) 
    {
        super(name, format, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, runnablePre, runnablePost);
    }

    private static final LineState THICK_LINES = new LineState(OptionalDouble.of(4.0D));

    public static final RenderType OVERLAY_LINES = create("overlay_lines",
            DefaultVertexFormats.POSITION_COLOR, 
            GL11.GL_LINES, 
            256,
            RenderType.State.builder().setLineState(THICK_LINES)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(NO_TEXTURE)
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false));
    
	private static final RenderType OUTLINE_SOLID =
			RenderType.create("outline_solid", DefaultVertexFormats.NEW_ENTITY, GL11.GL_QUADS, 256, false,
				false, RenderType.State.builder()
					.setTextureState(new TextureState(new ResourceLocation("forge:textures/white.png"), false, false))
					.setCullState(CULL)
					.setLightmapState(LIGHTMAP)
					.setOverlayState(OVERLAY)
					.createCompositeState(false));

	public static RenderType getOutlineSolid() {
		return OUTLINE_SOLID;
	}
    
}
