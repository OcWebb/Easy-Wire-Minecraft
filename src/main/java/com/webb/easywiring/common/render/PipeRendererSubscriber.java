package com.webb.easywiring.common.render;

import com.mojang.math.Vector3f;
import com.webb.easywiring.common.render.overlayRenderableTypes.BlockOverlayRenderable;
import com.webb.easywiring.common.render.overlayRenderableTypes.DepthLineOverlayRenderable;
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
    private static final int TEXT_COLOR = FastColor.ARGB32.color(0, 0, 0, 255);

// here for ease of access - /fill ~-15 ~-10 ~-15 ~15 ~0 ~15 minecraft:air

    @SubscribeEvent
    public static void renderPipePlacerPath(RenderLevelStageEvent event)
    {
        if (event.getStage() != Stage.AFTER_TRIPWIRE_BLOCKS)
        {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (!player.getMainHandItem().getItem().getRegistryName().toString().equals("easywiring:pipe_placer"))
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
        for (Node node : PipePlacer.currentPath.getNodes())
        {
            OverlayRenderer.add(new BlockOverlayRenderable(node.blockPos, 0.75f, 200, 200, 200));

            boolean isCloseEnough = nodesSortedByDistance.indexOf(node) >= nodesSortedByDistance.size() - 12;
            if (isCloseEnough) //player.isCrouching() &&
            {
                OverlayRenderer.add(
                        new DepthLineOverlayRenderable(node, 0.75f, 40, 230, 20)
                );

                if (node.directionToAir != null)
                {
                    stack.pushPose();

                    BlockPos closestAirBlockPosition = node.blockPos.relative(node.directionToAir, node.distanceToAir);

                    RenderUtils.moveToBlockFace(stack, closestAirBlockPosition, node.directionToAir);
                    stack.translate(-0.43, -0.49, -0.5 + LINE_SPACEING);
                    stack.scale(0.01f, 0.01f, 0.01f);
                    stack.mulPose(Vector3f.XP.rotationDegrees(90));

                    for (int i = 0; i < node.debugInformation.size(); i++)
                    {
                        String debugMessage = node.debugInformation.get(i);
                        stack.translate(0, 6*i, 0);
                        Minecraft.getInstance().font.draw(stack, debugMessage, 0, 0, TEXT_COLOR);
                    }

                    stack.popPose();
                }
            }
        }


        for (BlockPos machine : PipePlacer.machines)
        {
            OverlayRenderer.add(new BlockOverlayRenderable(machine, 0.9f, 240, 20, 20) );
        }

        OverlayRenderer.renderAll(buffer, stack);

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

}
