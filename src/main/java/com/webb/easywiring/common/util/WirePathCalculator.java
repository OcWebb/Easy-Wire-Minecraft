package com.webb.easywiring.common.util;

import java.awt.geom.IllegalPathStateException;
import java.util.*;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WirePathCalculator
{
	static int MAX_SEARCH = 3000;

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
		Node finalNode = null;
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
						Node neighborNode = generateNode(world, neighborBlock,  goalBlock, false, blockBufferAmount, curNode);
						openList.add(neighborNode);
						exploredBlocks.add(neighborNode.blockPos.asLong());
					}

				}
			}

			// check the block above and below the starting block as well
			BlockPos aboveBlock = curNode.blockPos.above();

			if (!exploredBlocks.contains(aboveBlock.asLong()))
			{
				Node aboveNode = generateNode(world, aboveBlock, goalBlock, false, blockBufferAmount, curNode);

				openList.add(aboveNode);
			}

			BlockPos belowBlock = curNode.blockPos.below();

			if (!exploredBlocks.contains(belowBlock.asLong()))
			{
				Node belowNode = generateNode(world, belowBlock, goalBlock, false, blockBufferAmount, curNode);
				openList.add(belowNode);
			}


			// remove from open list and add to closed
			openList.remove(curNode);
			closedList.add(curNode);

			iterations++;
			distanceToDestinationBlock = curNode.blockPos.distManhattan(goalBlock);
			if (distanceToDestinationBlock <= 1)
			{
				finalNode = curNode;
				break;
			}
		}

		if (finalNode != null)
		{
			Node curNode = finalNode;
			path.addNode(0, curNode);

			while (curNode.parent != null)
			{
				Direction direction = invertDirection(curNode.directionToParent);

				curNode = curNode.parent;
				boolean success = path.addNodeHead(curNode, direction);
				if (!success)
				{
					throw new IllegalPathStateException("Attempted to insert node which would result in non-continuous path");
				}
			}
		}

		return path;
	}

	public Node generateNode (Level world, BlockPos block, BlockPos destination, boolean isDiagnal, int blockBufferAmount)
	{
		return generateNode(world, block, destination, isDiagnal, blockBufferAmount, null);
	}

	public Node generateNode (Level world, BlockPos block, BlockPos destination, boolean isDiagnal, int blockBufferAmount, Node parent)
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

				if (curBlockState.isAir())
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

		Node newNode = new Node(block, score, parent, directionClosestToAir, shortestDistanceToAir);

		newNode.addDebugInformation("score: " + score);
		newNode.addDebugInformation("distanceToGoal: " + distanceToDestination);
		newNode.addDebugInformation("airDirection: " + directionClosestToAir);

		return newNode;
	}

	public Direction invertDirection (Direction inputDirection)
	{
		switch (inputDirection)
		{
			case UP: return Direction.DOWN;
			case DOWN: return Direction.UP;
			case NORTH: return Direction.SOUTH;
			case SOUTH: return Direction.NORTH;
			case EAST: return Direction.WEST;
			case WEST: return Direction.EAST;

			default: return null;
		}
	}

}
