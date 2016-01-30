package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.api.ICustomMissileRender;
import com.builtbroken.icbm.client.Assets;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Missile object that represents the small missile size
 * Created by robert on 12/29/2014.
 */
public class MissileSmall extends Missile implements ICustomMissileRender
{
    private static final float scale = .0015f;
    public MissileSmall(ItemStack stack)
    {
        super(stack, MissileCasings.SMALL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderMissileItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object... data)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_MISSILE_TEXTURE);

        GL11.glScalef(scale, scale, scale);
        if(type == IItemRenderer.ItemRenderType.INVENTORY)
        {
            GL11.glTranslatef(-100f, -80f, 200);
        }
        Assets.SMALL_MISSILE_MODEL.renderAll();
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderMissileInWorld()
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_MISSILE_TEXTURE);
        GL11.glScalef(.0015625f, .0015625f, .0015625f);
        Assets.SMALL_MISSILE_MODEL.renderAll();
        return true;
    }
}
