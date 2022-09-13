package com.webb.easywiring.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

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

    public boolean addNodeHead (Node node, Direction direction)
    {
        Node headNode = getHead();

        BlockPos newBlock = headNode.blockPos.relative(direction);

        if (newBlock.asLong() != node.blockPos.asLong())
        {
            return false;
        }

        addNode(0, node);
        return true;
    }

    public boolean addNodeTail (Node node, Direction direction)
    {
        Node tailNode = getTail();
        BlockPos newBlock = tailNode.blockPos.relative(direction);

        if (newBlock.asLong() != node.blockPos.asLong())
        {
            return false;
        }

        addNode(0, node);
        return true;
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

    public void returnPathToPreviousBlockState()
    {
        Level world = Minecraft.getInstance().level;
        for (Node node : nodes)
        {
            world.setBlock(node.blockPos, node.replacedBlockState, 1);
        }
    }

}
