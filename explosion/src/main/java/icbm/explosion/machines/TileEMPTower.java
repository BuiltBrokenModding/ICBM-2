package icbm.explosion.machines;

import icbm.Reference;
import icbm.core.ICBMCore;
import icbm.core.prefab.TileICBM;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.explosive.blast.BlastEMP;

import java.io.IOException;

import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.AxisAlignedBB;
import resonant.api.IRedstoneReceptor;
import resonant.api.map.RadarRegistry;
import resonant.lib.multiblock.IBlockActivate;
import resonant.lib.multiblock.IMultiBlock;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;

public class TileEMPTower extends TileICBM implements IMultiBlock, IRedstoneReceptor, IBlockActivate, SimpleComponent
{
    // The maximum possible radius for the EMP to strike
    public static final int MAX_RADIUS = 150;

    public float rotation = 0;
    private float rotationDelta, prevXuanZhuanLu = 0;

    // The EMP mode. 0 = All, 1 = Missiles Only, 2 = Electricity Only
    public byte empMode = 0;

    private int cooldownTicks = 0;

    // The EMP explosion radius
    public int empRadius = 60;

    public TileEMPTower()
    {
        RadarRegistry.register(this);
        setEnergyHandler(new EnergyStorageHandler(0));
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

        if (!isReady())
        {
            cooldownTicks--;
        }

        if (ticks % 20 == 0 && getEnergyHandler().getEnergy() > 0)
            worldObj.playSoundEffect(xCoord, yCoord, zCoord, Reference.PREFIX + "machinehum", 0.5F, 0.85F * getEnergyHandler().getEnergy() / getEnergyHandler().getEnergyCapacity());

        rotationDelta = (float) (Math.pow(getEnergyHandler().getEnergy() / getEnergyHandler().getEnergyCapacity(), 2) * 0.5);
        rotation += rotationDelta;
        if (rotation > 360)
            rotation = 0;

        prevXuanZhuanLu = rotationDelta;
    }

    @Override
    public void onReceivePacket(int id, ByteArrayDataInput data, EntityPlayer player, Object... extra) throws IOException
    {
        switch (id)
        {
            case 0:
            {
                getEnergyHandler().setEnergy(data.readLong());
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

    @Callback
    public boolean isReady()
    {
        return getCooldown() <= 0;
    }

    @Callback
    public int getCooldown()
    {
        return cooldownTicks;
    }

    @Callback
    public int getMaxCooldown()
    {
        return 120;
    }

    private void updateCapacity()
    {
        this.getEnergyHandler().setCapacity(Math.max(300000000 * (this.empRadius / MAX_RADIUS), 1000000000));
        this.getEnergyHandler().setMaxReceive(this.getEnergyHandler().getEnergyCapacity() / 50);
        this.getEnergyHandler().setMaxExtract((long) (this.getEnergyHandler().getEnergyCapacity() / .9));
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return ICBMCore.PACKET_TILE.getPacket(this, 0, this.getEnergyHandler().getEnergy(), this.empRadius, this.empMode);
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

    @Callback(limit = 1)
    public boolean fire()
    {
        if (this.getEnergyHandler().checkExtract())
        {
            if (isReady())
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
                this.getEnergyHandler().extractEnergy();
                this.cooldownTicks = getMaxCooldown();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPowerOn()
    {
        fire();
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
    public String getComponentName()
    {
        return "emptower";
    }

    @Callback
    public byte getEmpMode()
    {
        return empMode;
    }

    @Callback
    public void setEmpMode(byte empMode)
    {
        if (empMode >= 0 && empMode <= 2)
            this.empMode = empMode;
    }

    @Callback
    public void empMissiles()
    {
        this.empMode = 1;
    }

    @Callback
    public void empAll()
    {
        this.empMode = 0;
    }

    @Callback
    public void empElectronics()
    {
        this.empMode = 2;
    }

    @Callback
    public int getEmpRadius()
    {
        return empRadius;
    }

    @Callback
    public int getMaxEmpRadius()
    {
        return MAX_RADIUS;
    }

    @Callback
    public void setEmpRadius(int empRadius)
    {
        int prev = getEmpRadius();
        this.empRadius = Math.min(Math.max(empRadius, 0), MAX_RADIUS);
        if (prev != getEmpRadius())
            updateCapacity();
    }

    @Callback
    public static int getMaxRadius()
    {
        return MAX_RADIUS;
    }

}
