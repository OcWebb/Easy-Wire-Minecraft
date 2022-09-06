package com.webb.easywiring.common.items;

import java.util.ArrayList;
import java.util.List;

import com.webb.easywiring.common.util.Path;
import com.webb.easywiring.common.util.Pathfinder;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PipePlacer extends Item
{
	public static ArrayList<BlockPos> machines = new ArrayList<BlockPos>();
	public static Path currentPath = new Path();
	public int distDown = 2;

	public PipePlacer(Properties properties)
	{
		super(properties);
	}


	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> toolTip, TooltipFlag flagIn)
	{
		super.appendHoverText(stack, worldIn, toolTip, flagIn);
		toolTip.add(new TextComponent("Yea pretty cool tool here"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		if (player.isCrouching())
		{
			machines.clear();
			currentPath.clear();
			System.out.println("machines cleared");
		}

		return super.use(world, player, hand);
	}


	@Override
	public InteractionResult useOn(UseOnContext ItemContext)
	{
		Level world = ItemContext.getLevel();
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
			return InteractionResult.SUCCESS;
		}

		if (!world.isClientSide)
		{
			// route between blocks
			Path path = Pathfinder.GetWirePath(world, machines.get(0), machines.get(1));
			currentPath = path;

			System.out.println("Path returned: " + path.size());

			if (!path.isEmpty())
			{
				
			}
			else
			{
				System.out.println("ERROR: Routing Failed");
			}

		}

		return InteractionResult.SUCCESS;
	}
}
