package com.builtbroken.icbm.content.display;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.framework.logic.TileNode;

/**
 * Version of the Missile Display that is static
 * Created by robert on 1/16/2015.
 */
@TileWrapped(className = "TileEntityWrapperMissile")
public class TileMissile extends TileNode
{
    public TileMissile()
    {
        super("TileMissile", ICBM.DOMAIN);
    }

    //TODO add GUI
    //TODO add option to select display type
    //TODO add option to change render angles
    //TODO add option to change skin of render
    //TODO add sub types
}
