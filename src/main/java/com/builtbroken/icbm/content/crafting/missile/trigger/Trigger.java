package com.builtbroken.icbm.content.crafting.missile.trigger;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.warhead.ITrigger;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.mc.prefab.module.AbstractModule;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleComponent;
import net.minecraft.item.ItemStack;

/**
 * Prefab for all triggers implemented in ICBM
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/21/2016.
 */
public abstract class Trigger extends AbstractModule implements ITrigger, IModuleComponent
{
    /** Current module this is contained inside */
    private IModule host;
    private Triggers triggerType;
    protected boolean enabled = false;

    public Trigger(ItemStack item, Triggers trigger)
    {
        super(item, trigger.moduleName);
        this.triggerType = trigger;
    }

    @Override
    public final String getSaveID()
    {
        return MissileModuleBuilder.INSTANCE.getID(this);
    }

    @Override
    public void addedToDevice(IModule module)
    {
        this.host = module;
    }

    @Override
    public void removedFromDevice(IModule module)
    {
        if (this.host != null && module != null && this.host != module)
        {
            ICBM.INSTANCE.logger().error(this + " was removed from a module[" + module + "] that was not it's host. Meaning something may have miss configured.");
        }
        this.host = null;
    }

    @Override
    public boolean enableTrigger(boolean yes)
    {
        this.enabled = yes;
        return yes;
    }

    @Override
    public boolean canToogleTriggerEnabled()
    {
        return true;
    }

    @Override
    public boolean isTriggerEnabled()
    {
        return enabled;
    }

    @Override
    public IModule getHost()
    {
        return this.host;
    }

    @Override
    public double getMass()
    {
        return triggerType.mass;
    }
}
