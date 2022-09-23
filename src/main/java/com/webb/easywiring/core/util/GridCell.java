package com.webb.easywiring.core.util;

import net.minecraft.client.gui.components.Widget;

public class GridCell 
{
	public int x;
	public int y;
	public int width;
	public int height;
	private Widget widget;
	
	
	public GridCell(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void setWidget(Widget newWidget)
	{
		this.widget = newWidget;
	}
	
	public Widget getWidget()
	{
		return this.widget;
	}
	
//	public void fill(int color)
//	{
//		Screen.fill(matrixStack, this.getX(c) - offset, this.y1+this.edgeOffsetY, this.getX(c) + offset, this.y2-this.edgeOffsetY, color);
//	}

}
