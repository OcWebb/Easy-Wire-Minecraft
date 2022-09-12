package com.webb.easywiring.common.util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node implements Comparable<Node>
{
	public BlockPos blockPos;
	public double score;
	public Node parent;
	public Direction directionToParent;
	public Direction directionToAir;
	public int distanceToAir;

	public ArrayList<String> debugInformation;

	public BlockState replacedBlockState;

	public Node(BlockPos BlockPos, double scoreIn, Node Parent, Direction DirectionToAir, int DistanceToAir)
	{
		Level world = Minecraft.getInstance().level;

		blockPos = BlockPos;
		score = scoreIn;
		parent = Parent;
		directionToParent = getDirectionToParent();
		distanceToAir = DistanceToAir;
		directionToAir = DirectionToAir;
		replacedBlockState = world.getBlockState(blockPos);
		debugInformation = new ArrayList<String>();
	}

	public Direction getDirectionToParent()
	{
		List<Direction> directions = Arrays.asList(Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH);
		for (Direction direction : directions)
		{
			if (parent != null && parent.blockPos.relative(direction).asLong() == blockPos.asLong())
			{
				return direction;
			}
		}

		return null;
	}

	public void addDebugInformation(String info)
	{
		debugInformation.add(info);
	}

	@Override
	public String toString() {
		return "Node{" +
				"blockPos=" + blockPos +
				", score=" + score +
				", parent=" + (parent!=null ? parent.blockPos.toString() : "none") +
				", directionFromParent=" + directionToParent +
				", directionToAir=" + directionToAir +
				", distanceToAir=" + distanceToAir +
				", debugInformation=" + debugInformation +
				", replacedBlockState=" + replacedBlockState +
				'}';
	}

	@Override
	public int compareTo(Node b)
	{
		return this.score < b.score ? 1 : (this.score > b.score) ? -1 : 0;
	}
}
