package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.icbm.content.fragments.FragmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/2/2016.
 */
public enum FragBlastType
{
    ARROW("ex.icon.fragment.arrow", "fragment.background.arrow", "blank", FragmentType.ARROW),
    COBBLESTONE("ex.icon.fragment.cobblestone", "fragment.background.cobblestone", "blank", FragmentType.BLOCK, Item.getItemFromBlock(Blocks.cobblestone)),
    WOOD("ex.icon.fragment.wood", "fragment.background.wood", "tnt-icon-2", FragmentType.BLOCK, Item.getItemFromBlock(Blocks.planks)),
    BLAZE("ex.icon.fragment.blaze", "fragment.background.blaze", "tnt-icon-2", FragmentType.BLAZE),
    SWORD_WOOD("ex.icon.fragment.sword.wood", "fragment.background.sword.wood", "blank", FragmentType.PROJECTILE, Items.iron_sword),
    SWORD_IRON("ex.icon.fragment.sword.iron", "fragment.background.sword.iron", "blank", FragmentType.PROJECTILE, Items.iron_sword),
    SWORD_GOLD("ex.icon.fragment.sword.gold", "fragment.background.sword.gold", "blank", FragmentType.PROJECTILE, Items.iron_sword),
    SWORD_DIAMOND("ex.icon.fragment.sword.diamond", "fragment.background.sword.diamond", "blank", FragmentType.PROJECTILE, Items.iron_sword);

    public final String icon_name;
    public final String corner_icon_name;
    public final String back_ground_icon_name;
    public final FragmentType fragmentType;
    public final Item material;

    FragBlastType(String corner_icon_name, String icon_name, String back_ground_icon_name, FragmentType type, Item item)
    {
        this.icon_name = icon_name;
        this.corner_icon_name = corner_icon_name;
        this.back_ground_icon_name = back_ground_icon_name;
        this.fragmentType = type;
        this.material = item;
    }

    FragBlastType(String corner_icon_name, String icon_name, String back_ground_icon_name, FragmentType type)
    {
        this(corner_icon_name, icon_name, back_ground_icon_name, type, null);
    }
}
