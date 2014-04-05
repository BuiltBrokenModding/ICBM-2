package icbm.explosion.entities;

import icbm.core.ICBMCore;
import icbm.explosion.explosive.blast.Blast;

import java.lang.reflect.Constructor;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.api.vector.IVectorWorld;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorWorld;
import calclavia.api.icbm.explosion.IEntityExplosion;
import calclavia.lib.network.PacketHandler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

/** The Entity handler responsible for entity explosions.
 * 
 * @author Calclavia */
public class EntityExplosion extends Entity implements IEntityAdditionalSpawnData, IEntityExplosion, IVectorWorld
{
    public Blast blast;

    private boolean endExplosion = false;

    public EntityExplosion(World world)
    {
        super(world);
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.yOffset = this.height / 2.0F;
        this.renderDistanceWeight = 2f;
        this.ignoreFrustumCheck = true;
        this.ticksExisted = 0;
    }

    public EntityExplosion(Blast blast)
    {
        this(blast.world());
        this.blast = blast;
        this.setPosition(blast.position.x, blast.position.y, blast.position.z);
    }

    @Override
    public String getEntityName()
    {
        return "Explosion";
    }

    @Override
    public void writeSpawnData(ByteArrayDataOutput data)
    {
        try
        {
            NBTTagCompound nbt = new NBTTagCompound();
            this.writeEntityToNBT(nbt);
            PacketHandler.writeNBTTagCompound(nbt, data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void readSpawnData(ByteArrayDataInput data)
    {
        try
        {
            this.readEntityFromNBT(PacketHandler.readNBTTagCompound(data));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void entityInit()
    {
    }

    /** returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for
     * spiders and wolves to prevent them from trampling crops */
    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /** Returns true if other Entities should be prevented from moving through this Entity. */
    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    /** Called to update the entity's position/logic. */
    @Override
    public void onUpdate()
    {
        if (this.blast == null)
        {
            this.setDead();
            ICBMCore.LOGGER.severe("Procedural explosion ended due to null! This is a bug!");
            return;
        }

        this.blast.controller = this;
        this.blast.position = new VectorWorld((IVectorWorld) this);

        if (this.blast.isMovable() && (this.motionX != 0 || this.motionY != 0 || this.motionZ != 0))
        {
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
        }

        if (this.ticksExisted == 1)
        {
            this.blast.preExplode();
        }
        else if (this.ticksExisted % this.blast.proceduralInterval() == 0)
        {
            if (!this.endExplosion)
            {
                this.blast.onExplode();
            }
            else
            {
                this.blast.postExplode();
                this.setDead();
            }
        }
    }

    public void endExplosion()
    {
        this.endExplosion = true;
    }

    /** (abstract) Protected helper method to read subclass entity data from NBT. */
    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        try
        {
            NBTTagCompound baoZhaNBT = nbt.getCompoundTag("blast");

            if (this.blast == null)
            {
                Class clazz = Class.forName(baoZhaNBT.getString("class"));
                Constructor constructor = clazz.getConstructor(World.class, Entity.class, double.class, double.class, double.class, float.class);
                this.blast = (Blast) constructor.newInstance(this.worldObj, null, this.posX, this.posY, this.posZ, 0);
            }

            this.blast.readFromNBT(baoZhaNBT);
        }
        catch (Exception e)
        {
            ICBMCore.LOGGER.severe("ICBM error in loading an explosion!");
            e.printStackTrace();
        }
    }

    /** (abstract) Protected helper method to write subclass entity data to NBT. */
    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        NBTTagCompound baoZhaNBT = new NBTTagCompound();
        baoZhaNBT.setString("class", this.blast.getClass().getCanonicalName());
        this.blast.writeToNBT(baoZhaNBT);
        nbt.setCompoundTag("blast", baoZhaNBT);
    }

    @Override
    public float getShadowSize()
    {
        return 0F;
    }

    @Override
    public double x()
    {
        return this.posX;
    }

    @Override
    public double y()
    {
        return this.posY;
    }

    @Override
    public double z()
    {
        return this.posZ;
    }

    @Override
    public World world()
    {
        return this.worldObj;
    }
}
