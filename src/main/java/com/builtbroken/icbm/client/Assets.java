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
 * <p/>
 * Created by Dark (DarkGuardsman, Robert) on 3/1/2015.
 */
@SideOnly(Side.CLIENT)
public final class Assets
{
    //Missile models
    public static final WavefrontObject MICRO_MISSILE_MODEL = (WavefrontObject) model("Rocket_Micro.obj");
    public static final WavefrontObject SMALL_MISSILE_MODEL = (WavefrontObject) model("Missile_Small.obj");
    public static final WavefrontObject STANDARD_MISSILE_MODEL = (WavefrontObject) model("Missile_Standard.obj");
    public static final IModelCustom CLASSIC_MISSILE_MODEL = model("missile_conventional.tcn");

    //Crafting missile models
    public static final WavefrontObject SMALL_MISSILE_MODEL_2 = (WavefrontObject) model("Missile_Small_scale.obj"); //Segmented model for crafting
    public static final WavefrontObject STANDARD_MISSILE_MODEL_2 = (WavefrontObject) model("Missile_Standard_Crafting.obj"); //Segmented model for crafting

    //Launcher Models
    public static final IModelCustom PORTABLE_LAUNCHER_MODEL = model("small_launcher.tcn");
    public static final IModelCustom SMALL_SILO_MODEL = model("SmallSilo.tcn");
    public static final WavefrontObject STANDARD_SILO_MODEL = (WavefrontObject) model("StandardSilo.obj");

    //Weapon Models
    public static final IModelCustom RPG_MODEL = model("rocketLauncher.tcn");

    //Machine Models
    public static final IModelCustom LAUNCHER_CONTROLLER_MODEL = model("LauncherController.tcn");
    public static final IModelCustom WEAPON_CASE_MODEL = model("WeaponCase.tcn");
    public static final WavefrontObject SMALL_MISSILE_STATION_MODEL = (WavefrontObject) model("smallMissileStation.obj");
    public static final WavefrontObject SMALL_WARHEAD_STATION_MODEL = (WavefrontObject) model("smallWarheadStation.obj");
    public static final WavefrontObject AMS_MODEL = (WavefrontObject) model("AMS.obj");
    public static final WavefrontObject FoF_STATION_MODEL = (WavefrontObject) model("FoFComp.obj");

    //Block Models
    public static final WavefrontObject LAUNCHER_FRAME_BLOCK_MODEL = (WavefrontObject) model("Tower_Block.obj");
    public static final WavefrontObject LAUNCHER_FRAME_BLOCK_TOP_MODEL = (WavefrontObject) model("Launchertowerblockhead.obj");

    //Antenna models
    public static final WavefrontObject ANTENNA_TOWER_MODEL = (WavefrontObject) model("antenna/AntennaTower.obj");
    public static final WavefrontObject ANTENNA_PIKE_MODEL = (WavefrontObject) model("antenna/AntennaPike.obj");
    public static final WavefrontObject ANTENNA_NOTCH_MODEL = (WavefrontObject) model("antenna/AntennaNotch.obj");
    public static final WavefrontObject ANTENNA_INTERSECTION_MODEL = (WavefrontObject) model("antenna/AntennaIntersection.obj");
    public static final WavefrontObject ANTENNA_BASE_MODEL = (WavefrontObject) model("antenna/AntennaBase.obj");
    public static final WavefrontObject ANTENNA_ARM_MODEL = (WavefrontObject) model("antenna/AntennaArm.obj");

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
    public static final ResourceLocation LAUNCHER_FRAME_TEXTURE = texture("Tower_Block");
    public static final ResourceLocation LAUNCHER_FRAME_TOP_TEXTURE = texture("Launchertowerblockhead");
    public static final ResourceLocation AMS_TOP_TEXTURE = texture("AMSTop");
    public static final ResourceLocation AMS_TEXTURE = texture("AMSBottom");
    public static final ResourceLocation FoF_STATION_TEXTURE = texture("FoFComp");
    public static final ResourceLocation ANTENNA_TEXTURE = texture("antenna");

    public static IModelCustom model(String name)
    {
        return EngineModelLoader.loadModel(new ResourceLocation(ICBM.DOMAIN, References.MODEL_PATH + name));
    }

    public static ResourceLocation texture(String name)
    {
        return new ResourceLocation(ICBM.DOMAIN, "textures/models/" + name + ".png");
    }
}
