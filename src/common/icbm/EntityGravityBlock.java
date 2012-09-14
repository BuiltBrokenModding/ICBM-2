package icbm;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import universalelectricity.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityGravityBlock extends Entity implements IEntityAdditionalSpawnData
{
	public int blockID = 0;
	public int metadata = 0;
	
	public float yawChange = 0;
	public float pitchChange = 0;
	
	public float gravity =  0.045f;

    public EntityGravityBlock(World world)
    {
        super(world);
        this.ticksExisted = 0;
        this.preventEntitySpawning = true;
        this.isImmuneToFire = true;
        this.setSize(1F, 1F);
    }

    public EntityGravityBlock(World world, Vector3 position, int blockID, int metadata)
    {
        super(world);
        this.isImmuneToFire = true;
        this.ticksExisted = 0;
        setSize(0.98F, 0.98F);
        yOffset = height / 2.0F;
        setPosition(position.x + 0.5, position.y, position.z + 0.5);
        motionX = 0D;
        motionY = 0D;
        motionZ = 0D;
        this.blockID = blockID;
        this.metadata = metadata;
    }
    
    public EntityGravityBlock(World world, Vector3 position, int blockID, int metadata, float gravity)
    {
    	this(world, position, blockID, metadata);
    	this.gravity = gravity;
    }
    
    @Override
    public String getEntityName()
    {
    	return "Flying Block";
    }
    
    @Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		data.writeInt(this.blockID);
		data.writeInt(this.metadata);
		data.writeFloat(this.gravity);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		this.blockID = data.readInt();
		this.metadata = data.readInt();
		this.gravity = data.readFloat();
	}
    
    @Override
    protected void entityInit() { }
    
    public void onUpdate()
    {
        if(this.posY > 400 || this.blockID == 0 || this.blockID == ICBM.blockYinXing.blockID || this.blockID == Block.pistonExtension.blockID  || this.blockID == Block.waterMoving.blockID || this.blockID == Block.lavaMoving.blockID)
        {
            this.setDead();
            return;
        }
        
        this.motionY -= gravity;
        
        if(this.isCollided)
        {
        	this.pushOutOfBlocks(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
        }
        
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        
        if(this.yawChange > 0)
        {
        	this.rotationYaw += this.yawChange;
        	this.yawChange -= 2;
        }
        
        if(this.pitchChange > 0)
        {
        	this.rotationPitch += this.pitchChange;
        	this.pitchChange -= 2;
        }
        
        if((onGround && this.ticksExisted > 20) || this.ticksExisted > 20*120)
        {
        	this.setBlock();
        }
        
        this.ticksExisted ++;
    }
    
    public void setBlock()
    {
    	int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(posY);
        int k = MathHelper.floor_double(posZ);

    	worldObj.setBlockAndMetadataWithNotify(i, j, k, this.blockID, this.metadata);
    	setDead();
	}

	/**
     * Checks to see if and entity is touching the missile. If so, blow up!
     */

    @Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
    	//Make sure the entity is not an item
    	if(par1Entity instanceof EntityLiving)
    	{
    		((EntityLiving)par1Entity).attackEntityFrom(DamageSource.inWall, 3);
    	}
    	
    	return null;
    }

    protected void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
    	nbttagcompound.setInteger("metadata", this.metadata);
    	nbttagcompound.setInteger("blockID", this.blockID);
    	nbttagcompound.setFloat("gravity", this.gravity);
    }

    protected void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
    	this.metadata = nbttagcompound.getInteger("metadata");
    	this.blockID = nbttagcompound.getInteger("blockID");
    	this.gravity = nbttagcompound.getFloat("gravity");
    }

    @Override
    public float getShadowSize() { return 0.5F; }
    
    @Override
	public boolean canBePushed() { return true; }
    
    @Override
    protected boolean canTriggerWalking() {  return true; }
    
    @Override
    public boolean canBeCollidedWith() { return true; }
}