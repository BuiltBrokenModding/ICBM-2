package icbm.daodan;

import icbm.ICBM;
import icbm.ParticleSpawner;
import icbm.api.IMissile;
import icbm.dianqi.ItGenZongQi;
import icbm.jiqi.TFaSheDi;
import icbm.jiqi.TXiaoFaSheQi;
import icbm.zhapin.ZhaPin;

import java.util.Random;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import universalelectricity.prefab.Vector2;
import universalelectricity.prefab.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EDaoDan extends Entity implements IEntityAdditionalSpawnData, IMissile
{
	public int missileID = 0;
	public int skyLimit = 200;
    public Vector3 muBiao = null;
    public Vector3 startingPosition = null;
    public boolean isExploding = false;
    public Vector3 missileLauncherPosition = null;
        
    public int heightBeforeHit = 0;
    public int ticksInAir = -1;
    public double xDifference;
	public double yDifference;
    public double zDifference;
    public double flatDistance;
    public float launchAcceleration = 0.012F;
    //The flight time in ticks
    public float flightTime;
    public float acceleration;
	public int protectionTime = 20;
	
	private Ticket chunkTicket;
    
    //For anti-ballistic missile
    public EDaoDan lockedTarget;
	//Has this missile lock it's target before?
    public boolean didTargetLockBefore = false;
	//Tracking
    public int genZongE = -1;
	//For cluster missile
    public int missileCount = 0;
	
	//Cruise Missile
	public boolean isCruise;
	private boolean setExplode;
	private boolean setNormalExplode;
    
    public EDaoDan(World par1World)
    {
        super(par1World);
    	this.setSize(1F, 1F);
        this.renderDistanceWeight = 2F;
        this.isImmuneToFire = true;
    }
   
    /**
     * Spawns a traditional missile
     */
    public EDaoDan(World par1World, Vector3 position, Vector3 launcherPosition, int metadata)
    {
        this(par1World);
        this.missileID = metadata;
        this.startingPosition = position;
        this.missileLauncherPosition = launcherPosition;
        
        this.setPosition(this.startingPosition.x, this.startingPosition.y, this.startingPosition.z);
        this.setRotation(0, 90);
    }

    @Override
    public String getEntityName()
    {
    	if(this.missileID > 100)
    	{
        	return DaoDan.list[this.missileID].getMing();
    	}
    	
    	return ZhaPin.list[this.missileID].getDaoDanMing();
    }
    
    @Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		data.writeInt(this.missileID);
		
		data.writeDouble(this.startingPosition.x);
		data.writeDouble(this.startingPosition.y);
		data.writeDouble(this.startingPosition.z);

		data.writeDouble(this.missileLauncherPosition.x);
		data.writeDouble(this.missileLauncherPosition.y);
		data.writeDouble(this.missileLauncherPosition.z);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		this.missileID = data.readInt();
		
		this.startingPosition = new Vector3(data.readDouble(), data.readDouble(), data.readDouble());
		
		this.missileLauncherPosition = new Vector3(data.readDouble(), data.readDouble(), data.readDouble());
	}
    
    public void launchMissile(Vector3 muBiao)
    {
    	this.startingPosition = new Vector3(this.posX, this.posY, this.posZ);
    	this.muBiao = muBiao;
    	this.heightBeforeHit = (int)muBiao.y;
    	
    	//Calculate the distance difference of the missile
        this.xDifference = this.muBiao.x - this.startingPosition.x;
        this.yDifference = this.muBiao.y - this.startingPosition.y;
        this.zDifference = this.muBiao.z - this.startingPosition.z;
        
        //Calculate the power required to reach the target co-ordinates
        this.flatDistance = Vector2.distance(this.startingPosition.toVector2(),  this.muBiao.toVector2());

        this.skyLimit = 160+(int)(this.flatDistance*3);
        
        this.flightTime = (float)Math.max(100, 2*flatDistance);
        this.acceleration = (float)skyLimit*2/(flightTime*flightTime);

        this.ticksInAir = 0;
        
		this.worldObj.playSoundAtEntity(this, "icbm.missilelaunch", 4F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		DaoDanGuanLi.addMissile(this);
		
		System.out.println("Launching "+this.getEntityName()+" from "+startingPosition.intX()+", "+startingPosition.intY()+", "+startingPosition.intZ()+" to "+muBiao.intX()+", "+muBiao.intY()+", "+muBiao.intZ());
    }

    @Override
	public void entityInit()
    {
    	if(!ICBM.ALLOW_MISSILE_EXPLOSIVES)
    	{
    		this.setDead();
    		return;
    	}
    	
    	this.dataWatcher.addObject(16, -1);
    	this.daoDanInit(ForgeChunkManager.requestTicket(ICBM.instance, this.worldObj, Type.ENTITY));
    }
    
    public void daoDanInit(Ticket ticket)
    {
    	if (this.chunkTicket == null)
    	{
    		this.chunkTicket = ticket;
    		this.chunkTicket.bindEntity(this);
        	this.chunkTicket.getModData();
    	}
    	
    	ForgeChunkManager.forceChunk(this.chunkTicket, new ChunkCoordIntPair(this.chunkCoordX, this.chunkCoordZ));
    }
    
    public void updateLoadChunk(int oldChunkX, int oldChunkZ, int newChunkX, int newChunkZ)
    {
    	this.updateLoadChunk();
    }
    
    public void updateLoadChunk()
    {
    	if(!this.worldObj.isRemote && ICBM.ALLOW_LOAD_CHUNKS)
    	{	
	    	for(int x = -1; x <= 1; x++)
	    	{
	    		for(int z = -1; z <= 1; z++)
	        	{
	    			ChunkCoordIntPair chunkCoord = new ChunkCoordIntPair(this.chunkCoordX+x, this.chunkCoordZ+z);
	    			
	    			try
    				{
	    				ForgeChunkManager.forceChunk(this.chunkTicket, chunkCoord);
    				}
    				catch(Exception e)
    				{
    					System.err.println("Already Decorating!");
    				}
	        	}
	    	}
    	}
    }
    
    @Override
	public boolean canBeCollidedWith() { return true; } 
       
    /**
     * Called to update the entity's position/logic.
     */
    @Override
	public void onUpdate()
    {
    	if(this.setNormalExplode)
    	{
    		this.normalExplode();
    		return;
    	}
    	
    	if(this.setExplode)
    	{
    		this.explode();
    		return;
    	}
    	
    	super.onUpdate();

    	try
    	{
	    	if(this.worldObj.isRemote)
	    	{
	    		this.ticksInAir = this.dataWatcher.getWatchableObjectInt(16);
	    	}
	    	else
	    	{
	    		this.dataWatcher.updateObject(16, this.ticksInAir);
	    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	    	
    	if(this.ticksInAir >= 0)
    	{    		
    		//this.updateLoadChunk();
    		
    		if(!this.worldObj.isRemote)
    		{    			
	        	if(this.isCruise)
		    	{
	    			if(this.ticksInAir == 0)
	    			{
		    			this.motionX = this.xDifference/(flightTime*0.4);
		    			this.motionY = this.yDifference/(flightTime*0.4);
		    			this.motionZ = this.zDifference/(flightTime*0.4);	    			
	    			}
		        
	    	        this.rotationPitch = (float)(Math.atan(this.motionY/(Math.sqrt(this.motionX*this.motionX + this.motionZ*this.motionZ))) * 180 / Math.PI);
	    	   	 
	    			//Look at the next point
	    			this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);
	
	    			DaoDan.list[this.missileID].onTickFlight(this);
	    			
	    			this.noClip = false;
	    			
	    			this.moveEntity(this.motionX, this.motionY,this.motionZ);
	    			
	    			Vector3 position = Vector3.get(this);
	    			//this.isCollided = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) != 0;
	    	        
	    	        if((this.isCollided && this.ticksInAir >= 20) || this.ticksInAir > 20*600)
	    	        {
	    	        	this.explode();
	    	        }
	    		}
	    		else
	    		{
		    		//Start the launch
		    		if(this.ticksInAir < 20)
		    		{
			    		this.motionY = this.launchAcceleration*this.ticksInAir*(this.ticksInAir/2);
			 	        this.moveEntity(this.motionX, this.motionY, this.motionZ);		 	        
			 	    }
		    		else if(this.ticksInAir == 20)
		    		{
		    			this.motionY = this.acceleration*(this.flightTime/2);
		    			
		    			this.motionX = this.xDifference/flightTime;
		    			this.motionZ = this.zDifference/flightTime;
		    		}
		    		else
		    		{
		    			Vector3 currentPosition = new Vector3(this.posX, this.posY, this.posZ);
		    			double currentDistance = Vector2.distance(currentPosition.toVector2(),  this.muBiao.toVector2());
	
		    			this.motionY -= this.acceleration;
		    			
		    			this.rotationPitch = (float)(Math.atan(this.motionY/(Math.sqrt(this.motionX*this.motionX + this.motionZ*this.motionZ))) * 180 / Math.PI);
		 
		    			//Look at the next point
		    			this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);
		    			
		    			DaoDan.list[this.missileID].onTickFlight(this);
		    			
			 	        this.moveEntity(this.motionX, this.motionY, this.motionZ);
			 	        
		    	    	this.lastTickPosX = this.posX;
		    	        this.lastTickPosY = this.posY;
		    	        this.lastTickPosZ = this.posZ;
		    	
		    	        //If the missile contacts anything, it will explode.
		    	        if(this.isCollided)
		    	        {
		    	            this.explode();
		    	        }
		    	        
		    	        //If the missile is commanded to explode before impact
		    	        if(heightBeforeHit > 0 && this.motionY < 0)
		    	        {
		    	        	//Check the block below it.
		    	        	int blockBelowID = this.worldObj.getBlockId((int)this.posX, (int)this.posY - heightBeforeHit, (int)this.posZ);
		    	        	
		    	        	if(blockBelowID > 0)
		    	        	{
		    	        		heightBeforeHit = 0;
		    	        		this.explode();
		    	        	}
		    	        }
		    		} // end else
		    	}
    		}
    		
    		this.spawnMissileSmoke();
    		this.protectionTime  --;
    		this.ticksInAir ++;
    	}
    	else
    	{    
    		
    		//Check to find the launcher in which this missile belongs in.
    		if(this.missileLauncherPosition == null)
    		{
    			this.setDead();
    			return;
    		}
    		
    		TileEntity tileEntity = this.worldObj.getBlockTileEntity((int)missileLauncherPosition.x, (int)missileLauncherPosition.y, (int)missileLauncherPosition.z);
			
			if(tileEntity == null)
			{
				this.setDead();
				return;
			}
			
			if(tileEntity.isInvalid())
			{
				this.setDead();
				return;
			}
			
			if(tileEntity instanceof TFaSheDi)
			{
				if(((TFaSheDi)tileEntity).eDaoDan == null)
				{
					((TFaSheDi)tileEntity).eDaoDan = this;
				}
			}
			else if(tileEntity instanceof TXiaoFaSheQi)
			{
				if(((TXiaoFaSheQi)tileEntity).containingMissile == null)
				{
					((TXiaoFaSheQi)tileEntity).containingMissile = this;
				}
								
				this.isCruise = true;
				this.noClip = true;
								
				this.xDifference = ((TXiaoFaSheQi)tileEntity).getTarget().x - this.startingPosition.x;
		        this.yDifference = ((TXiaoFaSheQi)tileEntity).getTarget().y - this.startingPosition.y;
		        this.zDifference = ((TXiaoFaSheQi)tileEntity).getTarget().z - this.startingPosition.z;
		        		        
		        this.flatDistance = Vector2.distance(this.startingPosition.toVector2(),  ((TXiaoFaSheQi)tileEntity).getTarget().toVector2());
		        this.skyLimit = 150+(int)(this.flatDistance*1.8);
		        this.flightTime = (float)Math.max(100, 2.4*flatDistance);
		        this.acceleration = (float)skyLimit*2/(flightTime*flightTime);
		        
		        this.motionX = this.xDifference/(flightTime*0.6);
    			this.motionY = this.yDifference/(flightTime*0.6);
    			this.motionZ = this.zDifference/(flightTime*0.6);
    			    			
				float newRotationPitch = (float)(Math.atan(this.motionY/(Math.sqrt(this.motionX*this.motionX + this.motionZ*this.motionZ))) * 180 / Math.PI);
    			float newRotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);
    			
    			if(newRotationYaw - this.rotationYaw != 0)
    			{
    				this.rotationYaw += (newRotationYaw - this.rotationYaw)*0.1;
    			}
    			
    			if(newRotationPitch - this.rotationPitch != 0)
    			{
    				this.rotationPitch += (newRotationPitch - this.rotationPitch)*0.1;
    			}
			}
    	}
    }
    
    @Override
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
    	if(DaoDan.list[this.missileID] != null)
    	{
    		return DaoDan.list[this.missileID].onInteract(this, par1EntityPlayer);
    	}
    	
        return false;
    }
    
    private void spawnMissileSmoke() 
    {
    	if(this.worldObj.isRemote)
	    {
	    	Vector3 position = Vector3.get(this);
	    	ParticleSpawner.spawnParticle("smoke", this.worldObj, position);
	    	this.worldObj.spawnParticle("flame", position.x, position.y, position.z, 0, 0, 0);
    	}
	}

	/**
     * Checks to see if and entity is touching the missile. If so, blow up!
     */

    @Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
    	//Make sure the entity is not an item
    	if(!(par1Entity instanceof EntityItem) && this.protectionTime <= 0)
    	{
    		if(par1Entity instanceof EDaoDan)
        	{
    			((EDaoDan)par1Entity).setNormalExplode();
        	}
    		
    		this.setExplode();
    	}
    	
    	return null;
    }

    public void setNormalExplode()
    {
    	this.setNormalExplode = true;
    }
    
    public void setExplode()
    {
    	this.setExplode = true;
    }
    
    @Override
    public void setDead()
    {
    	if(chunkTicket != null)
    	{
    		ForgeChunkManager.releaseTicket(chunkTicket);
    	}
        super.setDead();
    }
    
    @Override
    public void explode()
    {
    	try
	    {
	    	//Make sure the missile is not already exploding
	    	if(!this.isExploding)
	    	{
		    	this.isExploding = true;
		    	
		    	if(!this.worldObj.isRemote)
		    	{
		    		if(this.missileID == 0)
		    		{
			    		this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 5F);
		    		}
		    		else
		    		{
		    			DaoDan.list[this.missileID].onExplode(this);
		    		}
		    		
		    		System.out.println(this.getEntityName()+" landed on "+(int) this.posX+", "+(int) this.posY+", "+(int) this.posZ);
		    	}
		    	
		    	this.setDead();
	    	}
    	}
    	catch(Exception e)
    	{
    		System.err.println("Missile failed to explode properly. Report this to the developers.");
    	}
    }

    @Override
    public void normalExplode()
    {
    	if(!this.isExploding)
    	{
			this.isExploding = true;
	    	
	    	if(!this.worldObj.isRemote)
	    	{
	    		this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 5F);
	    	}
	    	
    		this.setDead();
    	}
    }
    
    public void dropMissileAsItem()
    {
    	if(!this.isExploding)
    	{
	    	EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(ICBM.itemDaoDan, 1, this.missileID+1));
	        float var13 = 0.05F;
	        Random random = new Random();
	        entityItem.motionX = ((float)random.nextGaussian() * var13);
	        entityItem.motionY = ((float)random.nextGaussian() * var13 + 0.2F);
	        entityItem.motionZ = ((float)random.nextGaussian() * var13);
	        this.worldObj.spawnEntityInWorld(entityItem);
	        this.setDead();
    	}
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
	protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	this.startingPosition = Vector3.readFromNBT("startingPosition", par1NBTTagCompound);
    	this.muBiao =  Vector3.readFromNBT("targetPosition", par1NBTTagCompound);
    	this.missileLauncherPosition =  Vector3.readFromNBT("missileLauncherPosition", par1NBTTagCompound);
    	this.acceleration = par1NBTTagCompound.getFloat("acceleration");
    	this.heightBeforeHit = par1NBTTagCompound.getInteger("HeightBeforeHit");
        this.missileID = par1NBTTagCompound.getInteger("missileID");
        this.ticksInAir = par1NBTTagCompound.getInteger("ticksInAir");
        this.isCruise = par1NBTTagCompound.getBoolean("isCruise");
    }
    
    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
	*/
    @Override
	protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	this.startingPosition.writeToNBT("startingPosition", par1NBTTagCompound);
    	if(this.muBiao != null)
    	{
    		this.muBiao.writeToNBT("targetPosition", par1NBTTagCompound);
    	}
    	this.missileLauncherPosition.writeToNBT("missileLauncherPosition", par1NBTTagCompound);

    	par1NBTTagCompound.setFloat("acceleration", this.acceleration);
    	par1NBTTagCompound.setInteger("missileID", this.missileID);
    	par1NBTTagCompound.setInteger("HeightBeforeHit", this.heightBeforeHit);
    	par1NBTTagCompound.setInteger("ticksInAir", this.ticksInAir);
    	par1NBTTagCompound.setBoolean("isCruise", this.isCruise);
    }   
    
    @Override
	public float getShadowSize()
    {
        return 1.0F;
    }
}
