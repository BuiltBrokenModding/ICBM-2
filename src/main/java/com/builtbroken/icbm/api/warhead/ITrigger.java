package com.builtbroken.icbm.api.warhead;

import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.modules.IModule;
import net.minecraft.world.World;

/**
 * Module that acts as a trigger.
 * <p>
 * Enabled status can be thought of as a safety system. This way
 * the trigger action is not mistakenly activated. Handling
 * of the enable status should be done by the trigger. Getters
 * and Setters only exist for the enable status for automated
 * launch system and user interaction.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/21/2016.
 */
public interface ITrigger extends IModule
{
    /**
     * Direct pass threw from {@link com.builtbroken.icbm.api.modules.IWarhead#trigger(TriggerCause, World, double, double, double)}
     * <p>
     * DO NOT ASSUME what the trigger is attached to as it can be anything. So when trigger only return
     * true if the trigger functioned, or false if its still in place.
     *
     * @param triggerCause - what caused the trigger call to be made
     * @param world        - world the trigger is inside
     * @param x            - location of the trigger
     * @param y            - location of the trigger
     * @param z            - location of the trigger
     * @return true if the trigger process should continue setting off the action on the other end.
     */
    boolean shouldTrigger(TriggerCause triggerCause, World world, double x, double y, double z);

    /**
     * Called to enable the trigger.
     * <p>
     * Not all triggers can be enabled and
     * not all triggers can be disabled once
     * enabled.
     *
     * @param yes - true if the trigger should be enabled
     * @return true if the trigger was enabled or was alreaddy enabled
     */
    boolean enableTrigger(boolean yes);

    /**
     * Can the trigger be toggled from
     * enabled to disable, and disabled to enabled.
     *
     * @return true if it can
     */
    boolean canToogleTriggerEnabled();

    /**
     * Is the trigger currently enabled/live
     *
     * @return true if yes
     */
    boolean isTriggerEnabled();
}
