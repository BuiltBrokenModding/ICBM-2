package com.builtbroken.icbm.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/15/2017.
 */
public final class ICBM_API
{
    //Version # checks, injected by build system
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

    // Blocks
    public static Block blockWarhead;
    public static Block blockMissileWorkstation;

    //Blocks: Launchers
    public static Block blockLauncherFrame;

    //Blocks: Command System
    public static Block blockAntenna;
    public static Block blockCommandCentral;
    public static Block blockCommandSiloConnector;
    public static Block blockCommandSiloDisplay;
    public static Block blockDirectSiloController;

    //Blocks: Effects
    public static Block blockCake;
    public static Block blockCrashMissile;

    //Items: Tool
    public static Item itemRocketLauncher;

    //Items: Parts
    public static Item itemEngineModules;
    public static Item itemGuidanceModules;
    public static Item itemMissileParts;
    public static Item itemExplosive;
    public static Item itemExplosivePart;
    public static Item itemTrigger;

    //Items: Entity
    public static Item itemMissileCart;
    public static Item itemMissile;

    //Development only content
    public static Block blockExplosiveMarker;
}
