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
import java.util.function.Predicate;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class PipePlacerScreen extends Screen 
{
	
	public int sizeFactor = 5;
	public GuiGrid grid;
	public GuiGrid subGrid;
	
    protected PipePlacerScreen() 
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
        grid = new GuiGrid(4, 3, canvasStartX, canvasStartY, width - canvasStartX, height - canvasStartY);
        
        GridButton b1 = new GridButton(grid, 2, 2, new StringTextComponent("Click Me"), 
        		(button) -> {
        			ClientPlayerEntity player = minecraft.player;
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
        this.addButton(b1);
        
        GridButton b2 = new GridButton(grid, 1, 1, new StringTextComponent("Button"), 
        		(button) -> {
        			ClientPlayerEntity player = minecraft.player;
        			
        			if (player == null)
        				return;
        		},
        		0xff_d300e2,
        		2);
        this.addButton(b2);
        
        
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) 
    {
    	this.grid.render(matrixStack, this.minecraft.font);
    	this.t.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}




//TextFieldWidget t = new TextFieldWidget(this.minecraft.font, grid.getX(1), grid.getY(2), 50, 20, (TextFieldWidget)null, new StringTextComponent("text field"));
//
//Predicate<String> onlyNumeric = (s -> s.matches("^[1-9]\\d*$"));
//
//t.setFilter(onlyNumeric);
//
//this.addWidget(this.t);



//        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
//        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
//        bufferbuilder.ver.endVertex();
//        bufferbuilder.vertex(p_238460_0_, (float)p_238460_3_, (float)p_238460_4_, 0.0F).color(f, f1, f2, f3).endVertex();
//        bufferbuilder.end();
//        WorldVertexBufferUploader.end(bufferbuilder);