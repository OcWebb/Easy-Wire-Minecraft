package com.webb.easywiring.common.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.webb.easywiring.common.util.Node;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;

public class IOverlayRenderable implements Comparable<IOverlayRenderable>
{
    Vector3f position;
    double distanceToPlayer;

    public IOverlayRenderable (Vector3f position)
    {
        position = position;
        Player player = Minecraft.getInstance().player;
        if (player != null)
        {
            distanceToPlayer = player.distanceToSqr(position.x(), position.y(), position.z());
        }
    }

    public void render(MultiBufferSource.BufferSource buffer, PoseStack mstack)
    {

    }

    @Override
    public int compareTo(IOverlayRenderable b)
    {
        return this.distanceToPlayer < b.distanceToPlayer ? 1 : (this.distanceToPlayer > b.distanceToPlayer) ? -1 : 0;
    }

}
