package icbm.jiqi;

import icbm.ICBM;
import icbm.ICBMCommonProxy;
import icbm.ItZiDan;
import icbm.ParticleSpawner;
import icbm.TYinXing;
import icbm.extend.IMB;
import icbm.zhapin.EZhaPin;
import icbm.zhapin.ex.ExHongSu;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.Ticker;
import universalelectricity.electricity.ElectricInfo;
import universalelectricity.implement.IElectricityStorage;
import universalelectricity.implement.IRedstoneReceptor;
import universalelectricity.network.ConnectionHandler;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import universalelectricity.prefab.Vector3;

import com.google.common.io.ByteArrayDataInput;

public class TLeiShePao extends TileEntityElectricityReceiver implements IPacketReceiver, IMB
{	
	//Watts required per tick.
	public static final int YAO_DIAN = 8;
	
	private static final int BURN_BAN_JING = 3;
	
	public float rotationYaw = 0;
	public float rotationPitch = 0;
	
	public float displayRotationYaw = 0;
	public float displayRotationPitch = 0;

	private float rotationSpeed;
	
    public double dian = 0;

    public boolean autoMode = false;
	
	private EntityPlayer mountedPlayer = null;

	private EFake entityFake = null;
	
	private int gunChargingTicks = 0;
	
    private ItemStack[] containingItems = new ItemStack[4];
    
	private boolean isAntimatter;

	private float explosionSize;

	private int explosionDepth;
	
	private int yongZhe = 0;
	private boolean packetGengXin = true;
	    
