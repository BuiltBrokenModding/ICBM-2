package com.builtbroken.icbm.content.missile.parts.trigger;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.content.missile.parts.MissileModuleBuilder;
import com.builtbroken.icbm.content.missile.parts.trigger.impact.ImpactTriggerElectrical;
import com.builtbroken.icbm.content.missile.parts.trigger.impact.ImpactTriggerMechanical;
import com.builtbroken.icbm.content.missile.parts.trigger.impact.ImpactTriggerRedstone;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.function.Function;

/**
 * Enum of trigger types that can be installed into the warhead
 * Created by robert on 12/13/2014.
 */
public enum Triggers
{
    //TODO add modifier by missile size
    /** Triggers on impact, can fail to trigger */
    MECHANICAL_IMPACT("impact.mechanical", ImpactTriggerMechanical.class, 54431.1, i -> new ImpactTriggerMechanical(i)), //120 pounds
    /** Triggers on impact, less chance to fail */
    ELECTRICAL_IMPACT("impact.electrical", ImpactTriggerElectrical.class, 27215.5, i -> new ImpactTriggerElectrical(i)), //60 pounds
    /** Triggers on impact, no fail chance, no safety, no customization, extremely cheap, DANGEROUS TO USE */
    REDSTONE_IMPACT("impact.redstone", ImpactTriggerRedstone.class, 9071.85, i -> new ImpactTriggerRedstone(i));
    /** Triggers after x time, can break when impacts */
    //MECHANICAL_TIMER("timer.mechanical", ImpactTriggerMechanical.class, 6803.89),
    /** Triggers after x time, can be set in ticks */
    //ELECTRICAL_TIMER("timer.electrical", ImpactTriggerMechanical.class, 2267.96),
    /** Triggers by remote signal */
    //REMOTE("remote", ImpactTriggerMechanical.class, 22679.6), //50 pounds (Heavy metal case, Vibration Dampeners, Antenna, Circuits, Wiring)
    /** Triggers by location */
    //GPS("gps", ImpactTriggerMechanical.class, 22679.6), //50 pounds (Heavy metal case, Vibration Dampeners, Antenna, Circuits, Wiring)
    /** Triggers x distance from ground, same as impact, mechanically triggered */
    //WIRE_RANGE("range.mechanical", ImpactTriggerMechanical.class, 158757),
    /** Triggers x distance from ground, same as impact, optically triggered */
    //LASER_RANGE("range.laser", ImpactTriggerMechanical.class, 11339.8);

    /** Name of the module and registry name */
    public final String moduleName;
    /** Mass of the trigger mechanism */
    public final double mass;
    /** Class file to create new instances from */
    public Class<? extends Trigger> clazz;

    @SideOnly(Side.CLIENT)
    public IIcon icon;

    public Function<ItemStack, Trigger> triggerFactory;

    Triggers(String moduleName, Class<? extends Trigger> clazz, double mass, Function<ItemStack, Trigger> triggerFactory)
    {
        this.moduleName = "trigger." + moduleName;
        this.mass = mass;
        this.clazz = clazz;
        this.triggerFactory = triggerFactory;
    }

    public static Triggers get(ItemStack insert)
    {
        return get(insert.getItemDamage());
    }

    public static Triggers get(int meta)
    {
        if (meta >= 0 && meta < values().length)
        {
            return values()[meta];
        }
        return MECHANICAL_IMPACT;
    }

    public ItemStack newModuleStack()
    {
        return new ItemStack(ICBM_API.itemTrigger, 1, ordinal());
    }

    public Trigger newModule()
    {
        return triggerFactory.apply(newModuleStack());
    }

    public Trigger buildModule(ItemStack stack)
    {
        return triggerFactory.apply(stack);
    }

    public static void register()
    {
        for (Triggers module : values())
        {
            MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, module.moduleName, module.clazz, true);
        }
    }
}
