package com.builtbroken.icbm.content.launcher.controller.remote.connector;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.controller.ISiloConnectionData;
import com.builtbroken.icbm.api.controller.ISiloConnectionPoint;
import com.builtbroken.icbm.api.launcher.ILauncher;
import com.builtbroken.icbm.api.launcher.INamedLauncher;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.jlib.data.network.IByteBufReader;
import com.builtbroken.jlib.data.network.IByteBufWriter;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.map.radio.wireless.ConnectionStatus;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/22/2016.
 */
public class SiloConnectionData implements ISiloConnectionData, ISave, IByteBufWriter, IByteBufReader
{
    public World world;
    public int dim, x, y, z;
    protected ILauncher launcher;

    public SiloConnectionData(ILauncher launcher)
    {
        this.launcher = launcher;
        world = launcher.world();
        x = (int) Math.floor(launcher.x());
        y = (int) Math.floor(launcher.y());
        z = (int) Math.floor(launcher.z());
    }

    public SiloConnectionData(NBTTagCompound compoundTagAt)
    {
        load(compoundTagAt);
    }

    public SiloConnectionData(ByteBuf buf)
    {
        readBytes(buf);
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
        if (launcher instanceof TileEntity && ((TileEntity) launcher).isInvalid()
                || launcher instanceof Entity && !((Entity) launcher).isEntityAlive()
                || launcher instanceof ITileNode && !((ITileNode) launcher).getHost().isHostValid())
        {
            launcher = null;
        }
        //If tiles is missing update
        if (world() != null && launcher == null)
        {
            if (world().blockExists(x, y, z)) //Ensure the chunk is loaded
            {
                TileEntity tile = world().getTileEntity(x, y, z);
                if (tile instanceof ILauncher)
                {
                    launcher = (ILauncher) tile;
                }
                else if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof ILauncher)
                {
                    launcher = (ILauncher) ((ITileNodeHost) tile).getTileNode();
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
    public void openGui(EntityPlayer player, TileEntity openingTile, ISiloConnectionPoint connector)
    {
        if (world() != null && !world().isRemote && getSilo() != null)
        {
            if (getSilo() instanceof TileAbstractLauncher)
            {
                TileAbstractLauncher launcher = (TileAbstractLauncher) getSilo();
                Object[] data = new Object[2];
                data[0] = openingTile;
                data[1] = connector;
                launcher.returnGuiData.put(player, data);
            }
            player.openGui(ICBM.INSTANCE, 1, world, x, y, z);
        }
    }

    @Override
    public boolean hasSettingsGui()
    {
        return true;
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

    @Override
    public Object readBytes(ByteBuf buf)
    {
        load(ByteBufUtils.readTag(buf));
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, save(new NBTTagCompound()));
        return buf;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof SiloConnectionData)
        {
            return ((SiloConnectionData) obj).dim == dim && ((SiloConnectionData) obj).x == x && ((SiloConnectionData) obj).y == y && ((SiloConnectionData) obj).z == z;
        }
        return false;
    }
}
