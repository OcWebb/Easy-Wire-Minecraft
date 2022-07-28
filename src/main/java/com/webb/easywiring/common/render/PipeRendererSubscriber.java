package com.webb.easywiring.common.render;

import net.minecraftforge.fml.common.Mod;
import net.minecraft.util.math.*;
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
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.*;


@EventBusSubscriber(modid = EasyWiring.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PipeRendererSubscriber 
{
	private static final float LINE_WIDTH = .08F;
	private static final int LINE_RED = 99;
	private static final int LINE_GREEN = 32;
	private static final int LINE_BLUE = 32;
	private static final int LINE_ALPHA = 255;
	private static final int LIGHT_FULLBRIGHT = 0xF000F0;
	
	@SubscribeEvent
	public static void renderMachines(RenderWorldLastEvent event)
	{
		ClientPlayerEntity player = Minecraft.getInstance().player;
		
        if (!player.getMainHandItem().getItem().getRegistryName().toString().equals("easywiring:pipe_placer")) 
        {
            return;
        }
        
        Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		
		MatrixStack stack = event.getMatrixStack();
		stack.pushPose();
		
		Vector3d cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		stack.translate(-cam.x, -cam.y, -cam.z);
        
		for (BlockPos machine : PipePlacer.machines)
		{
			drawLineBetween(buffer, stack, 
					new Vector3d(machine.getX(), machine.getY() + 2, machine.getZ()), 
					new Vector3d(machine.getX(), machine.getY(), machine.getZ()),
					LINE_WIDTH, LINE_RED, LINE_GREEN, LINE_BLUE, LINE_ALPHA);
		}
        
//		Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
//		IVertexBuilder builder = buffer.getBuffer(OutlineRenderType.OVERLAY_LINES); 
//		
//		MatrixStack stack = event.getMatrixStack();
//		stack.pushPose();
//		
//		Vector3d cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
//		stack.translate(-cam.x, -cam.y, -cam.z);
//		
//		Matrix4f mat = stack.last().pose();
//		
//		for (BlockPos machine : PipePlacer.machines)
//		{
//			builder = renderBlockOutline(mat, builder, machine, 25, 240, 40);
//		}
//		
//		for (BlockPos position : PipePlacer.currentPath)
//		{
//			builder = renderBlockOutline(mat, builder, position, 255, 255, 255);
//		}
//		
//		stack.popPose();
//		
//		buffer.endBatch(OutlineRenderType.OVERLAY_LINES);
//		
//		for (BlockPos block : PipePlacer.machines)
//		{
//			
//		}
//		
//		for (BlockPos position : PipePlacer.currentPath)
//		{
//			builder = renderBlockOutline(mat, builder, position, 255, 255, 255);
//		}
//		
//		stack.popPose();
//		
//		buffer.endBatch(CustomRenderTypes.getOutlineSolid());
	}
	
	private static void drawLineBetween(IRenderTypeBuffer buffer, MatrixStack mstack, Vector3d local, Vector3d target, float lineWidth, int r, int g, int b, int a)
    {
        IVertexBuilder builder = buffer.getBuffer(RenderType.leash());

        //Calculate yaw
        float rotY = (float) MathHelper.atan2(target.x - local.x, target.z - local.z);

        //Calculate pitch
        double distX = target.x - local.x;
        double distZ = target.z - local.z;
        float rotX = (float) MathHelper.atan2(target.y - local.y, MathHelper.sqrt(distX * distX + distZ * distZ));

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
	
	private static void renderBlockOutline(IRenderTypeBuffer buffer, MatrixStack mstack, Vector3d local, Vector3d target, float lineWidth, int r, int g, int b, int a)
    {
        IVertexBuilder builder = buffer.getBuffer(RenderType.leash());

        //Calculate yaw
        float rotY = (float) MathHelper.atan2(target.x - local.x, target.z - local.z);

        //Calculate pitch
        double distX = target.x - local.x;
        double distZ = target.z - local.z;
        float rotX = (float) MathHelper.atan2(target.y - local.y, MathHelper.sqrt(distX * distX + distZ * distZ));

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
    
	
//	public static IVertexBuilder renderBlockOutline(Matrix4f mat, IVertexBuilder builder, BlockPos block, int r, int g, int b)
//	{
//		// bottom face
//		builder.vertex(mat, block.getX(), block.getY(), block.getZ() + 1).color(r, g, b, 255).endVertex();
//		builder.vertex(mat, block.getX(), block.getY(), block.getZ()).color(r, g, b, 255).endVertex();
//		
//		builder.vertex(mat, block.getX(), block.getY(), block.getZ()).color(r, g, b, 255).endVertex();
//		builder.vertex(mat, block.getX() + 1, block.getY(), block.getZ()).color(r, g, b, 255).endVertex();
//		
//		builder.vertex(mat, block.getX() + 1, block.getY(), block.getZ()).color(r, g, b, 255).endVertex();
//		builder.vertex(mat, block.getX() + 1, block.getY(), block.getZ() + 1).color(r, g, b, 255).endVertex();
//		
//		builder.vertex(mat, block.getX(), block.getY(), block.getZ() + 1).color(r, g, b, 255).endVertex();
//		builder.vertex(mat, block.getX() + 1, block.getY(), block.getZ() + 1).color(r, g, b, 255).endVertex();
//		
//		// top face
//		builder.vertex(mat, block.getX(), block.getY() + 1, block.getZ() + 1).color(r, g, b, 255).endVertex();
//		builder.vertex(mat, block.getX(), block.getY() + 1, block.getZ()).color(r, g, b, 255).endVertex();
//		
//		builder.vertex(mat, block.getX(), block.getY() + 1, block.getZ()).color(r, g, b, 255).endVertex();
//		builder.vertex(mat, block.getX() + 1, block.getY() + 1, block.getZ()).color(r, g, b, 255).endVertex();
//		
//		builder.vertex(mat, block.getX() + 1, block.getY() + 1, block.getZ()).color(r, g, b, 255).endVertex();
//		builder.vertex(mat, block.getX() + 1, block.getY() + 1, block.getZ() + 1).color(r, g, b, 255).endVertex();
//		
//		builder.vertex(mat, block.getX(), block.getY() + 1, block.getZ() + 1).color(r, g, b, 255).endVertex();
//		builder.vertex(mat, block.getX() + 1, block.getY() + 1, block.getZ() + 1).color(r, g, b, 255).endVertex();
//		
//		// side walls
//		builder.vertex(mat, block.getX(), block.getY(), block.getZ()).color(r, g, b, 255).endVertex();
//		builder.vertex(mat, block.getX(), block.getY() + 1, block.getZ()).color(r, g, b, 255).endVertex();
//		
//		builder.vertex(mat, block.getX() + 1, block.getY(), block.getZ()).color(r, g, b, 255).endVertex();
//		builder.vertex(mat, block.getX() + 1, block.getY() + 1, block.getZ()).color(r, g, b, 255).endVertex();
//		
//		builder.vertex(mat, block.getX(), block.getY(), block.getZ() + 1).color(r, g, b, 255).endVertex();
//		builder.vertex(mat, block.getX(), block.getY() + 1, block.getZ() + 1).color(r, g, b, 255).endVertex();
//		
//		builder.vertex(mat, block.getX() + 1, block.getY(), block.getZ() + 1).color(r, g, b, 255).endVertex();
//		builder.vertex(mat, block.getX() + 1, block.getY() + 1, block.getZ() + 1).color(r, g, b, 255).endVertex();
//		
//		return builder;
//	}

}
