package icbm.content.missile;

import cpw.mods.fml.common.network.ByteBufUtils;
import icbm.explosion.DamageUtility;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import resonant.lib.transform.vector.IVector3;
import resonant.lib.transform.vector.Vector3;

/** Will be moved to RE once the code is finilized allowing
 * for any projectile like object(Arrow, Rocket, Thrown item, etc)
 * to be created by extending this code
 * Created by robert on 11/30/2014.
 */
public class EntityProjectile extends Entity implements IProjectile
{
    protected Vector3 sourceOfProjectile = null;
    protected Entity firedByEntity = null;

    protected boolean canDamage = false;

    private int _ticksInAir = -1;
    private int _ticksInGround = -1;
    private float _health = -1;

    public EntityProjectile(World w)
    {
        super(w);
        this.setSize(1F, 1F);
        this.renderDistanceWeight = 3;
        this.isImmuneToFire = true;
        this.ignoreFrustumCheck = true;
    }

    public EntityProjectile(EntityLivingBase entity)
    {
        this(entity.worldObj);
        Vector3 launcher = new Vector3(entity).add(new Vector3(0, 0.5, 0));
        Vector3 playerAim = new Vector3(entity.getLook(1));
        Vector3 start = launcher.add(playerAim.multiply(1.1));

        this.firedByEntity = entity;
        this.sourceOfProjectile = start;
        this.setPosition(start.x(), start.y(), start.z());
        this.rotationYaw = entity.rotationYaw;
        this.rotationPitch = entity.rotationPitch;
    }

    public EntityProjectile(World w, Vector3 startAndSource)
    {
        this(w);
        this.sourceOfProjectile = startAndSource;
        this.setPosition(startAndSource.x(), startAndSource.y(), startAndSource.z());
    }

    @Override
    protected void entityInit()
    {
        this.dataWatcher.addObject(6, _health);
        this.dataWatcher.addObject(17, _ticksInAir);
        this.dataWatcher.addObject(16, _ticksInGround);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        setTicksInAir(getTicksInAir() + 1);

        if (!this.worldObj.isRemote && this.getTicksInAir() >= 0)
        {
            //Update on ground tick tracker
            if(this.onGround)
                this.setTicksInGround(this.getTicksInGround() + 1);
            else
                this.setTicksInGround(0);

            //Missile stopped moving
            if (this.motionX <= 0.001 && this.motionY <= 0.001 && this.motionZ <= 0.001)
            {
                onStoppedMoving();
            }

            //Update movement logic
            if(!checkForAndTriggerCollision())
            {
                updateMotion();
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
            }
        }
    }

    @Override
    public void onEntityUpdate()
    {
        this.worldObj.theProfiler.startSection("entityBaseTick");

        if (this.ridingEntity != null && this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }

        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;

        if (this.posY < -640.0D)
        {
            this.kill();
        }

        this.worldObj.theProfiler.endSection();
    }

    /** Checks if the projectile has collided with something
     * Then triggers methods saying the projectile has collided with something
     *
     * @return true if the collision stops the projectile from moving
     */
    protected boolean checkForAndTriggerCollision()
    {
        boolean stopped = false;
        //Handle collision with blocks
        Block block = this.worldObj.getBlock((int) this.posX, (int) this.posY, (int) this.posZ);
        if(block != null && !block.isAir(worldObj, (int) posX, (int) posY, (int) posZ) && !(block instanceof IFluidBlock || block instanceof BlockLiquid))
        {
            stopped = onCollideWithBlock(block, (int) posX, (int) posY, (int) posZ);
        }

        // If the missile contacts anything, it will explode.
        if (this.isCollided)
        {
            stopped = true;
            onStoppedMoving();
        }

        return stopped;
    }

