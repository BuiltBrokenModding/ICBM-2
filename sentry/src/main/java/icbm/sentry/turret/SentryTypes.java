package icbm.sentry.turret;

import icbm.sentry.turret.auto.TurretAntiAir;
import icbm.sentry.turret.auto.TurretGun;
import icbm.sentry.turret.auto.SentryLaser;
import icbm.sentry.turret.mount.MountedRailGun;
import calclavia.lib.utility.LanguageUtility;

/** Enum of all sentries created by ICBM */
public enum SentryTypes
{
    GUN_TURRET(TurretGun.class),
    LASER_TURRET(SentryLaser.class),
    ANTI_AIRCRAFT_TURRET(TurretAntiAir.class),
    RAILGUN(MountedRailGun.class);

    private final Class<? extends Sentry> clazz;
    private final String id;

    private SentryTypes(Class<? extends Sentry> clazz)
    {
        this.clazz = clazz;
        this.id = LanguageUtility.toCamelCase(name());
    }

    public final String getId()
    {
        return this.id;
    }

    public static SentryTypes get(int id)
    {
        if (id >= 0 && id < SentryTypes.values().length)
            return SentryTypes.values()[id];

        return null;
    }

    /** get the SentryType for the SaveManager registered ID */
    public static SentryTypes get(String id)
    {
        for (SentryTypes type : SentryTypes.values())
            if (id.endsWith(type.id))
                return type;

        return null;
    }

    /** Called to load sentry types into sentry registry */
    public static void load()
    {
        for (SentryTypes type : SentryTypes.values())
        {
            if (type.id != null && type.clazz != null)
            {
                SentryRegistry.registerSentry(type.id, type.clazz);
            }
        }
    }
}
