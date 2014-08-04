package icbm.explosion.explosive;

import icbm.core.ICBMCore;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.items.ItemRemoteDetonator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import resonant.api.IRotatable;
import resonant.api.explosion.IExplosive;
import resonant.api.explosion.IExplosiveContainer;
import resonant.lib.network.IPacketReceiver;

import com.google.common.io.ByteArrayDataInput;

public class TileExplosive extends TileEntity implements IExplosiveContainer, IPacketReceiver, IRotatable
{
    public boolean exploding = false;
    public int haoMa = 0;
    public NBTTagCompound nbtData = new NBTTagCompound();

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
        this.haoMa = par1NBTTagCompound.getInteger("explosiveID");
        this.nbtData = par1NBTTagCompound.getCompoundTag("data");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("explosiveID", this.haoMa);
        par1NBTTagCompound.setTag("data", this.nbtData);
    }

    @Override
    public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
    {
        try
        {
            final byte ID = data.readByte();

            if (ID == 1)
            {
                haoMa = data.readInt();
                worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
            }
            else if (ID == 2 && !this.worldObj.isRemote)
            {
                // Packet explode command
                if (player.inventory.getCurrentItem().getItem() instanceof ItemRemoteDetonator)
                {
                    ItemStack itemStack = player.inventory.getCurrentItem();
                    BlockExplosive.yinZha(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.haoMa, 0);
                    ((ItemRemoteDetonator) ICBMExplosion.itemRemoteDetonator).discharge(itemStack, ItemRemoteDetonator.ENERGY, true);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return ICBMCore.PACKET_TILE.getPacket(this, (byte) 1, this.haoMa);
    }

    @Override
    public ForgeDirection getDirection()
    {
        return ForgeDirection.getOrientation(this.getBlockMetadata());
    }

    @Override
    public void setDirection(ForgeDirection facingDirection)
    {
        this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, facingDirection.ordinal(), 2);
    }

    @Override
    public IExplosive getExplosiveType()
    {
        return ExplosiveRegistry.get(this.haoMa);
    }

    @Override
    public NBTTagCompound getTagCompound()
    {
        return this.nbtData;
    }
}
