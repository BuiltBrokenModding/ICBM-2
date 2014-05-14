package icbm.sentry.gs.block;

import icbm.core.ICBMCore;
import icbm.core.prefab.TileICBM;
import icbm.sentry.ICBMSentry;

import java.awt.Color;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraftforge.common.ForgeDirection;
import resonant.api.IRedstoneReceptor;
import resonant.api.IRotatable;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TileLaserGate extends TileICBM implements IRotatable, IRedstoneReceptor {

	private TileLaserGate pairedGate;
	private final int LASER_REACH_LENGTH = 10;
	
	private boolean renderLaser = false;
	
	public TileLaserGate() {
		super();
		setEnergyHandler(new EnergyStorageHandler(10000, 100));
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		
		if(!worldObj.isRemote) {
			if(ticks%10==0) {
				if(pairedGate == null) findLaserPair();
			}
		}
		
		if(renderLaser) {
			if(pairedGate != null) {
				//Well.. apparently this is being called server side only, 
				ICBMSentry.proxy.renderBeam(worldObj, new Vector3(this), new Vector3(getLaserPair()), Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 1);
				
				if(!worldObj.isRemote) System.out.println("Server: AHHH");
				if(worldObj.isRemote) System.out.println("Client: AHHH");
			}
		}		
	}
	
	@Override
	public void invalidate() {
		if(this.pairedGate != null) this.pairedGate.setLaserPair(null);
		setLaserPair(null);
		this.renderLaser = false;
		
		super.invalidate();
	}
	
	public void setLaserPair(TileLaserGate lsg) {
		this.pairedGate = lsg;
	}
	
	public TileLaserGate getLaserPair() {
		return pairedGate;
	}
	
	public void findLaserPair() {
		for(int i = LASER_REACH_LENGTH; i > 0; i--) {
			Vector3 loc = new Vector3(this).translate(getFacingDirection(), i);
			if(loc.getTileEntity(worldObj) instanceof TileLaserGate && loc.getTileEntity(worldObj) != this) {
				TileLaserGate fence = (TileLaserGate) loc.getTileEntity(worldObj);
	             if (fence.getFacingDirection() == this.getFacingDirection()) {
	            	 setLaserPair(fence);
	            	 fence.setLaserPair(this);
	            	 return;
	             }
			}
		}
	}
	
	public ForgeDirection getFacingDirection() {
		return this.getDirection();
	}

	
	@Override
	public void onPowerOff() {
		// Stop Drawing Guff
		if(pairedGate != null) {
			this.renderLaser = false;
			this.getLaserPair().renderLaser = false;
		}
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		super.onDataPacket(net, pkt);
		
		NBTTagCompound tag = pkt.data;
		if (tag.hasKey("renderLaser")) {
			renderLaser = tag.getBoolean("renderLaser");
		}
	}
	
	@Override
	public Packet getDescriptionPacket() {
		super.getDescriptionPacket();
		
		if (!this.worldObj.isRemote) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("renderLaser", renderLaser);

            return ICBMCore.PACKET_TILE.getPacket(this, tag);
        }
		 
        return null;
	}
	
	@Override
	public void onPowerOn() {
		if(pairedGate != null) {
			this.renderLaser = true;
			this.getLaserPair().renderLaser = true;
			PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 30, 0, getDescriptionPacket());
		}


	}	
}
