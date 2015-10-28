package com.builtbroken.icbm.content.crafting.missile.guidance;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.ModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.guidance.chips.GuidanceChipOne;
import com.builtbroken.icbm.content.crafting.missile.guidance.chips.GuidanceChipThree;
import com.builtbroken.icbm.content.crafting.missile.guidance.chips.GuidanceChipTwo;
import com.builtbroken.icbm.content.crafting.missile.guidance.clocks.GuidanceGearsDiamond;
import com.builtbroken.icbm.content.crafting.missile.guidance.clocks.GuidanceGearsSteel;
import com.builtbroken.icbm.content.crafting.missile.guidance.clocks.GuidanceGearsStone;
import com.builtbroken.icbm.content.crafting.missile.guidance.clocks.GuidanceGearsWood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

/**
 * Created by robert on 12/28/2014.
 */
public enum GuidanceModules
{
    //Engines
    WOOD_GEARS("guidance.gears.wood", GuidanceGearsWood.class),
    STONE_GEARS("guidance.gears.stone", GuidanceGearsStone.class),
    STEEL_GEARS("guidance.gears.steel", GuidanceGearsSteel.class),
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
        ItemStack stack = new ItemStack(ICBM.itemGuidanceModules, 1, ordinal());
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
    }
}
