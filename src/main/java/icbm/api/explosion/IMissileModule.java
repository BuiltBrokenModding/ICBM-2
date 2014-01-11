package icbm.api.explosion;

import icbm.api.ITier;
import icbm.api.sentry.IICBMModule;

/** Basic parts of a modular missile setup
 * 
 * @author DarkGuardsman */
public interface IMissileModule extends IICBMModule, ITier
{

    /** Weight of the modular if it impacts max speed of the missile. in kilo-grams */
    public float getMass();

    /** Missile Engine modular. Use to calculate max speed and acceleration. Use init or update to
     * calculate fuel drain and fuel stats
     * 
     * @author DarkGuardsman */
    public static interface IMissileEngine extends IMissileModule
    {
        /** Max speed this engine can move the missile */
        public float getMaxSpeed();

        /** Acceleration per second the missile gets up to max speed */
        public float getAcceleration();

    }

    /** Body of the missile and platform for the rest of the parts. This will influence what parts
     * can be added and the general body stats of the missile.
     * 
     * @author DarkGuardsman */
    public static interface IMissileBody extends IMissileModule
    {
        public float getHealthBonus();

        public float getArmorBonus();

        public float getDamageChance();

        public float getFireChance();
    }

    /** The end that goes boom when the missile hits the target. Nothing really special as this is
     * more of a container to store the explosive properties of the missile.
     * 
     * @author DarkGuardsman */
    public static interface IMissileWarhead extends IMissileModule, IExplosiveContainer
    {

    }
}
