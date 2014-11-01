package icbm.explosion.machines.launcher;

import icbm.core.ICBMCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.api.IRotatable;
import resonant.api.ITier;

import com.google.common.io.ByteArrayDataInput;
import resonant.content.prefab.java.TileAdvanced;
import resonant.lib.multiblock.reference.IMultiBlock;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketReceiver;
import resonant.lib.network.netty.AbstractPacket;
import resonant.lib.transform.vector.Vector3;

import java.util.ArrayList;
import java.util.List;

/** This tile entity is for the screen of the missile launcher
 * 
 * @author Calclavia */
public class TileLauncherFrame extends TileAdvanced implements IPacketReceiver, ITier, IMultiBlock, IRotatable
{
    // The tier of this screen
    private int tier = 0;
    private byte orientation = 3;

    public TileLauncherFrame()
    {
        super(Material.iron);
    }

    @Override
    public void read(ByteBuf data, EntityPlayer player, PacketType packet)
    {
        try
        {
            this.orientation = data.readByte();
            this.tier = data.readInt();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, this.orientation, this.getTier());
    }

    /** Gets the inaccuracy of the missile based on the launcher support frame's tier */
    public int getInaccuracy()
    {
        switch (this.tier)
        {
            default:
                return 15;
            case 1:
                return 7;
            case 2:
                return 0;
        }
    }

    /** Determines if this TileEntity requires update calls.
     * 
     * @return True if you want updateEntity() to be called, false if not */
    @Override
    public boolean canUpdate()
    {
        return false;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.tier = par1NBTTagCompound.getInteger("tier");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("tier", this.tier);
    }

    @Override
    public int getTier()
    {
        return this.tier;
    }

    @Override
    public void setTier(int tier)
    {
        this.tier = tier;
    }

    @Override
    public List<Vector3> getMultiBlockVectors()
    {
        List<Vector3> list = new ArrayList<Vector3>();
        list.add(new Vector3(0, 1, 0));
        list.add(new Vector3(0, 2, 0));
        return list;
    }

    @Override
    public ForgeDirection getDirection()
    {
        return ForgeDirection.getOrientation(this.orientation);
    }

    @Override
    public void setDirection(ForgeDirection facingDirection)
    {
        this.orientation = (byte) facingDirection.ordinal();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }
}
