package icbm.core.blocks;

import icbm.core.ICBMCore;
import icbm.core.items.ItemSignalDisrupter;
import icbm.core.prefab.TileFrequency;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.electricity.IVoltageInput;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.network.IPacketReceiver;
import calclavia.lib.prefab.tile.IRedstoneProvider;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileProximityDetector extends TileFrequency implements IRedstoneProvider, IPacketReceiver, IVoltageInput
{
    private static final int MAX_DISTANCE = 30;

    private static final float DIAN = 5;

    public boolean isDetect = false;

    public Vector3 minCoord = new Vector3(9, 9, 9);
    public Vector3 maxCoord = new Vector3(9, 9, 9);

    public byte mode = 0;

    private final Set<EntityPlayer> yongZhe = new HashSet<EntityPlayer>();

    public boolean isInverted = false;

    public TileProximityDetector()
    {
        setEnergyHandler(new EnergyStorageHandler(200, 100));
    }

    @Override
    public void initiate()
    {
        super.initiate();
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            if (this.ticks % 20 == 0)
            {
                for (EntityPlayer wanJia : this.yongZhe)
                {
                    PacketDispatcher.sendPacketToPlayer(this.getDescriptionPacket(), (Player) wanJia);
                }

                boolean isDetectThisCheck = false;

                if (this.getEnergyHandler().checkExtract())
                {
                    AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(this.xCoord - minCoord.x, this.yCoord - minCoord.y, this.zCoord - minCoord.z, this.xCoord + maxCoord.x + 1D, this.yCoord + maxCoord.y + 1D, this.zCoord + maxCoord.z + 1D);
                    List<EntityLivingBase> entitiesNearby = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bounds);

                    for (EntityLivingBase entity : entitiesNearby)
                    {
                        if (entity instanceof EntityPlayer && (this.mode == 0 || this.mode == 1))
                        {
                            boolean gotDisrupter = false;

                            for (ItemStack inventory : ((EntityPlayer) entity).inventory.mainInventory)
                            {
                                if (inventory != null)
                                {
                                    if (inventory.getItem() instanceof ItemSignalDisrupter)
                                    {
                                        if (((ItemSignalDisrupter) inventory.getItem()).getFrequency(inventory) == getFrequency())
                                        {
                                            gotDisrupter = true;
                                            break;
                                        }
                                    }
                                }
                            }

                            if (gotDisrupter)
                            {
                                if (this.isInverted)
                                {
                                    isDetectThisCheck = true;
                                    break;
                                }

                                continue;
                            }

                            if (!this.isInverted)
                            {
                                isDetectThisCheck = true;
                            }
                        }
                        else if (!this.isInverted && !(entity instanceof EntityPlayer) && (this.mode == 0 || this.mode == 2))
                        {
                            isDetectThisCheck = true;
                            break;
                        }
                    }

                    this.getEnergyHandler().extractEnergy();
                }

                if (isDetectThisCheck != this.isDetect)
                {
                    this.isDetect = isDetectThisCheck;
                    this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
                }

            }
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return ICBMCore.PACKET_TILE.getPacket(this, 0, this.getEnergyHandler().getEnergy(), getFrequency(), this.mode, this.isInverted, this.minCoord.intX(), this.minCoord.intY(), this.minCoord.intZ(), this.maxCoord.intX(), this.maxCoord.intY(), this.maxCoord.intZ());
    }

    @Override
    public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
    {
        try
        {
            switch (data.readInt())
            {
                case 0:
                {
                    this.setEnergy(ForgeDirection.UNKNOWN, data.readLong());
                    this.setFrequency(data.readInt());
                    this.mode = data.readByte();
                    this.isInverted = data.readBoolean();
                    this.minCoord = new Vector3(Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())));
                    this.maxCoord = new Vector3(Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())));
                    break;
                }
                case 1:
                {
                    // Mode
                    mode = data.readByte();
                    break;
                }
                case 2:
                {
                    // Freq
                    setFrequency(data.readInt());
                    break;
                }
                case 3:
                {
                    // MinVec
                    minCoord = new Vector3(Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())));
                    break;
                }
                case 4:
                {
                    // MaxVec
                    maxCoord = new Vector3(Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())));
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        this.mode = nbt.getByte("mode");
        this.isInverted = nbt.getBoolean("isInverted");

        this.minCoord = new Vector3(nbt.getCompoundTag("minCoord"));
        this.maxCoord = new Vector3(nbt.getCompoundTag("maxCoord"));
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setByte("mode", this.mode);
        nbt.setBoolean("isInverted", this.isInverted);

        nbt.setCompoundTag("minCoord", this.minCoord.writeToNBT(new NBTTagCompound()));
        nbt.setCompoundTag("maxCoord", this.maxCoord.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public boolean isPoweringTo(ForgeDirection side)
    {
        return this.isDetect;
    }

    @Override
    public boolean isIndirectlyPoweringTo(ForgeDirection side)
    {
        return this.isDetect;
    }

    @Override
    public long getVoltageInput(ForgeDirection direction)
    {
        return 240;
    }

    @Override
    public void onWrongVoltage(ForgeDirection direction, long voltage)
    {

    }

}
