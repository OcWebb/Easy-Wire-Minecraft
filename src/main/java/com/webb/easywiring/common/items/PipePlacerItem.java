package com.webb.easywiring.common.items;

import java.util.ArrayList;
import java.util.List;

import org.openjdk.nashorn.internal.runtime.regexp.joni.constants.EncloseType;

import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PipePlacerItem extends Item
{
	private ArrayList<BlockPos> machines = new ArrayList<BlockPos>();
	public int distDown = 2;
	
	public PipePlacerItem(Properties properties) 
	{
		super(properties);
	}
	
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> toolTip, ITooltipFlag flagIn) 
	{
		super.appendHoverText(stack, worldIn, toolTip, flagIn);
		toolTip.add(new StringTextComponent("Yea pretty cool tool here"));
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (player.isCrouching())
		{
			machines.clear();
			System.out.println("machines cleared");
		}
		return super.use(world, player, hand);
	}
	
	
	@Override
	public ActionResultType useOn(ItemUseContext ItemContext) 
	{
		World world = ItemContext.getLevel();
		BlockPos blockPos = ItemContext.getClickedPos();
		
		if (world.isClientSide)
		{
			if (machines.size() >= 2)
			{
				machines.clear();
			}
			
			if (!machines.contains(blockPos))
			{
				machines.add(blockPos);
			}
		}
		
		if (machines.size() < 2)
		{
			return ActionResultType.SUCCESS;
		}
		
		if (!world.isClientSide)
		{
			// route between blocks
			ArrayList<BlockPos> path = routeToBlock(machines.get(0), machines.get(1));
			
			for (BlockPos curBlock : path)
			{
				world.removeBlock(curBlock, false);
			}
			
			machines.clear();
		}
		
		return ActionResultType.SUCCESS;
	}
	
	
	public ArrayList<BlockPos> routeToBlock(BlockPos startBlock, BlockPos destBlock)
	{
		ArrayList<BlockPos> pathBetweenBlocks = new ArrayList<BlockPos>();
		int distToDest = startBlock.distManhattan(destBlock);
		BlockPos curBlock = startBlock;
		
		// remove distDown blocks below startBlock
		for (int i = 1; i <= distDown; i++)
		{	
			curBlock = startBlock.below(i);
			pathBetweenBlocks.add(curBlock);
		}
		
		// add blocks along path
		for (int i = 0; i < distToDest; i++)
		{
			ArrayList<BlockPos> nextBlocks = getNextBlock(curBlock, destBlock.below(distDown));
			
			if (nextBlocks.size() == 0) { break; };
			
			pathBetweenBlocks.addAll(nextBlocks);
			curBlock = nextBlocks.get(0);
		}
		
		// remove distDown blocks below destBlock
		for (int i = 1; i <= distDown; i++)
		{	
			pathBetweenBlocks.add(destBlock.below(i));
		}
		
		return pathBetweenBlocks;
	}
	
	
	public ArrayList<BlockPos> getNextBlock(BlockPos block, BlockPos dest)
	{
		ArrayList<BlockPos> blocksToAdd = new ArrayList<BlockPos>();
		int lowestDistance = 999999999;
		
		for (int xoff = -1; xoff <= 1; xoff++)
		{
			for (int zoff = -1; zoff <= 1; zoff++)
			{
				BlockPos curBlock = block.offset(xoff, 0, zoff);
				int curDist = curBlock.distManhattan(dest);
				boolean isDiagnal = (Math.abs(zoff) == 1 && Math.abs(xoff) == 1);
				
				//System.out.println("x:"+xoff+" z:"+zoff+" score:"+curDist);
				
				//								prioritize diagnals because they look better
				if ((curDist < lowestDistance) || (curDist == lowestDistance && isDiagnal) )
				{
					lowestDistance = curDist;
					blocksToAdd.clear();
					blocksToAdd.add(curBlock);
					
					// add additional block for removal if on diagnal to keep path contiguous
					if (isDiagnal)
					{
						blocksToAdd.add(block.offset(0, 0, zoff));
					}
				}
			}
		}
		
		return blocksToAdd;
	}
	
	
	
}
