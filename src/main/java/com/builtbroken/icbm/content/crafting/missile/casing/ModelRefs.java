package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.ICBM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

/**
 * Created by robert on 3/1/2015.
 */
@SideOnly(Side.CLIENT)
public class ModelRefs
{
    public static final IModelCustom MICRO_MISSILE_MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(ICBM.DOMAIN, ICBM.MODEL_PREFIX + "missile_micro.tcn"));
    public static final IModelCustom SMALL_MISSILE_MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(ICBM.DOMAIN, ICBM.MODEL_PREFIX + "Missile_Small.obj"));

    public static final  ResourceLocation GREY_FAKE_TEXTURE = new ResourceLocation(ICBM.DOMAIN, ICBM.MODEL_TEXTURE_PATH + "grey.png");
    public static final  ResourceLocation MICRO_MISSILE_TEXTURE = new ResourceLocation(ICBM.DOMAIN, ICBM.MODEL_TEXTURE_PATH + "missile.micro.default.png");


}
