package com.builtbroken.icbm.content.missile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.blast.IBlastHandler;
import com.builtbroken.icbm.api.blast.IExHandlerTileMissile;
import com.builtbroken.mc.api.entity.IFoF;
import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.icbm.content.missile.tile.TileCrashedMissile;
import com.builtbroken.icbm.content.missile.tracking.MissileTracker;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosive;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.content.resources.items.ItemSheetMetal;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.WorldChangeHelper;
import com.builtbroken.mc.lib.world.radar.RadarRegistry;
import com.builtbroken.mc.prefab.entity.EntityProjectile;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic missile like projectile that explodes on impact
 */
public class EntityMissile extends EntityProjectile implements IExplosive, IMissileEntity, IEntityAdditionalSpawnData, IFoF
{
    private IMissile missile;

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
        if (missile != null)
        {
            if (missile.getEngine() != null)
            {
                missile.getEngine().update(this);
            }
            if (missile.getGuidance() != null)
            {
                missile.getGuidance().update(this);
            }
            if (missile.getWarhead() != null)
            {
                missile.getWarhead().update(this);
            }
        }
        if (ticksInAir == 5 && !worldObj.isRemote)
        {
            RadarRegistry.add(this);
        }
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
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
        ICBM.INSTANCE.logger().info("Entity: " + entity + " has fire a missile " + missile);
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
            ICBM.INSTANCE.logger().info("Missile created and fired -> " + entityMissile);
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
        onImpact(ent.posX, ent.posY, ent.posZ, false, new TriggerCause.TriggerEntityImpact(ent, this, v));
    }

    @Override
    public void onImpactTile(MovingObjectPosition hit)
    {
        super.onImpactTile(hit);
        if (!noReport && sourceOfProjectile != null)
        {
            ITileNode tile = sourceOfProjectile.getTileNode(worldObj);
            if (tile instanceof TileAbstractLauncher)
            {
                ((TileAbstractLauncher) tile).onImpactOfMissile(this);
            }
        }
        //Special handling for missiles that survive on impact to do blast effects
        IExplosiveHandler handler = getExplosive();
        if (handler instanceof IExHandlerTileMissile && ((IExHandlerTileMissile) handler).doesSpawnMissileTile(missile, this))
        {
            Pos pos = new Pos(hit.hitVec).add(ForgeDirection.getOrientation(sideTile));
            TileCrashedMissile.placeFromMissile(this, worldObj, pos.xi(), pos.yi(), pos.zi());
        }
        else
        {
            onImpact(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord, true, new TriggerCause.TriggerBlockImpact(inBlockID, this, getVelocity())); //TODO check that velocity is correct
        }
    }

    @Override
    public void setDead()
    {
        if (!worldObj.isRemote)
        {
            RadarRegistry.remove(this);
        }
        super.setDead();
        if (!noReport && sourceOfProjectile != null)
        {
            ITileNode tile = sourceOfProjectile.getTileNode(worldObj);
            if (tile instanceof TileAbstractLauncher)
            {
                ((TileAbstractLauncher) tile).onDeathOfMissile(this);
            }
        }
    }

    /**
     * Called when the entity impacts something
     *
     * @param x
     * @param y
     * @param z
     */
    protected void onImpact(double x, double y, double z, boolean tile, TriggerCause triggerCause)
    {
        //TODO implement impact senor
        //TODO check to see if nose cone hit the ground
        //  TODO use 30 degree check for nose hit /\
        //  TODO if not hit leave entity on ground to roll around
        if (!worldObj.isRemote)
        {
            ICBM.INSTANCE.logger().info(this + String.format(" impact %1$,.2fx %1$,.2fy %1$,.2fz", x, y, z));

            boolean canPlaceTile = true;
            boolean doDrops = true;
            boolean engineBlew = false;
            boolean explosiveBlew = false;
            IExplosiveHandler handler = null;

            if (missile.getWarhead() != null)
            {
                handler = missile.getWarhead().getExplosive();
                //TODO add failure chance for warhead to go off
                //TODO add API call for failure chance
                //TODO add event call for failure chance
                //TODO add event for failure pre
                //TODO add event for failure post
                WorldChangeHelper.ChangeResult result = missile.getWarhead().trigger(triggerCause, worldObj, x, y, z);
                explosiveBlew = result == WorldChangeHelper.ChangeResult.COMPLETED || result == WorldChangeHelper.ChangeResult.PARTIAL_COMPLETE_WITH_FAILURE;
            }
            if (missile != null && missile.getEngine() != null)
            {
                engineBlew = missile.getEngine().onDestroyed(this, missile);
            }
            //TODO add entity version of placed tile

            //Engine blew up by explosive didn't, drop items and delete engine
            if (engineBlew)
            {
                if (worldObj.rand.nextFloat() >= 0.8f)
                {
                    //TODO drop scraps from engine
                }
                missile.setEngine(null);
            }

            //Consume explosive
            if (explosiveBlew)
            {
                missile.getWarhead().setExplosiveStack(null);
            }

            if ((!(handler instanceof IBlastHandler) || ((IBlastHandler) handler).doesDamageMissile(this, missile, missile.getWarhead(), explosiveBlew, engineBlew) || !((IBlastHandler) handler).allowMissileToDrop(this, missile, missile.getWarhead())))
            {
                canPlaceTile = false;
            }

            if (canPlaceTile)
            {
                if (tile)
                {
                    Pos pos = new Pos(xTile, yTile, zTile).add(ForgeDirection.getOrientation(sideTile));
                    TileCrashedMissile.placeFromMissile(this, worldObj, pos.xi(), pos.yi(), pos.zi());
                }
                else
                {
                    InventoryUtility.dropItemStack(new Location(this), missile.toStack());
                }
            }
            else if (doDrops)
            {
                doDropsOnDeath(explosiveBlew, engineBlew);
            }
            this.setDead();
        }
        else
        {
            doClientImpact(x, y, z);
        }
    }

    protected void doDropsOnDeath(boolean warheadBlew, boolean engineBlew)
    {
        IExplosiveHandler handler = missile.getWarhead().getExplosive();
        if (handler instanceof IBlastHandler && ((IBlastHandler) handler).doesVaporizeParts(this, missile, missile.getWarhead(), warheadBlew, engineBlew))
        {
            //No drops to drop
            return;
        }

        List<ItemStack> drops = null;
        boolean genScraps = warheadBlew || engineBlew;
        if (handler instanceof IBlastHandler)
        {
            drops = ((IBlastHandler) handler).getDropsForMissile(this, missile, missile.getWarhead(), warheadBlew, engineBlew);
            genScraps = ((IBlastHandler) handler).doesDamageMissile(this, missile, missile.getWarhead(), warheadBlew, engineBlew);
        }

        if (drops == null)
        {
            drops = new ArrayList();
            if (Engine.itemSheetMetal != null)
            {
                //TODO replace with scrap metal
                drops.add(ItemSheetMetal.SheetMetal.EIGHTH.stack());
                drops.add(ItemSheetMetal.SheetMetal.EIGHTH.stack());
                drops.add(ItemSheetMetal.SheetMetal.EIGHTH.stack());
                drops.add(ItemSheetMetal.SheetMetal.EIGHTH.stack());
            }

            if (missile.getEngine() != null)
            {
                if (!genScraps)
                {
                    drops.add(missile.getEngine().toStack());
                }
                missile.setEngine(null);
            }
            if (missile.getWarhead() != null)
            {
                //TODO turn into custom entity so impact can still happen to set off warhead
                if (!genScraps)
                {
                    drops.add(missile.getWarhead().toStack());
                }
                missile.setWarhead(null);
            }
            if (missile.getGuidance() != null)
            {
                if (!genScraps)
                {
                    drops.add(missile.getGuidance().toStack());
                }
                missile.setGuidance(null);
            }
        }

        //TODO add drop event
        //TODO add config to prevent drops
        if (drops != null)
        {
            for (ItemStack stack : drops)
            {
                //TODO add chance for item to turn into a projectile fragment that can do damage
                //TODO make random increase with explosion size to throw parts a great distance
                InventoryUtility.dropItemStack(worldObj, posX, posY, posZ, stack, 5 + worldObj.rand.nextInt(30), worldObj.rand.nextFloat());
            }
        }
    }

    protected void doClientImpact(double x, double y, double z)
    {
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

    public IMissile getMissile()
    {
        return missile;
    }

    public void setMissile(IMissile missile)
    {
        this.missile = missile;
        this.inAirKillTime = MissileCasings.get(missile.getMissileSize()).maxFlightTimeInTicks;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("missileStack"))
        {
            ItemStack stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("missileStack"));
            if (stack != null)
            {
                if (stack.getItem() instanceof IMissileItem)
                {
                    setMissile(((IMissileItem) stack.getItem()).toMissile(stack));
                }
                else
                {
                    setMissile(MissileModuleBuilder.INSTANCE.buildMissile(stack));
                }
            }
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

    @Override
    public String toString()
    {
        return String.format("Missile[ %d@dim %.2fx %.2fy %.2fz ]@", worldObj.provider.dimensionId, posX, posY, posZ) + hashCode();
    }
}