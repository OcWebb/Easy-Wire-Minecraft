package com.webb.easywiring.common.util;

import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Pathfinder 
{
	private static WirePathCalculator wirePathCalculator = new WirePathCalculator();
	
	public static ArrayList<BlockPos> GetWirePath (World world, BlockPos startBlock, BlockPos destBlock, int distDown)
	{
		ArrayList<BlockPos> path = wirePathCalculator.CalculatePath(world, startBlock, destBlock, distDown);
		
		return path;
	}

}
