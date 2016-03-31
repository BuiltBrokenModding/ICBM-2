package com.builtbroken.icbm.content.crafting.parts;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;

/**
 * Crafting items for explosives
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/28/2016.
 */
public class ItemExplosiveParts extends Item implements IPostInit
{
    public ItemExplosiveParts()
    {
        this.setUnlocalizedName(ICBM.PREFIX + "explosivePart");
        this.setTextureName(ICBM.PREFIX + "explosivePart");
        this.setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(this.getIconString());
        for (ExplosiveParts part : ExplosiveParts.values())
        {
            part.icon = reg.registerIcon(ICBM.PREFIX + part.iconName);
        }
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass)
    {
        if (stack.getItemDamage() >= 0 && stack.getItemDamage() < ExplosiveParts.values().length)
        {
            return ExplosiveParts.values()[stack.getItemDamage()].icon;
        }
        return itemIcon;
    }

    @Override
    public int getRenderPasses(int metadata)
    {
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        if (stack.getItemDamage() >= 0 && stack.getItemDamage() < ExplosiveParts.values().length)
        {
            return super.getUnlocalizedName(stack) + "." + ExplosiveParts.values()[stack.getItemDamage()].iconName;
        }
        return super.getUnlocalizedName(stack);
    }

    @Override
    public void onPostInit()
    {
        //TODO add ore dictionary support
        GameRegistry.addRecipe(new ShapedOreRecipe(ExplosiveParts.ARROW_TUBE.newItem(), "PAP", 'P', Items.paper, 'A', Items.arrow));
        GameRegistry.addRecipe(new ShapedOreRecipe(ExplosiveParts.ARROW_BUNDLE.newItem(), "AAA", "AAA", 'A', ExplosiveParts.ARROW_TUBE.newItem()));
        GameRegistry.addRecipe(new ShapedOreRecipe(ExplosiveParts.GUNPOWDER_CHARGE.newItem(), "SGS", "GRG", "SGS", 'P', Items.paper, 'G', ExplosiveParts.POWDER_STICK.newItem(), 'R', OreNames.REDSTONE, 'S', OreNames.STRING));
        GameRegistry.addRecipe(new ShapedOreRecipe(ExplosiveParts.MAGIC_CHARGE.newItem(), "PSP", "GRG", "PSP", 'P', Items.paper, 'G', Items.experience_bottle, 'R', OreNames.REDSTONE, 'S', OreNames.STRING));
        GameRegistry.addRecipe(new ShapedOreRecipe(ExplosiveParts.POWDER_STICK.newItem(), "PGP", 'P', Items.paper, 'G', Items.gunpowder));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (ExplosiveParts parts : ExplosiveParts.values())
        {
            list.add(parts.newItem());
        }
    }

    public enum ExplosiveParts
    {
        /** ARROW in a tube of paper */
        ARROW_TUBE("arrowTube"),
        /** Wrapped bundle of arrows in tubes */
        ARROW_BUNDLE("arrowBundle"),
        /** Small amount of gunpowder in a paper container */
        GUNPOWDER_CHARGE("gunpowderCharge"),
        /** Magic based explosion */
        MAGIC_CHARGE("magicCharge"),
        /** Stick of gunpowder */
        POWDER_STICK("powderStick");

        public IIcon icon;
        public final String iconName;

        ExplosiveParts(String iconName)
        {
            this.iconName = iconName;
        }

        public ItemStack newItem()
        {
            return new ItemStack(ICBM.itemExplosivePart, 1, ordinal());
        }

    }
}
