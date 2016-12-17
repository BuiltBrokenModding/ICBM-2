package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.icbm.content.fragments.FragmentType;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/2/2016.
 */
public enum FragBlastType
{
    ARROW("ex.icon.fragment.arrow", "fragment.background.arrow", "blank", null),
    COBBLESTONE("ex.icon.fragment.cobblestone", "fragment.background.cobblestone", "blank", FragmentType.BLOCK, Blocks.cobblestone),
    WOOD("ex.icon.fragment.wood", "fragment.background.wood", "tnt-icon-2", FragmentType.BLOCK, Blocks.planks),
    BLAZE("ex.icon.fragment.blaze", "fragment.background.blaze", "tnt-icon-2", FragmentType.BLAZE);

    public final String icon_name;
    public final String corner_icon_name;
    public final String back_ground_icon_name;
    public final FragmentType fragmentType;
    public final Block blockMaterial;

    FragBlastType(String corner_icon_name, String icon_name, String back_ground_icon_name, FragmentType type, Block block)
    {
        this.icon_name = icon_name;
        this.corner_icon_name = corner_icon_name;
        this.back_ground_icon_name = back_ground_icon_name;
        this.fragmentType = type;
        this.blockMaterial = block;
    }

    FragBlastType(String corner_icon_name, String icon_name, String back_ground_icon_name, FragmentType type)
    {
        this(corner_icon_name, icon_name, back_ground_icon_name, type, null);
    }


}
