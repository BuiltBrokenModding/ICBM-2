package com.builtbroken.icbm.client;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.lib.render.model.loader.EngineModelLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.WavefrontObject;

/**
 * Reference class for holding all model and textures used for rendering.
 *
 * Created by Dark (DarkGuardsman, Robert) on 3/1/2015.
 */
@SideOnly(Side.CLIENT)
public final class Assets
{
    //Missile models
    public static final IModelCustom MICRO_MISSILE_MODEL = model("Rocket_Micro.obj");
    public static final IModelCustom SMALL_MISSILE_MODEL = model("Missile_Small.obj");
    public static final IModelCustom STANDARD_MISSILE_MODEL = model("Missile_Standard.obj");
    public static final IModelCustom CLASSIC_MISSILE_MODEL = model("missile_conventional.tcn");

    //Crafting missile models
    public static final IModelCustom SMALL_MISSILE_MODEL_2 = model("Missile_Small_scale.obj"); //Segmented model for crafting
    public static final WavefrontObject STANDARD_MISSILE_MODEL_2 = (WavefrontObject)model("Missile_Standard_Crafting.obj"); //Segmented model for crafting

    //Launcher Models
    public static final IModelCustom PORTABLE_LAUNCHER_MODEL = model("small_launcher.tcn");
    public static final IModelCustom SMALL_SILO_MODEL = model("SmallSilo.tcn");
    public static final IModelCustom STANDARD_SILO_MODEL = model("StandardSilo.obj");

    //Weapon Models
    public static final IModelCustom RPG_MODEL = model("rocketLauncher.tcn");

    //Machine Models
    public static final IModelCustom LAUNCHER_CONTROLLER_MODEL = model("LauncherController.tcn");
    public static final IModelCustom WEAPON_CASE_MODEL = model("WeaponCase.tcn");
    public static final IModelCustom SMALL_MISSILE_STATION_MODEL = model("smallMissileStation.obj");
    public static final IModelCustom SMALL_WARHEAD_STATION_MODEL = model("smallWarheadStation.obj");

    //Block Models
    public static final IModelCustom LAUNCHER_FRAME_BLOCK_MODEL = model("launchertowerblock.obj");

    //Textures
    public static final ResourceLocation GREY_FAKE_TEXTURE = texture("grey");
    public static final ResourceLocation MICRO_MISSILE_TEXTURE = texture("Rocket_Micro");
    public static final ResourceLocation SMALL_MISSILE_TEXTURE = texture("Missile_Small");
    public static final ResourceLocation CLASSIC_MISSILE_TEXTURE = texture("missile_condensed");
    public static final ResourceLocation STANDARD_MISSILE_TEXTURE = texture("missile_standard/missile_standard");
    public static final ResourceLocation STANDARD_MISSILE_FINS_TEXTURE = texture("missile_standard/missile_standard_fins");
    public static final ResourceLocation RPG_TEXTURE = texture("rocketLauncher");
    public static final ResourceLocation LAUNCHER_CONTROLLER_TEXTURE = texture("LauncherController");
    public static final ResourceLocation WEAPON_CASE_TEXTURE = texture("WeaponCase");
    public static final ResourceLocation STANDARD_SILO_TEXTURE = texture("StandardSilo");


    public static IModelCustom model(String name)
    {
        return EngineModelLoader.loadModel(new ResourceLocation(ICBM.DOMAIN, References.MODEL_PATH + name));
    }

    public static ResourceLocation texture(String name)
    {
        return new ResourceLocation(ICBM.DOMAIN, "textures/models/" + name + ".png");
    }
}
