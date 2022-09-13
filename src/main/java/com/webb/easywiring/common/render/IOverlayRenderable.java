package com.webb.easywiring.common.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.webb.easywiring.common.util.Node;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.system.CallbackI;

public class IOverlayRenderable implements Comparable<IOverlayRenderable>
{
    public Vector3f position;
    public double distanceToPlayer = Double.POSITIVE_INFINITY;

    public IOverlayRenderable (Vector3f _position)
    {
        position = _position;
        _updateDistance();
    }
    private void _updateDistance()
    {
        Player player = Minecraft.getInstance().player;
        if (player != null)
        {
            distanceToPlayer = player.distanceToSqr(position.x(), position.y(), position.z());
        }
    }

    public void render(MultiBufferSource.BufferSource buffer, PoseStack mstack)
    {
        _updateDistance();
    }

    @Override
    public int compareTo(IOverlayRenderable b)
    {
        return Double.compare(b.distanceToPlayer, this.distanceToPlayer);
    }

}
