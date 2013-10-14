package icbm.explosion.missile.modular;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import icbm.api.ITier;
import icbm.explosion.ICBMExplosion;
import calclavia.lib.multiblock.IMultiBlock;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.IRotatable;
import universalelectricity.prefab.tile.TileEntityAdvanced;

public class TileEntityMissileTable extends TileEntityAdvanced implements IMultiBlock, ITier, IRotatable, IPacketReceiver
{
    public int tier = -1;
    /** Side placed on */
    public ForgeDirection placedSide = ForgeDirection.UP;
    /** 0 - 3 of rotation on the given side */
    public byte rotationSide = 0;
    public boolean rotating = false;

    @Override
    public Vector3[] getMultiBlockVectors()
    {
        return getMultiBlockVectors(placedSide, rotationSide);
    }

    public static Vector3[] getMultiBlockVectors(ForgeDirection side, byte rot)
    {
        //rotation doesn't really effect the multi block too much however placed side does
        if (side == ForgeDirection.UP || side == ForgeDirection.DOWN)
        {
            //line up on the x
            if (rot == 0 || rot == 2)
            {
                return new Vector3[] { new Vector3(1, 0, 0), new Vector3(-1, 0, 0) };
            }
            //lined up on the z
            return new Vector3[] { new Vector3(0, 0, 1), new Vector3(0, 0, -1) };
        }
        else
        {
            //Lined up with x or z
            if (rot == 0 || rot == 2)
            {
                if (side == ForgeDirection.NORTH || side == ForgeDirection.SOUTH)
                {
                    return new Vector3[] { new Vector3(-1, 0, 0), new Vector3(1, 0, 0) };
                }
                else if (side == ForgeDirection.EAST || side == ForgeDirection.WEST)
                {
                    return new Vector3[] { new Vector3(0, 0, -1), new Vector3(0, 0, 1) };
                }
            }
            //Lined up with the Y
            return new Vector3[] { new Vector3(0, 1, 0), new Vector3(0, -1, 0) };
        }
    }

    @Override
    public int getTier()
    {
        if (tier == -1)
        {
            this.setTier(this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
        }
        return tier;
    }

    @Override
    public void setTier(int tier)
    {
        this.tier = tier & 3;
    }

    @Override
    public ForgeDirection getDirection()
    {
        //direction is actually based on the rotation of the object on the side of a block. This way the assembly line rotation block will rotate it correctly. As well for wrench support

        if (this.rotationSide == 0)
        {
            return ForgeDirection.NORTH;
        }
        else if (this.rotationSide == 2)
        {
            return ForgeDirection.SOUTH;
        }
        else if (this.rotationSide == 1)
        {
            return ForgeDirection.EAST;
        }
        else
        {
            return ForgeDirection.WEST;
        }
    }

    @Override
    public void setDirection(ForgeDirection direction)
    {
        byte rot = 0;

        if (direction == ForgeDirection.NORTH)
        {
            rot = 0;
        }
        else if (direction == ForgeDirection.SOUTH)
        {
            rot = 2;
        }
        else if (direction == ForgeDirection.EAST)
        {
            rot = 1;
        }
        else
        {
            rot = 3;
        }
        if (BlockMissileTable.canRotateBlockTo(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.placedSide, rot))
        {
            this.rotationSide = rot;
            this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public void setPlacedSide(ForgeDirection side)
    {
        this.placedSide = side;
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setRotation(byte rot)
    {
        this.rotationSide = rot;
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.rotationSide = nbt.getByte("rotationSide");
        this.placedSide = ForgeDirection.getOrientation((int) nbt.getByte("placedSide"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByte("rotationSide", this.rotationSide);
        nbt.setByte("placedSide", (byte) this.placedSide.ordinal());
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
    {
        if (this.worldObj.isRemote)
        {
            byte id = dataStream.readByte();
            if (id == 0)
            {
                this.rotationSide = dataStream.readByte();
                this.placedSide = ForgeDirection.getOrientation(dataStream.readByte());
                this.worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
                System.out.println("Rotation: " + rotationSide);
                System.out.println("Side: " + placedSide.toString());
            }
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketManager.getPacket(ICBMExplosion.CHANNEL, this, ((byte) 0), ((byte) this.rotationSide), ((byte) this.placedSide.ordinal()));
    }

}
