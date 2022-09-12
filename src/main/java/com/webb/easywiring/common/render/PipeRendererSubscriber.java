package com.webb.easywiring.common.render;

import com.mojang.math.Vector3f;
import com.webb.easywiring.common.util.Node;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.webb.easywiring.EasyWiring;
import com.webb.easywiring.common.items.PipePlacer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.core.BlockPos;
import com.mojang.math.Matrix4f;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.client.renderer.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static net.minecraft.client.gui.GuiComponent.drawCenteredString;
import static net.minecraftforge.client.event.RenderLevelStageEvent.Stage;


@EventBusSubscriber(modid = EasyWiring.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PipeRendererSubscriber
{
    private static final int LIGHT_FULLBRIGHT = 0xF000F0;
    private static final int TEXT_COLOR = FastColor.ARGB32.color(0, 0, 0, 255);

// here for ease of access - /fill ~-15 ~-10 ~-15 ~15 ~0 ~15 minecraft:air

    @SubscribeEvent
    public static void renderMachines(RenderLevelStageEvent event)
    {
        LocalPlayer player = Minecraft.getInstance().player;

        if (!player.getMainHandItem().getItem().getRegistryName().toString().equals("easywiring:pipe_placer") ||
            event.getStage() != Stage.AFTER_TRIPWIRE_BLOCKS)
        {
            return;
        }

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        PoseStack stack = event.getPoseStack();

        RenderSystem.disableDepthTest();
        stack.pushPose();

        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        stack.translate(-cam.x(), -cam.y(), -cam.z());

        ArrayList<Node> nodesSortedByDistance = sortBlocksByDistanceToPlayer(player, PipePlacer.currentPath.getNodes());

        float LINE_SPACEING = 0.3F;
        for (Node node : nodesSortedByDistance)
        {
            renderBlockOutline(buffer, stack,
                    node.blockPos, 200, 200, 200, 1);

            boolean isCloseEnough = nodesSortedByDistance.indexOf(node) >= nodesSortedByDistance.size() - 12;
            if (isCloseEnough) //player.isCrouching() &&
            {
                renderNodesDepth(buffer, stack,
                        node, 0.05f, 40, 230, 20, 1);

                if (node.directionToAir != null)
                {
                    stack.pushPose();

                    BlockPos closestAirBlockPosition = node.blockPos.relative(node.directionToAir, node.distanceToAir);

                    moveToBlockFace(stack, closestAirBlockPosition, node.directionToAir);
                    stack.translate(-0.43, -0.49, -0.5 + LINE_SPACEING);
                    stack.scale(0.01f, 0.01f, 0.01f);
                    stack.mulPose(Vector3f.XP.rotationDegrees(90));

                    for (int i = 0; i < node.debugInformation.size(); i++)
                    {
                        String debugMessage = node.debugInformation.get(i);
                        System.out.println(debugMessage);
                        stack.translate(0, 6*i, 0);
                        Minecraft.getInstance().font.draw(stack, debugMessage, 0, 0, TEXT_COLOR);
                    }

                    stack.popPose();
                }



            }
        }


        for (BlockPos machine : PipePlacer.machines)
        {
            renderBlockOutline(buffer, stack,
                    machine, 240, 20, 20, 1);

            stack.pushPose();
            moveToBlockFace(stack, machine, Direction.UP);
            stack.translate(-0.43, 0.51, -0.5 + LINE_SPACEING);
            stack.scale(0.01f, 0.01f, 0.01f);
            stack.mulPose(Vector3f.XP.rotationDegrees(90));

            // line 2
            stack.pushPose();
            stack.translate(0, 0, LINE_SPACEING*3);
            Minecraft.getInstance().font.draw(stack, "A machine that", 0, 0, TEXT_COLOR);
            stack.popPose();

            // line 3
            stack.pushPose();
            stack.translate(0, 0, LINE_SPACEING*6);
            Minecraft.getInstance().font.draw(stack, "needs power?", 0, 9, TEXT_COLOR);
            stack.popPose();

            stack.popPose();
        }

        stack.popPose();
        RenderSystem.enableDepthTest();
    }

    private static ArrayList<Node> sortBlocksByDistanceToPlayer(LocalPlayer player, ArrayList<Node> unsortedList)
    {
        ArrayList<Node> sortedList = new ArrayList<Node>();
        sortedList.addAll(unsortedList);
        Vec3 playerPos = player.position();
        Vec3i intPlayerPos = new Vec3i(playerPos.x, playerPos.y+1, playerPos.z);

        Collections.sort(sortedList, new Comparator<Node>() {
            @Override
            public int compare(Node first, Node second)
            {
                double firstDistance = first.blockPos.distSqr(intPlayerPos);
                double secondDistance = second.blockPos.distSqr(intPlayerPos);

                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return firstDistance > secondDistance ? -1 : (firstDistance < secondDistance) ? 1 : 0;
            }
        });

        return sortedList;
    }


    private static void renderNodesDepth(MultiBufferSource.BufferSource buffer, PoseStack mstack, Node node, float lineWidth, int r, int g, int b, int a)
    {
        ArrayList<BlockPos> blocksToSurface = new ArrayList<BlockPos>();
        Level world = Minecraft.getInstance().level;

        if (node.distanceToAir > 0 && node.directionToAir != null)
        {
            mstack.pushPose();

            float colorDarkenPercent = 0.1F;

            for (int i = 1; i < node.distanceToAir; i++)
            {
                BlockPos currentBlock = node.blockPos.relative(node.directionToAir, i);
                float crosslineWidth = 0.25f;

                if (i == 1)
                {
                    crosslineWidth = 0.85f;
                }

                int red = (int) Math.floor(r*(1-colorDarkenPercent*i));
                int green = (int) Math.floor(g*(1-colorDarkenPercent*i));
                int blue = (int) Math.floor(b*(1-colorDarkenPercent*i));

                renderDepthLine(buffer, mstack,
                        currentBlock, node.directionToAir, lineWidth, crosslineWidth, red, green, blue, a);

                mstack.pushPose();
                mstack.mulPose(Vector3f.YP.rotationDegrees(90));
                renderDepthLine(buffer, mstack,
                        currentBlock, node.directionToAir, lineWidth, crosslineWidth, red, green, blue, a);
                mstack.popPose();


            }
            mstack.popPose();
        }
    }

    private static void renderDepthLine(MultiBufferSource.BufferSource buffer, PoseStack stack, BlockPos block, Direction direction, float lineWidth, float crosslineWidth, int r, int g, int b, int a)
    {
        VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.overlayLines());

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        stack.pushPose();

        moveToBlockFace(stack, block, direction);

        Matrix4f matrix = stack.last().pose();

        builder.vertex(matrix, 0, -0.5F, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 0.5F, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        // top cross
        float crosslineHalfWidth = crosslineWidth/2;
        builder.vertex(matrix, -crosslineHalfWidth, 0.5F, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, crosslineHalfWidth, 0.5F, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        // bottom cross
        builder.vertex(matrix, -crosslineHalfWidth, -0.5F, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, crosslineHalfWidth, -0.5F, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        stack.popPose();

        buffer.endBatch(CustomRenderTypes.overlayLines());
    }

    private static void moveToBlockFace(PoseStack stack, BlockPos blockPos, Direction direction)
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

    private static void renderBlockOutline(MultiBufferSource.BufferSource buffer, PoseStack mstack, BlockPos block, int r, int g, int b, int a)
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
        builder.vertex(matrix, 1, 1, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 1, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 1, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 1, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        //Draw bottom face of block
        builder.vertex(matrix, 1, 0, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 0, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 0, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 0, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        //Draw s1 face of block
        builder.vertex(matrix, 1, 1, 0).color(r-10, g-10, b-10, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 0, 0).color(r-10, g-10, b-10, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 0, 1).color(r-10, g-10, b-10, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 1, 1).color(r-10, g-10, b-10, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        //Draw -s1 face of block
        builder.vertex(matrix, 0, 1, 0).color(r-10, g-10, b-10, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 0, 0).color(r-10, g-10, b-10, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 0, 1).color(r-10, g-10, b-10, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 1, 1).color(r-10, g-10, b-10, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        //Draw s2 face of block
        builder.vertex(matrix, 0, 1, 1).color(r+10, g+10, b+10, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 0, 1).color(r+10, g+10, b+10, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 0, 1).color(r+10, g+10, b+10, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 1, 1).color(r+10, g+10, b+10, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        //Draw -s2 face of block
        builder.vertex(matrix, 0, 1, 0).color(r+10, g+10, b+10, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 0, 0).color(r+10, g+10, b+10, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 0, 0).color(r+10, g+10, b+10, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 1, 0).color(r+10, g+10, b+10, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        mstack.popPose();

        buffer.endBatch(CustomRenderTypes.overlayQuads());
    }

}
