package icbm.sentry.turret.items;

import icbm.Reference;
import icbm.core.prefab.item.ItemICBMBase;
import icbm.sentry.interfaces.IUpgrade;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import calclavia.lib.utility.LanguageUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSentryUpgrade extends ItemICBMBase implements IUpgrade
{

    public static final Icon[] ICONS = new Icon[TurretUpgradeType.values().length];

    public ItemSentryUpgrade(int id)
    {
        super(id, "turretUpgrades");
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
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
            par3List.addAll(LanguageUtility.splitStringPerWord(TurretUpgradeType.getDescription(itemStack.getItemDamage()), 4));
            //par3List.add("\u00a7cDamage: " + (UnitDisplay.roundDecimals((itemStack.getTagCompound().getInteger("upgradeDamage") / TurretUpgradeType.getMaxUses(itemStack.getItemDamage()))) + "%"));

        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return "item." + Reference.PREFIX + TurretUpgradeType.values()[itemStack.getItemDamage()].iconName;
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
            ICONS[i] = iconRegister.registerIcon(Reference.PREFIX + TurretUpgradeType.values()[i].iconName);
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
    public void getTypes(List<String> types, ItemStack itemstack)
    {
        types.add(TurretUpgradeType.values()[itemstack.getItemDamage()].getUpgradeName());
    }

    @Override
    public double getUpgradeEfficiance(ItemStack itemstack, String type)
    {
        return TurretUpgradeType.values()[itemstack.getItemDamage()].bonus;
    }

    public static enum TurretUpgradeType
    {
        RANGE("targetCard", IUpgrade.TARGET_RANGE, 0.25, LanguageUtility.getLocal("info.upgrade.range")),
        COLLECTOR("shellCollector", IUpgrade.SHELL_COLLECTOR, 1, LanguageUtility.getLocal("info.upgrade.collect"));

        public final String iconName;
        public final String details;
        private final String upgradeName;
        private final double bonus;

        private TurretUpgradeType(String name, String upgradeName, double bonus, String de)
        {
            this.iconName = name;
            this.details = de;
            this.upgradeName = upgradeName;
            this.bonus = bonus;
        }

        public String getUpgradeName()
        {
            return upgradeName;
        }

        public double getBonus()
        {
            return bonus;
        }

        public static String getDescription(int meta)
        {
            if (meta < values().length)
            {
                return "" + values()[meta].details;
            }
            return LanguageUtility.getLocal("info.upgrade.sentry");
        }
    }

}
