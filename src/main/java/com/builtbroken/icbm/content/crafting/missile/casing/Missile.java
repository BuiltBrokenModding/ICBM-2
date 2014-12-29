package com.builtbroken.icbm.content.crafting.missile.casing;

import com.builtbroken.icbm.api.IModuleContainer;
import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.engine.Engine;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/** Crafting object for the missile
 * Contains all the peaces that make up the
 * missile and allow it to function
 *
 * @author Darkguardsman
 */
public class Missile extends AbstractModule implements IModuleContainer
{
    private Warhead warhead;
    private Guidance guidance;
    private Engine engine;

    public Missile(ItemStack stack)
    {
        super(stack);
        load(stack);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if(nbt.hasKey("warhead"))
        {
            setWarhead(MissileModuleBuilder.INSTANCE.buildWarhead(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("warhead"))));
        }
        if(nbt.hasKey("engine"))
        {
            setEngine(MissileModuleBuilder.INSTANCE.buildEngine(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("engine"))));
        }
        if(nbt.hasKey("guidance"))
        {
            setGuidance(MissileModuleBuilder.INSTANCE.buildGuidance(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("guidance"))));
        }
    }

    @Override
    public void save(NBTTagCompound nbt)
    {
        if(getWarhead() != null)
            nbt.setTag("warhead", getWarhead().toStack().writeToNBT(new NBTTagCompound()));
        if(getEngine() != null)
            nbt.setTag("engine", getEngine().toStack().writeToNBT(new NBTTagCompound()));
        if(getGuidance() != null)
            nbt.setTag("guidance", getGuidance().toStack().writeToNBT(new NBTTagCompound()));
    }

    @Override
    public boolean canInstallModule(ItemStack stack, AbstractModule module)
    {
        return false;
    }

    @Override
    public boolean installModule(ItemStack stack, AbstractModule module)
    {
        return false;
    }

    public void setWarhead(Warhead warhead)
    {
        this.warhead = warhead;
    }

    public void setGuidance(Guidance guidance)
    {
        this.guidance = guidance;
    }

    public void setEngine(Engine engine)
    {
        this.engine = engine;
    }

    public Warhead getWarhead()
    {
        return warhead;
    }

    public Guidance getGuidance()
    {
        return guidance;
    }

    public Engine getEngine()
    {
        return engine;
    }
}
