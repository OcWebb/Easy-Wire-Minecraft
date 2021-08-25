package com.webb.easywiring.core.util;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class GridButton extends Button {

	public GridButton(GuiGrid grid, int row, int column, ITextComponent text, IPressable onPress) 
	{	
		super(grid.getX(row), grid.getY(column), grid.getWidth(), grid.getHeight(), text, onPress);
		System.out.println("grid.getX(row), grid.getY(column), grid.getWidth(), grid.getHeight()");
		System.out.println(grid.getX(row) + "  " + grid.getY(column) + "  " + grid.getWidth() + "  " + grid.getHeight());
	}

}
