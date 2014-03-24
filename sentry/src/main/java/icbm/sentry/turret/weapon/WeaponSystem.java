package icbm.sentry.turret.weapon;

import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.interfaces.ITurretUpgrade;
import icbm.sentry.interfaces.IWeaponSystem;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.vector.IVector3;
import universalelectricity.api.vector.IVectorWorld;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;
import calclavia.api.IRotation;
import calclavia.api.icbm.sentry.IAmmunition;
import calclavia.api.icbm.sentry.ProjectileType;
import calclavia.lib.utility.inventory.InventoryUtility;

/** Modular system designed to handle all weapon related firing, and impact for an object. Requires
 * that it be constructed with a defined location. This location can be provided by using an entity,
 * tile, or world location.
 * 
 * @author DarkGuardsman */
public abstract class WeaponSystem implements IWeaponSystem, IVectorWorld, IRotation
{
    //Location of the weapon system
    private VectorWorld host_vector = null;
    private Entity host_entity = null;
    private TileEntity host_tile = null;

    //Properties
    protected float rayTraceLimit = 100;
    protected int itemsConsumedPerShot = 1;
    protected Vector3 aimOffset = new Vector3();
    protected IInventory ammoBay = null;

    public WeaponSystem(VectorWorld pos)
    {
        this.host_vector = pos;
    }

    public WeaponSystem(TileEntity tile)
    {
        this.host_tile = tile;
        if (tile instanceof IInventory)
        {
            ammoBay = (IInventory) tile;
        }
    }

    public WeaponSystem(Entity entity)
    {
        this.host_entity = entity;
        if (entity instanceof IInventory)
        {
            ammoBay = (IInventory) entity;
        }
    }

    public WeaponSystem(ITurret turret)
    {
        if (turret instanceof Entity)
        {
            this.host_entity = (Entity) turret;
        }
        else if (turret instanceof TileEntity)
        {
            this.host_tile = (TileEntity) turret;
        }
        else if (turret.getHost() instanceof Entity)
        {
            this.host_entity = (Entity) turret.getHost();
        }
        else if (turret.getHost() instanceof TileEntity)
        {
            this.host_tile = (TileEntity) turret.getHost();
        }
        else
        {
            throw new IllegalArgumentException("Failed to create WeaponSystem, could not define a host location or type");
        }
    }

    public void setHost(VectorWorld placement)
    {
        this.host_vector = placement;
    }

    public void setHost(TileEntity host)
    {
        this.host_tile = host;
    }

    public void setHost(Entity host)
    {
        this.host_entity = host;
    }

    @Override
    public World world()
    {
        if (host_entity != null)
            return host_entity.worldObj;
        else if (host_tile != null)
            return host_tile.getWorldObj();
        else
            return host_vector.world;
    }

    @Override
    public double x()
    {
        if (host_entity != null)
            return host_entity.posX;
        else if (host_tile != null)
            return host_tile.xCoord + 0.5;
        else
            return this.host_vector.x;
    }

    @Override
    public double y()
    {
        if (host_entity != null)
            return host_entity.posY;
        else if (host_tile != null)
            return host_tile.yCoord + 0.5;
        else
            return this.host_vector.y;
    }

    @Override
    public double z()
    {
        if (host_entity != null)
            return host_entity.posZ;
        else if (host_tile != null)
            return host_tile.zCoord + 0.5;
        else
            return this.host_vector.z;
    }

    @Override
    public double yaw()
    {
        if (turret() != null)
            return turret().yaw();
        else if (host_entity != null)
            return host_entity.rotationYaw;
        else
            return 0.0;
    }

    @Override
    public double pitch()
    {
        if (turret() != null)
            return turret().pitch();
        else if (host_entity != null)
            return host_entity.rotationPitch;
        else
            return 0.0;
    }

    @Override
    public double roll()
    {
        return 0.0;
    }

    @Override
    public void fire(IVector3 target)
    {
        doFire(target);
    }

    @Override
    public void fire(Entity entity)
    {
        fire(Vector3.fromCenter(entity));
    }

    /** Internal version of fire(Vector3) allowing repeat fire events */
    protected void doFire(IVector3 target)
    {
        Vector3 hit = new Vector3(target);
        MovingObjectPosition endTarget = getBarrelEnd().rayTrace(world(), hit, true);
        if (endTarget != null)
        {
            if (endTarget.typeOfHit == EnumMovingObjectType.ENTITY)
            {
                onHitEntity(endTarget.entityHit);
            }
            else if (endTarget.typeOfHit == EnumMovingObjectType.TILE)
            {
                onHitBlock(new Vector3(endTarget.hitVec));
            }
        }
    }

    @Override
    public void fireClient(IVector3 hit)
    {
        drawParticleStreamTo(world(), getBarrelEnd(), hit);
    }

