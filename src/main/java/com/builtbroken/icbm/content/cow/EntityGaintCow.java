package com.builtbroken.icbm.content.cow;

import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.missile.data.missile.MissileSize;
import com.builtbroken.icbm.content.missile.item.ItemMissile;
import com.builtbroken.jlib.data.network.IByteBufWriter;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketEntity;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIControlledByPlayer;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/1/2017.
 */
public class EntityGaintCow extends EntityCreature implements IPacketIDReceiver, IEntityAdditionalSpawnData, IByteBufWriter
{
    private final EntityAIControlledByPlayer aiControlledByPlayer;

    IMissile missile;

    public EntityGaintCow(World p_i1683_1_)
    {
        super(p_i1683_1_);
        this.setSize(0.9F * 4, 1.3F * 4);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiControlledByPlayer = new EntityAIControlledByPlayer(this, 0.3F));
        //this.tasks.addTask(3, new EntityAITempt(this, 1.25D, Items.wheat, false));
        //AFK Tasks
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }


    public EntityAIControlledByPlayer getAIControlledByPlayer()
    {
        return this.aiControlledByPlayer;
    }


    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.20000000298023224D);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(17, Byte.valueOf((byte) 0));
    }

    @Override
    public boolean interact(EntityPlayer player)
    {
        ItemStack heldItem = player.getHeldItem();

        if (heldItem != null)
        {
            if (heldItem.getItem() == Items.saddle)
            {
                if (!getSaddled() && !worldObj.isRemote)
                {
                    setSaddled(true);
                    if (!player.capabilities.isCreativeMode)
                    {
                        player.inventory.decrStackSize(player.inventory.currentItem, 1);
                        player.inventoryContainer.detectAndSendChanges();
                    }
                }
            }
            else if (heldItem.getItem() == InventoryUtility.getItem("icbm:smallsilo")) //TODO cache item
            {
                setHasMissileLauncher(true);
            }
            else if (heldItem.getItem() instanceof ItemMissile) //TODO use API
            {
                if (missile == null)
                {
                    IMissile missile = ((ItemMissile) heldItem.getItem()).toMissile(heldItem);
                    if (missile.getMissileSize() == MissileSize.SMALL.ordinal() && !worldObj.isRemote)
                    {
                        this.missile = missile;
                        if (!player.capabilities.isCreativeMode)
                        {
                            player.inventory.decrStackSize(player.inventory.currentItem, 1);
                            player.inventoryContainer.detectAndSendChanges();
                        }
                        syncDataToClient();
                    }
                }
            }
        }
        else if (this.getSaddled() && !this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == player))
        {
            player.mountEntity(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean isAIEnabled()
    {
        return true;
    }

    @Override
    public boolean canBeSteered()
    {
        ItemStack itemstack = ((EntityPlayer) this.riddenByEntity).getHeldItem();
        return itemstack != null && itemstack.getItem() == Items.carrot_on_a_stick;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("Saddle", this.getSaddled());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.setSaddled(nbt.getBoolean("Saddle"));
    }

    @Override
    protected String getLivingSound()
    {
        return "mob.cow.say";
    }

    @Override
    protected String getHurtSound()
    {
        return "mob.cow.hurt";
    }

    @Override
    protected String getDeathSound()
    {
        return "mob.cow.hurt";
    }

    @Override
    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        this.playSound("mob.cow.step", 0.15F, 1.0F);
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }

    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
        if (this.getSaddled())
        {
            this.dropItem(Items.saddle, 1);
        }
    }

    /**
     * Returns true if the pig is saddled.
     */
    public boolean getSaddled()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    /**
     * Set or remove the saddle of the pig.
     */
    public void setSaddled(boolean b)
    {
        if (b)
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) 1));
        }
        else
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) 0));
        }
    }

    /**
     * Gets if the entity has a missile launcher
     */
    public boolean getHasMissileLauncher()
    {
        return (this.dataWatcher.getWatchableObjectByte(17) & 1) != 0;
    }

    /**
     * Sets that the entity has a missile launcher
     *
     * @param b
     */
    public void setHasMissileLauncher(boolean b)
    {
        if (b)
        {
            this.dataWatcher.updateObject(17, Byte.valueOf((byte) 1));
        }
        else
        {
            this.dataWatcher.updateObject(17, Byte.valueOf((byte) 0));
        }
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (id == -1)
        {
            readSpawnData(buf);
        }
        return false;
    }

    protected void syncDataToClient()
    {
        PacketEntity packetEntity = new PacketEntity(this).add(this);
        Engine.packetHandler.sendToAllAround(packetEntity, worldObj, posX, posY, posZ, 100);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        //TODO sync missile
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        //TODO sync missile
    }

    @Override
    public ByteBuf writeBytes(ByteBuf buf)
    {
        writeSpawnData(buf);
        return buf;
    }
}
