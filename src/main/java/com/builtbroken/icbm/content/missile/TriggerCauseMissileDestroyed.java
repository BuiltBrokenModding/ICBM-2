package com.builtbroken.icbm.content.missile;

import com.builtbroken.mc.api.event.TriggerCause;
import net.minecraft.util.DamageSource;

/**
 * Trigger cause used when the missile was destroyed. Most likely by a forcefield(Reika's mods) or AA gun(ICBM sentries, Flans, etc)
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/5/2016.
 */
public class TriggerCauseMissileDestroyed extends TriggerCause
{
    /** What killed the missile, may be null */
    public final Object source;
    /** What damage was used to kill the missile, may be null */
    public final DamageSource damage;

    /**
     * Only constructor
     *
     * @param source - optional, what killed the missile
     * @param damage - optional, what damaged was used
     * @param scale  - amount to scale the explosion by, must be greater than  or equal to zero
     */
    public TriggerCauseMissileDestroyed(Object source, DamageSource damage, float scale)
    {
        super("MissileDestroyed");
        this.source = source;
        this.damage = damage;
        this.effectScaleChange = scale;
    }
}
