package com.builtbroken.icbm.content.crafting.missile.trigger;

/** Enum of trigger types that can be installed into the warhead
 * Created by robert on 12/13/2014.
 */
public enum Triggers
{
    //TODO abstract so that any mod can create new trigger devices
    //TODO add checks to see if the trigger types can be st off by fire
    //TODO add arming conditions
    //TODO add trigger conditions
    /** Triggers on impact, can fail to trigger */
    MECHANICAL_IMPACT,
    /** Triggers on impact, less chance to fail */
    ELECTRICAL_IMPACT,
    /** Triggers after x time, can break when impacts */
    MECHANICAL_TIMER,
    /** Triggers after x time, can be set in ticks */
    ELECTRICAL_TIMER,
    /** Triggers by remote signal */
    REMOTE,
    /** Triggers by location */
    GPS,
    /** Triggers x distance from ground, same as impact, mechanically triggered */
    WIRE_RANGE,
    /** Triggers x distance from ground, same as impact, optically triggered */
    LASER_RANGE
}
