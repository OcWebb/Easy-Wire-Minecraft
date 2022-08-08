package com.webb.easywiring.common.render;

import com.mojang.math.Matrix3f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
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

import static net.minecraftforge.client.event.RenderLevelStageEvent.Stage;


@EventBusSubscriber(modid = EasyWiring.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PipeRendererSubscriber
{
    private static final int LIGHT_FULLBRIGHT = 0xF000F0;

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

        for (BlockPos node : PipePlacer.currentPath)
        {
            renderBlockOutline(buffer, stack,
                    node,
                    1, 195, 195, 195, 1);
        }

        for (BlockPos machine : PipePlacer.machines)
        {
            renderBlockOutline(buffer, stack,
                    machine,
                    1, 240, 20, 20, 1);
        }

        stack.popPose();
        RenderSystem.enableDepthTest();
    }

    private static void renderBlockOutline(MultiBufferSource.BufferSource buffer, PoseStack mstack, BlockPos block, float lineWidth, int r, int g, int b, int a)
    {
        VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.overlay());

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        mstack.pushPose();
        mstack.translate(x, y, z);

        Matrix4f matrix = mstack.last().pose();

        float scale = 0.75f;
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
        builder.vertex(matrix, 1, 1, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 0, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 0, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 1, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        //Draw -s1 face of block
        builder.vertex(matrix, 0, 1, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 0, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 0, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 1, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        //Draw s2 face of block
        builder.vertex(matrix, 0, 1, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 0, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 0, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 1, 1).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        //Draw -s2 face of block
        builder.vertex(matrix, 0, 1, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 0, 0, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 0, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();
        builder.vertex(matrix, 1, 1, 0).color(r, g, b, a).uv2(LIGHT_FULLBRIGHT).endVertex();

        mstack.popPose();

        buffer.endBatch(CustomRenderTypes.overlay());
    }

}
