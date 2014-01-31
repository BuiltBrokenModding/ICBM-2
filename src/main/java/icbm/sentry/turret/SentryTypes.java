package icbm.sentry.turret;

import icbm.sentry.turret.modules.AutoSentryAntiAir;
import icbm.sentry.turret.modules.AutoSentryClassic;
import icbm.sentry.turret.modules.AutoSentryTwinLaser;
import icbm.sentry.turret.modules.mount.MountedRailGun;

/** Enum of all sentries created by ICBM */
public enum SentryTypes
{
    AA("AutoAntiAir", AutoSentryAntiAir.class),
    CLASSIC("AutoGun", AutoSentryClassic.class),
    LASER("AutoLaser", AutoSentryTwinLaser.class),
    RAILGUN("MountedRail", MountedRailGun.class),
    VOID("null", null);

    private final Class<? extends Sentry> clazz;
    private final String id;

    private SentryTypes(String id, Class<? extends Sentry> clazz)
    {
        this.clazz = clazz;
        this.id = id;
    }

    public static SentryTypes get(int id)
    {
        if (id >= 0 && id < SentryTypes.values().length)
            return SentryTypes.values()[id];
        else
            return SentryTypes.VOID;
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
