package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.mc.prefab.entity.EntityProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/2/2016.
 */
public class EntityFragment extends EntityProjectile
{
    /** Velocity to break glass */
    public static final double GLASS_BREAK_VELOCITY = 1;


    protected Fragments fragmentType;

    public EntityFragment(World world)
    {
        super(world);
    }

    public EntityFragment(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityFragment(World world, EntityLivingBase shooter, EntityLivingBase target, float p_i1755_4_, float p_i1755_5_)
    {
        super(world, shooter, target, p_i1755_4_, p_i1755_5_);
    }

    public EntityFragment(World world, EntityLivingBase shooter, float f)
    {
        super(world, shooter, f);
    }

    @Override
    protected void onImpactTile()
    {
        //TODO break glass if velocity is high enough
    }

    @Override
    protected void onImpactEntity(Entity entityHit, float velocity)
    {
        super.onImpactEntity(entityHit, velocity);
        double damage = 1;
        if(fragmentType != null)
        {

        }
    }
}
