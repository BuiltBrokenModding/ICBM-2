package icbm.zhapin;

import net.minecraft.src.Entity;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.implement.IRotatable;
import universalelectricity.prefab.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EZhaDan extends Entity implements IRotatable, IEntityAdditionalSpawnData
{
    //How long the fuse is (in ticks)
    public int fuse = 90;
    
    //The ID of the explosive
    public int explosiveID = 0;

	private int metadata = -1;
	
	private byte orientation = 3;

    public EZhaDan(World par1World)
    {
        super(par1World);
        this.fuse = 0;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.yOffset = this.height / 2.0F;
    }

    public EZhaDan(World par1World, Vector3 position, byte orientation, int explosiveID)
    {
        this(par1World);
        this.setPosition(position.x, position.y, position.z);
        float var8 = (float)(Math.random() * Math.PI * 2.0D);
        this.motionX = (-((float)Math.sin(var8)) * 0.02F);
        this.motionY = 0.20000000298023224D;
        this.motionZ = (-((float)Math.cos(var8)) * 0.02F);
        this.prevPosX = position.x;
        this.prevPosY = position.y;
        this.prevPosZ = position.z;
        this.explosiveID = explosiveID;
        this.fuse = ZhaPin.list[explosiveID].yinXin();
        this.orientation = orientation;
        
        ZhaPin.list[explosiveID].yinZhaQian(par1World, this);        
    }
    
    public EZhaDan(World par1World, Vector3 position, int explosiveID, byte orientation, int metadata)
    {
    	this(par1World, position, orientation, explosiveID);
    	this.metadata = metadata;
    }
    
    public String getEntityName()
    {
    	return "Explosives";
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
	public void onUpdate()
    {   
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        this.motionX *= 0.95;
        this.motionY -= 0.045D;
        this.motionZ *= 0.95;

        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if(this.fuse < 1)
        {
	       this.explode();
        }
        else
        {
        	ZhaPin.list[explosiveID].onYinZha(this.worldObj, new Vector3(this.posX, this.posY, this.posZ), this.fuse);
        }
        
        this.fuse --;
        
        super.onUpdate();
    }
    
    public void explode()
    {
    	 this.worldObj.spawnParticle("hugeexplosion", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
         ZhaPin.createBaoZha(this.worldObj, Vector3.get(this), this, this.explosiveID);
         this.setDead();
    }

    public void destroyedByExplosion()
    {
    	this.fuse = ZhaPin.list[explosiveID].onBeiZha();
    }
    
    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
	protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	this.fuse = par1NBTTagCompound.getByte("Fuse");
    	this.metadata = par1NBTTagCompound.getInteger("metadata");
    	this.explosiveID = par1NBTTagCompound.getInteger("explosiveID");
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
	protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	par1NBTTagCompound.setByte("Fuse", (byte)this.fuse);
    	par1NBTTagCompound.setInteger("metadata", this.metadata);
        par1NBTTagCompound.setInteger("explosiveID", this.explosiveID);
    }

    
    @Override
	public float getShadowSize()
    {
        return 0.5F;
    }
    
    @Override
	protected void entityInit() {}

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    @Override
	protected boolean canTriggerWalking() { return true; }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    @Override
	public boolean canBeCollidedWith() { return true; }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    @Override
    public boolean canBePushed() { return true; }

	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.orientation);
	}

	@Override
	public void setDirection(ForgeDirection facingDirection) 
	{
		this.orientation = (byte) facingDirection.ordinal();
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		data.writeInt(this.explosiveID);
		data.writeInt(this.fuse);
		data.writeByte(this.orientation);
		data.writeInt(this.metadata);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		this.explosiveID = data.readInt();
		this.fuse = data.readInt();
		this.orientation = data.readByte();
		this.metadata = data.readInt();
	}
}
