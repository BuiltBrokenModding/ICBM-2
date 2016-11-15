package com.builtbroken.icbm.content.rail.entity;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.api.rails.ITransportCartHasItem;
import com.builtbroken.mc.api.rails.ITransportRail;
import com.builtbroken.mc.api.rails.ITransportRailBlock;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.lib.helper.MathUtility;
import com.builtbroken.mc.lib.helper.WrenchUtility;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.entity.EntityBase;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.inventory.filters.IInventoryFilter;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/29/2016.
 */
public class EntityCart extends EntityBase implements IPacketIDReceiver, IEntityAdditionalSpawnData, ITransportCartHasItem
{
    /** Speed the cart wants to be pushed at */
    public static final float PUSH_SPEED = 0.05f;

    /** Missile object being carried, if changed update {@link #cargoStack} */
    private IMissile cargoMissile;

    /** Missile ItemStack being carried, if changed update {@link #cargoMissile} */
    private ItemStack cargoStack;

    /** Side of the cart that the rail exists */
    public ForgeDirection railSide;
    /** Direction the rail below us is facing. */
    public ForgeDirection railDirection;
    /** Direction the cart is facing */
    public ForgeDirection facingDirection = ForgeDirection.NORTH;
    /** Last measure height of a the rail. */
    public float railHeight = 0.3f;

    /** Length of the cart */
    public float length = 3;
    /** Toggle to invalidate the collision box and have it reset next tick */
    private boolean invalidBox = false;
    /** Called to trigger a packet update next tick */
    private boolean updateClient = true;

    private CartTypes _cartType = CartTypes.SMALL;

    @SideOnly(Side.CLIENT)
    public double lastRenderX;
    @SideOnly(Side.CLIENT)
    public double lastRenderY;
    @SideOnly(Side.CLIENT)
    public double lastRenderZ;

    public EntityCart(World world)
    {
        super(world);
        height = 0.7f;
        width = .95f;
    }

