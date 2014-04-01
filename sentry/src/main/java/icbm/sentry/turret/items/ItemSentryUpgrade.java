package icbm.sentry.turret.items;

import icbm.Reference;
import icbm.core.prefab.item.ItemICBMBase;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.IUpgrade;
import icbm.sentry.platform.TileTurretPlatform;

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

/** Upgrades used by any machine in the sentry module
 * 
 * @author DarkGuardsman */
public class ItemSentryUpgrade extends ItemICBMBase implements IUpgrade
{

    public static final Icon[] ICONS = new Icon[Upgrades.values().length];

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
            par3List.addAll(LanguageUtility.splitStringPerWord(Upgrades.getDescription(itemStack.getItemDamage()), 4));
            //par3List.add("\u00a7cDamage: " + (UnitDisplay.roundDecimals((itemStack.getTagCompound().getInteger("upgradeDamage") / TurretUpgradeType.getMaxUses(itemStack.getItemDamage()))) + "%"));

        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return "item." + Reference.PREFIX + Upgrades.values()[itemStack.getItemDamage()].iconName;
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
        for (int i = 0; i < Upgrades.values().length; i++)
        {
            ICONS[i] = iconRegister.registerIcon(Reference.PREFIX + Upgrades.values()[i].iconName);
        }
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < Upgrades.values().length; i++)
        {
            par3List.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public void getTypes(List<String> types, ItemStack itemstack)
    {
        types.add(Upgrades.values()[itemstack.getItemDamage()].getUpgradeName());
    }

    @Override
    public double getUpgradeEfficiance(ItemStack itemstack, String type)
    {
        return Upgrades.values()[itemstack.getItemDamage()].bonus;
    }

    @Override
    public boolean canApplyTo(ItemStack itemStack, Object object)
    {
        if (itemStack != null && itemStack.getItemDamage() < Upgrades.values().length)
        {
            Upgrades upgrade = Upgrades.values()[itemStack.getItemDamage()];
            if (object instanceof ITurret && upgrade.getType() == 0)
            {
                return true;
            }
            if (object instanceof TileTurretPlatform && upgrade.getType() == 1)
            {
                return true;
            }
        }
        return false;
    }

    /** Enum of upgrade data */
    public static enum Upgrades
    {
        RANGE("targetCard", IUpgrade.TARGET_RANGE, 0.25, LanguageUtility.getLocal("info.upgrade.range")),
        COLLECTOR("shellCollector", IUpgrade.SHELL_COLLECTOR, 1, LanguageUtility.getLocal("info.upgrade.collect"));

        public final String iconName;
        public final String details;
        private final String upgradeName;
        private final double bonus;
        private int upgrade_type = 0;

        private Upgrades(String name, String upgradeName, double bonus, String de)
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

        public int getType()
        {
            return upgrade_type;
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
