package com.builtbroken.icbm.content.missile.parts.guidance;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.mc.prefab.module.ModuleBuilder;
import com.builtbroken.icbm.content.missile.parts.MissileModuleBuilder;
import com.builtbroken.icbm.content.missile.parts.guidance.chips.GuidanceChipOne;
import com.builtbroken.icbm.content.missile.parts.guidance.chips.GuidanceChipThree;
import com.builtbroken.icbm.content.missile.parts.guidance.chips.GuidanceChipTwo;
import com.builtbroken.icbm.content.missile.parts.guidance.clocks.GuidanceGearsDiamond;
import com.builtbroken.icbm.content.missile.parts.guidance.clocks.GuidanceGearsIron;
import com.builtbroken.icbm.content.missile.parts.guidance.clocks.GuidanceGearsStone;
import com.builtbroken.icbm.content.missile.parts.guidance.clocks.GuidanceGearsWood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

/**
 * Enum of guidance modules register by ICBM
 * Created by robert on 12/28/2014.
 */
public enum GuidanceModules
{
    //Engines
    WOOD_GEARS("guidance.gears.wood", GuidanceGearsWood.class),
    STONE_GEARS("guidance.gears.stone", GuidanceGearsStone.class),
    IRON_GEARS("guidance.gears.iron", GuidanceGearsIron.class),
    DIAMOND_GEARS("guidance.gears.diamond", GuidanceGearsDiamond.class),
    CHIP_ONE("guidance.chip.one", GuidanceChipOne.class),
    CHIP_TWO("guidance.chip.two", GuidanceChipTwo.class),
    CHIP_THREE("guidance.chip.three", GuidanceChipThree.class);

    protected final String name;
    protected final Class<? extends Guidance> clazz;

    protected IIcon icon;

    GuidanceModules(String name, Class<? extends Guidance> clazz)
    {
        this.name = name;
        this.clazz = clazz;
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
        ItemStack stack = new ItemStack(ICBM_API.itemGuidanceModules, 1, ordinal());
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString(ModuleBuilder.SAVE_ID, "icbm." + name);
        return stack;
    }

    public Guidance newModule()
    {
        return MissileModuleBuilder.INSTANCE.buildGuidance(newModuleStack());
    }

    public static void register()
    {
        for (GuidanceModules module : values())
        {
            MissileModuleBuilder.INSTANCE.register(ICBM.DOMAIN, module.name, module.clazz, true);
        }
    }
}
