package com.webb.easywiring.common.items;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

import com.webb.easywiring.client.render.EventBusSubscriberClient;

public class BlockReplacerItem extends Item
{
	public int maxBlocks = 30;
	public Queue<BlockPos> openList = new LinkedList<BlockPos>();
	public Queue<BlockPos> closedList = new LinkedList<BlockPos>();
	
	public BlockReplacerItem(Properties properties) 
	{
		super(properties);
	}
	
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> toolTip, ITooltipFlag flagIn) 
	{
		super.appendHoverText(stack, worldIn, toolTip, flagIn);
		toolTip.add(new StringTextComponent("You know, replaces blocks"));
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> EventBusSubscriberClient::openCustomScreen);
		return super.use(world, player, hand);
	}
	
	
	@Override
	public ActionResultType useOn(ItemUseContext ItemContext) 
	{
		World world = ItemContext.getLevel();
		BlockPos blockPos = ItemContext.getClickedPos();
		String name = getBlockName(world, blockPos);
		
		
		if (world.isClientSide)
		{
			openList.add(blockPos);
		
			while (closedList.size() < maxBlocks)
			{
				BlockPos curBlock = openList.poll();
				
				if (curBlock == null)
				{
					System.out.println("Open List empty");
					break;
				}
				closedList.add(curBlock);
				
				ArrayList<BlockPos> neighbors = getNeighborBlocks(world, curBlock, name);
				
				openList.addAll(neighbors);
			}
			
		}
		
		if (!world.isClientSide)
		{
			System.out.println("blocks removed: " + closedList.size());
			for (BlockPos curBlock : closedList)
			{
				world.removeBlock(curBlock, false);
			}
			
			closedList.clear();
			openList.clear();
		}
		
		
		return ActionResultType.SUCCESS;
	}
	
	public ArrayList<BlockPos> getNeighborBlocks(World world, BlockPos block, String blockName)
	{
		ArrayList<BlockPos> blocksToAdd = new ArrayList<BlockPos>();
		
		for (int xoff = -1; xoff <= 1; xoff++)
		{
			for (int zoff = -1; zoff <= 1; zoff++)
			{
				BlockPos curBlock = block.offset(xoff, 0, zoff);
				String name = getBlockName(world, curBlock);
				
				if (name.equals(blockName) && 
					!closedList.contains(curBlock) && 
					!openList.contains(curBlock) && 
					block != curBlock)
				{
					blocksToAdd.add(curBlock);
				}
			}
		}
		
		// check above
		BlockPos aboveBlock = block.above();
		String aboveBlockName = getBlockName(world, aboveBlock);
		
		if (aboveBlockName.equals(blockName) && 
			!closedList.contains(aboveBlock) && 
			!openList.contains(aboveBlock))
		{
			blocksToAdd.add(aboveBlock);
		}
		
		// and below
		BlockPos belowBlock = block.below();
		String belowBlockName = getBlockName(world, belowBlock);
		
		if (belowBlockName.equals(blockName) && 
			!closedList.contains(belowBlock) && 
			!openList.contains(belowBlock))
		{
			blocksToAdd.add(belowBlock);
		}
		
		return blocksToAdd;
	}
	
	public String getBlockName (World world, BlockPos block)
	{
		BlockState blockState = world.getBlockState(block);
		Block blockObj = blockState.getBlock();
		String blockName = blockObj.getRegistryName().toString();
		
		return blockName;
	}
	
}
