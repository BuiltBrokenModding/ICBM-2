package icbm.content.missile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import resonant.lib.transform.vector.IVector3;

/** Will be moved to RE once the code is finilized allowing
 * for any projectile like object(Arrow, Rocket, Thrown item, etc)
 * to be created by extending this code
 * Created by robert on 11/30/2014.
 */
public class EntityProjectile extends Entity implements IProjectile
{
    protected IVector3 sourceOfProjectile = null;

    protected int ticksInAir = -1;

    public EntityProjectile(World w)
    {
        super(w);
    }

    public EntityProjectile(World w, IVector3 startAndSource)
    {
        super(w);
        this.sourceOfProjectile = startAndSource;
        this.setPosition(startAndSource.x(), startAndSource.y(), startAndSource.z());
    }

    @Override
    protected void entityInit()
    {
        this.dataWatcher.addObject(16, -1);
    }

    @Override
    public void onUpdate()
    {
        if (this.worldObj.isRemote)
        {
            this.ticksInAir = this.dataWatcher.getWatchableObjectInt(16);
        }
        else
        {
            this.dataWatcher.updateObject(16, ticksInAir);
        }
    }

    /** Called to update the aim of the projectile, used for guided projectiles */
    public void recalculatePath()
    {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_)
    {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_)
    {

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
        this.ticksInAir = 0;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }
}
