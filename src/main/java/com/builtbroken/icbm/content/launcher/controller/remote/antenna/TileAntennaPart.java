package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import com.builtbroken.mc.prefab.tile.entity.TileEntityBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/18/2016.
 */
public class TileAntennaPart extends TileEntityBase
{
    protected AntennaNetwork network;
    protected HashMap<ForgeDirection, TileEntity> connections = new HashMap();

    int ticks = 0;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        ticks++;
        if (ticks == 1 || ticks % 80 == 0)
        {
            connections.clear();
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                TileEntity entity = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
                if (entity instanceof TileAntennaPart)
                {
                    connections.put(dir, entity);
                    if(((TileAntennaPart) entity).network != null)
                    {
                        if (network == null)
                        {
                            network = ((TileAntennaPart) entity).network;
                        }
                        else if (network != null)
                        {
                            network.merge((TileAntennaPart) entity);
                        }
                    }
                }
            }
        }
        if (ticks >= Integer.MAX_VALUE - 1)
        {
            ticks = 1;
        }
    }
}