    /** Draws a particle stream towards a location.
     * 
     * @author Based on MachineMuse */
    public void drawParticleStreamTo(World world, Vector3 start, IVector3 hit)
    {
        Vector3 direction = start.toAngle(hit).toVector();
        double scale = 0.02;
        Vector3 currentPoint = start.clone();
        Vector3 difference = new Vector3(hit).difference(start);
        double magnitude = difference.getMagnitude();

        while (currentPoint.distance(hit) > scale)
        {
            world.spawnParticle("townaura", currentPoint.x, currentPoint.y, currentPoint.z, 0.0D, 0.0D, 0.0D);
            currentPoint.add(difference.clone().scale(scale));
        }
    }

    /** Gets the current end point for the barrel in the world */
    protected Vector3 getBarrelEnd()
    {
        return new Vector3(x(), y(), z()).translate(this.aimOffset);
    }

    /** Called when the weapon hits an entity */
    protected void onHitEntity(Entity entity)
    {

    }

    /** Called when the weapon hits a block */
    protected void onHitBlock(Vector3 block)
    {

    }

    /** Checked to see if the ItemStack is ammo for the weapon */
    public boolean isAmmo(ItemStack stack)
    {
        return stack != null && stack.getItem() instanceof IAmmunition && ((IAmmunition) stack.getItem()).getType(stack) != ProjectileType.UNKNOWN;
    }

    @Override
    public boolean canFire()
    {
        return consumeAmmo(itemsConsumedPerShot, false);
    }

    /** Used to consume ammo or check if ammo can be consumed
     * 
     * @param count - number of items to consume
     * @param doConsume - true items will be consumed
     * @return true if all rounds were consumed */
    public boolean consumeAmmo(int count, boolean doConsume)
    {
        int consumeCount = 0;
        int need = count;

        if (count > 0 && ammoBay != null)
        {
            for (int slot = 0; slot < ammoBay.getSizeInventory(); slot++)
            {
                ItemStack itemStack = ammoBay.getStackInSlot(slot);

                if (isAmmo(itemStack))
                {
                    IAmmunition ammo = (IAmmunition) itemStack.getItem();
                    if (ammo.getAmmoCount(itemStack) >= need)
                    {
                        if (doConsume)
                        {
                            ammoBay.setInventorySlotContents(slot, this.doConsumeAmmo(itemStack, need));
                        }
                        return true;
                    }
                    else
                    {
                        int consume = need - ammo.getAmmoCount(itemStack);
                        if (doConsume)
                        {
                            ammoBay.setInventorySlotContents(slot, this.doConsumeAmmo(itemStack, consume));
                        }
                        need -= consume;
                    }
                    consumeCount += ammo.getAmmoCount(itemStack);
                }
            }
        }

        return consumeCount >= count;
    }

    /** Internal method for handle what happens when ammo is consumed
     * 
     * @return ItemStack that goes back into the inventory after removing a few rounds of ammo from
     * it */
    protected ItemStack doConsumeAmmo(ItemStack stack, int sum)
    {
        if (stack != null && sum > 0)
        {
            if (!(stack.getItem() instanceof IAmmunition))
            {
                ItemStack splitStack = stack.splitStack(sum);
                if (stack != null && stack.stackSize > 0)
                {
                    return splitStack;
                }
            }
            else
            {
                IAmmunition ammo = (IAmmunition) stack.getItem();
                ItemStack shell = ammo.getShell(stack, sum);
                ItemStack ammo_left = ammo.consumeAmmo(stack, sum);

                if (turret() != null && shell != null)
                {
                    if (turret().upgrades().containsKey(ITurretUpgrade.SHELL_COLLECTOR))
                        shell = InventoryUtility.putStackInInventory(ammoBay, shell, ForgeDirection.UNKNOWN.ordinal(), true);
                    if (shell != null)
                        InventoryUtility.dropItemStack(world(), x(), y(), z(), shell, 5, 0);
                }
                if (ammo_left != null && ammo_left.stackSize > 0)
                {
                    return ammo_left;
                }
                return null;
            }
        }
        return stack;
    }

    public ITurret turret()
    {
        if (this.host_entity instanceof ITurret)
        {
            return (ITurret) this.host_entity;
        }
        if (this.host_entity instanceof ITurretProvider)
        {
            if (((ITurretProvider) this.host_entity).getTurret() != null)
            {
                return ((ITurretProvider) this.host_entity).getTurret();
            }
        }
        if (this.host_tile instanceof ITurret)
        {
            return (ITurret) this.host_tile;
        }
        if (this.host_tile instanceof ITurretProvider)
        {
            if (((ITurretProvider) this.host_tile).getTurret() != null)
            {
                return ((ITurretProvider) this.host_tile).getTurret();
            }
        }
        return null;
    }
}
