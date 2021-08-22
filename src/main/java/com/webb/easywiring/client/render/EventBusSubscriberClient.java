package com.webb.easywiring.client.render;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import com.webb.easywiring.EasyWiring;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;

@EventBusSubscriber(modid = EasyWiring.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventBusSubscriberClient 
{
	private EventBusSubscriberClient ()
	{
		
	}
	
	public static void openCustomScreen()
	{
	    Minecraft.getInstance().setScreen(new ItemReplacerScreen());
	}
	
}
