package com.webb.easywiring.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
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
public class ItemReplacerScreen extends Screen {
	
	public int sizeFactor = 5;
	
    protected ItemReplacerScreen() 
    {
    	super(new StringTextComponent("Title"));
    }
  

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        
        int canvasStartX = width / sizeFactor;
        int canvasStartY = height / sizeFactor;
        
        this.addButton(new Button(canvasStartX + 10, canvasStartY + 80, 50, 20,
                new StringTextComponent("button test"), (button) -> {
		            ClientPlayerEntity player = minecraft.player;
		
		            if (player == null)
		                return;
		
		            button.setMessage(new StringTextComponent("Changed"));
                }
        ));
        
        TextFieldWidget textWidget = new TextFieldWidget(font, 0, 0, 160, 20, new StringTextComponent("Numeric Input"));
        textWidget.setBordered(true);
        this.addWidget(textWidget);
        
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    	int canvasStartX = width / sizeFactor;
        int canvasStartY = height / sizeFactor;
        
    	drawBackground(matrixStack);
    	
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        
        drawString(matrixStack, font, new StringTextComponent("block 1"), canvasStartX + 10, canvasStartY + 40, TextFormatting.WHITE.getColor());
    }
    
    public void drawBackground(MatrixStack matrixStack)
    {
    	int x1 = this.width / sizeFactor;
        int y1 = this.height / sizeFactor;
        int x2 = this.width - x1;
        int y2 = this.height - y1;
        
        fill(matrixStack, x1, y1, x2, y2, 0xff_353535);
    }

}

//        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
//        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
//        bufferbuilder.ver.endVertex();
//        bufferbuilder.vertex(p_238460_0_, (float)p_238460_3_, (float)p_238460_4_, 0.0F).color(f, f1, f2, f3).endVertex();
//        bufferbuilder.end();
//        WorldVertexBufferUploader.end(bufferbuilder);