package icbm.explosion;

import net.minecraftforge.common.util.ForgeDirection;
import resonant.lib.wrapper.ForgeDirectionWrapper;

/** Object that tell an explosive how it was triggered
 * Created on 11/18/2014.
 * @author Darkguardsman
 */
public abstract class Trigger
{
    private String triggerName = "unknown";

    public Trigger(String name)
    {
        this.triggerName = name;
    }

    /** Side based Trigger */
    public static class TriggerSide extends Trigger
    {
        public final ForgeDirection triggeredSide;
        public TriggerSide(String name, ForgeDirection side)
        {
            super(name);
            triggeredSide = side;
        }
    }

    /** Trigger by fire */
    public static class TriggerFire extends TriggerSide
    {
        public TriggerFire(ForgeDirection side)
        {
            super("fire", side);
        }
    }

    /** Trigger by restone signal */
    public static class TriggerRedstone extends TriggerSide
    {
        /** 0-15 how strong the signal of the restone was */
        public final int strength;

        public TriggerRedstone(ForgeDirection side, int strength)
        {
            super("redstone", side);
            this.strength = strength;
        }
    }
}
