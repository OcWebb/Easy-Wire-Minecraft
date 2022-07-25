package com.webb.easywiring.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WirePathCalculator 
{
	 static int MAX_SEARCH = 800;
	 
	 public ArrayList<BlockPos> CalculatePath (World world, BlockPos startBlock, BlockPos destBlock, int distDown)
	{
		ArrayList<BlockPos> path = new ArrayList<BlockPos>();
		ArrayList<BlockPos> blocksToAvoid = new ArrayList<BlockPos>();
		BlockPos currentBlock = startBlock;
		BlockPos goalBlock = destBlock.below(distDown);
		
		blocksToAvoid.add(startBlock);
		blocksToAvoid.add(destBlock);
		
		// remove distDown blocks below startBlock
		for (int i = 1; i <= distDown; i++)
		{	
			currentBlock = startBlock.below(i);
			path.add(currentBlock);
		}
		
		
		// add blocks along path
		int distanceToDestinationBlock = startBlock.distManhattan(goalBlock);
		int iterations = 0;
		
		ArrayList<Node> openList = new ArrayList<Node>();
		ArrayList<Node> closedList = new ArrayList<Node>();
		openList.add(new Node(currentBlock, calculateScore(world, currentBlock, goalBlock, false), null));
		
		while (openList.size() > 0 && 
				iterations < MAX_SEARCH &&
				distanceToDestinationBlock >= 1)
		{
			Collections.sort(openList);
			Node curNode = openList.get(0);
			
			ArrayList<Long> exploredBlocks = new ArrayList<Long>();
			
			for (BlockPos b : blocksToAvoid)
			{
				exploredBlocks.add(b.asLong());
			}
			
			for (Node n : closedList)
			{
				exploredBlocks.add(n.block.asLong());
			}
			
			for (Node n : openList)
			{
				exploredBlocks.add(n.block.asLong());
			}
			
			// process all neighbor blocks
			for (int zoff = -1; zoff <= 1; zoff++)
			{
				for (int xoff = -1; xoff <= 1; xoff++)
				{
					if ((Math.abs(zoff) == 1 && Math.abs(xoff) == 1)) 
					{
						continue;
					}
					
					BlockPos neighborBlock = curNode.block.offset(xoff, 0, zoff);
					
					// avoid adding already processed nodes
					if (!exploredBlocks.contains(neighborBlock.asLong()))
					{
						boolean isDiagnal = (Math.abs(zoff) == 1 && Math.abs(xoff) == 1);
						double curScore = calculateScore(world, neighborBlock, goalBlock, isDiagnal);
						Node neighborNode = new Node(neighborBlock, curScore, curNode);
						openList.add(neighborNode);
						exploredBlocks.add(neighborNode.block.asLong());
					}
					
				}
			}
			
			// check the block above and below the starting block aswell
			BlockPos aboveBlock = curNode.block.above();
			
			if (!exploredBlocks.contains(aboveBlock.asLong()))
			{
				double aboveBlockScore = calculateScore(world, aboveBlock, goalBlock, false);
				Node aboveNode = new Node(aboveBlock, aboveBlockScore, curNode);
				openList.add(aboveNode);
			}
			
			BlockPos belowBlock = curNode.block.below();
			
			if (!exploredBlocks.contains(belowBlock.asLong()))
			{
				double belowBlockScore = calculateScore(world, belowBlock, goalBlock, false);
				Node belowNode = new Node(belowBlock, belowBlockScore, curNode);
				openList.add(belowNode);
			}
			
			
			// remove from open list and add to closed
			openList.remove(curNode);
			closedList.add(curNode);
			
			iterations++;
			distanceToDestinationBlock = curNode.block.distManhattan(goalBlock);
		}
		
		if (distanceToDestinationBlock <= 1)
		{
			Collections.sort(openList);
			Node curNode = openList.get(0);
			while (curNode.parent != null)
			{
				path.add(curNode.block);
				curNode = curNode.parent;
			}
		}
		
		// remove distDown blocks below destBlock
		for (int i = 1; i <= distDown; i++)
		{	
			path.add(destBlock.below(i));
		}
		
		return path;
	}
 
	
	public double calculateScore (World world, BlockPos block, BlockPos destination, boolean isDiagnal)
	{
		
		double distanceToDestination = block.distManhattan(destination);
		
		BlockState currentBlockState = world.getBlockState(block);
		Block currentBlockObject = currentBlockState.getBlock();
		
		BlockPos aboveBlock = block.offset(0, 1, 0);
		BlockState aboveState = world.getBlockState(aboveBlock);
		Block aboveBlockObject = aboveState.getBlock();
		
		boolean currentBlockIsAir = currentBlockObject.isAir(currentBlockState, world, block);
		boolean currentBlockIsOnSurface = aboveBlockObject.isAir(aboveState, world, aboveBlock);
		
		double score = -distanceToDestination;
		
		if (currentBlockIsAir || currentBlockIsOnSurface)
		{
			score = score - 6;
		}
		
		if (isDiagnal)
		{
			score = score + 0.5;
		}
		
		return score;
	}
	
}
