package com.webb.easywiring.common.items;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.item.Item.Properties;

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
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> toolTip, TooltipFlag flagIn)
	{
		super.appendHoverText(stack, worldIn, toolTip, flagIn);
		toolTip.add(new TextComponent("You know, replaces blocks"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		if (player.isCrouching())
		{

		}
		return super.use(world, player, hand);
	}


	@Override
	public InteractionResult useOn(UseOnContext ItemContext)
	{
		Level world = ItemContext.getLevel();
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
			System.out.println("closedList: " + closedList.size());
			for (BlockPos curBlock : closedList)
			{
				world.removeBlock(curBlock, false);
			}

			closedList.clear();
			openList.clear();
		}


		return InteractionResult.SUCCESS;
	}

	public ArrayList<BlockPos> getNeighborBlocks(Level world, BlockPos block, String blockName)
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

	public String getBlockName (Level world, BlockPos block)
	{
		BlockState blockState = world.getBlockState(block);
		Block blockObj = blockState.getBlock();
		String blockName = blockObj.getRegistryName().toString();

		return blockName;
	}

}
