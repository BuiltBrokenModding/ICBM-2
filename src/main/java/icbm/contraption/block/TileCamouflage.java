package icbm.contraption.block;

import icbm.core.ICBMCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import calclavia.lib.network.IPacketReceiver;
import calclavia.lib.network.PacketHandler;

import com.google.common.io.ByteArrayDataInput;

public class TileCamouflage extends TileEntity implements IPacketReceiver
{
    // The block Id this block is trying to mimick
    private int blockID = 0;
    private int blockMeta = 0;
    private boolean isSolid = true;

    /** Bitmask **/
    private byte renderSides = 0b000000;

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @Override
    public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
    {
        try
        {
            this.setMimicBlock(data.readInt(), data.readInt());
            this.renderSides = data.readByte();
            this.isSolid = data.readBoolean();
            this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return ICBMCore.PACKET_TILE.getPacket(this, this.blockID, this.blockMeta, this.renderSides, this.isSolid);
    }

    public boolean getCanCollide()
    {
        return this.isSolid;
    }

    public void setCanCollide(boolean isSolid)
    {
        this.isSolid = isSolid;

        if (!this.worldObj.isRemote)
        {
            PacketHandler.sendPacketToClients(this.getDescriptionPacket());
        }
    }

    public void toggleCollision()
    {
        this.setCanCollide(!this.isSolid);
    }

    public int getMimicBlockID()
    {
        return this.blockID;
    }

    public int getMimicBlockMeta()
    {
        return this.blockMeta;
    }

    public void setMimicBlock(int blockID, int metadata)
    {
        if (this.blockID != blockID || this.blockMeta != metadata)
        {
            this.blockID = Math.max(blockID, 0);
            this.blockMeta = Math.max(metadata, 0);

            if (!this.worldObj.isRemote)
            {
                PacketHandler.sendPacketToClients(this.getDescriptionPacket());
            }
        }
    }

    public boolean canRenderSide(ForgeDirection direction)
    {
        return (renderSides & (1 << direction.ordinal())) != 0;
    }

    public void setRenderSide(ForgeDirection direction, boolean isClear)
    {
        if (isClear)
        {
            renderSides = (byte) (renderSides | (1 << direction.ordinal()));
        }
        else
        {
            renderSides = (byte) (renderSides & ~(1 << direction.ordinal()));

        }

        if (!this.worldObj.isRemote)
        {
            PacketHandler.sendPacketToClients(this.getDescriptionPacket());
        }
    }

    public void toggleRenderSide(ForgeDirection direction)
    {
        this.setRenderSide(direction, !canRenderSide(direction));
    }

    public void setRenderSide(boolean isClear)
    {
        if (isClear)
        {
            this.renderSides = 63;
        }
        else
        {
            this.renderSides = 0;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        this.blockID = nbt.getInteger("jiaHaoMa");
        this.blockMeta = nbt.getInteger("jiaMetadata");
        this.renderSides = nbt.getByte("renderSides");
        this.isSolid = nbt.getBoolean("isYing");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setInteger("jiaHaoMa", this.blockID);
        nbt.setInteger("jiaMetadata", this.blockMeta);
        nbt.setByte("renderSides", renderSides);
        nbt.setBoolean("isYing", this.isSolid);
    }

}
