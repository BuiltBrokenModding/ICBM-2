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
 * Created by robert on 12/29/2014.
 */
public class MissileMicro extends Missile implements ICustomMissileRender
{

    public MissileMicro(ItemStack stack)
    {
        super(stack, MissileCasings.MICRO);
    }

    @Override @SideOnly(Side.CLIENT)
    public boolean renderMissileItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object... data)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.MICRO_MISSILE_TEXTURE);
        GL11.glScalef(.0015625f, .0015625f, .0015625f);
        Assets.MICRO_MISSILE_MODEL.renderAll();
        return true;
    }

    @Override @SideOnly(Side.CLIENT)
    public boolean renderMissileInWorld()
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.MICRO_MISSILE_TEXTURE);
        GL11.glScalef(.0015625f, .0015625f, .0015625f);
        Assets.MICRO_MISSILE_MODEL.renderAll();
        return true;
    }
}
