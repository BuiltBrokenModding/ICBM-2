package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.ICBM;
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
 * Created by robert on 1/16/2015.
 */
public class MissileMicroClassic extends MissileMicro
{
    @SideOnly(Side.CLIENT)
    public static final IModelCustom MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(ICBM.DOMAIN, ICBM.MODEL_PREFIX + "missile_micro.tcn"));

    @SideOnly(Side.CLIENT)
    public static final  ResourceLocation TEXTURE = new ResourceLocation(ICBM.DOMAIN, ICBM.MODEL_TEXTURE_PATH + "missile.micro.default.png");

    public MissileMicroClassic(ItemStack stack)
    {
        super(stack);
    }

    @Override
    public boolean renderMissileItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object... data)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
        GL11.glScalef(.5f, .5f, .5f);
        MODEL.renderAll();
        return true;
    }

    @Override
    public boolean renderMissileInWorld()
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
        GL11.glScalef(.5f, .5f, .5f);
        MODEL.renderAll();
        return true;
    }
}
