package com.builtbroken.icbm.content.launcher.controller.remote.antenna;

import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.prefab.tile.entity.TileEntityBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/18/2016.
 */
public class TileAntennaPart extends TileEntityBase implements IWorldPosition
{
    protected AntennaNetwork antennaNetwork;
    protected HashMap<ForgeDirection, TileEntity> connections = new HashMap();

    int ticks = 0;
    boolean doConnectionUpdate = true;
    boolean doRenderUpdate = true;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if(!worldObj.isRemote)
        {
            ticks++;
            if (doConnectionUpdate || ticks % 80 == 0)
            {
                doConnectionUpdate = false;
                updateConnections();
                //TODO check if connection state changed
                doRenderUpdate = true;
            }
            else if (doRenderUpdate)
            {
                updateRenderState();
            }
            if (ticks >= Integer.MAX_VALUE - 5)
            {
                ticks = 1;
            }
        }
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (connections.size() > 0)
        {
            for (TileEntity tile : connections.values())
            {
                if (!tile.isInvalid() && tile instanceof TileAntennaPart)
                {
                    ((TileAntennaPart) tile).doConnectionUpdate = true;
                }
            }
            connections.clear();
        }
    }

    /**
     * Builds a map of tile connections to sides
     */
    protected void updateConnections()
    {
        connections.clear();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
            if(tile != null && canConnect(dir, tile))
            {
                connections.put(dir, tile);
                if (tile instanceof TileAntennaPart)
                {
                    if (((TileAntennaPart) tile).antennaNetwork != null)
                    {
                        if (antennaNetwork == null)
                        {
                            ((TileAntennaPart) tile).antennaNetwork.add(this);
                        }
                        else if (antennaNetwork != null && ((TileAntennaPart) tile).antennaNetwork != null)
                        {
                            antennaNetwork.merge((TileAntennaPart) tile);
                        }
                    }
                }
            }
        }
    }

    protected boolean canConnect(ForgeDirection side, TileEntity tile)
    {
        return tile instanceof TileAntennaPart;
    }

    /**
     * Mainly updates data used by the client renderer(e.g. Metadata)
     */
    public void updateRenderState()
    {
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        int antennaParts = connections.size(); //TODO redo
        //Intersection
        if (antennaParts > 3)
        {
            if (meta != 3)
            {
                //TODO implement advanced render to reduce extra connections rendering that are not used
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 3, 3);
            }
        }
        else if (antennaParts == 2)
        {
            if (connections.get(ForgeDirection.UP) != null && connections.get(ForgeDirection.DOWN) != null)
            {
                //Do notch detection logic, set notch when rod connection changes from rod -> not rod
                TileEntity above = connections.get(ForgeDirection.UP);
                TileEntity bellow = connections.get(ForgeDirection.DOWN);
                boolean found = false;
                if(above instanceof TileAntennaPart && bellow instanceof TileAntennaPart)
                {
                    boolean connectionA = ((TileAntennaPart)above).connections.get(ForgeDirection.UP) != null && ((TileAntennaPart)above).connections.get(ForgeDirection.DOWN) != null;
                    boolean connectionB = ((TileAntennaPart)bellow).connections.get(ForgeDirection.UP) != null && ((TileAntennaPart)bellow).connections.get(ForgeDirection.DOWN) != null;
                    if(connectionA && !connectionB || connectionB && !connectionA)
                    {
                        found = true;
                        if (found && meta != 4)
                        {
                            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 4, 3);
                        }
                    }
                }

                //Set normal rod
                if (!found && meta != 1)
                {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 3);
                }
            }
            else if (connections.get(ForgeDirection.EAST) != null && connections.get(ForgeDirection.WEST) != null)
            {
                if (meta != 6)
                {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 6, 3);
                }
            }
            else if (connections.get(ForgeDirection.NORTH) != null && connections.get(ForgeDirection.SOUTH) != null)
            {
                if (meta != 7)
                {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 7, 3);
                }
            }
            //Any other connections is the same as intersection TODO make reduced connection render eg L shaped connector
            else if (meta != 3)
            {
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 3, 3);
            }
        }
        else if (antennaParts == 1)
        {
            if (connections.get(ForgeDirection.DOWN) != null)
            {
                if (meta != 5)
                {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 5, 3);
                }
            }
            else if(connections.get(ForgeDirection.EAST) != null || connections.get(ForgeDirection.WEST) != null)
            {
                if (meta != 6)
                {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 6, 3);
                }
            }
            else if (connections.get(ForgeDirection.NORTH) != null || connections.get(ForgeDirection.SOUTH) != null)
            {
                if (meta != 7)
                {
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 7, 3);
                }
            }
        }
    }

    @Override
    public World oldWorld()
    {
        return worldObj;
    }

    @Override
    public double x()
    {
        return xCoord;
    }

    @Override
    public double y()
    {
        return yCoord;
    }

    @Override
    public double z()
    {
        return zCoord;
    }
}
