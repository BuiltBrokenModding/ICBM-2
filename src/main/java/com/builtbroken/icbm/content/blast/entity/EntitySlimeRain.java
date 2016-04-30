package com.builtbroken.icbm.content.blast.entity;

import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.World;

/**
 * Version of the slime with custom settings, such as no fall damage, multi-colors, no drops, hats, trolling, etc
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2016.
 */
public class EntitySlimeRain extends EntitySlime
{
    //TODO make slimes default to 3x3x3 pixels in size
    //TODO on collision with another slime attempt to merge
    //TODO if 20+ slimes are in a 2.5x2.5 area then form them into a large slime using a twister spinning effect

    //TODO implement colors
    //TODO implement sticking to objects
    //TODO implement ticking to entities
    //TODO implement hats -> hat mod support :P

    public EntitySlimeRain(World p_i1742_1_)
    {
        super(p_i1742_1_);
    }
}
