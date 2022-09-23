package com.webb.easywiring.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.webb.easywiring.core.util.GridButton;
import com.webb.easywiring.core.util.GridCell;
import com.webb.easywiring.core.util.GuiGrid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PipePlacerScreen extends Screen
{
	
	public int sizeFactor = 5;
	public GuiGrid grid;
	
    public PipePlacerScreen()
    {
    	super(new TextComponent("Title"));
    }
  

    @Override
    public boolean isPauseScreen() 
    {
        return false;
    }

    @Override
    public void init()
    {
        super.init();
        
        int canvasStartX = width / sizeFactor;
        int canvasStartY = height / sizeFactor;
        grid = new GuiGrid(4, 3, canvasStartX, canvasStartY, width - canvasStartX, height - canvasStartY);
        
        GridButton b1 = new GridButton(grid, 2, 2, new TextComponent("Click Me"),
        		(button) -> {
        			LocalPlayer player = minecraft.player;
        			GridButton b = (GridButton) button;
        			
        			if (player == null)
        				return;
        			
        			if (b.getColor() == 0xff_d300e2)
        			{
        				b.setColor(0xff_ff0000);
        			} else {
        				b.setColor(0xff_d300e2);
        			}
        		},
        		0xff_ff0000,
        		5);
        grid.addButton(2, 2, b1);
        
        GridButton b2 = new GridButton(grid, 1, 1, new TextComponent("Button"),
        		(button) -> {
        			LocalPlayer player = minecraft.player;
        			
        			if (player == null)
        				return;
        		},
        		0xff_d300e2,
        		2);
        grid.addButton(1, 1, b2);
        
        
    }

    @Override
    public void render(PoseStack mstack, int mouseX, int mouseY, float partialTicks)
    {
    	this.grid.render(mstack, this.minecraft.font);

        super.render(mstack, mouseX, mouseY, partialTicks);
    }
}
