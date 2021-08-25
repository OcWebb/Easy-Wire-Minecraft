package com.webb.easywiring.core.util;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class GuiGrid 
{
	public int numColumns;
	public int numRows;
	public int x1, y1;
	public int x2, y2;
	public int gridSizeX, gridSizeY;
	public int edgeOffsetX, edgeOffsetY;
	public int primaryColor = 0xff_ffffff;
	public int accentColor = 0xff_000000;
	
	GridCell[][] grid;
	
	public GuiGrid(GridCell cell, int numRows, int numColumns)
	{
		this(numRows, numColumns, cell.x, cell.y, cell.x + cell.width, cell.y + cell.height);
	}
	
	public GuiGrid(int numRows, int numColumns, int x1, int y1, int x2, int y2)
	{
		this.numColumns = numColumns;
		this.numRows = numRows;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		
		this.gridSizeX = x2 - x1;
		this.gridSizeY = y2 - y1;
		int columnSize = (gridSizeX / numColumns);
		int rowSize = (gridSizeY / numRows);
		this.edgeOffsetX = (gridSizeX - (columnSize * numColumns))/2;
		this.edgeOffsetY = (gridSizeY - (rowSize * numRows)) / 2;
		
		this.grid = new GridCell[numRows][numColumns];
		
		for (int r = 0; r < numRows; r++)
		{
			for (int c = 0; c < numColumns; c++)
			{
				grid[r][c] = new GridCell (getX(c+1), getY(r+1), getWidth(), getHeight());
			}
		}
	}
	
	
	public void addButton (int row, int column, Widget button)
	{
		getCell(row, column).setWidget(button);
	}
	
	
	public void render (MatrixStack matrixStack, FontRenderer font)
	{
		drawBackground(matrixStack);
		drawGridLines(matrixStack);
	}
	
	
	public void drawString (MatrixStack matrixStack, FontRenderer font, String text, int row, int column, float scale)
	{
		float centeredX = (getX(row) + getWidth()/2) / scale;
		float centeredY = (getY(column)+ getHeight()/2) / scale;
		
		GL11.glPushMatrix(); //Start new matrix
		GL11.glScalef(scale, scale, 0);
		GL11.glTranslatef(centeredX, centeredY - font.lineHeight/2, 0);
		Screen.drawCenteredString(matrixStack, font, new StringTextComponent(text), 0, 0, TextFormatting.WHITE.getColor());
		GL11.glPopMatrix();
	}
	
	public void fillCell (MatrixStack matrixStack, int row, int column, int color)
	{
		GridCell cell = getCell(row, column);
		Screen.fill(matrixStack, cell.x, cell.y, cell.x + cell.width, cell.y + cell.height, color);
	}
	
	public void fillCell (MatrixStack matrixStack, GridCell cell, int color)
	{
		Screen.fill(matrixStack, cell.x, cell.y, cell.x + cell.width, cell.y + cell.height, color);
	}
	
	
	public void drawGridLines(MatrixStack matrixStack)
	{
		int lineThickness = 2;
		int offset = lineThickness/2;
		int gridStartX = x1 + edgeOffsetX;
		int gridStartY = y1 + edgeOffsetY;
		int gridEndX = x2 - edgeOffsetX;
		int gridEndY = y2 - edgeOffsetY;
		
		for (int r = 1; r < numRows+2; r++)
		{
			Screen.fill(matrixStack, 
					gridStartX, 
					getY(r) - offset, 
					gridEndX, 
					getY(r) + offset, 
					primaryColor);
		}
		
		for (int c = 1; c < numColumns+2; c++)
		{
			Screen.fill(matrixStack, 
					getX(c) - offset, 
					gridStartY, 
					getX(c) + offset, 
					gridEndY, 
					primaryColor);
		}
		
		// draw squares in corners
		int squareSize = 6;
		
		//	* - -
		//	- - -	
		//	- - -
		Screen.fill(matrixStack, 
				gridStartX - squareSize, 
				gridStartY - squareSize, 
				gridStartX + squareSize, 
				gridStartY + squareSize, 
				primaryColor);
		
		Screen.fill(matrixStack, 
				gridStartX - squareSize/2, 
				gridStartY - squareSize/2, 
				gridStartX + squareSize/2, 
				gridStartY + squareSize/2, 
				accentColor);
		
		//	- - *
		//	- - -	
		//	- - -
		Screen.fill(matrixStack, 
				gridEndX - squareSize, 
				gridStartY - squareSize, 
				gridEndX + squareSize, 
				gridStartY + squareSize, 
				primaryColor);
		
		Screen.fill(matrixStack, 
				gridEndX - squareSize/2, 
				gridStartY - squareSize/2, 
				gridEndX + squareSize/2, 
				gridStartY + squareSize/2, 
				accentColor);
		
		//	- - -
		//	- - -	
		//	- - *
		Screen.fill(matrixStack, 
				gridEndX - squareSize, 
				gridEndY - squareSize, 
				gridEndX + squareSize, 
				gridEndY + squareSize, 
				primaryColor);
		
		Screen.fill(matrixStack, 
				gridEndX - squareSize/2, 
				gridEndY - squareSize/2, 
				gridEndX + squareSize/2, 
				gridEndY + squareSize/2, 
				accentColor);
		
		//	- - -
		//	- - -	
		//	* - -
		Screen.fill(matrixStack, 
				gridStartX - squareSize, 
				gridEndY - squareSize, 
				gridStartX + squareSize, 
				gridEndY + squareSize, 
				primaryColor);
		
		Screen.fill(matrixStack, 
				gridStartX - squareSize/2, 
				gridEndY - squareSize/2, 
				gridStartX + squareSize/2, 
				gridEndY + squareSize/2, 
				accentColor);
		
	}
	
	
	public void drawBackground(MatrixStack matrixStack)
    {
        Screen.fill(matrixStack, this.getX(1), this.getY(1), this.getX(this.numColumns+1), this.getY(this.numRows+1), 0xff_353535);
    }
	
	
	public GridCell getCell(int row, int column)
	{
		return grid[row-1][column-1];
	}
	
	
	public int getX (int row)
	{
		row = row - 1;
		int rowSizeX = this.gridSizeX / this.numColumns;
		
		return this.edgeOffsetX + this.x1 + rowSizeX * row;
	}
	
	public int getY (int column)
	{
		column = column - 1;
		int rowSizeY = this.gridSizeY / this.numRows;
		
		return rowSizeY * column + this.y1 + this.edgeOffsetY;
	}
	
	public int getWidth ()
	{
		int screenSizeX = x2 - x1;
		
		return screenSizeX/numColumns;
	}
	
	public int getHeight ()
	{
		int screenSizeY = y2 - y1;
		
		return screenSizeY/numRows;
	}
}