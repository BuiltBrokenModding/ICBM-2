package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import com.builtbroken.jlib.helpers.MathHelper;

/**
 * Core logic tile for antenna structure and linking to other tiles
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileAntenna extends TileAntennaPart
{
    private boolean hasInitScanned = false;
    private int randomTick = 30;

    public void doInitScan()
    {
        hasInitScanned = true;
        if (network == null)
        {
            network = new AntennaNetwork();
            network.base = this;
        }
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        //Sanity check to ensure structure is still good, roughly 2 mins with a slight random
        if (ticks % randomTick == 0)
        {
            randomTick = MathHelper.rand.nextInt(200) + 1000;
            if (ticks != 30 || !hasInitScanned) //Skip first check
            {
                doInitScan();
            }
        }
    }
}
