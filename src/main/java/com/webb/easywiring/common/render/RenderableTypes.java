package com.webb.easywiring.common.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;

public class RenderableTypes
{
    private static final int LIGHT_FULLBRIGHT = 0xF000F0;
    private static final int TEXT_COLOR = FastColor.ARGB32.color(0, 0, 0, 255);

    public class blockRenderType extends IOverlayRenderable
    {
        BlockPos block;
        float scale;
        int r, g, b;

        public blockRenderType(MultiBufferSource.BufferSource buffer, PoseStack mstack, Vector3f position,
                               float scale, int r, int g, int b)
        {
            super(position);
            scale = scale;
            r = r;
            g = g;
            b = b;
        }

        @Override
        public void render(MultiBufferSource.BufferSource buffer, PoseStack mstack)
        {
            VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.overlayQuads());

            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();

            mstack.pushPose();
            mstack.translate(x, y, z);

            Matrix4f matrix = mstack.last().pose();

            float scale = 0.9f;
            float offsetAmount = (1 - scale)/2;

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
            builder.vertex(matrix, 1, 1, 0)
                    .color(r, g, b, 1)
                    .uv2(LIGHT_FULLBRIGHT)
                    .endVertex();
        }
    }
}
