package icbm.core.items;

import icbm.Reference;
import icbm.core.prefab.item.ItemICBMBase;

import java.util.List;

import javax.swing.Icon;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSulfurDust extends ItemICBMBase
{
    @SideOnly(Side.CLIENT)
    IIcon salt_icon;

    public ItemSulfurDust(int id)
    {
        // Base name that we return.
        super("sulfur");
        
        // Tell minecraft we have multiple types of items based on damage.
        this.setHasSubtypes(true);
    }

    @Override
    public IIcon getIconFromDamage(int meta)
    {
        // Damage value of 1 is saltpeter.
        if (meta == 1)
        {
            return salt_icon;
        }
        
        return super.getIconFromDamage(meta);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        
        // Icon for base item which is sulfur dust.
        this.itemIcon = iconRegister.registerIcon(Reference.PREFIX + "sulfur");
        
        // First damage value contains icon for saltpeter.
        this.salt_icon = iconRegister.registerIcon(Reference.PREFIX + "saltpeter");
    }

    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        // Damage value of 1 returns name of saltpeter.
        if (par1ItemStack.getItemDamage() == 1)
        {
            return "item." + Reference.PREFIX + "saltpeter";
        }
        
        return super.getUnlocalizedName();
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        super.getSubItems(par1, par2CreativeTabs, par3List);
        par3List.add(new ItemStack(par1, 1, 1));
    }
}
