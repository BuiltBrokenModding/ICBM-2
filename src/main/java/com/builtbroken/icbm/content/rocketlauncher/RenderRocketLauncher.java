package com.builtbroken.icbm.content.rocketlauncher;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.Assets;
import com.builtbroken.mc.lib.render.model.loader.EngineModelLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderRocketLauncher implements IItemRenderer
{

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return item.getItem() == ICBM.itemRocketLauncher;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return item.getItem() == ICBM.itemRocketLauncher;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        GL11.glPushMatrix();

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.RPG_TEXTURE);

        if (type == ItemRenderType.INVENTORY)
        {
            GL11.glTranslatef(-0.1f, 0.1f, 0f);
            GL11.glRotatef(90, 0, 1, 0);
            GL11.glScalef(0.75f, 0.75f, 0.75f);
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            GL11.glRotatef(-75, 1, 0, 0);
            GL11.glRotatef(30, 0, 0, 1);
            GL11.glRotatef(20, 0, 1, 0);
            GL11.glTranslatef(0.2f, -0.1f, -0f);
            GL11.glScalef(2, 2, 2);
        }
        else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glTranslatef(-0.4f, 1.3f, 1f);
            GL11.glRotatef(-30, 0, 1, 0);
            GL11.glRotatef(13, 1, 0, 0);
            GL11.glScaled(1.8f, 1.8f, 1.8f);
        }
        else if (type == ItemRenderType.ENTITY)
        {
            GL11.glTranslatef(0, 0.3f, 0);
        }

        Assets.RPG_MODEL.renderAll();

        GL11.glPopMatrix();
    }
}
