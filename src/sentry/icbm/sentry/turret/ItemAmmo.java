package icbm.sentry.turret;

import icbm.core.ICBMCore;
import icbm.core.base.ItemICBMBase;
import icbm.sentry.ProjectileType;
import icbm.sentry.interfaces.IAmmunition;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmmo extends ItemICBMBase implements IAmmunition
{
    public static enum AmmoType
    {
        SHELL("bulletShell", ProjectileType.UNKNOWN, true),
        BULLET("bullet", ProjectileType.CONVENTIONAL, true),
        BULLETRAIL("bulletRailgun", ProjectileType.RAILGUN, true),
        BULLETANTI("bulletAntimatter", ProjectileType.RAILGUN, true),
        BULLETINF("bulletInfinite", ProjectileType.CONVENTIONAL, false);

        public String iconName;
        public ProjectileType type;
        public boolean consume;

        private AmmoType(String iconName, ProjectileType type, boolean consume)
        {
            this.iconName = iconName;
            this.type = type;
            this.consume = consume;
        }
    }

    public static final Icon[] ICONS = new Icon[AmmoType.values().length];

    public ItemAmmo(int id)
    {
        super(id, "ammunition");
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return "item." + ICBMCore.PREFIX + AmmoType.values()[itemStack.getItemDamage()].iconName;
    }

    @Override
    public Icon getIconFromDamage(int i)
    {
        return ICONS[i];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        for (int i = 0; i < AmmoType.values().length; i++)
        {
            ICONS[i] = iconRegister.registerIcon(ICBMCore.PREFIX + AmmoType.values()[i].iconName);
        }
    }

    @Override
    public ProjectileType getType(ItemStack itemStack)
    {
        if (itemStack.getItemDamage() < AmmoType.values().length)
        {
            return AmmoType.values()[itemStack.getItemDamage()].type;
        }

        return null;
    }

    @Override
    public boolean canDrop(int meta)
    {
        if (meta == AmmoType.BULLETINF.ordinal())
        {
            return false;
        }
        return true;
    }

    @Override
    public ItemStack onDroppedIntoWorld(ItemStack stack)
    {
        return stack;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world)
    {
        if (itemStack != null && itemStack.getItemDamage() == AmmoType.BULLETINF.ordinal())
        {
            return 40;
        }
        return super.getEntityLifespan(itemStack, world);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < AmmoType.values().length; i++)
        {
            par3List.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int getDamage()
    {
        return 8;
    }
}