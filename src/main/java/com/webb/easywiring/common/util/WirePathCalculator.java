package com.webb.easywiring.common.util;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WirePathCalculator
{
	static int MAX_SEARCH = 1000;

	public ArrayList<BlockPos> CalculatePath (Level world, BlockPos startBlock, BlockPos destBlock, int blockBuffer)
	{
		ArrayList<BlockPos> path = new ArrayList<BlockPos>();
		ArrayList<BlockPos> blocksToAvoid = new ArrayList<BlockPos>();
		BlockPos currentBlock = startBlock;
		BlockPos goalBlock = destBlock;

//		blocksToAvoid.add(startBlock);
//		blocksToAvoid.add(destBlock);

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
			Collections.sort(closedList);
			Node curNode = closedList.get(0);
			path.add(curNode.block);
			while (curNode.parent != null)
			{
				curNode = curNode.parent;
				path.add(curNode.block);
			}
		}

		return path;
	}


	public double calculateScore (Level world, BlockPos block, BlockPos destination, boolean isDiagnal)
	{

		double distanceToDestination = block.distManhattan(destination);

		BlockState currentBlockState = world.getBlockState(block);

		BlockPos aboveBlock = block.offset(0, 1, 0);
		BlockState aboveState = world.getBlockState(aboveBlock);

		BlockPos belowBlock = block.offset(0, -1, 0);
		BlockState belowState = world.getBlockState(belowBlock);

		boolean currentBlockIsAir = currentBlockState.isAir();
		boolean blockOnSurface = aboveState.isAir() || belowState.isAir();

		double score = -distanceToDestination;

		for (int zoff = -1; zoff <= 1; zoff++)
		{
			if (blockOnSurface)
			{
				break;
			}

			for (int xoff = -1; xoff <= 1; xoff++)
			{
				if ((Math.abs(zoff) == 1 && Math.abs(xoff) == 1))
				{
					continue;
				}

				BlockPos neighborBlock = block.offset(xoff, 0, zoff);
				BlockState neighborBlockState = world.getBlockState(neighborBlock);
				blockOnSurface = neighborBlockState.isAir();
			}
		}

		if (blockOnSurface)
		{
			score -= 6;
		}

		if (currentBlockIsAir)
		{
			score -= 10;
		}

		if (isDiagnal)
		{
			score += 0.5;
		}

		return score;
	}

}