    @Override
    public boolean hitByEntity(Entity entity)
    {
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode)
        {
            kill();
            return false;
        }
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        return boundingBox;
    }

    @Override
    public AxisAlignedBB getBoundingBox()
    {
        return boundingBox;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
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
                    updateClient = true;
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
                this.setRotation(rotationYaw + 90, rotationPitch);
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
                    updateClient = true;
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
    public void setType(final CartTypes cartType)
    {
        _cartType = cartType;
        this.width = getType().width;
        this.length = getType().length;
        invalidBox = true;
        updateClient = true;
    }

    /**
     * Gets the type of cart
     *
     * @return cart type
     */
    public CartTypes getType()
    {
        return _cartType;
    }

    @Override
    public void setPosition(double x, double y, double z)
    {
        //isPushedByWater()
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        invalidBox = true;
    }

    @Override
    public void setRotation(float yaw, float pitch)
    {
        this.rotationYaw = yaw % 360.0F;
        this.rotationPitch = pitch % 360.0F;
        invalidBox = true;

        float yaw2 = (float) MathUtility.clampAngleTo180(this.rotationYaw);
        if (yaw2 >= -45 && yaw2 <= 45)
        {
            facingDirection = ForgeDirection.NORTH;
        }
        else if (yaw2 <= -135 || yaw2 >= 135)
        {
            facingDirection = ForgeDirection.SOUTH;
        }
        else if (yaw2 >= 45 && yaw2 <= 135)
        {
            facingDirection = ForgeDirection.EAST;
        }
        else if (yaw2 >= -135 && yaw2 <= -45)
        {
            facingDirection = ForgeDirection.WEST;
        }
    }

    @Override
    public float getDesiredPushVelocity()
    {
        return PUSH_SPEED;
    }

    /**
     * Updates the collision box to match it's rotation
     */
    protected void validateBoundBox()
    {
        invalidBox = false;

        float halfWidth = this.width / 2.0F;
        float halfLength = this.length / 2.0F;
        float yaw = (float) Math.abs(MathUtility.clampAngleTo180(this.rotationYaw));
        if (yaw >= 45 && yaw <= 135)
        {
            halfWidth = this.length / 2.0F;
            halfLength = this.width / 2.0F;
        }
        this.boundingBox.setBounds(
                posX - (double) halfWidth,
                posY - (double) this.yOffset + (double) this.ySize,
                posZ - (double) halfLength,

                posX + (double) halfWidth,
                posY - (double) this.yOffset + (double) this.ySize + this.height,
                posZ + (double) halfLength);
    }


    @Override
    protected void setSize(float p_70105_1_, float p_70105_2_)
    {
        //Empty to prevent issues
    }

    @Override
    public void onUpdate()
    {
        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;

        final CartTypes type = getType();

        //Checks if the size changed, No change should result in zero
        if (Math.abs(type.length - length - type.width - width) > 0.1)
        {
            length = type.length;
            width = type.width;
            invalidBox = true;
        }

        //Updates the collision box
        if (invalidBox || boundingBox == null)
        {
            validateBoundBox();
        }

        //Grab rail side if null
        if (railSide == null)
        {
            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
            {
                Pos pos = new Pos((Entity) this).add(side);
                Block block = pos.getBlock(worldObj);
                final TileEntity tile = pos.getTileEntity(worldObj);
                if (!(block instanceof ITransportRailBlock || tile instanceof ITransportRail))
                {
                    railSide = side;
                    break;
                }
            }
        }

        Pos pos = new Pos((Entity) this).floor();
        Block block = pos.getBlock(worldObj);
        if (block == null)
        {
            pos = pos.add(railSide);
            block = pos.getBlock(worldObj);
        }
        final TileEntity tile = pos.getTileEntity(worldObj);

        if (!worldObj.isRemote)
        {
            //Kills entity if it falls out of the world, should never happen
            if (this.posY < -64.0D)
            {
                this.kill();
                return;
            }
            //Breaks entity if its not on a track
            else if (!(block instanceof ITransportRailBlock || tile instanceof ITransportRail))
            {
                destroyCart();
                return;
            }
            //Moves the entity
            if (motionX != 0 || motionY != 0 || motionZ != 0)
            {
                moveEntity(motionX, motionY, motionZ);
                recenterCartOnRail();
            }
        }

        if (isAirBorne)
        {
            motionY -= (9.8 / 20);
            motionY = Math.max(-2, motionY);
        }

        //Handles pushing entities out of the way
        doCollisionLogic();

        //Updates the rail and cart position
        if (tile instanceof ITransportRail)
        {
            ((ITransportRail) tile).tickRailFromCart(this);
        }
        else if (block instanceof ITransportRailBlock)
        {
            ((ITransportRailBlock) block).tickRailFromCart(this, world(), pos.xi(), pos.yi(), pos.zi(), worldObj.getBlockMetadata(pos.xi(), pos.yi(), pos.zi()));
        }

        if (!worldObj.isRemote && updateClient)
        {
            sentDescriptionPacket();
        }
    }

    @Override
    public void recenterCartOnRail()
    {
        this.recenterCartOnRail(railSide, railDirection, railHeight, false);
    }

    @Override
    public void recenterCartOnRail(ForgeDirection side, ForgeDirection facing, double railHeight, boolean trueCenter)
    {
        //Update cached data
        this.railDirection = facing;
        this.railHeight = (float) railHeight;
        this.railSide = side;

        if (side == ForgeDirection.UP)
        {
            posY = Math.floor(posY) + railHeight;
            motionY = 0;
        }
        else if (side == ForgeDirection.DOWN)
        {
            posY = Math.floor(posY) + 1 - railHeight;
            motionY = 0;
        }

        if (facing == ForgeDirection.NORTH || facing == ForgeDirection.SOUTH)
        {
            motionX = 0;
            posX = Math.floor(posX) + 0.5;
            if (trueCenter)
            {
                posZ = Math.floor(posZ) + 0.5;
            }
        }
        else if (facing == ForgeDirection.EAST || facing == ForgeDirection.WEST)
        {
            motionZ = 0;
            posZ = Math.floor(posZ) + 0.5;
            if (trueCenter)
            {
                posX = Math.floor(posX) + 0.5;
            }
        }
    }

    @Override
    public void setMotion(double x, double y, double z)
    {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    protected void doCollisionLogic()
    {
        AxisAlignedBB box = boundingBox.expand(0.2D, 0.0D, 0.2D);

        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, box);

        if (list != null && !list.isEmpty())
        {
            for (Object aList : list)
            {
                Entity entity = (Entity) aList;

                if (entity != this.riddenByEntity && entity.canBePushed())
                {
                    entity.applyEntityCollision(this);
                }
            }
        }
    }

    @Override
    public void applyEntityCollision(Entity entity)
    {
        //TODO toggle a center rail method to call next tick
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0;
    }

    public void destroyCart()
    {
        InventoryUtility.dropItemStack(this, toStack());
        setDead();
    }

    public ItemStack toStack()
    {
        return new ItemStack(ICBM.itemMissileCart, 1, getType().ordinal());
    }

    @Override
    public void onEntityUpdate()
    {
        //Empty to ignore default entity code
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        setType(CartTypes.values()[nbt.getInteger("cartType")]);
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
        updateClient = true;
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
        setType(CartTypes.values()[additionalData.readInt()]);
        if (additionalData.readBoolean())
        {
            setCargo(ByteBufUtils.readItemStack(additionalData));
        }
        else
        {
            setCargo(null);
        }
        invalidBox = true;
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
