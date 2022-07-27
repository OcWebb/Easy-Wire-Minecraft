package com.webb.easywiring.common.items;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openjdk.nashorn.internal.runtime.regexp.joni.constants.EncloseType;

import com.webb.easywiring.common.util.Node;
import com.webb.easywiring.common.util.Pathfinder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

public class PipePlacer extends Item
{
	public static ArrayList<BlockPos> machines = new ArrayList<BlockPos>();
	public static ArrayList<BlockPos> currentPath = new ArrayList<BlockPos>();
	public int distDown = 2;
	
	public PipePlacer(Properties properties) 
	{
		super(properties);
	}
	
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> toolTip, ITooltipFlag flagIn) 
	{
		super.appendHoverText(stack, worldIn, toolTip, flagIn);
		toolTip.add(new StringTextComponent("Yea pretty cool tool here"));
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (player.isCrouching())
		{
			machines.clear();
			currentPath.clear();
			System.out.println("machines cleared");
		}
		
		return super.use(world, player, hand);
	}
	
	
	@Override
	public ActionResultType useOn(ItemUseContext ItemContext) 
	{
		World world = ItemContext.getLevel();
		BlockPos blockPos = ItemContext.getClickedPos();
		
		if (world.isClientSide)
		{
			if (machines.size() >= 2)
			{
				machines.clear();
				currentPath.clear();
				
			}
			
			if (!machines.contains(blockPos))
			{
				machines.add(blockPos);
			}
		};
		
		if (machines.size() < 2)
		{
			return ActionResultType.SUCCESS;
		}
		
		if (!world.isClientSide)
		{
			// route between blocks
			ArrayList<BlockPos> path = Pathfinder.GetWirePath(world, machines.get(0), machines.get(1), distDown);
			currentPath = path;
			
			System.out.println("Path returned: " + path.size());
			
			if (!path.isEmpty())
			{
				for (BlockPos curBlock : path)
				{
//					world.removeBlock(curBlock, false);
//					world.setBlock(curBlock, Blocks.REDSTONE_BLOCK.getStateForPlacement(null), Constants.BlockFlags.DEFAULT);
					
				}
				
//				machines.clear();
			} 
			else
			{
				System.out.println("ERROR: Routing Failed");
			}
			
		}
		
		return ActionResultType.SUCCESS;
	}
}
