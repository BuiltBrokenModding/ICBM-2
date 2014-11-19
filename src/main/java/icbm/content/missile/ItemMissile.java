package icbm.content.missile;

import icbm.content.ItemSaveUtil;
import icbm.explosion.ExplosiveRegistry;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import resonant.api.explosion.IExplosive;
import resonant.lib.utility.LanguageUtility;

public class ItemMissile extends Item
{
    public ItemMissile()
    {
        this.setUnlocalizedName("missile");
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName()
    {
        return "icbm.missile";
    }

    public ItemStack getStackFor(String ex)
    {
        return getStackFor(ExplosiveRegistry.get(ex));
    }

    public ItemStack getStackFor(IExplosive ex)
    {
        if(ex != null && ExplosiveRegistry.isRegistered(ex))
        {
            ItemStack stack = new ItemStack(this);
            setExplosive(stack, ex);
            return stack;
        }
        return null;
    }

    public IExplosive getExplosive(ItemStack itemStack)
    {
       return ItemSaveUtil.getExplosive(itemStack);
    }

    public void setExplosive(ItemStack itemStack, IExplosive ex)
    {
        ItemSaveUtil.setExplosive(itemStack, ex);
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {

    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
    {
        super.addInformation(stack, player, list, bool);
        list.add(LanguageUtility.getLocal(getUnlocalizedName() + ".explosive") + ": " + getExplosive(stack));
    }
}
