package com.builtbroken.icbm.content.crafting;

import net.minecraft.item.ItemStack;

/**
 * Created by robert on 12/28/2014.
 */
public class Missile extends AbstractModule
{
    Warhead warhead;
    ItemStack guidance;
    Engine engine;

    public Missile(ItemStack stack)
    {
        this(stack, false);
    }

    public Missile(ItemStack stack, boolean load)
    {
        super(stack);
        if (load) load(stack);
    }

    public void setWarhead(Warhead warhead)
    {
        this.warhead = warhead;
    }

    public void setGuidance(ItemStack stack)
    {
        this.guidance = stack;
    }

    public void setEngine(Engine engine)
    {
        this.engine = engine;
    }
}