    public TLeiShePao()
    {
		super();
    }
    
	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side)
	{		
		if(!this.isDisabled())
    	{
			this.dian += ElectricInfo.getWatts(amps, voltage);
    	}
	}
	
	public void updateEntity()
	{
		super.updateEntity();
		
		if(!this.isDisabled())
    	{
    		if(mountedPlayer != null)
    		{
    			if(mountedPlayer.rotationPitch > 30) mountedPlayer.rotationPitch = 30;
    			if(mountedPlayer.rotationPitch < -45) mountedPlayer.rotationPitch = -45;
    			
    			this.rotationPitch = mountedPlayer.rotationPitch;
    			this.rotationYaw = mountedPlayer.rotationYaw;
    			
    			this.displayRotationPitch = this.rotationPitch*0.0175f;
    			this.displayRotationYaw = this.rotationYaw*0.0175f;
    		}
    		else if(this.entityFake != null)
    		{
    			this.entityFake.setDead();
    			this.entityFake = null;
    		}
    		
    		if(this.isBeingPowered() && this.dian >= this.YAO_DIAN)
    		{
    			if(Ticker.inGameTicks % 8 == 0)
    			{
    				this.worldObj.playSoundEffect((int)this.xCoord, (int)this.yCoord, (int)this.zCoord, "icbm.laser", 2F, 1F);
    				
    				MovingObjectPosition objectMouseOver = this.rayTrace(1500);
    				
    				if(objectMouseOver != null)
    		        {
    					if(objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
    					{
    						for(int x = -BURN_BAN_JING; x < BURN_BAN_JING; x ++)
    						{
    							for(int y = -BURN_BAN_JING; y < BURN_BAN_JING; y ++)
        						{
    								for(int z = -BURN_BAN_JING; z < BURN_BAN_JING; z ++)
    	    						{
    									Vector3 targetPosition = new Vector3(objectMouseOver.blockX+x, objectMouseOver.blockY+y, objectMouseOver.blockZ+z);
    									if(targetPosition.getBlockID(this.worldObj) == 0 && this.worldObj.isBlockNormalCube(objectMouseOver.blockX+x, objectMouseOver.blockY+y-1, objectMouseOver.blockZ+z))
    									{
    										targetPosition.setBlockWithNotify(this.worldObj, Block.fire.blockID);
    									}
    	    						}
        						}
    						}
    					}
    		        }
    			}
    			
    			this.dian = 0;
    		}
    	}
	
		if(!this.worldObj.isRemote)
		{
			if(Ticker.inGameTicks % 20 == 0)
			{
				if(this.mountedPlayer != null)
				{
					PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, Vector3.get(this), 15);
				}
				
				if(this.yongZhe > 0)
				{
					PacketManager.sendPacketToClients(PacketManager.getPacket(ICBM.CHANNEL, this, (int)4, this.dian, this.disabledTicks, this.autoMode), this.worldObj, Vector3.get(this), 15);
				}
			}
			
			if(this.packetGengXin)
			{
				this.worldObj.markBlockNeedsUpdate(this.xCoord, this.yCoord, this.zCoord);
				this.packetGengXin = false;
			}
		}
	}
	
	@Override
	public void handlePacketData(NetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream) 
	{
		try
	    {
	        int ID = dataStream.readInt();

	        if(ID == -1)
			{
				if(dataStream.readBoolean())
				{
					PacketManager.sendPacketToClients(this.getDescriptionPacket());
					this.yongZhe ++;
				}
				else
				{
					this.yongZhe --;
				}
			}
	        else if(ID == 1)
	        {
		        this.displayRotationYaw = dataStream.readFloat();
		        this.displayRotationPitch = dataStream.readFloat();
	        }
	        else if(ID == 2)
	        {
	        	this.mount(player);
	        }
	        else if(ID == 3)
	        {
	        	this.autoMode = !this.autoMode;
	        }
	        else if(ID == 4)
	        {
		        this.dian = dataStream.readDouble();
		        this.disabledTicks = dataStream.readInt();
		        this.autoMode = dataStream.readBoolean();
	        }
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	    }
	}
	
	@Override
    public Packet getDescriptionPacket()
    {
        return PacketManager.getPacket(ICBM.CHANNEL, this, (int)1, this.displayRotationYaw, this.displayRotationPitch);
    }

	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}

	@Override
	public double getVoltage()
	{
		return 120;
	}
	
	@Override
	public void onDestroy(TileEntity callingBlock)
	{
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord, this.zCoord, 0);
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord+1, this.zCoord, 0);
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		if(this.mountedPlayer != null && entityPlayer == this.mountedPlayer)
		{
			this.mountedPlayer =  null;
			entityPlayer.mountEntity(null);
			
			if(this.entityFake != null)
			{
				this.entityFake.setDead();
				this.entityFake = null;
			}
			entityPlayer.moveEntity(0, 3, 0);
		}
		else
		{
			entityPlayer.openGui(ICBM.instance, ICBMCommonProxy.GUI_LASER_TURRET, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		}
		
		return true;
	}
	
	public void mount(EntityPlayer entityPlayer)
	{
		//Creates a fake entity to be mounted on
		if(this.mountedPlayer == null)
		{
			if(!this.worldObj.isRemote)
			{
				this.entityFake  = new EFake(this.worldObj, new Vector3(this.xCoord+0.5, this.yCoord+0.25, this.zCoord+0.5), this);
				this.worldObj.spawnEntityInWorld(entityFake);
				entityPlayer.mountEntity(entityFake);
			}
			
			mountedPlayer = entityPlayer;
			entityPlayer.rotationYaw = 0;
			entityPlayer.rotationPitch = 0;
		}
	}

	@Override
	public void onCreate(Vector3 position)
	{
		this.worldObj.setBlockWithNotify(position.intX(), position.intY()+1, position.intZ(), ICBM.blockYinXing.blockID);
		((TYinXing)this.worldObj.getBlockTileEntity(position.intX(), position.intY()+1, position.intZ())).setMainBlock(position);
	}
	
	/**
     * Performs a ray trace for the distance specified and using the partial tick time. Args: distance, partialTickTime
     */
    public MovingObjectPosition rayTrace(double distance)
    {
        Vector3 muzzlePosition = getMuzzle();
        Vector3 lookDistance = ICBM.getLook(this.rotationYaw, this.rotationPitch);
        Vector3 var6 = Vector3.add(muzzlePosition, Vector3.multiply(lookDistance, distance));
        return this.worldObj.rayTraceBlocks(muzzlePosition.toVec3(), var6.toVec3());
    }
    
    public Vector3 getMuzzle()
    {
    	Vector3 position = new Vector3(this.xCoord+0.5, this.yCoord+1, this.zCoord+0.5);
    	return Vector3.add(position, Vector3.multiply(ICBM.getLook(this.rotationYaw, this.rotationPitch), 2.2));
    }

	@Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
    	
        this.rotationYaw = par1NBTTagCompound.getFloat("rotationYaw");
        this.rotationPitch = par1NBTTagCompound.getFloat("rotationPitch");
        
        this.displayRotationPitch = this.rotationPitch*0.0175f;
		this.displayRotationYaw = this.rotationYaw*0.0175f;
    	
        this.dian = par1NBTTagCompound.getDouble("electricityStored");
    }
    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setFloat("rotationYaw", this.rotationYaw);
        par1NBTTagCompound.setFloat("rotationPitch", this.rotationPitch);
        
        par1NBTTagCompound.setDouble("electricityStored", this.dian);
    }

	@Override
    public double wattRequest()
    {
        if(this.dian < YAO_DIAN && !this.isDisabled())
        {
            return Math.ceil(YAO_DIAN - this.dian);
        }

        return 0;
    }
	
	public boolean isBeingPowered()
	{
		return this.worldObj.isBlockGettingPowered(this.xCoord, this.yCoord, this.zCoord) || this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
	}
}
