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
import net.minecraft.util.text.ITextComponent;
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
		drawCoords(matrixStack);
		drawGridLines(matrixStack);
		drawCornerAccents(matrixStack);
	}
	
	public void drawCoords (MatrixStack matrixStack)
	{
		for (int c = 1; c <= this.numColumns; c++)
    	{
        	for (int r = 1; r <= this.numRows; r++)
    		{
    			
    			if ((r+c) % 2 == 0)
    			{
    				this.fillCell(matrixStack, r, c, 0xff_00bfff);
    			}
    		}
    	}
	}
	
	public void drawString (MatrixStack matrixStack, FontRenderer font, String text, int row, int column, float scale)
	{
		float centeredX = (this.getX(row) + this.getWidth()/2);
		float centeredY = (this.getY(column) + this.getHeight()/2) - font.lineHeight/2;
		
		Screen.drawCenteredString(matrixStack, font, new StringTextComponent(text), (int) centeredX, (int) centeredY, TextFormatting.WHITE.getColor());
	}
	
	public void drawString (MatrixStack matrixStack, FontRenderer font, ITextComponent text, int row, int column, float scale)
	{
		float centeredX = (this.getX(row) + this.getWidth()/2);
		float centeredY = (this.getY(column) + this.getHeight()/2) - font.lineHeight/2;
		
		Screen.drawCenteredString(matrixStack, font, text, (int) centeredX, (int) centeredY, TextFormatting.WHITE.getColor());
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
		int gridStartX = this.x1 + this.edgeOffsetX;
		int gridStartY = this.y1 + this.edgeOffsetY;
		int gridEndX = this.x2 - this.edgeOffsetX;
		int gridEndY = this.y2 - this.edgeOffsetY;
		
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
	}
	
	
	public void drawBackground(MatrixStack matrixStack)
    {
        Screen.fill(matrixStack, this.getX(1), this.getY(1), this.getX(this.numColumns+1), this.getY(this.numRows+1), 0xff_353535);
    }
	
	public void drawCornerAccents (MatrixStack matrixStack)
	{
		int gridStartX = this.x1 + this.edgeOffsetX;
		int gridStartY = this.y1 + this.edgeOffsetY;
		int gridEndX = this.x2 - this.edgeOffsetX;
		int gridEndY = this.y2 - this.edgeOffsetY;
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