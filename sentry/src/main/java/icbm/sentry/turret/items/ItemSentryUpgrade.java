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
import resonant.lib.utility.LanguageUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Upgrades used by any machine in the sentry module
 * 
 * @author DarkGuardsman */
public class ItemSentryUpgrade extends ItemICBMBase implements IUpgrade
{
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
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4)
    {
        super.addInformation(itemStack, player, list, par4);
        String local = LanguageUtility.getLocal(Upgrades.getDescription(itemStack.getItemDamage()));
        if (local != null && !local.isEmpty())
            list.addAll(LanguageUtility.splitStringPerWord(local, 4));
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return "item." + Reference.PREFIX + Upgrades.values()[itemStack.getItemDamage()].iconName;
    }

    @Override
    public Icon getIconFromDamage(int meta)
    {
        return Upgrades.getIcon(meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        for (int meta = 0; meta < Upgrades.values().length; meta++)
        {
            Upgrades.setIcon(meta, iconRegister.registerIcon(Reference.PREFIX + Upgrades.values()[meta].name));
        }
    }

    @Override
    public void getSubItems(int id, CreativeTabs tab, List list)
    {
        for (int meta = 0; meta < Upgrades.values().length; meta++)
        {
            list.add(new ItemStack(this, 1, meta));
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
            if (object instanceof ITurret && upgrade.getType() == Type.SENTRY)
            {
                return true;
            }
            else if (object instanceof TileTurretPlatform && upgrade.getType() == Type.PLATFORM)
            {
                return true;
            }
        }
        return false;
    }

    /** Enum of sub types for upgrades */
    public static enum Type
    {
        /** Generic turret upgrade */
        SENTRY,
        /** Block platform only upgrade */
        PLATFORM,
        /** Mobile turret only upgrade */
        MOBILESENTRY;
    }

    /** Enum of upgrade data */
    public static enum Upgrades
    {
        TARGET_RANGE("targetCard", IUpgrade.TARGET_RANGE, 0.25, "info.upgrade.range"),
        COLLECTOR("shellCollector", IUpgrade.SHELL_COLLECTOR, 1, "info.upgrade.collect"),
        SILENCER("silencer", IUpgrade.SILENCER, 1, "info.upgrade.silencer"),
        TARGET_SPEED("targettingSpeed", IUpgrade.SILENCER, 1, "info.upgrade.targetspeed");

        /** Texture/Icon name */
        public final String iconName;
        /** Generic name (not unlocalized or localized) */
        public final String name;
        /** localization reference for item info */
        public final String itemInfo;
        /** Icon for the item */
        public Icon icon;

        /** Upgrade common name ID (Allows several upgrades to share the same type_ */
        private final String upgradeID;
        /** Bonus percent */
        private final double bonus;
        /** Upgrade sub type */
        private Type upgrade_type = Type.SENTRY;

        private Upgrades(String name, String upgradeName, double bonus, String info)
        {
            this(name, upgradeName, "upgrade." + upgradeName, bonus, info);
        }

        private Upgrades(String name, String upgradeName, String texture, double bonus, String info)
        {
            this.name = name;
            this.iconName = texture;
            this.itemInfo = info;
            this.upgradeID = upgradeName;
            this.bonus = bonus;
        }

        /** Upgrade type/name (same as orename system) */
        public String getUpgradeName()
        {
            return upgradeID;
        }

        /** Gets the effect bonus */
        public double getBonus()
        {
            return bonus;
        }

        /** Gets the upgrade sub type */
        public Type getType()
        {
            return upgrade_type;
        }

        /** Icon for the item */
        public Icon icon()
        {
            return icon;
        }

        /** Grabs the icon based on meta */
        public static Icon getIcon(int meta)
        {
            if (meta >= 0 && meta < Upgrades.values().length)
            {
                return Upgrades.values()[meta].icon();
            }
            return null;
        }

        /** Grabs the icon based on meta */
        public static void setIcon(int meta, Icon icon)
        {
            if (meta >= 0 && meta < Upgrades.values().length)
            {
                Upgrades.values()[meta].icon = icon;
            }
        }

        /** Graps the localized description for the sub item */
        public static String getDescription(int meta)
        {
            if (meta < values().length)
            {
                return "" + values()[meta].itemInfo;
            }
            return LanguageUtility.getLocal("info.upgrade.sentry");
        }
    }

}
