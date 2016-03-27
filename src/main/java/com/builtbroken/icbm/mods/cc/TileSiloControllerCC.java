package com.builtbroken.icbm.mods.cc;

import com.builtbroken.icbm.content.launcher.controller.direct.TileSiloController;
import com.builtbroken.mc.prefab.tile.Tile;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileSiloControllerCC extends TileSiloController
{
    @Override
    public Tile newTile(World world, int meta)
    {
        return new TileSiloControllerCC();
    }
}
