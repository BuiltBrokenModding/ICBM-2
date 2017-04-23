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
 * <p>
 * Created by Dark (DarkGuardsman, Robert) on 3/1/2015.
 */
@SideOnly(Side.CLIENT)
public final class Assets
{
    //Missile models
    public static final WavefrontObject MICRO_MISSILE_MODEL = (WavefrontObject) model("missile/micro.obj");
    public static final WavefrontObject SMALL_MISSILE_MODEL = (WavefrontObject) model("missile/Missile_Small_scale.obj");
    public static final WavefrontObject STANDARD_MISSILE_MODEL = (WavefrontObject) model("missile/Missile_Standard.obj");
    public static final IModelCustom CLASSIC_MISSILE_MODEL = model("missile/missile_conventional.tcn");

    //Crafting missile models
    public static final WavefrontObject STANDARD_MISSILE_MODEL_2 = (WavefrontObject) model("missile/Missile_Standard_Crafting.obj"); //Segmented model for crafting

    //Launcher Models
    public static final IModelCustom SMALL_SILO_MODEL = model("smallSilo/small-missile-silo.obj");

    //Mag
    public static final WavefrontObject SMALL_MAG_MODEL = (WavefrontObject) model("mag/small.mag.obj");

    //Weapon Models
    public static final IModelCustom RPG_MODEL = model("rocketLauncher.tcn");

    //Machine Models
    //public static final WavefrontObject SMALL_MISSILE_STATION_MODEL = (WavefrontObject) model("small-missile-workstation.obj"); TODO update old model
    public static final WavefrontObject SMALL_MISSILE_STATION_MODEL2 = (WavefrontObject) model("workstation.missile.small.obj");
    public static final WavefrontObject FoF_STATION_MODEL = (WavefrontObject) model("fof/fofStation.obj");

    //Block Models
    public static final WavefrontObject LAUNCHER_FRAME_BLOCK_MODEL = (WavefrontObject) model("standardLauncher/reg-missile-station-base.obj");
    public static final WavefrontObject LAUNCHER_FRAME_BLOCK_TOP_MODEL = (WavefrontObject) model("standardLauncher/reg-missile-station-top.obj");

    //Antenna models
    public static final WavefrontObject ANTENNA_TOWER_MODEL = (WavefrontObject) model("antenna/AntennaTower.obj");
    public static final WavefrontObject ANTENNA_PIKE_MODEL = (WavefrontObject) model("antenna/AntennaPike.obj");
    public static final WavefrontObject ANTENNA_NOTCH_MODEL = (WavefrontObject) model("antenna/AntennaNotch.obj");
    public static final WavefrontObject ANTENNA_INTERSECTION_MODEL = (WavefrontObject) model("antenna/AntennaIntersection.obj");
    public static final WavefrontObject ANTENNA_BASE_MODEL = (WavefrontObject) model("antenna/AntennaBase.obj");
    public static final WavefrontObject ANTENNA_ARM_MODEL = (WavefrontObject) model("antenna/AntennaArm.obj");
    public static final WavefrontObject ANTENNA_ARM2_MODEL = (WavefrontObject) model("antenna/AntennaArm2.obj");

    //Carts
    public static final WavefrontObject CART1x3 = (WavefrontObject) model("carts/ThreeByOneMissileCart.obj");
    public static final WavefrontObject CART1x1 = (WavefrontObject) model("carts/1x1missiletransporter.obj");
    public static final WavefrontObject CART3x3 = (WavefrontObject) model("carts/3x3missiletransporter.obj");

    //Textures
    public static final ResourceLocation GREY_FAKE_TEXTURE = texture("grey");

    public static final ResourceLocation MICRO_MISSILE_TEXTURE = texture("Rocket_Micro");
    public static final ResourceLocation SMALL_MISSILE_TEXTURE = texture("Missile_Small");
    public static final ResourceLocation CLASSIC_MISSILE_TEXTURE = texture("missile_condensed");
    public static final ResourceLocation STANDARD_MISSILE_TEXTURE = texture("missile_standard/missile_standard");
    public static final ResourceLocation STANDARD_MISSILE_FINS_TEXTURE = texture("missile_standard/missile_standard_fins");

    public static final ResourceLocation RPG_TEXTURE = texture("rocketLauncher");

    public static final ResourceLocation LAUNCHER_FRAME_TEXTURE = texture("reg-missile-station");

    public static final ResourceLocation FoF_STATION_TEXTURE = texture("console-fof");

    public static final ResourceLocation ANTENNA_TEXTURE = texture("antenna");

    public static final ResourceLocation SMALL_WORKSTATION_TEXTURE = texture("small-missile-workstation");
    public static final ResourceLocation SMALL_WORKSTATION_TEXTURE2 = texture("workstation.missile.small");

    public static final ResourceLocation SMALL_SILO_TEXTURE = texture("small-missile-silo");
    public static final ResourceLocation SMALL_MAG_TEXTURE = texture("small-missile-mag");

    //GUI
    public static final ResourceLocation GUI_BUTTONS = new ResourceLocation(ICBM.DOMAIN, "textures/gui/gui.buttons.32pix.png");

    public static IModelCustom model(String name)
    {
        return EngineModelLoader.loadModel(new ResourceLocation(ICBM.DOMAIN, References.MODEL_PATH + name));
    }

    public static ResourceLocation texture(String name)
    {
        return new ResourceLocation(ICBM.DOMAIN, "textures/models/" + name + ".png");
    }
}
