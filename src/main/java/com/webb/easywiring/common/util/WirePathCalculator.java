package com.webb.easywiring.common.util;

import java.util.*;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WirePathCalculator
{
	static int MAX_SEARCH = 2500;

	public ArrayList<BlockPos> CalculatePath (Level world, BlockPos startBlock, BlockPos destBlock, int blockBufferAmount)
	{
		ArrayList<BlockPos> path = new ArrayList<BlockPos>();
		BlockPos currentBlock = startBlock;
		BlockPos goalBlock = destBlock;

		// add blocks along path
		int distanceToDestinationBlock = startBlock.distManhattan(goalBlock);
		int iterations = 0;

		ArrayList<Node> openList = new ArrayList<Node>();
		ArrayList<Node> closedList = new ArrayList<Node>();
		openList.add(new Node(currentBlock, calculateScore(world, currentBlock, goalBlock, false, blockBufferAmount), null));

		while (openList.size() > 0 &&
				iterations < MAX_SEARCH &&
				distanceToDestinationBlock >= 1)
		{
			Collections.sort(openList);
			Node curNode = openList.get(0);

			Set<Long> exploredBlocks = new HashSet<Long>();

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
					boolean isDiagnal = Math.abs(zoff) == 1 && Math.abs(xoff) == 1;
					if (isDiagnal)
					{
						continue;
					}

					BlockPos neighborBlock = curNode.block.offset(xoff, 0, zoff);

					// avoid adding already processed nodes
					if (!exploredBlocks.contains(neighborBlock.asLong()))
					{
						double curScore = calculateScore(world, neighborBlock, goalBlock, isDiagnal, blockBufferAmount);
						Node neighborNode = new Node(neighborBlock, curScore, curNode);
						openList.add(neighborNode);
						exploredBlocks.add(neighborNode.block.asLong());
					}

				}
			}

			// check the block above and below the starting block as well
			BlockPos aboveBlock = curNode.block.above();

			if (!exploredBlocks.contains(aboveBlock.asLong()))
			{
				double aboveBlockScore = calculateScore(world, aboveBlock, goalBlock, false, blockBufferAmount);
				Node aboveNode = new Node(aboveBlock, aboveBlockScore, curNode);
				openList.add(aboveNode);
			}

			BlockPos belowBlock = curNode.block.below();

			if (!exploredBlocks.contains(belowBlock.asLong()))
			{
				double belowBlockScore = calculateScore(world, belowBlock, goalBlock, false, blockBufferAmount);
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


	public double calculateScore (Level world, BlockPos block, BlockPos destination, boolean isDiagnal, int blockBufferAmount)
	{

		double distanceToDestination = block.distManhattan(destination);

		BlockState currentBlockState = world.getBlockState(block);

		BlockPos aboveBlock = block.offset(0, 1, 0);
		BlockState aboveState = world.getBlockState(aboveBlock);

		BlockPos belowBlock = block.offset(0, -1, 0);
		BlockState belowState = world.getBlockState(belowBlock);

		boolean currentBlockIsAir = currentBlockState.isAir();
		boolean blockOnSurface = false;

		double score = -distanceToDestination;
		List<Direction> directions = Arrays.asList(Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH);

		int distanceToCheckForAir = blockBufferAmount*2;
		int shortestDistanceToAir = distanceToCheckForAir;
		Direction directionClosestToAir;

		for (Direction direction : directions)
		{
			for (int offset = 1; offset <= distanceToCheckForAir; offset++)
			{
				if (offset >= shortestDistanceToAir) { break; }

				BlockPos curBlock = block.relative(direction, offset);
				BlockState curBlockState = world.getBlockState(curBlock);

				if (curBlockState.isAir() && offset < shortestDistanceToAir)
				{
					shortestDistanceToAir = offset;
					directionClosestToAir = direction;
					break;
				}
			}
		}

		if (shortestDistanceToAir < blockBufferAmount)
		{
			score -= (blockBufferAmount - shortestDistanceToAir)*3;
		}
		else if (shortestDistanceToAir > blockBufferAmount)
		{
			score -= (shortestDistanceToAir - blockBufferAmount)*3;
		}
		else
		{
			score += 4;
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
