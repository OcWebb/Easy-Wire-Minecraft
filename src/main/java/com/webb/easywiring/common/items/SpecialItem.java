package com.webb.easywiring.common.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openjdk.nashorn.internal.runtime.regexp.joni.constants.EncloseType;

import com.webb.easywiring.common.util.Node;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

public class SpecialItem extends Item
{
	private ArrayList<BlockPos> machines = new ArrayList<BlockPos>();
	private ArrayList<BlockPos> path = new ArrayList<BlockPos>();
	public int distDown = 2;
	
	public SpecialItem(Properties properties) 
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
		};
		
		if (machines.size() < 2)
		{
			return ActionResultType.SUCCESS;
		}
		
		if (!world.isClientSide)
		{
			// route between blocks
			routeToBlock(world, machines.get(0), machines.get(1));
			
			for (BlockPos curBlock : path)
			{
				world.removeBlock(curBlock, false);
			}
			
			machines.clear();
			path.clear();
		}
		
		return ActionResultType.SUCCESS;
	}
	
	
	public void routeToBlock(World world, BlockPos startBlock, BlockPos destBlock)
	{
		BlockPos curBlock = startBlock;
		
		// remove distDown blocks below startBlock
		for (int i = 1; i <= distDown; i++)
		{	
			curBlock = startBlock.below(i);
			path.add(curBlock);
		}
		
		// remove distDown blocks below destBlock
		for (int i = 1; i <= distDown; i++)
		{	
			path.add(destBlock.below(i));
		}
		
		
		// add blocks along path
		int distToDest = startBlock.distManhattan(destBlock);
		int iterations = 0;
		
		ArrayList<Node> openList = new ArrayList<Node>();
		ArrayList<Node> closedList = new ArrayList<Node>();
		openList.add(new Node(startBlock, calculateScore(world, startBlock, destBlock, false)));
		
		while (openList.size() <= 0 || iterations < 500)
		{
			ArrayList<BlockPos> nextBlocks = getNextBlock(world, curBlock, destBlock.below(distDown));
			Collections.sort(openList);
			
			if (nextBlocks.size() == 0) { break; };
			
			path.addAll(nextBlocks);
			curBlock = nextBlocks.get(0);
			iterations++;
			distToDest = curBlock.distManhattan(destBlock.below(distDown));
		}
	}
	
	
	public ArrayList<BlockPos> getNextBlock(World world, BlockPos block, BlockPos dest)
	{
		ArrayList<BlockPos> blocksToAdd = new ArrayList<BlockPos>();
		double highestScore = -999999999.0;
		
		for (int zoff = -1; zoff <= 1; zoff++)
		{
			for (int xoff = -1; xoff <= 1; xoff++)
			{
				BlockPos curBlock = block.offset(xoff, 0, zoff);
				boolean isDiagnal = (Math.abs(zoff) == 1 && Math.abs(xoff) == 1);
				double curScore = calculateScore(world, curBlock, dest, isDiagnal);
				
				if (machines.contains(curBlock))
				{
					continue;
				}
				
				if (curScore > highestScore)
				{
					
					highestScore = curScore;
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
		
		// check the block above and below the starting block aswell
		BlockPos aboveBlock = block.above();
		double aboveBlockScore = calculateScore(world, aboveBlock, dest, false);
		
		if (aboveBlockScore >= highestScore)
		{
			highestScore = aboveBlockScore;
			blocksToAdd.clear();
			blocksToAdd.add(aboveBlock);
		}
		
		BlockPos belowBlock = block.below();
		double belowScore = calculateScore(world, belowBlock, dest, false);
		
		if (belowScore >= highestScore)
		{
			highestScore = belowScore;
			blocksToAdd.clear();
			blocksToAdd.add(belowBlock);
		}
		
		return blocksToAdd;
	}
	
	public double calculateScore (World world, BlockPos block, BlockPos dest, boolean isDiagnal)
	{
		
		double distToDest = block.distManhattan(dest);
		double distToTargetMachine = block.distManhattan(machines.get(1));
		
		BlockState curBlockState = world.getBlockState(block);
		Block curBlockObj = curBlockState.getBlock();
		
		BlockPos aboveBlock = block.offset(0, 1, 0);
		BlockState aboveState = world.getBlockState(aboveBlock);
		Block aboveBlockObj = aboveState.getBlock();
		
		boolean curBlockIsAir = curBlockObj.isAir(curBlockState, world, block);
		boolean curBlockIsOnSurface = aboveBlockObj.isAir(aboveState, world, aboveBlock);
		
		double score = -distToDest + (distToTargetMachine/4);
		
		if (curBlockIsAir || curBlockIsOnSurface)
		{
			score = score + 8;
		}
		
		if (isDiagnal)
		{
			score = score + 0.5;
		}
		
		return score;
	}
	
}
