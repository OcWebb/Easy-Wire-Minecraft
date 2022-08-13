package com.webb.easywiring.common.util;

import net.minecraft.core.BlockPos;

public class Node implements Comparable<Node>
{
	public BlockPos block;
	public double score;
	public Node parent;


	public Node(BlockPos blockPos, double scoreIn, Node Parent)
	{
		block = blockPos;
		score = scoreIn;
		parent = Parent;
	}

	@Override
	public int compareTo(Node b)
	{
		return this.score < b.score ? 1 : (this.score > b.score) ? -1 : 0;
	}
}
