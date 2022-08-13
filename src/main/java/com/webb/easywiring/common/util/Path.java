package com.webb.easywiring.common.util;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;

public class Path
{
    private ArrayList<Node> nodes;


    public Path(ArrayList<Node> nodes)
    {
        nodes = new ArrayList<Node>();
    }

    public Node getHead()
    {
        return nodes.get(0);
    }
}
