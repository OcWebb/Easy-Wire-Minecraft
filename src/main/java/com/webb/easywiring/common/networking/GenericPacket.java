package com.webb.easywiring.common.networking;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public abstract class GenericPacket implements IPacket 
{

    public GenericPacket() 
    {

    }

    public GenericPacket(PacketBuffer buffer) 
    {
        this();
    }

    @Override
    public abstract void encode(PacketBuffer buffer);

    @Override
    public abstract boolean handle(Supplier<NetworkEvent.Context> contextSupplier, final AtomicBoolean result);

}