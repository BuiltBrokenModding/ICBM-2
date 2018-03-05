package com.builtbroken.icbm.content.missile.parts.guidance;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.content.missile.parts.MissileModuleBuilder;
import com.builtbroken.icbm.content.missile.parts.guidance.chips.GuidanceChipOne;
import com.builtbroken.icbm.content.missile.parts.guidance.chips.GuidanceChipThree;
import com.builtbroken.icbm.content.missile.parts.guidance.chips.GuidanceChipTwo;
import com.builtbroken.icbm.content.missile.parts.guidance.clocks.GuidanceGearsDiamond;
import com.builtbroken.icbm.content.missile.parts.guidance.clocks.GuidanceGearsIron;
import com.builtbroken.icbm.content.missile.parts.guidance.clocks.GuidanceGearsStone;
import com.builtbroken.icbm.content.missile.parts.guidance.clocks.GuidanceGearsWood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.function.Function;

/**
 * Enum of guidance modules register by ICBM
 * Created by robert on 12/28/2014.
 */
public enum GuidanceModules
{
    //Engines
    WOOD_GEARS("guidance.gears.wood", GuidanceGearsWood.class, i -> new GuidanceGearsWood(i)),
    STONE_GEARS("guidance.gears.stone", GuidanceGearsStone.class, i -> new GuidanceGearsStone(i)),
    IRON_GEARS("guidance.gears.iron", GuidanceGearsIron.class, i -> new GuidanceGearsIron(i)),
    DIAMOND_GEARS("guidance.gears.diamond", GuidanceGearsDiamond.class, i -> new GuidanceGearsDiamond(i)),
    CHIP_ONE("guidance.chip.one", GuidanceChipOne.class, i -> new GuidanceChipOne(i)),
    CHIP_TWO("guidance.chip.two", GuidanceChipTwo.class, i -> new GuidanceChipTwo(i)),
    CHIP_THREE("guidance.chip.three", GuidanceChipThree.class, i -> new GuidanceChipThree(i));

    protected final String name;
    protected final Class<? extends Guidance> clazz;

    protected IIcon icon;

    public Function<ItemStack, Guidance> guidanceFactory;

    GuidanceModules(String name, Class<? extends Guidance> clazz, Function<ItemStack, Guidance> guidanceFactory)
    {
        this.name = name;
        this.clazz = clazz;
        this.guidanceFactory = guidanceFactory;
    }

    public static GuidanceModules get(ItemStack stack)
    {
        return get(stack.getItemDamage());
    }

    public static GuidanceModules get(int meta)
    {
        if (meta >= 0 && meta < values().length)
        {
            return values()[meta];
        }
        return null;
    }

    public ItemStack newModuleStack()
    {
        return new ItemStack(ICBM_API.itemGuidanceModules, 1, ordinal());
    }

    public Guidance newModule()
    {
        return guidanceFactory.apply(newModuleStack());
    }

    public Guidance buildModule(ItemStack stack)
    {
        return guidanceFactory.apply(stack);
    }

    public static void register()
    {
        for (GuidanceModules module : values())
        {
            MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, module.name, module.clazz, true);
        }
    }
}
