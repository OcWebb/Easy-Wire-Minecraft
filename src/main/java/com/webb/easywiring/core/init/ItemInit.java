package com.webb.easywiring.core.init;

import com.webb.easywiring.EasyWiring;
import com.webb.easywiring.common.items.SpecialItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit 
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EasyWiring.MOD_ID);
	
	public static final RegistryObject<SpecialItem> PIPE_PLACER = 
			ITEMS.register("pipe_placer", 
					() -> new SpecialItem(new Item.Properties().tab(ItemGroup.TAB_MISC)));
}
