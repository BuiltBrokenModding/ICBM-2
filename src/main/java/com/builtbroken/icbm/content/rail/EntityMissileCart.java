package com.builtbroken.icbm.content.rail;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.api.rails.ITransportCartHasItem;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.lib.helper.WrenchUtility;
import com.builtbroken.mc.prefab.entity.cart.EntityAbstractCart;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.api.IInventoryFilter;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/29/2016.
 */
public class EntityMissileCart extends EntityAbstractCart implements IPacketIDReceiver, IEntityAdditionalSpawnData, ITransportCartHasItem
{
    private MissileCartTypes _cartType = MissileCartTypes.SMALL;

    /** Missile object being carried, if changed update {@link #cargoStack} */
    private IMissile cargoMissile;

    /** Missile ItemStack being carried, if changed update {@link #cargoMissile} */
    private ItemStack cargoStack;

    public EntityMissileCart(World world)
    {
        super(world);
    }

    @Override
    public boolean interactFirst(EntityPlayer player)
    {
        if (player.getHeldItem() == null)
        {
            if (cargoStack != null)
            {
                if (!worldObj.isRemote)
                {
                    //TODO add handling for stacking missile
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, cargoStack);
                    player.inventoryContainer.detectAndSendChanges();
                    setCargo(null);
                    markForClientSync();
                }
                return true;
            }
            else
            {
                if (!worldObj.isRemote)
                {
                    //TODO add handling for stacking missile
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, toStack());
                    player.inventoryContainer.detectAndSendChanges();
                    kill();
                }
                return true;
            }
        }
        else if (WrenchUtility.isWrench(player.getHeldItem()))
        {
            if (!worldObj.isRemote)
            {
                //TODO may need to modify based on side
                this.setCartRotation(rotationYaw + 90, rotationPitch);
            }
            return true;
        }
        else if (cargoStack == null) //TODO add handling for stacking missile
        {
            ItemStack heldItem = player.getHeldItem();
            IMissile missile = null;
            if (heldItem.getItem() instanceof IMissileItem)
            {
                missile = ((IMissileItem) heldItem.getItem()).toMissile(heldItem);
            }
            else if (heldItem.getItem() instanceof IModuleItem)
            {
                IModule module = ((IModuleItem) heldItem.getItem()).getModule(heldItem);
                if (module instanceof IMissile)
                {
                    missile = (IMissile) module;
                }
            }
            else
            {
                missile = MissileModuleBuilder.INSTANCE.buildMissile(heldItem);
            }

            if (missile != null)
            {
                if (!worldObj.isRemote)
                {
                    ItemStack copyStack = heldItem.copy();
                    copyStack.stackSize = 1;
                    setCargo(copyStack);
                    heldItem.stackSize -= 1;
                    if (heldItem.stackSize <= 0)
                    {
                        heldItem = null;
                    }
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, heldItem);
                    player.inventoryContainer.detectAndSendChanges();
                    markForClientSync();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the type of the cart, should
     * never be run after the cart is created
     *
     * @param cartType - type of the cart
     */
    public void setType(final MissileCartTypes cartType)
    {
        _cartType = cartType;
        this.width = getType().width;
        this.length = getType().length;
        markBoundsInvalid();
        markForClientSync();
    }

    /**
     * Gets the type of cart
     *
     * @return cart type
     */
    public MissileCartTypes getType()
    {
        return _cartType;
    }

    public ItemStack toStack()
    {
        return new ItemStack(ICBM.itemMissileCart, 1, getType().ordinal());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        setType(MissileCartTypes.values()[nbt.getInteger("cartType")]);
        if (nbt.hasKey("missile"))
        {
            setCargo(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("missile")));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("cartType", getType().ordinal());
        if (cargoStack != null)
        {
            nbt.setTag("missile", cargoStack.writeToNBT(new NBTTagCompound()));
        }
    }

    /**
     * Cargo missile being carried
     *
     * @return
     */
    public IMissile getCargoMissile()
    {
        return cargoMissile;
    }

    /**
     * Called to set the missile object data.
     * Never call this directly unless you need
     * special handling. Instead call {@link #setCargo(ItemStack)}
     *
     * @param cargoMissile - missile object
     */
    public void setCargoMissile(IMissile cargoMissile)
    {
        this.cargoMissile = cargoMissile;
        markForClientSync();
    }

    /**
     * Sets the ItemStack, representing a missile,
     * that is being carried as cargo
     *
     * @param cargo - cargo, should be convertable to a IMissile object
     */
    public void setCargo(ItemStack cargo)
    {
        this.cargoStack = cargo;
        if (cargo == null)
        {
            setCargoMissile(null);
        }
        else
        {
            IMissile missile = null;
            if (cargo.getItem() instanceof IMissileItem)
            {
                missile = ((IMissileItem) cargo.getItem()).toMissile(cargo);
            }
            else if (cargo.getItem() instanceof IModuleItem)
            {
                IModule module = ((IModuleItem) cargo.getItem()).getModule(cargo);
                if (module instanceof IMissile)
                {
                    missile = (IMissile) module;
                }
            }
            else
            {
                missile = MissileModuleBuilder.INSTANCE.buildMissile(cargo);
            }
            setCargoMissile(missile);
        }
    }

    /**
     * Checks if the missile can be stored inside of the cart
     *
     * @param missile - missile to test
     * @return true if it can
     */
    public boolean canAcceptMissile(IMissile missile)
    {
        return missile.getMissileSize() == getType().supportedCasingSize.ordinal();
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        super.writeSpawnData(buffer);
        buffer.writeInt(getType().ordinal());
        buffer.writeBoolean(cargoStack != null);
        if (cargoStack != null)
        {
            ByteBufUtils.writeItemStack(buffer, cargoStack);
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        super.readSpawnData(additionalData);
        setType(MissileCartTypes.values()[additionalData.readInt()]);
        if (additionalData.readBoolean())
        {
            setCargo(ByteBufUtils.readItemStack(additionalData));
        }
        else
        {
            setCargo(null);
        }
        markBoundsInvalid();
    }

    @Override
    public ItemStack getPickedResult(MovingObjectPosition target)
    {
        return toStack();
    }

    @Override
    public ItemStack getTransportedItem()
    {
        return cargoStack;
    }

    @Override
    public ItemStack setTransportedItem(ItemStack stack)
    {
        if (stack == null)
        {
            cargoStack = null;
        }
        else if (cargoStack == null)
        {
            ItemStack copy = stack.copy();
            copy.stackSize = 1;
            stack.stackSize -= 1;
            cargoStack = copy;
        }
        else if (InventoryUtility.stacksMatch(cargoStack, stack))
        {
            int room = InventoryUtility.roomLeftInStack(cargoStack);
            if (room > 0)
            {
                cargoStack.stackSize += room;
                stack.stackSize -= room;
                return stack;
            }
        }
        return stack;
    }

    @Override
    public boolean canAcceptItemForTransport(ItemStack stack)
    {
        return getInventoryFilter().isStackInFilter(stack);
    }

    @Override
    public IInventoryFilter getInventoryFilter()
    {
        return getType().filter;
    }
}
