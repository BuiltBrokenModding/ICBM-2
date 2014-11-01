package icbm.core.blocks;

import icbm.core.ICBMCore;
import icbm.core.items.ItemSignalDisrupter;
import icbm.core.prefab.TileFrequency;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.api.IRedstoneProvider;

import com.google.common.io.ByteArrayDataInput;
import resonant.engine.ResonantEngine;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketReceiver;
import resonant.lib.transform.vector.Vector3;

public class TileProximityDetector extends TileFrequency implements IRedstoneProvider, IPacketReceiver
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
        super(Material.iron);
        this.normalRender(true);
        this.canProvidePower(true);
        this.useSidedTextures_$eq(true);
    }

    @Override
    public void update()
    {
        super.update();

        if (!this.worldObj.isRemote)
        {
            if (this.ticks() % 20 == 0)
            {
                for (EntityPlayer player : this.yongZhe)
                {
                    ResonantEngine.instance.packetHandler.sendToPlayer(this.getDescPacket(), (EntityPlayerMP) player);
                }

                boolean isDetectThisCheck = false;

                if (this.energy().checkExtract())
                {
                    AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(this.xCoord - minCoord.x(), this.yCoord - minCoord.y(), this.zCoord - minCoord.z(), this.xCoord + maxCoord.x() + 1D, this.yCoord + maxCoord.y() + 1D, this.zCoord + maxCoord.z() + 1D);
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

                    this.energy().extractEnergy();
                }

                if (isDetectThisCheck != this.isDetect)
                {
                    this.isDetect = isDetectThisCheck;
                    this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
                }

            }
        }
    }

    @Override
    public boolean use(EntityPlayer player, int side, Vector3 hit)
    {
        if(!world().isRemote)
            player.openGui(ICBMCore.INSTANCE, 0, world(), hit.xi(), hit.yi(), hit.zi());
        return false;
    }

    @Override
    public boolean configure(EntityPlayer player,int side, Vector3 hit)
    {
        isInverted = !isInverted;
        if(!worldObj.isRemote)
        {
            player.addChatMessage(new ChatComponentText("Proximity Detector Inversion: " + isInverted));
        }
        return true;
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, 0, this.energy().getEnergy(), getFrequency(), this.mode, this.isInverted, this.minCoord.xi(), this.minCoord.yi(), this.minCoord.zi(), this.maxCoord.xi(), this.maxCoord.yi(), this.maxCoord.zi());
    }

    @Override
    public void read(ByteBuf data, EntityPlayer player, PacketType type)
    {
        try
        {
            switch (data.readInt())
            {
                case 0:
                {
                    this.energy().setEnergy(data.readDouble());
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

        nbt.setTag("minCoord", this.minCoord.writeNBT(new NBTTagCompound()));
        nbt.setTag("maxCoord", this.maxCoord.writeNBT(new NBTTagCompound()));
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
}
