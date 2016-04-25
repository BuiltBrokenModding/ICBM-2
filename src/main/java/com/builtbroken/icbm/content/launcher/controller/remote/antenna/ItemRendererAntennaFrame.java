package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import com.builtbroken.icbm.client.Assets;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/2/2015.
 */
public final class ItemRendererAntennaFrame implements IItemRenderer
{
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        int meta = item.getItemDamage();
        GL11.glPushMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glTranslatef(-0.0F, -0.5F, 0.0F);
        if (meta == 0 || meta == 1)
        {
            //TODO if meta == 1 change texture to say its connected to a base
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_TOWER_MODEL.renderAll();
        }
        else if (meta == 2)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_BASE_MODEL.renderAll();
        }
        else if (meta == 3)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_INTERSECTION_MODEL.renderAll();
        }
        else if (meta == 4)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_NOTCH_MODEL.renderAll();
        }
        else if (meta == 5)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_PIKE_MODEL.renderAll();
        }
        else if (meta == 6) //East West
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_ARM2_MODEL.renderAll();
        }
        else if (meta == 7) //North South
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.ANTENNA_TEXTURE);
            Assets.ANTENNA_ARM_MODEL.renderAll();
        }
        GL11.glPopMatrix();
    }
}
