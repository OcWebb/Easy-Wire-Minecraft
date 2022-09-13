package com.webb.easywiring.common.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class RenderUtils
{
    public static void moveToBlockFace(PoseStack stack, BlockPos blockPos, Direction direction)
    {
        stack.translate(blockPos.getX()+0.5, blockPos.getY()+0.5, blockPos.getZ()+0.5);
        switch (direction)
        {
            case UP:
                break;

            case DOWN:
                stack.mulPose(Vector3f.XP.rotationDegrees(-180));
                break;

            case NORTH:
                stack.mulPose(Vector3f.XP.rotationDegrees(-90));
            case SOUTH:
                stack.mulPose(Vector3f.XP.rotationDegrees(90));
                break;

            case EAST:
                stack.mulPose(Vector3f.ZP.rotationDegrees(-90));
            case WEST:
                stack.mulPose(Vector3f.ZP.rotationDegrees(90));
                stack.mulPose(Vector3f.YP.rotationDegrees(-90));
                break;
        }
    }
}
