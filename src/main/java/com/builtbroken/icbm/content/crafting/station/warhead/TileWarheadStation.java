package com.builtbroken.icbm.content.crafting.station.warhead;

import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import net.minecraft.block.material.Material;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/6/2016.
 */
public class TileWarheadStation extends TileModuleMachine
{
    public TileWarheadStation()
    {
        super("warheadStation", Material.iron);
        this.resistance = 10f;
        this.hardness = 10f;
    }
}
