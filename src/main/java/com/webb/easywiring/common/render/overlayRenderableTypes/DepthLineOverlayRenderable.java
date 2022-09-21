package com.webb.easywiring.common.render.overlayRenderableTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.webb.easywiring.common.render.CustomRenderTypes;
import com.webb.easywiring.common.render.IOverlayRenderable;
import com.webb.easywiring.common.render.RenderUtils;
import com.webb.easywiring.common.util.Node;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class DepthLineOverlayRenderable extends IOverlayRenderable
{
    private static final int LIGHT_FULLBRIGHT = 0xF000F0;
    Node _node;
    float _crossWidth;
    int _r, _g, _b;

    public DepthLineOverlayRenderable(Node node, float crossWidth, int r, int g, int b)
    {
        super(new Vector3f(node.blockPos.getX(), node.blockPos.getY(), node.blockPos.getZ()));
        _node = node;
        _r = r;
        _g = g;
        _b = b;
        _crossWidth = crossWidth;
    }

    @Override
    public void render(MultiBufferSource.BufferSource buffer, PoseStack mstack)
    {

        super.render(buffer, mstack);
        ArrayList<BlockPos> blocksToSurface = new ArrayList<BlockPos>();
        Level world = Minecraft.getInstance().level;
        float lineWidth = 0.05f;

        if (_node.distanceToAir > 0 && _node.directionToAir != null)
        {
            mstack.pushPose();

            float colorDarkenPercent = 0.1F;

            for (int i = 1; i < _node.distanceToAir; i++)
            {
                BlockPos currentBlock = _node.blockPos.relative(_node.directionToAir, i);
                float crosslineWidth = 0.25f;

                if (i == 1)
                {
                    crosslineWidth = 0.85f;
                }

                int red = (int) Math.floor(_r*(1-colorDarkenPercent*i));
                int green = (int) Math.floor(_g*(1-colorDarkenPercent*i));
                int blue = (int) Math.floor(_b*(1-colorDarkenPercent*i));

                renderDepthLine(buffer, mstack,
                        currentBlock, _node.directionToAir, lineWidth, crosslineWidth, red, green, blue);

                mstack.pushPose();
//                mstack.mulPose(Vector3f.YP.rotationDegrees(90));
                renderDepthLine(buffer, mstack,
                        currentBlock, _node.directionToAir, lineWidth, crosslineWidth, red, green, blue);
                mstack.popPose();


            }
            mstack.popPose();
        }
    }

    private static void renderDepthLine(MultiBufferSource.BufferSource buffer, PoseStack stack, BlockPos block, Direction direction, float lineWidth, float crosslineWidth, int r, int g, int b)
    {
        VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.overlayLines());

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        stack.pushPose();

        RenderUtils.moveToBlockFace(stack, block, direction);

        Matrix4f matrix = stack.last().pose();

        builder.vertex(matrix, 0, -0.5F, 0).color(r, g, b, 1).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 0.5F, 0).color(r, g, b, 1).uv2(LIGHT_FULLBRIGHT).endVertex();

        // top cross
        float crosslineHalfWidth = crosslineWidth/2;
        builder.vertex(matrix, -crosslineHalfWidth, 0.5F, 0).color(r, g, b, 1).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, crosslineHalfWidth, 0.5F, 0).color(r, g, b, 1).uv2(LIGHT_FULLBRIGHT).endVertex();

        // bottom cross
//        builder.vertex(matrix, -crosslineHalfWidth, -0.5F, 0).color(r, g, b, 1).uv2(LIGHT_FULLBRIGHT).endVertex();
//        builder.vertex(matrix, crosslineHalfWidth, -0.5F, 0).color(r, g, b, 1).uv2(LIGHT_FULLBRIGHT).endVertex();

        stack.popPose();

        buffer.endBatch(CustomRenderTypes.overlayLines());
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

