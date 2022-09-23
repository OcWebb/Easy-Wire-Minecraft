package com.webb.easywiring.common.networking;

public class Networking {

    // Do not touch.
//    private static int id = 0;
//    private static Networking instance;
//    private SimpleChannel CHANNEL;

    private Networking() {
    }

//    public static Networking get() {
//        if (instance == null) {
//            instance = new Networking();
//        }
//
//        return instance;
//    }
//
//    public void sendToClient(IPacket packet, ServerPlayerEntity playerEntity) {
//        CHANNEL.send(PacketDistributor.PLAYER.with(() -> playerEntity), packet);
//    }
//
//    public void sendToAll(IPacket packet, ServerWorld world) {
//        world.players().forEach((serverPlayerEntity) -> this.sendToClient(packet, serverPlayerEntity));
//    }
//
//    @SuppressWarnings("unused")
//    public void sendToServer(IPacket packet) {
//        CHANNEL.sendToServer(packet);
//    }
//
//    private <PCT extends IPacket> void registerPacket(Class<PCT> packetClass) {
//        CHANNEL.messageBuilder(packetClass, id++)
//                .encoder(IPacket::encode)
//                .decoder((buffer) -> {
//                    try {
//                        return packetClass.getConstructor(buffer.getClass()).newInstance(buffer);
//                    } catch (InvocationTargetException |
//                            InstantiationException |
//                            IllegalAccessException |
//                            NoSuchMethodException e) {
//                        e.printStackTrace();
//                        throw new IllegalStateException("Could not find class with name: " + packetClass.getName());
//                    }
//                })
//                .consumer((pct, contextSupplier) -> {
//                    return pct.handle(contextSupplier, new AtomicBoolean(true));
//                })
//                .add();
//    }
//
//    public void initialize() {
//        CHANNEL = NetworkRegistry.newSimpleChannel(IdBuilder.mod(ModConstants.CHANNEL_ID),
//                () -> ModConstants.NETWORK_PROTOCOL_VERSION,
//                ModConstants.NETWORK_PROTOCOL_VERSION::equals,
//                ModConstants.NETWORK_PROTOCOL_VERSION::equals);
//        this.registerPackets();
//    }
//
//    private void registerPackets() {
//        this.registerPacket(ItemReplacerPacket.class);
//    }

}