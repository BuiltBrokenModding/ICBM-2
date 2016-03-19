package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.IFoF;
import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.icbm.content.missile.tracking.MissileTracker;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosive;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.WorldChangeHelper;
import com.builtbroken.mc.lib.world.radar.RadarRegistry;
import com.builtbroken.mc.prefab.entity.EntityProjectile;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Basic missile like projectile that explodes on impact
 */
public class EntityMissile extends EntityProjectile implements IExplosive, IMissileEntity, IEntityAdditionalSpawnData, IFoF
{
    private Missile missile;

    //Used for guided version
    public IPos3D target_pos;

    //Used to prevent reporting when de-spawning
    public boolean noReport = false;

    public String fofTag = "";

    public EntityMissile(World w)
    {
        super(w);
        this.setSize(.5F, .5F);
        this.inAirKillTime = 144000 /* 2 hours */;
    }

    public EntityMissile(EntityLivingBase entity)
    {
        super(entity.worldObj, entity, 1);
        this.setSize(.5F, .5F);
        this.inAirKillTime = 144000 /* 2 hours */;
    }

    @Override
    protected void entityInit()
    {
        this.dataWatcher.addObject(6, Float.valueOf(1.0F));
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (ticksInAir == 5 && !worldObj.isRemote)
        {
            RadarRegistry.add(this);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage)
    {
        if (!worldObj.isRemote && !isEntityInvulnerable() && damage > 0)
        {
            setHealth(Math.max(0, getHealth() - damage));
            if (getHealth() <= 0)
            {
                destroyMissile(this, DamageSource.generic, 0.1f, true, true, true);
                setDead();
            }
            return true;
        }
        return false;
    }

    public void heal(float hp)
    {
        float f1 = this.getHealth();

        if (f1 > 0.0F)
        {
            this.setHealth(f1 + hp);
        }
    }

    public final float getHealth()
    {
        return this.dataWatcher.getWatchableObjectFloat(6);
    }

    public void setHealth(float hp)
    {
        this.dataWatcher.updateObject(6, Float.valueOf(MathHelper.clamp_float(hp, 0.0F, missile != null ? missile.getMaxHitPoints() : 10)));
    }


    public void setTarget(IPos3D target, boolean ark)
    {
        this.target_pos = target;
    }

    @Override
    public void setTarget(double x, double y, double z, boolean ark)
    {
        this.target_pos = new Pos(x, y, z);
    }

    @Override
    public void setTarget(Entity entity, boolean track)
    {
        //TODO center to entity center if EntityLivingBase
        setTarget(new Pos(entity), false);
    }

    public IPos3D getCurrentTargetPos()
    {
        return this.target_pos;
    }

    @Override
    public int[] getCurrentTarget()
    {
        if (this.target_pos != null)
        {
            return new int[]{(int) target_pos.x(), (int) target_pos.y(), (int) target_pos.z()};
        }
        return null;
    }

    @Override
    public void destroyMissile(Object source, DamageSource damage, float scaleExplosion, boolean allowDetonationOfWarhead, boolean allowDetonationOfEngine, boolean allowDetonationOfOther)
    {
        //TODO implement
        if (allowDetonationOfWarhead && getMissile().getWarhead() != null)
        {
            WorldChangeHelper.ChangeResult result = getMissile().getWarhead().trigger(new TriggerCauseMissileDestroyed(source, damage, scaleExplosion), worldObj, posX, posY, posZ);
            if (result == WorldChangeHelper.ChangeResult.COMPLETED || result == WorldChangeHelper.ChangeResult.PARTIAL_COMPLETE_WITH_FAILURE)
            {
                worldObj.playSoundEffect(posX, posY, posZ, "random.explode", 2.0F, 0.5F + worldObj.rand.nextFloat() * 0.2F);
            }
        }
        setDead();
    }


    /**
     * Fires a missile from the entity using its facing direction and location. For more
     * complex launching options create your own implementation.
     *
     * @param entity  - entity that is firing the missile, most likely a player with a launcher
     * @param missile - item stack that represents the missile plus explosive settings to fire
     */
    public static void fireMissileByEntity(Entity entity, ItemStack missile)
    {
        Entity entityMissile = null;
        if (missile.getItem() instanceof IMissileItem)
        {
            entityMissile = ((IMissileItem) missile.getItem()).getMissileEntity(missile, entity);
            entityMissile.setWorld(entity.worldObj);
        }
        fireMissileByEntity(entityMissile);
    }

    public static void fireMissileByEntity(Entity entityMissile)
    {
        if (entityMissile instanceof IMissileEntity)
        {
            ((IMissileEntity) entityMissile).setIntoMotion();
            entityMissile.worldObj.spawnEntityInWorld(entityMissile);
            entityMissile.worldObj.playSoundAtEntity(entityMissile, "icbm:icbm.missilelaunch", ICBM.missile_firing_volume, (1.0F + (entityMissile.worldObj.rand.nextFloat() - entityMissile.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
        }
    }

    @Override
    public String getCommandSenderName()
    {
        return getMissile() == null ? "Unknown-Missile" : getMissile().getWarhead() == null ? "Missile-Module" : "Missile with " + getMissile().getWarhead().getExplosive().toString() + " warhead";
    }

    @Override
    public void setIntoMotion()
    {
        if (missile != null && missile.canLaunch())
        {
            ticksInAir = 1;
            updateMotion();
            missile.getEngine().onLaunch(this, missile);
        }
    }

    @Override
    protected void updateMotion()
    {
        super.updateMotion();
        if (target_pos != null && !worldObj.isRemote)
        {
            if (this.posY >= MissileTracker.MAX_SPAWN_OUT_Y)
            {
                MissileTracker.addToTracker(this);
                RadarRegistry.remove(this);
            }
        }
        if (worldObj.isRemote && this.ticksInAir > 0)
        {
            for (int i = 0; i < 4; i++)
            {
                ICBM.proxy.spawnRocketTail(this);
            }
            if (this.ticksInAir % 5 == 0)
            {

            }
        }
    }

    @Override
    public IExplosiveHandler getExplosive()
    {
        return getMissile() != null && getMissile().getWarhead() != null ? getMissile().getWarhead().getExplosive() : null;
    }

    @Override
    public NBTTagCompound getAdditionalExplosiveData()
    {
        return getMissile() != null && getMissile().getWarhead() != null ? getMissile().getWarhead().getAdditionalExplosiveData() : null;
    }

    @Override
    public double getExplosiveSize()
    {
        return getMissile() != null && getMissile().getWarhead() != null ? getMissile().getWarhead().getExplosiveSize() : 0;
    }

    @Override
    protected void onImpactEntity(Entity ent, float v)
    {
        super.onImpactEntity(ent, v);
        onImpact(ent.posX, ent.posY, ent.posZ);
    }

    @Override
    public void onImpactTile()
    {
        super.onImpactTile();
        if (!noReport && sourceOfProjectile != null)
        {
            TileEntity tile = sourceOfProjectile.getTileEntity(worldObj);
            if (tile instanceof TileAbstractLauncher)
            {
                ((TileAbstractLauncher) tile).onImpactOfMissile(this);
            }
        }
        onImpact(xTile, yTile, zTile);
    }

    @Override
    public void setDead()
    {
        if(!worldObj.isRemote)
        {
            RadarRegistry.remove(this);
        }
        super.setDead();
        if (!noReport && sourceOfProjectile != null)
        {
            TileEntity tile = sourceOfProjectile.getTileEntity(worldObj);
            if (tile instanceof TileAbstractLauncher)
            {
                ((TileAbstractLauncher) tile).onDeathOfMissile(this);
            }
        }
    }

    protected void onImpact(double x, double y, double z)
    {
        if (!worldObj.isRemote)
        {
            if (missile.getWarhead() != null)
            {
                missile.getWarhead().trigger(new TriggerCause.TriggerCauseEntity(this), worldObj, x, y, z);
            }
            if (missile != null && missile.getEngine() != null)
            {
                missile.getEngine().onDestroyed(this, missile);
            }
            this.setDead();
        }
        else
        {
            doClientImpact(x, y, z);
        }
    }

    protected void doClientImpact(double x, double y, double z)
    {
        System.out.println("Impacted client side at " + x + " " + y + " " + z);
        if (missile.getWarhead() != null)
        {
            missile.getWarhead().trigger(new TriggerCause.TriggerCauseEntity(this), worldObj, x, y, z);
        }
    }

    @Override
    protected void decreaseMotion()
    {
        //TODO do handling per size
        if (ticksInAir > 1000)
        {
            super.decreaseMotion();
        }
    }

    public Missile getMissile()
    {
        return missile;
    }

    public void setMissile(Missile missile)
    {
        this.missile = missile;
        this.inAirKillTime = missile.casing.maxFlightTimeInTicks;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("missileStack"))
        {
            ItemStack stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("missileStack"));
            setMissile(MissileModuleBuilder.INSTANCE.buildMissile(stack));
        }
        if (nbt.hasKey("fofTag"))
        {
            fofTag = nbt.getString("fofTag");
        }
        if (nbt.hasKey("health"))
        {
            this.setHealth(nbt.getFloat("health"));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        if (getMissile() != null)
        {
            ItemStack stack = getMissile().toStack();
            nbt.setTag("missileStack", stack.writeToNBT(new NBTTagCompound()));
        }
        if (fofTag != null && !fofTag.isEmpty())
        {
            nbt.setString("fofTag", fofTag);
        }
        if (getHealth() > 0)
        {
            nbt.setFloat("health", getHealth());
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

    @Override
    public String getFoFTag()
    {
        return fofTag;
    }
}