package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICustomMissileRender;
import com.builtbroken.icbm.content.crafting.missile.MissileSizes;
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
public class MissileSmall extends Missile implements ICustomMissileRender
{
    @SideOnly(Side.CLIENT)
    public static final IModelCustom MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(ICBM.DOMAIN, ICBM.MODEL_PREFIX + "Missile_Small.obj"));

    @SideOnly(Side.CLIENT)
    public static final  ResourceLocation TEXTURE = new ResourceLocation(ICBM.DOMAIN, ICBM.MODEL_TEXTURE_PATH + "grey.png");

    public MissileSmall(ItemStack stack)
    {
        super(stack, MissileSizes.SMALL);
    }

    @Override
    public boolean renderMissileItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object... data)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
        GL11.glScalef(.0015625f, .0015625f, .0015625f);
        switch(type)
        {
            case INVENTORY: GL11.glTranslatef(-1.5f, -1.5f, 0);break;
        }
        MODEL.renderAll();
        return true;
    }

    @Override
    public boolean renderMissileInWorld()
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
        GL11.glScalef(.0015625f, .0015625f, .0015625f);
        MODEL.renderAll();
        return true;
    }
}
