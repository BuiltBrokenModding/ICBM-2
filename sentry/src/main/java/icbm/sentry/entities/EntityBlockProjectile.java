package icbm.sentry.entities;

import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import calclavia.lib.utility.inventory.InventoryUtility;

/** Fake entity that renders just like a block and is used as a projectile.
 * 
 * @author Darkguardsman */
public class EntityBlockProjectile extends EntityThrowable
{
    ItemStack stack = null;

    public EntityBlockProjectile(World world)
    {
        super(world);
    }

    public EntityBlockProjectile(World world, ItemStack stack)
    {
        this(world);
        this.stack = stack;
    }

    @Override
    protected void onImpact(MovingObjectPosition movingobjectposition)
    {
        if (stack != null)
        {
            if (movingobjectposition != null && movingobjectposition.entityHit != null)
            {
                float damageFromMotion = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

                movingobjectposition.entityHit.attackEntityFrom(DamageSource.fallingBlock, Math.max(damageFromMotion, 5));
            }

            InventoryUtility.dropItemStack(this.worldObj, this.posX, this.posY, this.posZ, stack, 10, 0);
        }
        this.setDead();
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("Stack"))
        {
            this.stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Stack"));
        }
        if (this.stack == null)
        {
            this.setDead();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        if (this.stack != null)
        {
            nbt.setCompoundTag("Stack", this.stack.writeToNBT(new NBTTagCompound()));
        }
    }

}
