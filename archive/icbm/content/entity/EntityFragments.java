package icbm.content.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityFragments extends Entity implements IEntityAdditionalSpawnData
{
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private Block inTile = null;
    private int inData = 0;
    private boolean inGround = false;
    public boolean doesArrowBelongToPlayer = false;
    public boolean isExplosive;
    public boolean isAnvil;
    private boolean isExploding = false;

    /** Seems to be some sort of timer for animating an arrow. */
    public int arrowShake = 0;

    /** The owner of this arrow. */
    private int ticksInGround;
    private int ticksInAir = 0;
    private final int damage = 11;
    private int knowBackStrength;

    /** Is this arrow a critical hit? (Controls particles and damage) */
    public boolean arrowCritical = false;
    public float explosionSize = 1.5F;

    public EntityFragments(World par1World)
    {
        super(par1World);
        this.setSize(0.5F, 0.5F);
    }

    public EntityFragments(World par1World, double x, double y, double z, boolean isExplosive, boolean isAnvil)
    {
        super(par1World);
        this.setPosition(x, y, z);
        this.yOffset = 0.0F;
        this.isExplosive = isExplosive;
        this.isAnvil = isAnvil;

        if (this.isAnvil)
        {
            this.setSize(1, 1);
        }
        else
        {
            this.setSize(0.5f, 0.5f);
        }
    }

    @Override
    public void writeSpawnData(ByteBuf data)
    {
        data.writeBoolean(this.isExplosive);
        data.writeBoolean(this.isAnvil);
    }

    @Override
    public void readSpawnData(ByteBuf data)
    {
        this.isExplosive = data.readBoolean();
        this.isAnvil = data.readBoolean();
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public String getCommandSenderName()
    {
        return "Fragments";
    }

    /** Uses the provided coordinates as a heading and determines the velocity from it with the set
     * force and random variance. Args: x, y, z, force, forceVariation */
    public void setArrowHeading(double par1, double par3, double par5, float par7, float par8)
    {
        float var9 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= var9;
        par3 /= var9;
        par5 /= var9;
        par1 += this.rand.nextGaussian() * 0.007499999832361937D * par8;
        par3 += this.rand.nextGaussian() * 0.007499999832361937D * par8;
        par5 += this.rand.nextGaussian() * 0.007499999832361937D * par8;
        par1 *= par7;
        par3 *= par7;
        par5 *= par7;
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
        float var10 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
        this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(par1, par5) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(par3, var10) * 180.0D / Math.PI);
        this.ticksInGround = 0;
    }

    /** Sets the velocity to the args. Args: x, y, z */
    @Override
    public void setVelocity(double par1, double par3, double par5)
    {
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float var7 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(par1, par5) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(par3, var7) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    private void explode()
    {
        if (!this.isExploding && !this.worldObj.isRemote)
        {
            this.isExploding = true;
            this.worldObj.createExplosion(this, this.xTile, this.yTile, this.zTile, this.explosionSize, true);
            this.setDead();
        }
    }

    /** Called to update the entity's position/logic. */
    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (this.isAnvil)
        {
            ArrayList entities = new ArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox));

            Iterator var5 = entities.iterator();

            while (var5.hasNext())
            {
                Entity entity = (Entity) var5.next();
                entity.attackEntityFrom(DamageSource.anvil, 15);
            }
        }

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float var1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, var1) * 180.0D / Math.PI);
        }

        Block var15 = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);

        if (var15 != null)
        {
            var15.setBlockBoundsBasedOnState(this.worldObj, this.xTile, this.yTile, this.zTile);
            AxisAlignedBB var2 = var15.getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile);

            if (var2 != null && var2.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ)))
            {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0)
        {
            --this.arrowShake;
        }

        if (this.inGround)
        {
            var15 = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);
            int var18 = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);

            if (var15 == this.inTile && var18 == this.inData)
            {
                if (this.isExplosive)
                {
                    explode();
                }
                else
                {
                    if (this.isAnvil && this.worldObj.rand.nextFloat() > 0.5f)
                    {
                        this.worldObj.playAuxSFX(1022, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
                    }

                    this.setDead();
                }
            }
            else
            {
                this.inGround = false;
                this.motionX *= (this.rand.nextFloat() * 0.2F);
                this.motionY *= (this.rand.nextFloat() * 0.2F);
                this.motionZ *= (this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        }
        else
        {
            ++this.ticksInAir;
            Vec3 var16 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 var17 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingObjPos = this.worldObj.rayTraceBlocks(var16, var17, false);
            var16 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            var17 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (movingObjPos != null)
            {
                var17 = Vec3.createVectorHelper(movingObjPos.hitVec.xCoord, movingObjPos.hitVec.yCoord, movingObjPos.hitVec.zCoord);
            }

            Entity var4 = null;
            List var5 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double var6 = 0.0D;
            int var8;
            float var10;

            for (var8 = 0; var8 < var5.size(); ++var8)
            {
                Entity var9 = (Entity) var5.get(var8);

                if (var9.canBeCollidedWith() && (this.ticksInAir >= 5))
                {
                    var10 = 0.3F;
                    AxisAlignedBB var11 = var9.boundingBox.expand(var10, var10, var10);
                    MovingObjectPosition var12 = var11.calculateIntercept(var16, var17);

                    if (var12 != null)
                    {
                        double var13 = var16.distanceTo(var12.hitVec);

                        if (var13 < var6 || var6 == 0.0D)
                        {
                            var4 = var9;
                            var6 = var13;
                        }
                    }
                }
            }

            if (var4 != null)
            {
                movingObjPos = new MovingObjectPosition(var4);
            }

            float speed;

            if (movingObjPos != null)
            {
                if (movingObjPos.entityHit != null)
                {
                    speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    int damage = (int) Math.ceil(speed * this.damage);

                    if (this.arrowCritical)
                    {
                        damage += this.rand.nextInt(damage / 2 + 2);
                    }

                    DamageSource damageSource = (new EntityDamageSourceIndirect("arrow", this, this)).setProjectile();

                    if (this.isBurning())
                    {
                        movingObjPos.entityHit.setFire(5);
                    }

                    if (movingObjPos.entityHit.attackEntityFrom(damageSource, damage))
                    {
                        if (movingObjPos.entityHit instanceof EntityLiving)
                        {
                            EntityLiving var24 = (EntityLiving) movingObjPos.entityHit;

                            if (!this.worldObj.isRemote)
                            {
                                var24.setArrowCountInEntity(var24.getArrowCountInEntity() + 1);
                            }

                            if (this.knowBackStrength > 0)
                            {
                                float var21 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

                                if (var21 > 0.0F)
                                {
                                    movingObjPos.entityHit.addVelocity(this.motionX * this.knowBackStrength * 0.6000000238418579D / var21, 0.1D, this.motionZ * this.knowBackStrength * 0.6000000238418579D / var21);
                                }
                            }
                        }

                        this.worldObj.playSoundAtEntity(this, "random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                        this.setDead();
                    }
                    else
                    {
                        this.motionX *= -0.10000000149011612D;
                        this.motionY *= -0.10000000149011612D;
                        this.motionZ *= -0.10000000149011612D;
                        this.rotationYaw += 180.0F;
                        this.prevRotationYaw += 180.0F;
                        this.ticksInAir = 0;
                    }
                }
                else
                {
                    this.xTile = movingObjPos.blockX;
                    this.yTile = movingObjPos.blockY;
                    this.zTile = movingObjPos.blockZ;
                    this.inTile = this.worldObj.getBlock(this.xTile, this.yTile, this.zTile);
                    this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
                    this.motionX = ((float) (movingObjPos.hitVec.xCoord - this.posX));
                    this.motionY = ((float) (movingObjPos.hitVec.yCoord - this.posY));
                    this.motionZ = ((float) (movingObjPos.hitVec.zCoord - this.posZ));
                    speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / speed * 0.05000000074505806D;
                    this.posY -= this.motionY / speed * 0.05000000074505806D;
                    this.posZ -= this.motionZ / speed * 0.05000000074505806D;
                    this.worldObj.playSoundAtEntity(this, "random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.arrowShake = 7;
                    this.arrowCritical = false;
                }
            }

            if (this.arrowCritical)
            {
                for (var8 = 0; var8 < 4; ++var8)
                {
                    this.worldObj.spawnParticle("crit", this.posX + this.motionX * var8 / 4.0D, this.posY + this.motionY * var8 / 4.0D, this.posZ + this.motionZ * var8 / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

            for (this.rotationPitch = (float) (Math.atan2(this.motionY, speed) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
            {
                ;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
            {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F)
            {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
            {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float var23 = 0.99F;
            var10 = 0.05F;

            if (this.isInWater())
            {
                for (int var25 = 0; var25 < 4; ++var25)
                {
                    float var24 = 0.25F;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * var24, this.posY - this.motionY * var24, this.posZ - this.motionZ * var24, this.motionX, this.motionY, this.motionZ);
                }

                var23 = 0.8F;
            }

            this.motionX *= var23;
            this.motionY *= var23;
            this.motionZ *= var23;
            this.motionY -= var10;
            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }

    /** (abstract) Protected helper method to write subclass entity data to NBT. */
    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("xTile", (short) this.xTile);
        par1NBTTagCompound.setShort("yTile", (short) this.yTile);
        par1NBTTagCompound.setShort("zTile", (short) this.zTile);
        par1NBTTagCompound.setInteger("inTile",  Block.getIdFromBlock(this.inTile));
        par1NBTTagCompound.setByte("inData", (byte) this.inData);
        par1NBTTagCompound.setByte("shake", (byte) this.arrowShake);
        par1NBTTagCompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));

        par1NBTTagCompound.setBoolean("isExplosive", this.isExplosive);
    }

    /** (abstract) Protected helper method to read subclass entity data from NBT. */
    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.xTile = par1NBTTagCompound.getShort("xTile");
        this.yTile = par1NBTTagCompound.getShort("yTile");
        this.zTile = par1NBTTagCompound.getShort("zTile");
        this.inTile = Block.getBlockById(par1NBTTagCompound.getByte("inTile"));
        this.inData = par1NBTTagCompound.getByte("inData") & 255;
        this.arrowShake = par1NBTTagCompound.getByte("shake") & 255;
        this.inGround = par1NBTTagCompound.getByte("inGround") == 1;

        this.isExplosive = par1NBTTagCompound.getBoolean("isExplosive");
    }

    /** Called by a player entity when they collide with an entity */
    @Override
    public void applyEntityCollision(Entity par1Entity)
    {
        super.applyEntityCollision(par1Entity);

        if (this.isExplosive && this.ticksExisted < 20 * 2)
        {
            this.explode();
        }
    }

    @Override
    public float getShadowSize()
    {
        return 0.0F;
    }

    /** If returns false, the item will not inflict any damage against entities. */
    @Override
    public boolean canAttackWithItem()
    {
        return false;
    }
}
