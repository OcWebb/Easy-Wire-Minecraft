package com.webb.easywiring.common.util;

import java.util.*;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WirePathCalculator
{
	static int MAX_SEARCH = 2500;

	public Path CalculatePath (Level world, BlockPos startBlock, BlockPos destBlock, int blockBufferAmount)
	{
		Path path = new Path();
		BlockPos currentBlock = startBlock;
		BlockPos goalBlock = destBlock;

		// add blocks along path
		int distanceToDestinationBlock = startBlock.distManhattan(goalBlock);
		int iterations = 0;

		ArrayList<Node> openList = new ArrayList<Node>();
		ArrayList<Node> closedList = new ArrayList<Node>();
		openList.add(generateNode(world, currentBlock, goalBlock, false, blockBufferAmount));

		while (openList.size() > 0 &&
				iterations < MAX_SEARCH &&
				distanceToDestinationBlock >= 1)
		{
			Collections.sort(openList);
			Node curNode = openList.get(0);

			Set<Long> exploredBlocks = new HashSet<Long>();

			for (Node n : closedList)
			{
				exploredBlocks.add(n.blockPos.asLong());
			}

			for (Node n : openList)
			{
				exploredBlocks.add(n.blockPos.asLong());
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

					BlockPos neighborBlock = curNode.blockPos.offset(xoff, 0, zoff);

					// avoid adding already processed nodes
					if (!exploredBlocks.contains(neighborBlock.asLong()))
					{
						Node neighborNode = generateNode(world, neighborBlock,  goalBlock, false, blockBufferAmount);
						neighborNode.parent = curNode;
						openList.add(neighborNode);
						exploredBlocks.add(neighborNode.blockPos.asLong());
					}

				}
			}

			// check the block above and below the starting block as well
			BlockPos aboveBlock = curNode.blockPos.above();

			if (!exploredBlocks.contains(aboveBlock.asLong()))
			{
				Node aboveNode = generateNode(world, aboveBlock, goalBlock, false, blockBufferAmount);
				aboveNode.parent = curNode;
				openList.add(aboveNode);
			}

			BlockPos belowBlock = curNode.blockPos.below();

			if (!exploredBlocks.contains(belowBlock.asLong()))
			{
				Node belowNode = generateNode(world, belowBlock, goalBlock, false, blockBufferAmount);
				belowNode.parent = curNode;
				openList.add(belowNode);
			}


			// remove from open list and add to closed
			openList.remove(curNode);
			closedList.add(curNode);

			iterations++;
			distanceToDestinationBlock = curNode.blockPos.distManhattan(goalBlock);
		}

		if (distanceToDestinationBlock <= 1)
		{
			Collections.sort(closedList);
			Node curNode = closedList.get(0);
			path.addNodeHead(curNode);

			while (curNode.parent != null)
			{
				curNode = curNode.parent;
				path.addNodeHead(curNode);
			}
		}

		return path;
	}


	public Node generateNode (Level world, BlockPos block, BlockPos destination, boolean isDiagnal, int blockBufferAmount)
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
		Direction directionClosestToAir = null;

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

		return new Node(block, score, null, directionClosestToAir, shortestDistanceToAir);
	}

}
