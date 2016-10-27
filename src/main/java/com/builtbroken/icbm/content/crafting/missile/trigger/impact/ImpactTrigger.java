package com.builtbroken.icbm.content.crafting.missile.trigger.impact;

import com.builtbroken.icbm.content.crafting.missile.trigger.Trigger;
import com.builtbroken.icbm.content.crafting.missile.trigger.Triggers;
import com.builtbroken.mc.api.entity.IWeightedEntity;
import com.builtbroken.mc.api.event.TriggerCause;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Base class for any sensor that is impact driven
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/27/2016.
 */
public abstract class ImpactTrigger extends Trigger
{
    private double minimalForce = 0;
    private double maximalForce = Integer.MAX_VALUE;

    public ImpactTrigger(ItemStack item, Triggers trigger)
    {
        super(item, trigger);
    }

    @Override
    public boolean shouldTrigger(TriggerCause triggerCause, World world, double x, double y, double z)
    {
        if (triggerCause instanceof TriggerCause.TriggerCauseImpact)
        {
            return isEnoughForceToTrigger((TriggerCause.TriggerCauseImpact) triggerCause);
        }
        return false;
    }

    /**
     * Checks if there is enough force to activate the trigger
     *
     * @param cause - trigger cause
     * @return true if it can
     */
    protected boolean isEnoughForceToTrigger(TriggerCause.TriggerCauseImpact cause)
    {
        double mass = IWeightedEntity.getMassOfEntity(cause.source);
        //https://www.chem.wisc.edu/deptfiles/genchem/netorial/modules/thermodynamics/energy/energy2.htm
        final double ke = 0.5 * mass * cause.velocity * cause.velocity;
        return ke >= getMinimalForce() && ke <= getMaximalForce();
    }

    public double getMinimalForce()
    {
        return minimalForce;
    }

    public double getMaximalForce()
    {
        return maximalForce;
    }
}
