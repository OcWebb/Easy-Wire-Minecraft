package com.webb.easywiring.common.util;

import net.minecraft.util.math.BlockPos;

public class Node 
{
	public BlockPos block;
	public double score;
	
	public Node(BlockPos blockPos, double scoreIn)
	{
		block = blockPos;
		score = scoreIn;
	}
}
