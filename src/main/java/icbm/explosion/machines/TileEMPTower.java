package icbm.explosion.machines;

import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.multiblock.fake.IMultiBlock;
import calclavia.lib.prefab.tile.IRedstoneReceptor;
import com.google.common.io.ByteArrayDataInput;
import icbm.Reference;
import icbm.api.RadarRegistry;
import icbm.core.ICBMCore;
import icbm.core.prefab.TileICBM;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.explosive.blast.BlastEMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;

import java.io.IOException;

public class TileEMPTower extends TileICBM implements IMultiBlock, IRedstoneReceptor, IBlockActivate
{
    // The maximum possible radius for the EMP to strike
    public static final int MAX_RADIUS = 150;

    public float xuanZhuan = 0;
    private float xuanZhuanLu, prevXuanZhuanLu = 0;

    // The EMP mode. 0 = All, 1 = Missiles Only, 2 = Electricity Only
    public byte empMode = 0;

    // The EMP explosion radius
    public int empRadius = 60;

    public TileEMPTower()
    {
        RadarRegistry.register(this);
        energy = new EnergyStorageHandler(0);
        updateCapacity();
    }

    @Override
    public void invalidate()
    {
        RadarRegistry.unregister(this);
        super.invalidate();
    }

    @Override
    public void initiate()
    {
        updateCapacity();
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (ticks % 20 == 0 && energy.getEnergy() > 0)
            worldObj.playSoundEffect(xCoord, yCoord, zCoord, Reference.PREFIX + "machinehum", 0.5F, 0.85F * energy.getEnergy() / energy.getEnergyCapacity());

        xuanZhuanLu = (float) (Math.pow(energy.getEnergy() / energy.getEnergyCapacity(), 2) * 0.5);
        xuanZhuan += xuanZhuanLu;
        if (xuanZhuan > 360)
            xuanZhuan = 0;

        prevXuanZhuanLu = xuanZhuanLu;
    }

    @Override
    public void onReceivePacket(int id, ByteArrayDataInput data, EntityPlayer player, Object... extra) throws IOException
    {
        switch (id)
        {
            case 0:
            {
                energy.setEnergy(data.readLong());
                empRadius = data.readInt();
                empMode = data.readByte();
                break;
            }
            case 1:
            {
                empRadius = data.readInt();
                updateCapacity();
                break;
            }
            case 2:
            {
                empMode = data.readByte();
                break;
            }
        }

        super.onReceivePacket(id, data, player, extra);
    }

    private void updateCapacity()
    {
        this.energy.setCapacity(Math.max(300000000 * (this.empRadius / MAX_RADIUS), 1000000000));
        this.energy.setMaxTransfer(this.energy.getEnergyCapacity() / 50);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return ICBMCore.PACKET_TILE.getPacket(this, 0, this.energy.getEnergy(), this.empRadius, this.empMode);
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
        if (this.energy.isFull())
        {
            switch (this.empMode)
            {
                default:
                    new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().setEffectEntities().explode();
                    break;
                case 1:
                    new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectEntities().explode();
                    break;
                case 2:
                    new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().explode();
                    break;
            }

            this.energy.setEnergy(0);
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

}
