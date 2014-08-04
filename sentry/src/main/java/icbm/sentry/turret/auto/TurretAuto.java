package icbm.sentry.turret.auto;

import icbm.sentry.interfaces.IAutoTurret;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.ai.TurretEntitySelector;
import icbm.sentry.turret.traits.SentryTrait;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import resonant.lib.utility.nbt.ISaveObj;

/** A Class that functions as the AI of automatic turrets. */
public abstract class TurretAuto extends Turret implements IAutoTurret
{
    protected Entity target;
    protected IEntitySelector selector;

    public TurretAuto(ITurretProvider host)
    {
        super(host);
        selector = new TurretEntitySelector(this);
    }

    @Override
    public void update()
    {
        super.update();

        if (!world().isRemote && this.getEnergy(ForgeDirection.UNKNOWN) >= SentryTrait.asLong(getTrait(ITurret.ENERGY_RUNNING_TRAIT)))
        {
            getAi().update();
            getServo().update();
            this.setEnergy(ForgeDirection.UNKNOWN, this.getEnergy(ForgeDirection.UNKNOWN) - SentryTrait.asLong(getTrait(ITurret.ENERGY_RUNNING_TRAIT)));
        }

    }

    @Override
    public void setTarget(Entity target)
    {
        this.target = target;
        double r = SentryTrait.asDouble(getTrait(target != null ? ITurret.ROTATION_SPEED_WITH_TARGET_TRAIT : ITurret.ROTATION_SPEED_TRAIT), 5);
        this.getServo().setRotationSpeed(r);
    }

    @Override
    public Entity getTarget()
    {
        return this.target;
    }

    @Override
    public IEntitySelector getEntitySelector()
    {
        return selector;
    }

    @Override
    public void save(NBTTagCompound nbt)
    {
        super.save(nbt);
        if (selector instanceof ISaveObj)
        {
            NBTTagCompound targetTag = new NBTTagCompound();
            ((ISaveObj) this.selector).save(targetTag);
            nbt.setCompoundTag("selector", targetTag);
        }
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        if (selector instanceof ISaveObj)
        {
            ((ISaveObj) this.selector).load(nbt.getCompoundTag("selector"));
        }

    }
}