    /** Called each tick to update motion and angles of the entity */
    protected void updateMotion()
    {
        this.rotationPitch = (float) (Math.atan(this.motionY / (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ))) * 180 / Math.PI);
        this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);
        if(isCollided)
        {
            motionX = 0;
            motionY = 0;
            motionZ = 0;
        }
    }

    /** Called when the missile hits a block
     *
     * @param block - block hit
     * @param x - xCoord of block
     * @param y - xCoord of block
     * @param z  - xCoord of block
     * @return true if the missile should stop
     */
    protected boolean onCollideWithBlock(Block block, int x, int y, int z)
    {
        onStoppedMoving();
        onImpact();
        return true;
    }

    /** Called when the projectile stops moving */
    protected void onStoppedMoving()
    {

    }

    /** Called when the missile hit something and stopped */
    protected void onImpact()
    {
        this.setDead();
    }

    /** Gets the predicted position of the projectile
     *
     * @param t - number of ticks to predicted for flight path
     * @return predicted position of the project, should not be null
     */
    public Vector3 getPredictedPosition(int t)
    {
        Vector3 newPos = new Vector3(this);

        for (int i = 0; i < t; i++)
        {
          newPos.add(motionX, motionY, motionZ);
        }

        return newPos;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        if (ignoreCollisionForEntity(entity))
            return null;

        return super.getCollisionBox(entity);
    }

    /** Checks if this projectile should ignore collisions
     * if it hits the entity in question
     *
     * @param entity - entity that will collide with
     * @return true if collision should be ignored
     */
    protected boolean ignoreCollisionForEntity(Entity entity)
    {
        //Prevent collision with firing entity for first few ticks
        if(entity == firedByEntity && getTicksInAir() <= 5)
        {
            return true;
        }
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        if(nbt.hasKey("startPos"))
            sourceOfProjectile = new Vector3(nbt.getCompoundTag("startPos"));
        _ticksInAir = nbt.getInteger("ticksInAir");
        _ticksInGround = nbt.getInteger("ticksInGround");
        _health = nbt.getFloat("health");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        if(sourceOfProjectile != null)
            nbt.setTag("startPos", sourceOfProjectile.writeNBT(new NBTTagCompound()));
        nbt.setInteger("ticksInAir", this.getTicksInAir());
        nbt.setInteger("ticksInGround", this.getTicksInGround());
        nbt.setFloat("health", this.getHealth());
    }

    @Override
    public void setThrowableHeading(double motionX, double motionY, double motionZ, float power, float spread)
    {
        float square = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= (double)square;
        motionY /= (double)square;
        motionZ /= (double)square;

        //Calculate spread area
        motionX += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)spread;
        motionY += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)spread;
        motionZ += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)spread;

        //Apply power
        motionX *= (double)power;
        motionY *= (double)power;
        motionZ *= (double)power;

        //Set motion
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

        //Set angles
        float f3 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(motionY, (double)f3) * 180.0D / Math.PI);

        //Set default values
        this.setTicksInAir(0);
    }

    public void setMotion(int power)
    {
        motionX = (double)(-MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI));
        motionZ = (double)(MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI));
        motionY = (double)(-MathHelper.sin(rotationPitch / 180.0F * (float)Math.PI));

        motionX *= power;
        motionY *= power;
        motionZ *= power;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage)
    {
        if (canDamage && DamageUtility.canHarm(this, source, damage))
        {
            this.setHealth(Math.max(getHealth() - damage, 0));
            if (getHealth() <= 0)
            {
                onDestroyedBy(source, damage);
            }
            return true;
        }
        return false;
    }

    /**
     * Called when the projectile is destroyed by damage source
     * @param source - source of damage
     * @param damage - amount of damage
     */
    protected void onDestroyedBy(DamageSource source, float damage)
    {
        this.setDead();
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    public int getTicksInGround()
    {
        if(worldObj == null || !worldObj.isRemote)
        {
            return _ticksInGround;
        }
        return this.dataWatcher.getWatchableObjectInt(16);
    }

    public void setTicksInGround(int ticks)
    {
        if (!this.worldObj.isRemote)
        {
            _ticksInGround = ticks;
            this.dataWatcher.updateObject(16, ticks);
        }
    }

    public int getTicksInAir()
    {
        if(worldObj == null || !worldObj.isRemote)
        {
            return _ticksInAir;
        }
        return this.dataWatcher.getWatchableObjectInt(17);
    }

    public void setTicksInAir(int ticks)
    {
        if (!this.worldObj.isRemote)
        {
            _ticksInAir = ticks;
            this.dataWatcher.updateObject(17, ticks);
        }
    }

    public final float getHealth()
    {
        if(worldObj == null || !worldObj.isRemote)
        {
            return _health;
        }
        return this.dataWatcher.getWatchableObjectFloat(6);
    }

    public void setHealth(float p_70606_1_)
    {
        _health = p_70606_1_;
        this.dataWatcher.updateObject(6, Float.valueOf(MathHelper.clamp_float(p_70606_1_, 0.0F, this.getMaxHealth())));
    }

    public float getMaxHealth()
    {
        return 5;
    }
}
