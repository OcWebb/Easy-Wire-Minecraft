package com.webb.easywiring.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.webb.easywiring.core.util.GridButton;
import com.webb.easywiring.core.util.GridCell;
import com.webb.easywiring.core.util.GuiGrid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraft.client.gui.FontRenderer;

import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ItemReplacerScreen extends Screen 
{
	
	public int sizeFactor = 5;
	public GuiGrid grid;
	public GuiGrid subGrid;
	
    protected ItemReplacerScreen() 
    {
    	super(new StringTextComponent("Title"));
    }
  

    @Override
    public boolean isPauseScreen() 
    {
        return false;
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) 
    {
        super.init(minecraft, width, height);
        
        int canvasStartX = width / sizeFactor;
        int canvasStartY = height / sizeFactor;
        grid = new GuiGrid(5, 3, canvasStartX, canvasStartY, width - canvasStartX, height - canvasStartY);
        subGrid = new GuiGrid(grid.getCell(2, 2), 4, 4);
        
        /*
        subGrid = new GuiGrid(2, 2, grid.getX(2), grid.getY(2), grid.getX(2) + grid.getWidth(), grid.getY(2) + grid.getHeight(), 0);
        
        GridButton b1 = new GridButton(grid, 1, 2, new StringTextComponent("One"), 
			        		(button) -> {
			        			ClientPlayerEntity player = minecraft.player;
			        			
			        			if (player == null)
			        				return;
			        			
			        			button.setMessage(new StringTextComponent("1"));
			        		});
        this.addButton(b1);
        
        TextFieldWidget textWidget = new TextFieldWidget(font, 0, 0, 160, 20, new StringTextComponent("Numeric Input"));
        this.addWidget(textWidget);
        */
        
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) 
    {
    	this.grid.render(matrixStack, this.font);
    	this.subGrid.render(matrixStack, this.font);
    	
    	for (int r = 1; r <= grid.numRows; r++)
    	{
    		for (int c = 1; c <= grid.numColumns; c++)
    		{
    			
    			if ((r+c) % 2 == 0)
    			{
    				grid.fillCell(matrixStack, r, c, 0xff_00bfff);
    			}
    			String coords = r + "-" + c;
    			grid.drawString(matrixStack, font, coords, r, c, 0.3f);
    		}
    	}
    	
    	grid.drawGridLines(matrixStack);
    	subGrid.drawGridLines(matrixStack);
    	
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}







//        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
//        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
//        bufferbuilder.ver.endVertex();
//        bufferbuilder.vertex(p_238460_0_, (float)p_238460_3_, (float)p_238460_4_, 0.0F).color(f, f1, f2, f3).endVertex();
//        bufferbuilder.end();
//        WorldVertexBufferUploader.end(bufferbuilder);