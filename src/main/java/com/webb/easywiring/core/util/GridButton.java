package com.webb.easywiring.core.util;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class GridButton extends Button {
	
	GuiGrid grid;
	int row, column;
	int color;
	int spacing;

	public GridButton(GuiGrid grid, int row, int column, TextComponent text, Button.OnPress onPress, int color)
	{	
		this (grid, row, column, text, onPress, color, 0);
	}
	
	public GridButton(GuiGrid grid, int row, int column, TextComponent text, Button.OnPress onPress, int color, int spacing)
	{	
		super(grid.getX(row) + spacing, grid.getY(column) + spacing, grid.getWidth() - 2*spacing, grid.getHeight() - 2*spacing, text, onPress);
		this.grid = grid;
		this.row = row;
		this.column = column;
		this.color = color;
		this.spacing = spacing;
	}

	@Override
	public void renderButton(PoseStack matrixStack, int x, int y, float color)
	{
		Minecraft mc = Minecraft.getInstance();
		
		int startX = this.grid.getX(this.row) + this.spacing;
		int startY = this.grid.getY(this.column) + this.spacing;
		int endX = this.grid.getX(this.row) + this.grid.getWidth() - this.spacing;
		int endY = this.grid.getY(this.column) + this.grid.getHeight() - this.spacing;
		
		Screen.fill(matrixStack,
				startX, 
				startY, 
				endX, 
				endY, 
				this.color);
		
		this.grid.drawString(matrixStack, mc.font, this.getMessage(), this.row, this.column, 1);
		
		if (this.isHovered)
		{
	         this.renderToolTip(matrixStack, x, y);
		}
	}
	
	public void setColor (int color)
	{
		this.color = color;
	}
	
	public float getColor ()
	{
		return this.color;
	}

}
