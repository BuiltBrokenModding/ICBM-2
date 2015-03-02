package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICustomMissileRender;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
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
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelRefs.MICRO_MISSILE_TEXTURE);
        GL11.glScalef(.5f, .5f, .5f);
        ModelRefs.MICRO_MISSILE_MODEL.renderAll();
        return true;
    }

    @Override @SideOnly(Side.CLIENT)
    public boolean renderMissileInWorld()
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelRefs.MICRO_MISSILE_TEXTURE);
        GL11.glScalef(.5f, .5f, .5f);
        ModelRefs.MICRO_MISSILE_MODEL.renderAll();
        return true;
    }
}
