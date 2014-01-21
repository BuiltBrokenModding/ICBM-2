package icbm.explosion.explosive.blast;

import icbm.Reference;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.explosive.Blast;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.prefab.potion.CustomPotionEffect;

public class BlastChemical extends Blast
{
    private static final int CHECK_BAN_JING = 16;
    private static final float NENG_LIANG = 10F;
    private int duration;
    /** Color of particles */
    private float red = 1, green = 1, blue = 1;
    private boolean playShortSoundFX;
    private boolean isContagious, isPoisonous, isConfuse, isMutate;

    public BlastChemical(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastChemical(World world, Entity entity, double x, double y, double z, float size, int duration, boolean playShortSoundFX)
    {
        this(world, entity, x, y, z, size);
        this.duration = duration / this.proceduralInterval();
        this.playShortSoundFX = playShortSoundFX;
    }

    public BlastChemical setRGB(float r, float g, float b)
    {
        this.red = r;
        this.green = g;
        this.blue = b;
        return this;
    }

    public BlastChemical setConfuse()
    {
        this.isConfuse = true;
        return this;
    }

    public BlastChemical setPoison()
    {
        this.isPoisonous = true;
        return this;
    }

    public BlastChemical setContagious()
    {
        this.isContagious = true;
        this.isMutate = true;
        return this;
    }

    @Override
    public void doPreExplode()
    {
        super.doPreExplode();
        if (!this.playShortSoundFX)
        {
            this.worldObj.playSoundEffect(this.position.x, this.position.y, this.position.z, Reference.PREFIX + "debilitation", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
        }
    }

    @Override
    public void doExplode()
    {
        float radius = this.getRadius();

        if (this.worldObj.isRemote)
        {
            for (int i = 0; i < 200; i++)
            {
                Vector3 diDian = new Vector3();

                diDian.x = Math.random() * radius / 2 - radius / 4;
                diDian.y = Math.random() * radius / 2 - radius / 4;
                diDian.z = Math.random() * radius / 2 - radius / 4;
                diDian.scale(Math.min(radius, callCount) / 10);

                if (diDian.getMagnitude() <= radius)
                {
                    diDian.translate(this.position);
                    ICBMExplosion.proxy.spawnParticle("smoke", this.worldObj, diDian, (Math.random() - 0.5) / 2, (Math.random() - 0.5) / 2, (Math.random() - 0.5) / 2, this.red, this.green, this.blue, 7.0F, 8);
                }
            }
        }

        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
        List<EntityLivingBase> allEntities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bounds);

        for (EntityLivingBase entity : allEntities)
        {
            if (this.isContagious)
            {
                ICBMExplosion.DU_CHUAN_RAN.poisonEntity(position, entity);
            }

            if (this.isPoisonous)
            {
                ICBMExplosion.DU_DU.poisonEntity(position, entity);
            }

            if (this.isConfuse)
            {
                entity.addPotionEffect(new CustomPotionEffect(Potion.confusion.id, 18 * 20, 0));
                entity.addPotionEffect(new CustomPotionEffect(Potion.digSlowdown.id, 20 * 60, 0));
                entity.addPotionEffect(new CustomPotionEffect(Potion.moveSlowdown.id, 20 * 60, 2));
            }
        }

        if (this.isMutate)
        {
            new BlastMutation(worldObj, this.exploder, position.x, position.y, position.z, this.getRadius()).explode();
        }

        if (this.playShortSoundFX)
        {
            worldObj.playSoundEffect(position.x + 0.5D, position.y + 0.5D, position.z + 0.5D, Reference.PREFIX + "gasleak", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);
        }

        if (this.callCount > this.duration)
        {
            this.controller.endExplosion();
        }
    }

    @Override
    public long getEnergy()
    {
        return 20;
    }

    /** The interval in ticks before the next procedural call of this explosive
     * 
     * @return - Return -1 if this explosive does not need proceudral calls */
    @Override
    public int proceduralInterval()
    {
        return 5;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.duration = nbt.getInteger("duration");
        this.isContagious = nbt.getBoolean("isContagious");
        this.isPoisonous = nbt.getBoolean("isPoisonous");
        this.isConfuse = nbt.getBoolean("isConfuse");
        this.isMutate = nbt.getBoolean("isMutate");
        this.red = nbt.getFloat("red");
        this.green = nbt.getFloat("green");
        this.blue = nbt.getFloat("blue");
        this.playShortSoundFX = nbt.getBoolean("playShortSoundFX");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("duration", this.duration);
        nbt.setBoolean("isContagious", this.isContagious);
        nbt.setBoolean("isPoisonous", this.isPoisonous);
        nbt.setBoolean("isConfuse", this.isConfuse);
        nbt.setBoolean("isMutate", this.isMutate);
        nbt.setFloat("red", this.red);
        nbt.setFloat("green", this.green);
        nbt.setFloat("blue", this.blue);
        nbt.setBoolean("playShortSoundFX", this.playShortSoundFX);

    }

}
