package com.webb.easywiring.common.render;

import java.util.OptionalDouble;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class OutlineRenderType extends RenderType 
{

    public OutlineRenderType(String name, VertexFormat format, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable runnablePre, Runnable runnablePost) 
    {
        super(name, format, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, runnablePre, runnablePost);
    }

    private static final LineState THICK_LINES = new LineState(OptionalDouble.of(16.0D));

    public static final RenderType OVERLAY_LINES = create("overlay_lines",
            DefaultVertexFormats.POSITION_COLOR, 
            GL11.GL_LINES, 
            256,
            RenderType.State.builder().setLineState(THICK_LINES)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(NO_TEXTURE)
                    .setWriteMaskState(COLOR_WRITE).createCompositeState(false));
    
//    public static final RenderType LINES = create("lines", 
//    		DefaultVertexFormats.POSITION_COLOR, 
//    		1, 
//    		256, 
//    		RenderType.State.builder().setLineState(THICK_LINES)
//	    		.setLayeringState(VIEW_OFFSET_Z_LAYERING)
//	    		.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
//	    		.setOutputState(ITEM_ENTITY_TARGET)
//	    		.setWriteMaskState(COLOR_DEPTH_WRITE)
//	    		.createCompositeState(false));
    
}
