package icbm.sentry.items;

import icbm.sentry.interfaces.IWeaponSystem;
import icbm.sentry.weapon.types.WeaponConventional;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

/** Conventional weapon
 * 
 * @author Archadia, DarkGuardsman */
public class ItemConventional extends ItemWeapon
{
    public enum Guns
    {
        UNKNOWN_GUN("item.gun.unknown", 0),
        AssultRifle("item.gun.rifle.assult", 3f),
        SHOTGUN("item.gun.shotgun", 2f);

        private String name;
        private float damage;

        private Guns()
        {

        }

        private Guns(String name, float damage)
        {
            this.name = name;
            this.damage = damage;
        }

        public static Guns getGun(ItemStack stack)
        {
            if (stack != null)
            {
                return getGun(stack.getItemDamage());
            }
            return UNKNOWN_GUN;
        }

        public static Guns getGun(int meta)
        {
            if (meta >= 0 & meta < Guns.values().length)
            {
                return Guns.values()[meta];
            }
            return UNKNOWN_GUN;
        }

        public static String getName(int meta)
        {
            Guns gun = getGun(meta);
            if (gun != null)
            {
                return gun.getName();
            }
            return "item.gun.name";
        }

        public String getName()
        {
            return name;
        }

        public static float getDamage(ItemStack stack)
        {
            Guns gun = getGun(stack);
            if (gun != null)
            {
                return gun.getDamage();
            }
            return 5f;
        }
        
        public static float getDamage(int meta)
        {
            Guns gun = getGun(meta);
            if (gun != null)
            {
                return gun.getDamage();
            }
            return 5f;
        }

        public float getDamage()
        {
            return damage;
        }
    }

    public ItemConventional(int id)
    {
        super(id);
    }
}
