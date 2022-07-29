package com.webb.easywiring.common.render;

import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.webb.easywiring.EasyWiring;
import com.webb.easywiring.common.items.PipePlacer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import com.mojang.math.Matrix4f;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.client.renderer.*;
import net.minecraft.util.Mth;


@EventBusSubscriber(modid = EasyWiring.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PipeRendererSubscriber
{
    private static final float LINE_WIDTH = .08F;
    private static final int LINE_RED = 99;
    private static final int LINE_GREEN = 32;
    private static final int LINE_BLUE = 32;
    private static final int LINE_ALPHA = 255;
    private static final int LIGHT_FULLBRIGHT = 0xF000F0;
// /fill ~-15 ~-10 ~-15 ~15 ~0 ~15 minecraft:air
    @SubscribeEvent
    public static void renderMachines(RenderLevelStageEvent event)
    {
        LocalPlayer player = Minecraft.getInstance().player;

        if (!player.getMainHandItem().getItem().getRegistryName().toString().equals("easywiring:pipe_placer"))
        {
            return;
        }

        BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        PoseStack stack = event.getPoseStack();
        stack.pushPose();

        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        stack.translate(-cam.x, -cam.y, -cam.z);

        for (BlockPos machine : PipePlacer.machines)
        {
            drawLineBetween(buffer, stack,
                    new Vec3(machine.getX(), machine.getY() + 2, machine.getZ()),
                    new Vec3(machine.getX(), machine.getY(), machine.getZ()),
                    LINE_WIDTH, LINE_RED, LINE_GREEN, LINE_BLUE, LINE_ALPHA);
        }

        stack.popPose();
    }

    private static void drawLineBetween(MultiBufferSource buffer, PoseStack mstack, Vec3 local, Vec3 target, float lineWidth, int r, int g, int b, int a)
    {
        VertexConsumer builder = buffer.getBuffer(RenderType.leash());

        //Calculate yaw
        float rotY = (float) Mth.atan2(target.x - local.x, target.z - local.z);

        //Calculate pitch
        float distX = (float)(target.x - local.x);
        float distZ = (float)(target.z - local.z);
        float rotX = (float) Mth.atan2(target.y - local.y, Mth.sqrt(distX * distX + distZ * distZ));

        mstack.pushPose();

        //Translate to start point
        mstack.translate(local.x, local.y, local.z);
        //Rotate to point towards end point
        mstack.mulPose(Vector3f.YP.rotation(rotY));
        mstack.mulPose(Vector3f.XN.rotation(rotX));

        //Calculate distance between points -> length of the line
        float distance = (float) local.distanceTo(target);

        Matrix4f matrix = mstack.last().pose();
        float halfWidth = lineWidth / 2F;

        //Draw horizontal quad
        builder.vertex(matrix, -halfWidth, 0,        0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix,  halfWidth, 0,        0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix,  halfWidth, 0, distance).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, -halfWidth, 0, distance).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        //Draw vertical Quad
        builder.vertex(matrix, 0, -halfWidth,        0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0,  halfWidth,        0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0,  halfWidth, distance).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, -halfWidth, distance).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        mstack.popPose();
    }

    private static void renderBlockOutline(MultiBufferSource buffer, PoseStack mstack, Vec3 local, Vec3 target, float lineWidth, int r, int g, int b, int a)
    {
        VertexConsumer builder = buffer.getBuffer(RenderType.leash());

        //Calculate yaw
        float rotY = (float) Mth.atan2(target.x - local.x, target.z - local.z);

        //Calculate pitch
        float distX = (float)(target.x - local.x);
        float distZ = (float)(target.z - local.z);
        float rotX = (float) Mth.atan2(target.y - local.y, Mth.sqrt(distX * distX + distZ * distZ));

        mstack.pushPose();

        //Translate to start point
        mstack.translate(local.x, local.y, local.z);
        //Rotate to point towards end point
        mstack.mulPose(Vector3f.YP.rotation(rotY));
        mstack.mulPose(Vector3f.XN.rotation(rotX));

        //Calculate distance between points -> length of the line
        float distance = (float) local.distanceTo(target);

        Matrix4f matrix = mstack.last().pose();
        float halfWidth = lineWidth / 2F;

        //Draw horizontal quad
        builder.vertex(matrix, -halfWidth, 0,        0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix,  halfWidth, 0,        0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix,  halfWidth, 0, distance).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, -halfWidth, 0, distance).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        //Draw vertical Quad
        builder.vertex(matrix, 0, -halfWidth,        0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0,  halfWidth,        0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0,  halfWidth, distance).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, -halfWidth, distance).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        mstack.popPose();
    }


}
