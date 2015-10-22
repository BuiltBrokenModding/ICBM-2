package com.builtbroken.test.icbm.content.crafting.station;

import com.builtbroken.icbm.content.crafting.station.TileAbstractWorkstation;
import com.builtbroken.mc.testing.tile.AbstractTileTest;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/22/2015.
 */
public abstract class TestAbstractWorkstation<T extends TileAbstractWorkstation> extends AbstractTileTest<T>
{
    public TestAbstractWorkstation(String name, Class<T> clazz) throws IllegalAccessException, InstantiationException
    {
        super(name, clazz);
    }
}
