package com.webb.easywiring.common.util;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class Pathfinder
{
	private static WirePathCalculator wirePathCalculator = new WirePathCalculator();

	public static Path GetWirePath (Level world, BlockPos startBlock, BlockPos destBlock)
	{
		Path path = new Path();

		try
		{
			path = wirePathCalculator.CalculatePath(world, startBlock, destBlock, 8);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(e);
		}

		return path;
	}

}
