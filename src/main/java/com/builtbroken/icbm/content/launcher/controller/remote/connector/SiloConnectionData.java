package com.builtbroken.icbm.content.launcher.controller.remote.connector;

import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.icbm.api.launcher.ILauncher;
import com.builtbroken.icbm.api.launcher.INamedLauncher;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.map.radio.wireless.ConnectionStatus;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/22/2016.
 */
public class SiloConnectionData implements ISiloConnectionData, ISave
{
    protected World world;
    protected int dim, x, y, z;
    protected ILauncher launcher;

    public SiloConnectionData(ILauncher launcher)
    {
        this.launcher = launcher;
        world = launcher.world();
        x = (int) launcher.x();
        y = (int) launcher.y();
        z = (int) launcher.z();
    }

    public SiloConnectionData(NBTTagCompound compoundTagAt)
    {
        load(compoundTagAt);
    }

    @Override
    public String getSiloName()
    {
        return getSilo() instanceof INamedLauncher ? ((INamedLauncher) getSilo()).getLauncherName() : null;
    }

    @Override
    public ILauncher getSilo()
    {
        //Ensure invalid tiles are always removed
        if (launcher instanceof TileEntity && ((TileEntity) launcher).isInvalid() || launcher instanceof Entity && !((Entity) launcher).isEntityAlive())
        {
            launcher = null;
        }
        //If tiles is missing update
        if (world != null && launcher == null)
        {
            if (world.blockExists(x, y, z)) //Ensure the chunk is loaded
            {
                TileEntity tile = world.getTileEntity(x, y, z);
                if (tile instanceof ILauncher)
                {
                    launcher = (ILauncher) tile;
                }
            }
        }
        return launcher;
    }

    @Override
    public IMissile getMissile()
    {
        return getSilo() != null ? getSilo().getMissile() : null;
    }

    @Override
    public ConnectionStatus getSiloStatus()
    {
        ILauncher launcher = getSilo();
        if (launcher == null)
        {
            return ConnectionStatus.NO_CONNECTION;
        }
        //TODO add power check
        //TODO add passkey check
        return ConnectionStatus.ONLINE;
    }

    @Override
    public World world()
    {
        if (world == null)
        {
            world = DimensionManager.getWorld(dim);
        }
        return world;
    }

    @Override
    public double x()
    {
        return x;
    }

    @Override
    public double y()
    {
        return y;
    }

    @Override
    public double z()
    {
        return z;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        dim = nbt.getInteger("dim");
        x = nbt.getInteger("x");
        y = nbt.getInteger("y");
        z = nbt.getInteger("z");
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setInteger("dim", world == null ? 0 : world.provider.dimensionId);
        nbt.setInteger("x", x);
        nbt.setInteger("y", y);
        nbt.setInteger("z", z);
        return nbt;
    }
}
