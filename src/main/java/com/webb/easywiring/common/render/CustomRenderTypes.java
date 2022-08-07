package com.webb.easywiring.common.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;

public class CustomRenderTypes extends RenderType
{
	public CustomRenderTypes(String name, VertexFormat format, VertexFormat.Mode p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable runnablePre, Runnable runnablePost)
	{
		super(name, format, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, runnablePre, runnablePost);
	}

	private static final RenderType OVERLAY = create(
			"overlay",
			DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
			VertexFormat.Mode.QUADS,
			2097152,
			true,
			true,
			overlayState());

	private static RenderType.CompositeState overlayState()
	{

//		return RenderType.CompositeState.builder()
//				.setLightmapState(LIGHTMAP)
//				.setShaderState(RENDERTYPE_TRANSLUCENT_SHADER)
//				.setTextureState(BLOCK_SHEET_MIPPED)
//				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
//				.setOutputState(TRANSLUCENT_TARGET)
//				.setDepthTestState(NO_DEPTH_TEST)
//				.setCullState(NO_CULL)
//				.createCompositeState(true);

		return RenderType.CompositeState.builder()
				.setLightmapState(LIGHTMAP)
				.setShaderState(RenderStateShard.POSITION_COLOR_LIGHTMAP_SHADER)
				.setTextureState(NO_TEXTURE)
				.setDepthTestState(NO_DEPTH_TEST)
				.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
				.setCullState(NO_CULL)
				.createCompositeState(true);
	}

	public static RenderType overlay()
	{
		return OVERLAY;
	}

}
