package com.builtbroken.icbm.content;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.lib.render.model.loader.EngineModelLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

/**
 * Reference class for holding all model and textures using for rendering. Mainly to avoid
 * loading this stuff up server side my mistake. While still ensuring all references are
 * static and final.
 * Created by robert on 3/1/2015.
 */
@SideOnly(Side.CLIENT)
public final class Assets
{
    //Models
    public static final IModelCustom MICRO_MISSILE_MODEL = model("missile_micro.tcn");
    public static final IModelCustom SMALL_MISSILE_MODEL = model("Missile_Small.obj");
    public static final IModelCustom CLASSIC_MISSILE_MODEL = model("missile_conventional.tcn");
    public static final IModelCustom PORTABLE_LAUNCHER_MODEL = model("small_launcher.tcn");
    public static final IModelCustom RPG_MODEL = model("rocketLauncher.tcn");

    //Textures
    public static final ResourceLocation GREY_FAKE_TEXTURE = texture("grey");
    public static final ResourceLocation MICRO_MISSILE_TEXTURE = texture("missile.micro.default");
    public static final ResourceLocation CLASSIC_MISSILE_TEXTURE = texture("missile_condensed");
    public static final ResourceLocation RPG_TEXTURE = texture("rocketLauncher");


    public static IModelCustom model(String name)
    {
        return EngineModelLoader.loadModel(new ResourceLocation(ICBM.DOMAIN, References.MODEL_PATH + name));
    }

    public static ResourceLocation texture(String name)
    {
        return new ResourceLocation(ICBM.DOMAIN, "textures/models/" + name + ".png");
    }
}
