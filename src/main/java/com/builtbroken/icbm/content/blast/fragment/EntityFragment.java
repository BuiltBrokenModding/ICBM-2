package com.builtbroken.icbm.content.blast.fragment;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.prefab.entity.EntityProjectile;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/2/2016.
 */
public class EntityFragment extends EntityProjectile implements IEntityAdditionalSpawnData
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
        super.onImpactTile();
        this.setDead(); //TODO delay death on impact
        //TODO break glass if velocity is high enough
    }

    @Override
    protected void onImpactEntity(Entity entityHit, float velocity)
    {
        super.onImpactEntity(entityHit, velocity);
        float damage = fragmentType != null ? fragmentType.scaleDamage(velocity) : 1;
        if (damage > 0)
        {
            entityHit.attackEntityFrom(new DamageFragment(fragmentType.name().toLowerCase(), entityHit, this), damage);
        }
        //TODO delay death on impact
        //TODO Implant in entity
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("fragmentType"))
        {
            int i = nbt.getInteger("fragmentType");
            if (i > 0 && i < Fragments.values().length)
            {
                fragmentType = Fragments.values()[i];
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        if (fragmentType != null)
        {
            nbt.setInteger("fragmentType", fragmentType.ordinal());
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        if (fragmentType != null)
        {
            buffer.writeByte(fragmentType.ordinal());
        }
        else
        {
            buffer.writeByte(-1);
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        try
        {
            byte i = additionalData.readByte();
            if (i > 0 && i < Fragments.values().length)
            {
                fragmentType = Fragments.values()[i];
            }
        }
        catch (Exception e)
        {
            ICBM.INSTANCE.logger().error("Failed to read spawn data for " + this);
        }
    }

    @Override
    public String toString()
    {
        return "EntityFragment[ dim@" + (worldObj != null && worldObj.provider != null ? worldObj.provider.dimensionId : "null") + " " + posX + "x " + posY + "y " + posZ + "z | " + fragmentType + "]@" + hashCode();
    }
}
