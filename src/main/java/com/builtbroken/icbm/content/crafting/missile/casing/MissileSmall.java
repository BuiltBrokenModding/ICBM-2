package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.api.ICustomMissileRender;
import com.builtbroken.icbm.content.Assets;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created by robert on 12/29/2014.
 */
public class MissileSmall extends Missile implements ICustomMissileRender
{

    public MissileSmall(ItemStack stack)
    {
        super(stack, MissileCasings.SMALL);
    }

    @Override @SideOnly(Side.CLIENT)
    public boolean renderMissileItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object... data)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_MISSILE_TEXTURE);
        GL11.glScalef(.0015625f, .0015625f, .0015625f);
        switch(type)
        {
            case INVENTORY: GL11.glTranslatef(-1.5f, -1.5f, 0);break;
        }
        Assets.SMALL_MISSILE_MODEL.renderAll();
        return true;
    }

    @Override @SideOnly(Side.CLIENT)
    public boolean renderMissileInWorld()
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_MISSILE_TEXTURE);
        GL11.glScalef(.0015625f, .0015625f, .0015625f);
        Assets.SMALL_MISSILE_MODEL.renderAll();
        return true;
    }
}
