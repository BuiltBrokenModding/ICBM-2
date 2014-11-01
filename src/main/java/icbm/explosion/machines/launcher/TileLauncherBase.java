package icbm.explosion.machines.launcher;

import icbm.Settings;
import icbm.core.ICBMCore;
import icbm.explosion.entities.EntityMissile;
import icbm.explosion.ex.Explosion;
import icbm.explosion.explosive.ExplosiveRegistry;
import icbm.explosion.items.ItemMissile;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.api.IRotatable;
import resonant.api.ITier;
import resonant.api.explosion.ExplosiveType;
import resonant.api.explosion.ILauncherContainer;
import resonant.api.explosion.ILauncherController;
import resonant.api.explosion.IMissile;
import resonant.api.explosion.ExplosionEvent.ExplosivePreDetonationEvent;
import resonant.engine.ResonantEngine;
import resonant.lib.multiblock.reference.IMultiBlock;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketReceiver;
import resonant.lib.transform.vector.IVectorWorld;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.utility.LanguageUtility;
import resonant.lib.content.prefab.java.TileInventory;
import com.google.common.io.ByteArrayDataInput;

import java.util.ArrayList;
import java.util.List;

/** This tile entity is for the base of the missile launcher
 * 
 * @author Calclavia */
public class TileLauncherBase extends TileInventory implements IPacketReceiver, ILauncherContainer, IRotatable, ITier, IMultiBlock
{
    // The missile that this launcher is holding
    public IMissile missile = null;

    // The connected missile launcher frame
    public TileLauncherFrame supportFrame = null;

    // The tier of this launcher base
    private int tier = 0;

    private ForgeDirection facingDirection = ForgeDirection.NORTH;

    private boolean packetGengXin = true;

    public TileLauncherBase()
    {
        super(Material.iron);
    }

    /** Returns the name of the inventory. */
    @Override
    public String getInventoryName()
    {
        return LanguageUtility.getLocal("gui.launcherBase.name");
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
    }

