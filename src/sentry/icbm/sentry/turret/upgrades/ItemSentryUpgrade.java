package icbm.sentry.turret.upgrades;

import icbm.core.ICBMCore;
import icbm.core.base.ItemICBMBase;
import icbm.sentry.ITurretUpgrade;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import universalelectricity.api.energy.UnitDisplay;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSentryUpgrade extends ItemICBMBase implements ITurretUpgrade
{

    public static final Icon[] ICONS = new Icon[TurretUpgradeType.values().length];

    public ItemSentryUpgrade(int par1)
    {
        super(par1, "turretUpgrades");
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
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        if (itemStack != null)
        {
            if (itemStack.getTagCompound() == null)
            {
                itemStack.setTagCompound(new NBTTagCompound());
            }
            par3List.add(TurretUpgradeType.getDescription(itemStack.getItemDamage()));
            par3List.add("\u00a7c" + (UnitDisplay.roundDecimals((itemStack.getTagCompound().getInteger("upgradeDamage") / TurretUpgradeType.getMaxUses(itemStack.getItemDamage()))) + "%"));

        }
    }

    @Override
    public boolean damageUpgrade(ItemStack itemStack, int damage)
    {
        if (itemStack.getItem() instanceof ItemSentryUpgrade)
        {
            if (itemStack.getTagCompound() == null)
            {
                itemStack.setTagCompound(new NBTTagCompound());
            }

            damage = damage + itemStack.getTagCompound().getInteger("upgradeDamage");
            if (damage > TurretUpgradeType.getMaxUses(itemStack.getItemDamage()))
            {
                itemStack.getTagCompound().setBoolean("broken", true);
            }
        }
        return false;
    }

    @Override
    public boolean isFunctional(ItemStack item)
    {
        if (item.getTagCompound() == null)
        {
            item.setTagCompound(new NBTTagCompound());
        }
        int damage = item.getTagCompound().getInteger("upgradeDamage");
        if (item.getTagCompound().getBoolean("broken") || damage > TurretUpgradeType.getMaxUses(item.getItemDamage()))
        {
            item.getTagCompound().setBoolean("broken", true);
        }
        return false;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return "item." + ICBMCore.PREFIX + TurretUpgradeType.values()[itemStack.getItemDamage()].iconName;
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
        for (int i = 0; i < TurretUpgradeType.values().length; i++)
        {
            ICONS[i] = iconRegister.registerIcon(ICBMCore.PREFIX + TurretUpgradeType.values()[i].iconName);
        }
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < TurretUpgradeType.values().length; i++)
        {
            par3List.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public TurretUpgradeType getType(ItemStack itemstack)
    {
        return TurretUpgradeType.values()[itemstack.getItemDamage()];
    }

    public static enum TurretUpgradeType
    {
        RANGE("targetCard", 5000, "Upgrades the targeting range of the sentry gun."),
        COLLECTOR("shellCollector", 1000, "Allows the sentry to collect ejected shell casings.");

        public String iconName;
        public String details = "Upgrade for a sentry gun";
        public int maxUses = 1000;

        private TurretUpgradeType(String name, int maxDamage, String de)
        {
            this.iconName = name;
            this.maxUses = maxDamage;
            this.details = de;

        }

        public static int getMaxUses(int meta)
        {
            if (meta < values().length)
            {
                return values()[meta].maxUses;
            }
            return 1000;
        }

        public static String getDescription(int meta)
        {
            if (meta < values().length)
            {
                return "" + values()[meta].details;
            }
            return "An upgrade for a sentry gun";
        }
    }

}
