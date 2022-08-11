package com.webb.easywiring.common.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public class CustomRenderTypes extends RenderType
{
	public CustomRenderTypes(String name, VertexFormat format, VertexFormat.Mode p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable runnablePre, Runnable runnablePost)
	{
		super(name, format, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, runnablePre, runnablePost);
	}

	private static final RenderType OVERLAY_QUADS = create(
			"overlay_quads",
			DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
			VertexFormat.Mode.QUADS,
			2097152,
			true,
			true,
			overlayQuadState());

	private static final RenderType OVERLAY_LINES = create(
			"overlay_lines",
			DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
			VertexFormat.Mode.LINES,
			256,
			false,
			false,
			overlayLinesState());

	private static RenderType.CompositeState overlayQuadState()
	{
		return RenderType.CompositeState.builder()
				.setLightmapState(LIGHTMAP)
				.setShaderState(POSITION_COLOR_LIGHTMAP_SHADER)
				.setTextureState(NO_TEXTURE)
				.setDepthTestState(NO_DEPTH_TEST)
				.setCullState(NO_CULL)
				.createCompositeState(true);
	}

	private static RenderType.CompositeState overlayLinesState()
	{
		return RenderType.CompositeState.builder()
				.setLightmapState(LIGHTMAP)
				.setShaderState(RENDERTYPE_LINES_SHADER)
				.setLineState(new LineStateShard(OptionalDouble.of(4D)))
				.setTextureState(NO_TEXTURE)
				.setCullState(NO_CULL)
				.setDepthTestState(NO_DEPTH_TEST)
				.createCompositeState(true);
	}

	public static RenderType overlayQuads()
	{
		return OVERLAY_QUADS;
	}

	public static RenderType overlayLines()
	{
		return OVERLAY_LINES;
	}
}
