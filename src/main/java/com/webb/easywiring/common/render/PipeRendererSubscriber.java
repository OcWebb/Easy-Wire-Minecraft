package com.webb.easywiring.common.render;

import com.mojang.math.Vector3f;
import com.webb.easywiring.common.util.Node;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.Vec3i;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

        for (Node node : nodesSortedByDistance)
        {
            renderBlockOutline(buffer, stack,
                    node.blockPos, 200, 200, 200, 1);

            boolean isCloseEnough = nodesSortedByDistance.indexOf(node) >= nodesSortedByDistance.size() - 4;
            if (player.isCrouching() && isCloseEnough || true)
            {
                renderNodeDepthLine(buffer, stack,
                        node, 0.02f, 40, 230, 20, 1);

                stack.pushPose();
                BlockPos closestAirBlockPosition = node.blockPos.relative(node.directionToAir, node.distanceToAir);


                stack.scale(0.04f, 0.04f, 0.04f);
                switch (node.directionToAir)
                {
                    case UP:
                        stack.translate(closestAirBlockPosition.getX()+0.5, closestAirBlockPosition.getY()+1.02, closestAirBlockPosition.getZ()+0.5);
                        stack.mulPose(Vector3f.XP.rotationDegrees(90));
                        break;
                    case DOWN:
                        stack.translate(closestAirBlockPosition.getX()+0.5, closestAirBlockPosition.getY()-0.02, closestAirBlockPosition.getZ()+0.5);
                        stack.mulPose(Vector3f.XP.rotationDegrees(-90));
                        break;
                    case NORTH:
                        stack.translate(closestAirBlockPosition.getX()+0.5, closestAirBlockPosition.getY()+0.5, closestAirBlockPosition.getZ()-0.02);
                        break;
                        //stack.mulPose(Vector3f.XP.rotationDegrees(-90));
                    case SOUTH:
                        stack.translate(closestAirBlockPosition.getX()+0.5, closestAirBlockPosition.getY()+0.5, closestAirBlockPosition.getZ()-1.02);
                        stack.mulPose(Vector3f.XP.rotationDegrees(-180));
                        break;
                }
                System.out.println(node.directionToAir.toString() + ", " + nodesSortedByDistance.indexOf(node));

                RenderSystem.disableDepthTest();
                Minecraft.getInstance().font.draw(stack, node.directionToAir.toString(), 0, 0, TEXT_COLOR);

                stack.popPose();
            }
        }

        for (BlockPos machine : PipePlacer.machines)
        {
            renderBlockOutline(buffer, stack,
                    machine, 240, 20, 20, 1);
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


    private static void renderNodeDepthLine(MultiBufferSource.BufferSource buffer, PoseStack mstack, Node node, float lineWidth, int r, int g, int b, int a)
    {
        ArrayList<BlockPos> blocksToSurface = new ArrayList<BlockPos>();
        Level world = Minecraft.getInstance().level;

        if (node.distanceToAir > 0)
        {
            for (int i = 1; i < node.distanceToAir; i++)
            {
                BlockPos ithBlockAbove = node.blockPos.relative(node.directionToAir, i);

                float crosslineWidth = 0.25f;

                if (i == node.distanceToAir-1)
                {
                    crosslineWidth = 0.85f;
                }

                renderDepthLine(buffer, mstack,
                        ithBlockAbove, lineWidth, crosslineWidth, r, g, b, a);
            }
        }
    }

    private static void renderDepthLine(MultiBufferSource.BufferSource buffer, PoseStack mstack, BlockPos block, float lineWidth, float crosslineWidth, int r, int g, int b, int a)
    {
        VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.overlayLines());

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        mstack.pushPose();

        mstack.translate(x+0.5, y, z+0.5);
        Matrix4f matrix = mstack.last().pose();

        builder.vertex(matrix, 0, 0, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 1, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        // top cross
        float crosslineHalfWidth = crosslineWidth/2;
        builder.vertex(matrix, -crosslineHalfWidth, 1, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, crosslineHalfWidth, 1, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        builder.vertex(matrix, -crosslineHalfWidth, 0, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, crosslineHalfWidth, 0, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        mstack.popPose();

        buffer.endBatch(CustomRenderTypes.overlayLines());
    }

    private static void renderText(MultiBufferSource.BufferSource buffer, PoseStack mstack, BlockPos block, String text, int r, int g, int b, int a)
    {
//        MultiBufferSource.BufferSource irendertypebuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
//
//        int i = Font.drawInBatch(p_228078_1_, p_228078_2_, p_228078_3_, p_228078_4_, p_228078_6_, p_228078_5_,
//                irendertypebuffer, false, 0, LightTexture.FULL_BRIGHT);
//        irendertypebuffer.endBatch();
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
