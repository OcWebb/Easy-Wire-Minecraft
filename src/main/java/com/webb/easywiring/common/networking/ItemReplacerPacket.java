//package com.webb.easywiring.common.networking;
//
//import net.minecraft.item.ItemStack;
//import net.minecraft.network.PacketBuffer;
//import net.minecraftforge.fml.network.NetworkEvent;
//
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.function.Supplier;
//
//public class ItemReplacerPacket extends GenericPacket {
//
//    private final ItemStack stack;
//
//    public ItemReplacerPacket(ItemStack stack) {
//        this.stack = stack;
//    }
//
//    public ItemReplacerPacket(PacketBuffer buffer) {
//        super(buffer);
//
//        this.stack = buffer.readItem();
//    }
//
//    @Override
//    public void encode(PacketBuffer buffer) {
//        buffer.writeItemStack(stack, true);
//    }
//
//    @Override
//    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier, AtomicBoolean result) {
////        contextSupplier.get().enqueueWork(() -> ScreenUtilities.openInitialBookScreen(this.stack));
////
////        contextSupplier.get().setPacketHandled(result.get());
//        return result.get();
//    }
//
//}package com.webb.easywiring.common.networking;
//
//import net.minecraft.item.ItemStack;
//import net.minecraft.network.PacketBuffer;
//import net.minecraftforge.fml.network.NetworkEvent;
//
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.function.Supplier;
//
//public class ItemReplacerPacket extends GenericPacket {
//
//    private final ItemStack stack;
//
//    public ItemReplacerPacket(ItemStack stack) {
//        this.stack = stack;
//    }
//
//    public ItemReplacerPacket(PacketBuffer buffer) {
//        super(buffer);
//
//        this.stack = buffer.readItem();
//    }
//
//    @Override
//    public void encode(PacketBuffer buffer) {
//        buffer.writeItemStack(stack, true);
//    }
//
//    @Override
//    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier, AtomicBoolean result) {
////        contextSupplier.get().enqueueWork(() -> ScreenUtilities.openInitialBookScreen(this.stack));
////
////        contextSupplier.get().setPacketHandled(result.get());
//        return result.get();
//    }
//
//}