package icbm.content.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fluids.IFluidBlock;
import resonant.lib.transform.vector.Vector3;

/** @author Calclavia */
public class EntityFlyingBlock extends Entity implements IEntityAdditionalSpawnData
{
    public Block mimicBlock = null;
    public int metadata = 0;

    public float yawChange = 0;
    public float pitchChange = 0;

    public float gravity = 0.045f;

    public EntityFlyingBlock(World world)
    {
        super(world);
        this.ticksExisted = 0;
        this.preventEntitySpawning = true;
        this.isImmuneToFire = true;
        this.setSize(1F, 1F);
    }

    public EntityFlyingBlock(World world, Vector3 position, Block mimicBlock, int metadata)
    {
        super(world);
        this.isImmuneToFire = true;
        this.ticksExisted = 0;
        setSize(0.98F, 0.98F);
        yOffset = height / 2.0F;
        setPosition(position.x() + 0.5, position.y(), position.z() + 0.5);
        motionX = 0D;
        motionY = 0D;
        motionZ = 0D;
        this.mimicBlock = mimicBlock;
        this.metadata = metadata;
    }

    public EntityFlyingBlock(World world, Vector3 position, Block mimicBlock, int metadata, float gravity)
    {
        this(world, position, mimicBlock, metadata);
        this.gravity = gravity;
    }


    public String getEntityName()
    {
        return "Flying Block";
    }

    @Override
    public void writeSpawnData(ByteBuf data)
    {
        data.writeInt(Block.getIdFromBlock(mimicBlock));
        data.writeInt(this.metadata);
        data.writeFloat(this.gravity);
        data.writeFloat(yawChange);
        data.writeFloat(pitchChange);
    }

    @Override
    public void readSpawnData(ByteBuf data)
    {
        mimicBlock = Block.getBlockById(data.readInt());
        metadata = data.readInt();
        gravity = data.readFloat();
        yawChange = data.readFloat();
        pitchChange = data.readFloat();
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public void onUpdate()
    {
        if (this.posY > 400 || this.mimicBlock == null)
        {
            this.setDead();
            return;
        }

        if (this.mimicBlock == Blocks.piston_extension || this.mimicBlock instanceof BlockLiquid || this.mimicBlock instanceof IFluidBlock)
        {
            this.setDead();
            return;
        }

        this.motionY -= gravity;

        if (this.isCollided)
        {
           //TODO this.applyEntityCollision().pushOutOfBlocks(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.yawChange > 0)
        {
            this.rotationYaw += this.yawChange;
            this.yawChange -= 2;
        }

        if (this.pitchChange > 0)
        {
            this.rotationPitch += this.pitchChange;
            this.pitchChange -= 2;
        }

        if ((this.onGround && this.ticksExisted > 20) || this.ticksExisted > 20 * 120)
        {
            this.setBlock();
            return;
        }

        this.ticksExisted++;
    }

    public void setBlock()
    {
        if (!this.worldObj.isRemote)
        {
            int i = MathHelper.floor_double(posX);
            int j = MathHelper.floor_double(posY);
            int k = MathHelper.floor_double(posZ);

            this.worldObj.setBlock(i, j, k, this.mimicBlock, this.metadata, 2);
        }

        this.setDead();
    }

    /** Checks to see if and entity is touching the missile. If so, blow up! */

    @Override
    public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
        // Make sure the entity is not an item
        if (par1Entity instanceof EntityLiving)
        {
            if (mimicBlock != null)
            {
                if ((this.motionX > 2 || this.motionY > 2 || this.motionZ > 2))
                {
                    int damage = (int) (1.2 * (Math.abs(this.motionX) + Math.abs(this.motionY) + Math.abs(this.motionZ)));
                    ((EntityLiving) par1Entity).attackEntityFrom(DamageSource.fallingBlock, damage);
                }
            }
        }

        return null;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setInteger("metadata", this.metadata);
        nbttagcompound.setInteger("mimicBlock", Block.getIdFromBlock(this.mimicBlock));
        nbttagcompound.setFloat("gravity", this.gravity);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        this.metadata = nbttagcompound.getInteger("metadata");
        this.mimicBlock = Block.getBlockById(nbttagcompound.getInteger("mimicBlock"));
        this.gravity = nbttagcompound.getFloat("gravity");
    }

    @Override
    public float getShadowSize()
    {
        return 0.5F;
    }

    @Override
    public boolean canBePushed()
    {
        return true;
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return true;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }
}
