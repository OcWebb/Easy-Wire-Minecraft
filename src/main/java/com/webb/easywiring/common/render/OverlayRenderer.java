package com.webb.easywiring.common.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.PriorityQueue;

public class OverlayRenderer
{
    private static PriorityQueue<IOverlayRenderable> renderQueue = new PriorityQueue<IOverlayRenderable>();

    public static void add(IOverlayRenderable renderable)
    {
        renderQueue.add(renderable);
    }

    public static void renderAll(MultiBufferSource.BufferSource buffer, PoseStack mstack)
    {
        if (renderQueue.isEmpty()) { return; }

        RenderSystem.disableDepthTest();
        IOverlayRenderable currentOverlay = renderQueue.poll();

        while (currentOverlay != null)
        {
            currentOverlay.render(buffer, mstack);
            currentOverlay = renderQueue.poll();
        }

        RenderSystem.enableDepthTest();
    }
}
