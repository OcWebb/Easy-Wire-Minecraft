package com.webb.easywiring.common.render;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.webb.easywiring.EasyWiring;
import com.webb.easywiring.common.items.PipePlacer;
import com.webb.easywiring.core.init.ItemInit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@EventBusSubscriber(modid = EasyWiring.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PipeRendererSubscriber 
{
	
	@SubscribeEvent
	public static void renderMachines(RenderWorldLastEvent event)
	{
		ClientPlayerEntity player = Minecraft.getInstance().player;
		
        if (!player.getMainHandItem().getItem().getRegistryName().toString().equals("easywiring:pipe_placer")) 
        {
            return;
        }
        
		Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		IVertexBuilder builder = buffer.getBuffer(OutlineRenderType.OVERLAY_LINES); 
		
		MatrixStack stack = event.getMatrixStack();
		stack.pushPose();
		
		Vector3d cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		stack.translate(-cam.x, -cam.y, -cam.z);
		
		Matrix4f mat = stack.last().pose();

		for (int i = 0; i < PipePlacer.currentPath.size()-1; i++)
		{
			BlockPos currentNode = PipePlacer.currentPath.get(i);
			BlockPos nextNode = PipePlacer.currentPath.get(i+1);
			
			builder.vertex(mat, currentNode.getX(), currentNode.getY(), currentNode.getZ()).color(255, 255, 255, 255).endVertex();
			builder.vertex(mat, nextNode.getX(), nextNode.getY(), nextNode.getZ()).color(255, 255, 255, 255).endVertex();
		}
		
		stack.popPose();
		
		buffer.endBatch(OutlineRenderType.OVERLAY_LINES);
	}

}
