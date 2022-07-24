package com.webb.easywiring.common.util;
import java.util.ArrayList;
import java.util.Comparator;

import net.minecraft.util.math.BlockPos;

public class Node implements Comparable<Node>
{
	public BlockPos block;
	public double score;
	public Node parent;
//	public ArrayList<Node> children;
	
	
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
