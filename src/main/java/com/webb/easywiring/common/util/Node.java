package com.webb.easywiring.common.util;

import java.util.Comparator;

import net.minecraft.util.math.BlockPos;

public class Node implements Comparator<Node>
{
	public BlockPos block;
	public double score;
	
	public Node(BlockPos blockPos, double scoreIn)
	{
		block = blockPos;
		score = scoreIn;
	}
	
	public int compare(Node a, Node b)
	{
		return (int) (a.score - b.score);
	}
}
