package com.webb.easywiring;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.webb.easywiring.core.init.ItemInit;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EasyWiring.MOD_ID)
public class EasyWiring
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "easywiring";

    public EasyWiring()
    {
        IEventBus bus =  FMLJavaModLoadingContext.get().getModEventBus();

        // Register the setup method for modloading
        bus.addListener(this::setup);

        ItemInit.ITEMS.register(bus);



        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {

    }


}
