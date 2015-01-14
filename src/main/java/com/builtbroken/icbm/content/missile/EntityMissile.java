package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.IMissile;
import com.builtbroken.icbm.content.crafting.missile.MissileSizes;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosive;
import com.builtbroken.mc.api.explosive.IExplosiveContainer;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.entity.EntityProjectile;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Basic missile like projectile that explodes on impact
 */
public class EntityMissile extends EntityProjectile implements IExplosiveContainer, IMissile, IEntityAdditionalSpawnData
{

    private Missile missile;

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
    public static void fireMissileByEntity(EntityLivingBase entity, ItemStack missile, MissileSizes size)
    {
        EntityMissile entityMissile = new EntityMissile(entity);
        entityMissile.setMissile(MissileSizes.loadMissile(missile));
        entityMissile.setTicksInAir(1);
        entityMissile.setMotion(1);
        entityMissile.worldObj.spawnEntityInWorld(entityMissile);

        //Player audio effect
        entityMissile.worldObj.playSoundAtEntity(entityMissile, ICBM.PREFIX + "missilelaunch", 4F, (1.0F + (entityMissile.worldObj.rand.nextFloat() - entityMissile.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

    }

    @Override
    public String getCommandSenderName()
    {
        return getMissile() == null ? "Unknown-Missile" : getMissile().getWarhead() == null ? "Missile-Module" : "Missile with " + getMissile().getWarhead().ex.toString() + " warhead";
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
            Pos position = new Pos(this);
            // The distance of the smoke relative
            // to the missile.
            double distance = - 1.2f;

            // The horizontal distance of the
            // smoke.
            double dH = Math.cos(Math.toRadians(this.rotationPitch)) * distance;
            // The delta X and Z.
            // The delta Y of the smoke.
            Pos delta = new Pos(Math.sin(Math.toRadians(this.rotationYaw)) * dH, Math.sin(Math.toRadians(this.rotationPitch)) * distance, Math.cos(Math.toRadians(this.rotationYaw)) * dH);

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
    public IExplosive getExplosive()
    {
        return getMissile() != null && getMissile().getWarhead() != null ? getMissile().getWarhead().ex : null;
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

    public Missile getMissile()
    {
        return missile;
    }

    public void setMissile(Missile missile)
    {
        this.missile = missile;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        if(nbt.hasKey("missileStack"))
        {
            ItemStack stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("missileStack"));
            setMissile(MissileSizes.loadMissile(stack));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        if(getMissile() != null)
        {
            ItemStack stack = getMissile().toStack();
            nbt.setTag("missileStack", stack.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeEntityToNBT(tag);
        ByteBufUtils.writeTag(buffer, tag);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        readEntityFromNBT(ByteBufUtils.readTag(additionalData));
    }
}