package icbm;

import java.util.List;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;

public class ItemSpecialMissile extends ItemMissile
{
    public static String[] names = {"Missile Module", "Anti-Ballistic Missile", "Cluster Missile", "Nuclear Cluster Missile"};

	public ItemSpecialMissile(String name, int id, int texture)
    {
        super(name, id, texture);
        this.setTabToDisplayOn(CreativeTabs.tabCombat);
    }
    @Override
	public String getItemNameIS(ItemStack itemstack)
    {
        return names[itemstack.getItemDamage()];
    }

    @Override
	public int getIconFromDamage(int i)
    {
        return this.iconIndex;
    }

    @Override
   	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
    	for(int i = 0; i < names.length; i++)
        {
    		par3List.add(new ItemStack(this, 1, i));
        }
    }
}
