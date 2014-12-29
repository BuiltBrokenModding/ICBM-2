package com.builtbroken.icbm.content.crafting.missile;

import com.builtbroken.icbm.content.crafting.AbstractModule;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasing;
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
public class Missile extends AbstractModule
{
    Warhead warhead;
    Guidance guidance;
    MissileCasing casing;
    Engine engine;

    public Missile(ItemStack stack)
    {
        super(stack);
        load(stack);
    }

    public void setWarhead(Warhead warhead)
    {
        this.warhead = warhead;
    }

    public void setCasing(MissileCasing guidance)
    {
        this.casing = guidance;
    }

    public void setGuidance(Guidance guidance)
    {
        this.guidance = guidance;
    }

    public void setEngine(Engine engine)
    {
        this.engine = engine;
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
        if(nbt.hasKey("casing"))
        {
            setCasing(MissileModuleBuilder.INSTANCE.buildCasing(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("casing"))));
        }
        if(nbt.hasKey("guidance"))
        {
            setGuidance(MissileModuleBuilder.INSTANCE.buildGuidance(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("guidance"))));
        }
    }

    @Override
    public void save(NBTTagCompound nbt)
    {
        if(warhead != null)
            nbt.setTag("warhead", warhead.toStack().writeToNBT(new NBTTagCompound()));
        if(casing != null)
            nbt.setTag("casing", casing.toStack().writeToNBT(new NBTTagCompound()));
        if(engine != null)
            nbt.setTag("engine", engine.toStack().writeToNBT(new NBTTagCompound()));
        if(guidance != null)
            nbt.setTag("guidance", guidance.toStack().writeToNBT(new NBTTagCompound()));
    }
}
