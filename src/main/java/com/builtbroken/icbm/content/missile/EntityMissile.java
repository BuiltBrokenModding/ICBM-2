package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.IMissile;
import com.builtbroken.icbm.content.crafting.missile.MissileSizes;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import resonant.api.explosive.IExplosive;
import resonant.api.explosive.IExplosiveContainer;
import resonant.api.TriggerCause;
import resonant.lib.world.explosive.ExplosiveRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import resonant.lib.prefab.EntityProjectile;
import resonant.lib.transform.vector.Vector3;

/**
 * Basic missile like projectile that explodes on impact
 */
public class EntityMissile extends EntityProjectile implements IExplosiveContainer, IMissile
{

    protected Missile missile;

    public EntityMissile(World w)
    {
        super(w);
        this.setSize(.5F, .5F);
    }

    public EntityMissile(EntityLivingBase entity)
    {
        super(entity);
        this.setSize(.5F, .5F);
    }

    /**
     * Fires a missile from the entity using its facing direction and location. For more
     * complex launching options create your own implementation.
     *
     * @param entity  - entity that is firing the missile, most likely a player with a launcher
     * @param missile - item stack that represents the missile plus explosive settings to fire
     */
    public static void fireMissileByEntity(EntityLivingBase entity, ItemStack missile)
    {
        EntityMissile entityMissile = new EntityMissile(entity);
        entityMissile.missile = MissileSizes.loadMissile(missile);
        entityMissile.setTicksInAir(1);
        entityMissile.setMotion(1);
        entityMissile.worldObj.spawnEntityInWorld(entityMissile);

        //Player audio effect
        entityMissile.worldObj.playSoundAtEntity(entityMissile, ICBM.PREFIX + "missilelaunch", 4F, (1.0F + (entityMissile.worldObj.rand.nextFloat() - entityMissile.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

    }

    @Override
    public String getCommandSenderName()
    {
        return missile == null ? "Corrupted-Missile" : missile.getWarhead() == null ? "Missile-Module" : "Missile with " + missile.getWarhead().ex.toString() + " warhead";
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (!this.isCollided && !this.onGround && this.getTicksInAir() > 0)
            this.spawnMissileSmoke();

    }

    private void spawnMissileSmoke()
    {
        if (this.worldObj.isRemote)
        {
            Vector3 position = new Vector3(this);
            // The distance of the smoke relative
            // to the missile.
            double distance = - 1.2f;
            Vector3 delta = new Vector3();
            // The delta Y of the smoke.
            delta.y_$eq(Math.sin(Math.toRadians(this.rotationPitch)) * distance);
            // The horizontal distance of the
            // smoke.
            double dH = Math.cos(Math.toRadians(this.rotationPitch)) * distance;
            // The delta X and Z.
            delta.x_$eq(Math.sin(Math.toRadians(this.rotationYaw)) * dH);
            delta.z_$eq(Math.cos(Math.toRadians(this.rotationYaw)) * dH);

            position.add(delta);
            this.worldObj.spawnParticle("flame", position.x(), position.y(), position.z(), 0, 0, 0);
            ICBM.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
            position.multiply(1 - 0.001 * Math.random());
            ICBM.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
            position.multiply(1 - 0.001 * Math.random());
            ICBM.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
            position.multiply(1 - 0.001 * Math.random());
            ICBM.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        if(nbt.hasKey("missileStack"))
        {
            ItemStack stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("missileStack"));
            missile = MissileSizes.loadMissile(stack);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        if(missile != null)
        {
            ItemStack stack = missile.toStack();
            nbt.setTag("missileStack", stack.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public IExplosive getExplosive()
    {
        return missile != null && missile.getWarhead() != null ? missile.getWarhead().ex : null;
    }

    @Override
    protected void onStoppedMoving()
    {
        onImpact();
    }

    @Override
    protected void onImpact()
    {
        super.onImpact();
        NBTTagCompound tag = new NBTTagCompound();
        writeEntityToNBT(tag);
        ExplosiveRegistry.triggerExplosive(worldObj, posX, posY, posZ, getExplosive(), new TriggerCause.TriggerCauseEntity(this), 5, tag);
    }
}