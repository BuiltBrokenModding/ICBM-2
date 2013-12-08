package icbm.explosion.machines;

import icbm.api.RadarRegistry;
import icbm.core.ICBMCore;
import icbm.core.implement.IRedstoneReceptor;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.explosive.blast.BlastEmp;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.compatibility.TileEntityUniversalElectrical;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import calclavia.lib.multiblock.IBlockActivate;
import calclavia.lib.multiblock.IMultiBlock;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileEntityEmpTower extends TileEntityUniversalElectrical implements IPacketReceiver, IMultiBlock, IRedstoneReceptor, IBlockActivate
{
    // The maximum possible radius for the EMP to strike
    public static final int MAX_RADIUS = 150;

    public float xuanZhuan = 0;
    private float xuanZhuanLu, prevXuanZhuanLu = 0;

    // The EMP mode. 0 = All, 1 = Missiles Only, 2 = Electricity Only
    public byte empMode = 0;

    // The EMP explosion radius
    public int empRadius = 60;

    private final Set<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

    public TileEntityEmpTower()
    {
        RadarRegistry.register(this);
    }

    @Override
    public void invalidate()
    {
        RadarRegistry.unregister(this);
        super.invalidate();
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (this.ticks % 20 == 0 && this.getEnergyStored() > 0)
        {
            this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, ICBMCore.PREFIX + "machinehum", 0.5F, 0.85F * this.getEnergyStored() / this.getMaxEnergyStored());
        }

        this.xuanZhuanLu = (float) (Math.pow(this.getEnergyStored() / this.getMaxEnergyStored(), 2) * 0.5);
        this.xuanZhuan += xuanZhuanLu;
        if (this.xuanZhuan > 360)
            this.xuanZhuan = 0;

        if (!this.worldObj.isRemote)
        {
            if (this.ticks % 3 == 0)
            {
                for (EntityPlayer wanJia : this.playersUsing)
                {
                    PacketDispatcher.sendPacketToPlayer(this.getDescriptionPacket(), (Player) wanJia);
                }
            }

            if (this.ticks % 60 == 0 && this.prevXuanZhuanLu != this.xuanZhuanLu)
            {
                PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 35);
            }
        }

        this.prevXuanZhuanLu = this.xuanZhuanLu;
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
    {
        try
        {
            final int ID = dataStream.readInt();

            if (ID == -1)
            {
                if (dataStream.readBoolean())
                {
                    PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 15);
                    this.playersUsing.add(player);
                }
                else
                {
                    this.playersUsing.remove(player);
                }
            }
            else if (ID == 1)
            {
                this.setEnergyStored(dataStream.readFloat());
                this.empRadius = dataStream.readInt();
                this.empMode = dataStream.readByte();
            }
            else if (ID == 2)
            {
                this.empRadius = dataStream.readInt();
            }
            else if (ID == 3)
            {
                this.empMode = dataStream.readByte();
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
        return PacketManager.getPacket(ICBMExplosion.CHANNEL, this, 1, this.getEnergyStored(), this.empRadius, this.empMode);
    }

    @Override
    public float getVoltage()
    {
        return 240;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        this.empRadius = par1NBTTagCompound.getInteger("banJing");
        this.empMode = par1NBTTagCompound.getByte("muoShi");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("banJing", this.empRadius);
        par1NBTTagCompound.setByte("muoShi", this.empMode);
    }

    @Override
    public void onPowerOn()
    {
        if (this.getEnergyStored() >= this.getMaxEnergyStored())
        {
            switch (this.empMode)
            {
                default:
                    new BlastEmp(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().setEffectEntities().explode();
                    break;
                case 1:
                    new BlastEmp(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectEntities().explode();
                    break;
                case 2:
                    new BlastEmp(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().explode();
                    break;
            }

            this.setEnergyStored(0);
        }
    }

    @Override
    public void onPowerOff()
    {
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        entityPlayer.openGui(ICBMExplosion.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }

    @Override
    public Vector3[] getMultiBlockVectors()
    {
        return new Vector3[] { new Vector3(0, 1, 0) };
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        return (float) Math.ceil(this.getMaxEnergyStored() - this.getEnergyStored());
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        return 0;
    }

    @Override
    public float getMaxEnergyStored()
    {
        return Math.max(3000 * ((float) this.empRadius / (float) MAX_RADIUS), 1000);
    }
}
