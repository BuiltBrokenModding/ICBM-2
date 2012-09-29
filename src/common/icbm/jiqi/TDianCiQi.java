package icbm.jiqi;

import icbm.BYinXing;
import icbm.ICBM;
import icbm.ICBMCommonProxy;
import icbm.extend.IChunkLoadHandler;
import icbm.extend.IMB;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.Chunk;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.Ticker;
import universalelectricity.electricity.ElectricInfo;
import universalelectricity.implement.IElectricityStorage;
import universalelectricity.implement.IRedstoneReceptor;
import universalelectricity.network.ConnectionHandler;
import universalelectricity.network.ConnectionHandler.ConnectionType;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.ISimpleConnectionHandler;
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import universalelectricity.prefab.Vector3;

import com.google.common.io.ByteArrayDataInput;

public class TDianCiQi extends TileEntityElectricityReceiver implements IChunkLoadHandler, IElectricityStorage, IPacketReceiver, IMB, IRedstoneReceptor, ISimpleConnectionHandler
{
    //The maximum possible radius for the EMP to strike
    public static final int MAX_RADIUS = 60;
	
	public float xuanZhuan = 0;
	private float xuanZhuanLu = 0;
	
    private double dianXiaoShi = 0;
    
    //The EMP mode. 0 = All, 1 = Missiles Only, 2 = Electricity Only
    public byte muoShi = 0;
    
    //The EMP explosion radius
    public int banJing = 60;

	private int yongZhe = 0;

	private boolean packetGengXin = false;
    
    public TDianCiQi()
    {
		super();
		ConnectionHandler.registerConnectionHandler(this);
    }
    
  	/**
	 * Called every tick. Super this!
	 */
	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side)
	{			
		if(!this.isDisabled())
    	{
			this.setWattHours(this.dianXiaoShi+ElectricInfo.getWattHours(amps, voltage));
    	}
	}
	
	public void updateEntity()
	{
		super.updateEntity();
		
		if(!this.isDisabled())
    	{
			if(Ticker.inGameTicks % 20 == 0 && this.dianXiaoShi > 0)
			{
				this.worldObj.playSoundEffect((int)this.xCoord, (int)this.yCoord, (int)this.zCoord, "icbm.machinehum", 0.5F, (float) (0.85F*this.dianXiaoShi/this.getMaxWattHours()));
			}
			
			this.xuanZhuanLu = (float) (Math.pow(this.dianXiaoShi/this.getMaxWattHours(), 2)*0.5);
			this.xuanZhuan += xuanZhuanLu;
			if(this.xuanZhuan > 360) this.xuanZhuan = 0;
    	}
					
		if(!this.worldObj.isRemote)
		{
	        if(this.packetGengXin || Ticker.inGameTicks % 40 == 0 && this.yongZhe  > 0)
	        {
	        	PacketManager.sendTileEntityPacketWithRange(this, "ICBM", 15, (int)1, this.dianXiaoShi, this.disabledTicks, this.banJing, this.muoShi);
	        	this.packetGengXin = true;
	        }
		}
    }
	
	@Override
	public void handlePacketData(NetworkManager network, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
        {
			final int ID = dataStream.readInt();
			
			if(ID == -1)
			{
				if(dataStream.readBoolean())
				{
					this.yongZhe ++;
				}
				else
				{
					this.yongZhe --;
				}
			}
			else if(ID == 1)
			{
	            this.dianXiaoShi = dataStream.readDouble();
	            this.disabledTicks = dataStream.readInt();
	            this.banJing = dataStream.readInt();
	            this.muoShi = dataStream.readByte();
			}
			else if(ID == 2)
			{
	            this.banJing = dataStream.readInt();
			}
			else if(ID == 3)
			{
	            this.muoShi = dataStream.readByte();
			}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}
	
	@Override
	public void handelConnection(ConnectionType type, Object... data)
	{
		if(type == ConnectionType.LOGIN_SERVER)
		{
			this.packetGengXin  = true;
		}
	}

	@Override
    public double wattRequest()
    {
        if (!this.isDisabled())
        {
            return Math.ceil(ElectricInfo.getWatts(this.getMaxWattHours()) - ElectricInfo.getWatts(this.dianXiaoShi));
        }

        return 0;
    }
	 
	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}

	@Override
	public double getVoltage()
	{
		return 220;
	}
	
	/**
     * Reads a tile entity from NBT.
     */
    @Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);
    	
    	this.dianXiaoShi = par1NBTTagCompound.getDouble("dianXiaoShi");
    	this.banJing = par1NBTTagCompound.getInteger("banJing");
    	this.muoShi = par1NBTTagCompound.getByte("muoShi");
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);

    	par1NBTTagCompound.setDouble("dianXiaoShi", this.dianXiaoShi);
    	par1NBTTagCompound.setInteger("banJing", this.banJing);
    	par1NBTTagCompound.setByte("muoShi", this.muoShi);
    }

	@Override
	public void onPowerOn() 
	{
		if(this.dianXiaoShi >= this.getMaxWattHours())
		{
			if(this.muoShi == 0 || this.muoShi == 1)
			{
				ZhaPin.EMPSignal.doBaoZha(worldObj, new Vector3(this.xCoord, this.yCoord, this.zCoord), null, this.banJing, -1);
			}
			
			if(this.muoShi == 0 || this.muoShi == 2)
			{
				ZhaPin.EMPWave.doBaoZha(worldObj, new Vector3(this.xCoord, this.yCoord, this.zCoord), null, this.banJing, -1);
			}

			this.dianXiaoShi = 0;
		}
	}

	@Override
	public void onPowerOff() { }
	
	@Override
	public void onDestroy(TileEntity callingBlock)
	{
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord, this.zCoord, 0);
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord+1, this.zCoord, 0);
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		entityPlayer.openGui(ICBM.instance, ICBMCommonProxy.GUI_EMP_TOWER, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		return true;
	}

	@Override
	public void onCreate(Vector3 position)
	{
		position.y += 1;
		BYinXing.makeInvisibleBlock(this.worldObj, position, Vector3.get(this));
	}

	@Override
	public double getMaxWattHours()
	{
		return Math.max(1500*((float)this.banJing/(float)MAX_RADIUS), 500);
	}

	@Override
    public double getWattHours(Object... data)
    {
    	return this.dianXiaoShi;
    }

	@Override
	public void setWattHours(double wattHours, Object... data)
	{
		this.dianXiaoShi = Math.max(Math.min(wattHours, this.getMaxWattHours()), 0);
	}
	
	@Override
	public void onChunkLoad(Chunk chunk)
	{
    	this.packetGengXin = true;
	}

	@Override
	public void onChunkUnload(Chunk chunk)
	{
		
	}
}
