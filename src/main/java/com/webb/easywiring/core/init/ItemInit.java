package com.webb.easywiring.core.init;

import com.webb.easywiring.EasyWiring;
import com.webb.easywiring.common.items.PipePlacer;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EasyWiring.MOD_ID);

	public static final RegistryObject<PipePlacer> PIPE_PLACER =
			ITEMS.register("pipe_placer",
					() -> new PipePlacer(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

}
