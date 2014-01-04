package icbm.contraption.block;

import icbm.contraption.ICBMContraption;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import com.builtbroken.minecraft.network.ISimplePacketReceiver;
import com.builtbroken.minecraft.network.PacketHandler;
import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

public class TileEntityCamouflage extends TileEntity implements ISimplePacketReceiver
{
    // The block Id this block is trying to mimik
    private int blockID = 0;
    private int blockMeta = 0;
    private boolean isSolid = true;
    private final boolean[] renderSides = new boolean[] { false, false, false, false, false, false };

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @Override
    public boolean simplePacket(String id, ByteArrayDataInput dataStream, Player player)
    {
        try
        {
            if (id.equalsIgnoreCase("desc"))
            {
                this.setMimicBlock(dataStream.readInt(), dataStream.readInt());
                this.renderSides[0] = dataStream.readBoolean();
                this.renderSides[1] = dataStream.readBoolean();
                this.renderSides[2] = dataStream.readBoolean();
                this.renderSides[3] = dataStream.readBoolean();
                this.renderSides[4] = dataStream.readBoolean();
                this.renderSides[5] = dataStream.readBoolean();
                this.isSolid = dataStream.readBoolean();
                this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketHandler.instance().getTilePacket(ICBMContraption.CHANNEL, "desc", this, this.blockID, this.blockMeta, this.renderSides[0], this.renderSides[1], this.renderSides[2], this.renderSides[3], this.renderSides[4], this.renderSides[5], this.isSolid);
    }

    public boolean getCanCollide()
    {
        return this.isSolid;
    }

    public void setCanCollide(boolean isYing)
    {
        this.isSolid = isYing;

        if (!this.worldObj.isRemote)
        {
            PacketHandler.instance().sendPacketToClients(this.getDescriptionPacket());
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
                PacketHandler.instance().sendPacketToClients(this.getDescriptionPacket());
            }
        }
    }

    public boolean getRenderSide(ForgeDirection direction)
    {
        if (direction.ordinal() < renderSides.length)
        {
            return renderSides[direction.ordinal()];
        }

        return false;
    }

    public void setRenderSide(ForgeDirection direction, boolean isQing)
    {
        if (direction.ordinal() < renderSides.length)
        {
            renderSides[direction.ordinal()] = isQing;

            if (!this.worldObj.isRemote)
            {
                PacketHandler.instance().sendPacketToClients(this.getDescriptionPacket());
            }
        }
    }

    public void setQing(ForgeDirection direction)
    {
        this.setRenderSide(direction, !getRenderSide(direction));
    }

    public void setQing(boolean isQing)
    {
        for (int i = 0; i < this.renderSides.length; i++)
        {
            this.setRenderSide(ForgeDirection.getOrientation(i), isQing);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        this.blockID = par1NBTTagCompound.getInteger("jiaHaoMa");
        this.blockMeta = par1NBTTagCompound.getInteger("jiaMetadata");

        for (int i = 0; i < renderSides.length; i++)
        {
            this.renderSides[i] = par1NBTTagCompound.getBoolean("qingBian" + i);
        }

        this.isSolid = par1NBTTagCompound.getBoolean("isYing");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("jiaHaoMa", this.blockID);
        par1NBTTagCompound.setInteger("jiaMetadata", this.blockMeta);

        for (int i = 0; i < renderSides.length; i++)
        {
            par1NBTTagCompound.setBoolean("qingBian" + i, this.renderSides[i]);
        }

        par1NBTTagCompound.setBoolean("isYing", this.isSolid);
    }

}