    /** Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner
     * uses this to count ticks and creates a new spawn inside its implementation. */
    @Override
    public void update()
    {
        super.update();

        if (this.supportFrame == null)
        {
            for (byte i = 2; i < 6; i++)
            {
                Vector3 position = new Vector3(this.xCoord, this.yCoord, this.zCoord);
                position.add(ForgeDirection.getOrientation(i));

                TileEntity tileEntity = this.worldObj.getTileEntity(position.xi(), position.yi(), position.zi());

                if (tileEntity instanceof TileLauncherFrame)
                {
                    this.supportFrame = (TileLauncherFrame) tileEntity;
                    // TODO this.supportFrame.setDirection(VectorHelper.getOrientationFromSide(ForgeDirection.getOrientation(i), ForgeDirection.NORTH));
                }
            }
        }
        else
        {
            if (this.supportFrame.isInvalid())
            {
                this.supportFrame = null;
            }
            else if (this.packetGengXin || this.ticks() % (20 * 30) == 0 && this.supportFrame != null && !this.worldObj.isRemote)
            {
                ResonantEngine.instance.packetHandler.sendToAllAround(this.getDescPacket(), (IVectorWorld) this, 64);
            }
        }

        if (!this.worldObj.isRemote)
        {
            this.setMissile();

            if (this.packetGengXin || this.ticks() % (20 * 30) == 0)
            {
                ResonantEngine.instance.packetHandler.sendToAllAround(this.getDescPacket(), (IVectorWorld) this, 64);
                this.packetGengXin = false;
            }
        }
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, (byte) this.facingDirection.ordinal(), this.tier);
    }

    @Override
    public void read(ByteBuf data, EntityPlayer player, PacketType type)
    {
        this.facingDirection = ForgeDirection.getOrientation(data.readByte());
        this.tier = data.readInt();
    }

    public void setMissile()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.getStackInSlot(0) != null)
            {
                if (this.getStackInSlot(0).getItem() instanceof ItemMissile)
                {
                    int explosiveID = this.getStackInSlot(0).getItemDamage();

                    if (ExplosiveRegistry.get(explosiveID) instanceof Explosion)
                    {
                        Explosion missile = (Explosion) ExplosiveRegistry.get(explosiveID);

                        ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(this.worldObj, this.xCoord, this.yCoord, this.zCoord, ExplosiveType.AIR, missile);
                        MinecraftForge.EVENT_BUS.post(evt);

                        if (!evt.isCanceled())
                        {
                            if (this.missile == null)
                            {
                                Vector3 startingPosition = new Vector3((this.xCoord + 0.5f), (this.yCoord + 1.8f), (this.zCoord + 0.5f));
                                this.missile = new EntityMissile(this.worldObj, startingPosition, new Vector3(x(), y(), z()), explosiveID);
                                this.worldObj.spawnEntityInWorld((Entity) this.missile);
                                return;
                            }
                            else
                            {
                                if (this.missile.getExplosiveType().getID() == explosiveID)
                                {
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            if (this.missile != null)
            {
                ((Entity) this.missile).setDead();
            }

            this.missile = null;
        }
    }

    /** Launches the missile
     * 
     * @param target - The target in which the missile will land in */
    public void launchMissile(Vector3 target, int gaoDu)
    {
        // Apply inaccuracy
        float inaccuracy;

        if (this.supportFrame != null)
        {
            inaccuracy = this.supportFrame.getInaccuracy();
        }
        else
        {
            inaccuracy = 30f;
        }

        inaccuracy *= (float) Math.random() * 2 - 1;

        target.addEquals(inaccuracy, 0, inaccuracy);

        this.decrStackSize(0, 1);
        this.missile.launch(target, gaoDu);
        this.missile = null;
    }

    // Checks if the missile target is in range
    public boolean isInRange(Vector3 target)
    {
        if (target != null)
            return !shiTaiYuan(target) && !shiTaiJin(target);

        return false;
    }

    /** Checks to see if the target is too close.
     * 
     * @param target
     * @return */
    public boolean shiTaiJin(Vector3 target)
    {
        // Check if it is greater than the minimum range
        if (new Vector3(this.xCoord, 0, this.zCoord).subtract(new Vector3(target.x(), 0, target.z())).magnitude() < 10)
        {
            return true;
        }

        return false;
    }

    // Is the target too far?
    public boolean shiTaiYuan(Vector3 target)
    {
        double distance = new Vector3(this.xCoord, 0, this.zCoord).subtract(new Vector3(target.x(), 0, target.z())).magnitude();
        // Checks if it is greater than the maximum range for the launcher base
        if (this.tier == 0)
        {
            if (distance < Settings.DAO_DAN_ZUI_YUAN / 10)
            {
                return false;
            }
        }
        else if (this.tier == 1)
        {
            if (distance < Settings.DAO_DAN_ZUI_YUAN / 5)
            {
                return false;
            }
        }
        else if (this.tier == 2)
        {
            if (distance < Settings.DAO_DAN_ZUI_YUAN)
            {
                return false;
            }
        }

        return true;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.tier = nbt.getInteger("tier");
        this.facingDirection = ForgeDirection.getOrientation(nbt.getByte("facingDirection"));
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("tier", this.tier);
        nbt.setByte("facingDirection", (byte) this.facingDirection.ordinal());
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
    }

    @Override
    public boolean use(EntityPlayer player, int side, Vector3 hit)
    {
        if (player.inventory.getCurrentItem() != null)
        {
            if (player.inventory.getCurrentItem().getItem() instanceof ItemMissile)
            {
                if (this.getStackInSlot(0) == null)
                {

                    this.setInventorySlotContents(0, player.inventory.getCurrentItem());
                    if (!player.capabilities.isCreativeMode)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    return true;
                }
                else
                {
                    ItemStack player_held = player.inventory.getCurrentItem();
                    if (!player.capabilities.isCreativeMode)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, this.getStackInSlot(0));
                    this.setInventorySlotContents(0, player_held);
                    return true;
                }
            }
        }
        else if (this.getStackInSlot(0) != null)
        {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, this.getStackInSlot(0));
            this.setInventorySlotContents(0, null);
            return true;
        }

        return true;
    }

    @Override
    public void invalidate()
    {
        if (this.missile != null)
        {
            ((Entity) this.missile).setDead();
        }

        super.invalidate();
    }

    @Override
    public List<Vector3> getMultiBlockVectors()
    {
        List<Vector3> blocks = new ArrayList<Vector3>();
        if (this.facingDirection == ForgeDirection.SOUTH || this.facingDirection == ForgeDirection.NORTH)
        {
            blocks.add(new Vector3(1, 0, 0));
            blocks.add(new Vector3(1, 1, 0));
            blocks.add(new Vector3(1, 2, 0));
            blocks.add(new Vector3(-1, 0, 0));
            blocks.add(new Vector3(-1, 1, 0));
            blocks.add(new Vector3(-1, 2, 0));
        }
        else
        {
            blocks.add(new Vector3(0, 0, 1));
            blocks.add(new Vector3(0, 1, 1));
            blocks.add(new Vector3(0, 2, 1));
            blocks.add(new Vector3(0, 0, -1));
            blocks.add(new Vector3(0, 1, -1));
            blocks.add(new Vector3(0, 2, -1));
        }
        return blocks;
    }

    @Override
    public ForgeDirection getDirection()
    {
        return this.facingDirection;
    }

    @Override
    public void setDirection(ForgeDirection facingDirection)
    {
        this.facingDirection = facingDirection;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        return slot == 0 && stack.getItem() instanceof ItemMissile;
    }

    @Override
    public IMissile getContainingMissile()
    {
        return this.missile;
    }

    @Override
    public void setContainingMissile(IMissile missile)
    {
        this.missile = missile;
    }

    @Override
    public ILauncherController getController()
    {
        for (byte i = 2; i < 6; i++)
        {
            Vector3 position = new Vector3(x(), y(), z()).add(ForgeDirection.getOrientation(i));

            TileEntity tileEntity = position.getTileEntity(this.worldObj);

            if (tileEntity instanceof ILauncherController)
            {
                return (ILauncherController) tileEntity;
            }
        }

        return null;
    }

    @Override
    public int[] getMissileSlots()
    {
        return new int[] { 0 };
    }
}
