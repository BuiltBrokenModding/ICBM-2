package icbm;

import icbm.electronics.ItemLaserDesignator;
import icbm.electronics.ItemRadarGun;
import icbm.electronics.ItemSignalDisrupter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import universalelectricity.Vector3;
import universalelectricity.extend.ItemElectric;
import universalelectricity.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * This class is used for sending and receiving packets between the server
 * and the client. You can directly use this by registering this packet manager
 * with NetworkMod. Example:
 * @NetworkMod(channels = { "BasicComponents" }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketManager.class)
 * 
 * Check out {@link #BasicComponents} for better reference.
 * 
 * @author Calclavia
 */
public class ICBMPacketManager extends PacketManager
{
	public static final int RADAR_GUN_PACKET = 1;
	public static final int LASER_DESIGNATOR_PACKET = 2;
	public static final int SIGNAL_DISRUPTER_PACKET = 3;

	@Override
	public void handlePacketData(NetworkManager network, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		int ID = dataStream.readInt();

		if(ID == RADAR_GUN_PACKET)
		{
			if(player.inventory.getCurrentItem().getItem() instanceof ItemRadarGun)
			{
				ItemStack itemStack = player.inventory.getCurrentItem();
				//Saves the frequency in the itemstack
				if (itemStack.stackTagCompound == null)
				{
					 itemStack.setTagCompound(new NBTTagCompound());
				}
	
				itemStack.stackTagCompound.setInteger("x", dataStream.readInt());
				itemStack.stackTagCompound.setInteger("y", dataStream.readInt());
				itemStack.stackTagCompound.setInteger("z", dataStream.readInt());
				((ItemElectric) ICBM.itemRadarGun).onUseElectricity(ItemRadarGun.ELECTRICITY_REQUIRED, itemStack);
			}
		}
		else if(ID == LASER_DESIGNATOR_PACKET)
		{
			if(player.inventory.getCurrentItem().getItem() instanceof ItemLaserDesignator)
			{				
				ItemStack itemStack = player.inventory.getCurrentItem();
				Vector3 position = new Vector3(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());

				((ItemLaserDesignator)ICBM.itemLaserDesignator).setLauncherCountDown(itemStack, 119);
	    		
				player.worldObj.playSoundEffect(position.intX(), player.worldObj.getHeightValue(position.intX(), position.intZ()), position.intZ(), "icbm.airstrike", 5.0F, (1.0F + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
	    		
				player.worldObj.spawnEntityInWorld(new EntityLightBeam(player.worldObj, position, 5*20, 0F, 1F, 0F));
	    		
				((ItemElectric) ICBM.itemRadarGun).onUseElectricity(ItemLaserDesignator.ELECTRICITY_CAPACITY, itemStack);
			}
		}
		else if(ID == SIGNAL_DISRUPTER_PACKET)
		{
			if(player.inventory.getCurrentItem().getItem() instanceof ItemSignalDisrupter)
			{
				ItemStack itemStack = player.inventory.getCurrentItem();
				
            	((ItemSignalDisrupter)itemStack.getItem()).setFrequency(itemStack, dataStream.readShort());
            	System.out.println("SET");
			}
		}
	}
}
