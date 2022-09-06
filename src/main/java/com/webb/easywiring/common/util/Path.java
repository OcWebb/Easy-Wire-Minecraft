package com.webb.easywiring.common.util;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;

public class Path
{
    private ArrayList<Node> nodes;

    public Path(ArrayList<Node> nodesIn)
    {
        nodes = nodesIn;
    }

    public Path()
    {
        nodes = new ArrayList<Node>();
    }

    public Node getHead()
    {
        return nodes.get(0);
    }

    public Node getTail()
    {
        return nodes.get(nodes.size()-1);
    }

    public ArrayList<Node> getNodes()
    {
        return nodes;
    }

    public ArrayList<BlockPos> getBlockPosArray()
    {
        ArrayList<BlockPos> blockPosArray = new ArrayList<BlockPos>();
        nodes.forEach(node -> blockPosArray.add(node.blockPos));

        return blockPosArray;
    }

    public void addNodeHead (Node node)
    {
        addNode(0, node);
    }

    public void addNodeTail (Node node)
    {
        addNode(nodes.size()-1, node);
    }

    public void addNode(int position, Node node)
    {
        nodes.add(position, node);
    }

    public int size()
    {
        return nodes.size();
    }

    public void clear()
    {
        nodes.clear();
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

    public boolean isValid()
    {
        for (Node node : nodes)
        {
            BlockPos parentExpectedBlock = node.parent.blockPos.relative(node.getDirectionToParent());

            if (node.blockPos.asLong() != parentExpectedBlock.asLong())
            {
                return false;
            }
        }

        return true;
    }

}
