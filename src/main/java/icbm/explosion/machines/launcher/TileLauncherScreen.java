package icbm.explosion.machines.launcher;

import icbm.core.ICBMCore;
import icbm.explosion.ICBMExplosion;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.api.IRotatable;
import resonant.api.ITier;
import resonant.api.explosion.IMissile;
import resonant.api.explosion.LauncherType;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketIDReceiver;
import resonant.lib.network.handle.IPacketReceiver;
import resonant.lib.network.netty.AbstractPacket;
import resonant.lib.transform.vector.VectorWorld;
import resonant.lib.transform.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;

/** This tile entity is for the screen of the missile launcher
 * 
 * @author Calclavia */
public class TileLauncherScreen extends TileLauncherPrefab implements ITier, IRotatable, IPacketIDReceiver
{
    // The rotation of this missile component
    private byte fangXiang = 3;

    // The tier of this screen
    private int tier = 0;

    // The missile launcher base in which this
    // screen is connected with
    public TileLauncherBase laucherBase = null;

    public short gaoDu = 3;

    private final Set<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

    @Override
    public void onInstantiate()
    {
        super.onInstantiate();
        this.setCapacity(this.getLaunchCost() * 2);
        this.setMaxTransfer(this.getLaunchCost());
    }

    @Override
    public void update()
    {
        super.update();

        if (this.laucherBase == null)
        {
            for (byte i = 2; i < 6; i++)
            {
                VectorWorld position = toVectorWorld();
                position.add(ForgeDirection.getOrientation(i));

                TileEntity tileEntity = position.getTileEntity();

                if (tileEntity != null)
                {
                    if (tileEntity instanceof TileLauncherBase)
                    {
                        this.laucherBase = (TileLauncherBase) tileEntity;
                        this.fangXiang = i;
                    }
                }
            }
        }
        else
        {
            if (this.laucherBase.isInvalid())
            {
                this.laucherBase = null;
            }
        }

        if (this.ticks() % 100 == 0 && this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
        {
            this.launch();
        }

        if (!this.worldObj.isRemote)
        {
            if (this.ticks() % 3 == 0)
            {
                if (this.targetPos == null)
                {
                    this.targetPos = new Vector3(this.xCoord, 0, this.zCoord);
                }
            }

            if (this.ticks() % 600 == 0)
            {
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            }
        }
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, 0, this.fangXiang, this.tier, this.getFrequency(), this.gaoDu);
    }

    @Override
    public AbstractPacket getGuiPacket()
    {
        return new PacketTile(this, 4, this.getEnergyStorage().getEnergy(), this.targetPos);
    }

    @Override
    public void placeMissile(ItemStack itemStack)
    {
        if (this.laucherBase != null)
        {
            if (!this.laucherBase.isInvalid())
            {
                this.laucherBase.setInventorySlotContents(0, itemStack);
            }
        }
    }

    @Override
    public boolean read(ByteBuf data, int id, EntityPlayer player, PacketType type)
    {
        switch (id)
        {
            case 0:
            {
                this.fangXiang = data.readByte();
                this.tier = data.readInt();
                this.setFrequency(data.readInt());
                this.gaoDu = data.readShort();
                break;
            }
            case 1:
            {
                this.setFrequency(data.readInt());
                break;
            }
            case 2:
            {
                this.targetPos = new Vector3(data.readInt(), data.readInt(), data.readInt());

                if (this.getTier() < 2)
                {
                    this.targetPos.y_$eq(0);
                }
                break;
            }
            case 3:
            {
                this.gaoDu = (short) Math.max(Math.min(data.readShort(), Short.MAX_VALUE), 3);
                break;
            }
            case 4:
            {
                this.getEnergyStorage().setEnergy(data.readLong());
                this.targetPos = new Vector3(data.readInt(), data.readInt(), data.readInt());
                break;
            }
        }
        return true;
    }

    // Checks if the missile is launchable
    @Override
    public boolean canLaunch()
    {
        if (this.laucherBase != null && this.laucherBase.missile != null)
        {
            if (this.getEnergyStorage().extractEnergy(getLaunchCost(), false) >= getLaunchCost())
            {
                return this.laucherBase.isInRange(this.targetPos);
            }
        }
        return false;
    }

    /** Calls the missile launcher base to launch it's missile towards a targeted location */
    @Override
    public void launch()
    {
        if (this.canLaunch())
        {
            this.getEnergyStorage().extractEnergy(getLaunchCost(), true);
            this.laucherBase.launchMissile(this.targetPos.clone(), this.gaoDu);
        }
    }

    /** Gets the display status of the missile launcher
     * 
     * @return The string to be displayed */
    @Override
    public String getStatus()
    {
        String color = "\u00a74";
        String status = LanguageUtility.getLocal("gui.misc.idle");

        if (this.laucherBase == null)
        {
            status = LanguageUtility.getLocal("gui.launcherScreen.statusMissing");
        }
        else if (!this.getEnergyStorage().checkExtract())
        {
            status = LanguageUtility.getLocal("gui.launcherScreen.statusNoPower");
        }
        else if (this.laucherBase.missile == null)
        {
            status = LanguageUtility.getLocal("gui.launcherScreen.statusEmpty");
        }
        else if (this.targetPos == null)
        {
            status = LanguageUtility.getLocal("gui.launcherScreen.statusInvalid");
        }
        else if (this.laucherBase.shiTaiJin(this.targetPos))
        {
            status = LanguageUtility.getLocal("gui.launcherScreen.statusClose");
        }
        else if (this.laucherBase.shiTaiYuan(this.targetPos))
        {
            status = LanguageUtility.getLocal("gui.launcherScreen.statusFar");
        }
        else
        {
            color = "\u00a72";
            status = LanguageUtility.getLocal("gui.launcherScreen.statusReady");
        }

        return color + status;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        this.tier = par1NBTTagCompound.getInteger("tier");
        this.fangXiang = par1NBTTagCompound.getByte("facingDirection");
        this.gaoDu = par1NBTTagCompound.getShort("gaoDu");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("tier", this.tier);
        par1NBTTagCompound.setByte("facingDirection", this.fangXiang);
        par1NBTTagCompound.setShort("gaoDu", this.gaoDu);
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
        this.getEnergyStorage().setCapacity(getEnergyCapacity());
    }

    @Override
    public ForgeDirection getDirection()
    {
        return ForgeDirection.getOrientation(this.fangXiang);
    }

    @Override
    public void setDirection(ForgeDirection facingDirection)
    {
        this.fangXiang = (byte) facingDirection.ordinal();
    }

    public long getLaunchCost()
    {
        switch (this.getTier())
        {
            case 0:
                return 50000;
            case 1:
                return 80000;
        }

        return 100000;
    }

    @Override
    public boolean use(EntityPlayer entityPlayer, int side, Vector3 hit)
    {
        if(!this.worldObj.isRemote)
        	entityPlayer.openGui(ICBMExplosion.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        return true;
    }

    @Override
    public LauncherType getLauncherType()
    {
        return LauncherType.TRADITIONAL;
    }

    @Override
    public IMissile getMissile()
    {
        if (this.laucherBase != null)
        {
            return this.laucherBase.getContainingMissile();
        }

        return null;
    }
}
