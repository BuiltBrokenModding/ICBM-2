package icbm.sentry.turret.items;

import icbm.Reference;
import icbm.core.prefab.item.ItemICBMBase;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import calclavia.api.icbm.sentry.IAmmunition;
import calclavia.api.icbm.sentry.ProjectileType;
import calclavia.lib.utility.inventory.InventoryUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmmo extends ItemICBMBase implements IAmmunition
{
    public static enum AmmoType
    {
        SHELL("bulletShell", ProjectileType.UNKNOWN, true),
        BULLET("bullet", ProjectileType.CONVENTIONAL, true),
        BULLET_RAIL("bulletRailgun", ProjectileType.RAILGUN, true),
        BULLET_ANTIMATTER("bulletAntimatter", ProjectileType.RAILGUN, true),
        BULLET_INFINITE("bulletInfinite", ProjectileType.CONVENTIONAL, false);

        public String iconName;
        public ProjectileType type;
        public boolean consume;

        private AmmoType(String iconName, ProjectileType type, boolean consume)
        {
            this.iconName = iconName;
            this.type = type;
            this.consume = consume;
        }

        public static AmmoType get(ItemStack itemStack)
        {
            if (itemStack != null && itemStack.getItem() instanceof ItemAmmo)
            {
                if (itemStack.getItemDamage() < AmmoType.values().length)
                {
                    return AmmoType.values()[itemStack.getItemDamage()];
                }
            }
            return null;
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
        return "item." + Reference.PREFIX + AmmoType.values()[itemStack.getItemDamage()].iconName;
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
            ICONS[i] = iconRegister.registerIcon(Reference.PREFIX + AmmoType.values()[i].iconName);
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
    public ItemStack onDroppedIntoWorld(ItemStack stack)
    {
        AmmoType type = AmmoType.get(stack);
        if (type == AmmoType.BULLET_INFINITE)
        {
            return null;
        }
        return stack;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world)
    {
        if (itemStack != null && itemStack.getItemDamage() == AmmoType.BULLET_INFINITE.ordinal())
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
    public float getDamage()
    {
        return 8;
    }

    @Override
    public ItemStack consumeAmmo(ItemStack itemStack, int count)
    {
        AmmoType type = AmmoType.get(itemStack);
        if (type != null)
        {
            if (type.consume)
            {
                return InventoryUtility.decrStackSize(itemStack, count);
            }
        }
        return itemStack;
    }

    @Override
    public ItemStack getShell(ItemStack itemStack, int count)
    {
        AmmoType type = AmmoType.get(itemStack);
        if (type != AmmoType.SHELL && type != AmmoType.BULLET_INFINITE)
        {
            return new ItemStack(this, count, AmmoType.SHELL.ordinal());
        }
        return null;
    }

    @Override
    public int getAmmoCount(ItemStack itemStack)
    {
        if (AmmoType.get(itemStack) == AmmoType.BULLET_INFINITE)
        {
            return Integer.MAX_VALUE;
        }
        return itemStack.stackSize;
    }
}