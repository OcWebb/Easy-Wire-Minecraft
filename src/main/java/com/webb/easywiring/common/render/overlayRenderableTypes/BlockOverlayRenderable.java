package com.webb.easywiring.common.render.overlayRenderableTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.webb.easywiring.common.render.CustomRenderTypes;
import com.webb.easywiring.common.render.IOverlayRenderable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;

public class BlockOverlayRenderable extends IOverlayRenderable
{
    private final int LIGHT_FULLBRIGHT = 0xF000F0;
    BlockPos _block;
    float _scale;
    int _r, _g, _b;

    public BlockOverlayRenderable(BlockPos block, float scale, int r, int g, int b)
    {
        super(new Vector3f(block.getX(), block.getY(), block.getZ()));
        _block = block;
        _scale = scale;
        _r = r;
        _g = g;
        _b = b;
    }

    @Override
    public void render(MultiBufferSource.BufferSource buffer, PoseStack mstack)
    {

        super.render(buffer, mstack);

        int x = _block.getX();
        int y = _block.getY();
        int z = _block.getZ();

        mstack.pushPose();
        mstack.translate(x, y, z);

        Matrix4f matrix = mstack.last().pose();
        float scale = 0.9f;
        float offsetAmount = (1 - scale)/2;
        int contrast = 20;
        VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.overlayQuads());

        mstack.translate(offsetAmount, offsetAmount, offsetAmount);
        mstack.scale(scale, scale, scale);

        //Draw top face of block
        addVertex(builder, matrix, 1, 1, 0);
        addVertex(builder, matrix, 0, 1, 0);
        addVertex(builder, matrix, 0, 1, 1);
        addVertex(builder, matrix, 1, 1, 1);

        //Draw bottom face of block
        addVertex(builder, matrix, 1, 0, 0);
        addVertex(builder, matrix, 0, 0, 0);
        addVertex(builder, matrix, 0, 0, 1);
        addVertex(builder, matrix, 1, 0, 1);

        setColor(_r-contrast, _g-contrast, _b-contrast);
        //Draw s1 face of block
        addVertex(builder, matrix, 1, 1, 0);
        addVertex(builder, matrix, 1, 0, 0);
        addVertex(builder, matrix, 1, 0, 1);
        addVertex(builder, matrix, 1, 1, 1);

        //Draw -s1 face of block
        addVertex(builder, matrix, 0, 1, 0);
        addVertex(builder, matrix, 0, 0, 0);
        addVertex(builder, matrix, 0, 0, 1);
        addVertex(builder, matrix, 0, 1, 1);

        setColor(_r+contrast, _g+contrast, _b+contrast);
        //Draw s2 face of block
        addVertex(builder, matrix, 0, 1, 1);
        addVertex(builder, matrix, 0, 0, 1);
        addVertex(builder, matrix, 1, 0, 1);
        addVertex(builder, matrix, 1, 1, 1);

        //Draw -s2 face of block
        addVertex(builder, matrix, 0, 1, 0);
        addVertex(builder, matrix, 0, 0, 0);
        addVertex(builder, matrix, 1, 0, 0);
        addVertex(builder, matrix, 1, 1, 0);

        mstack.popPose();
        buffer.endBatch(CustomRenderTypes.overlayQuads());
    }


    public void addVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float z)
    {
        builder.vertex(matrix, x, y, z)
                .color(_r, _g, _b, 1)
                .uv2(LIGHT_FULLBRIGHT)
                .endVertex();
    }

    public void setColor(int r, int g, int b)
    {
        _r = r;
        _g = g;
        _b = b;
    }

}

