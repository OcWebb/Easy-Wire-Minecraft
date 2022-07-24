package com.webb.easywiring.core.init;

import com.webb.easywiring.EasyWiring;
import com.webb.easywiring.common.items.BlockReplacerItem;
import com.webb.easywiring.common.items.PipePlacerItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit 
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EasyWiring.MOD_ID);

	public static final RegistryObject<PipePlacerItem> PIPE_PLACER = 
			ITEMS.register("pipe_placer", 
					() -> new PipePlacerItem(new Item.Properties().tab(ItemGroup.TAB_MISC)));
	
	public static final RegistryObject<BlockReplacerItem> BLOCK_REPLACER = 
			ITEMS.register("block_replacer", 
					() -> new BlockReplacerItem(new Item.Properties().tab(ItemGroup.TAB_MISC)));
}
