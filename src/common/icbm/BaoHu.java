package icbm;

import icbm.zhapin.ZhaPin.ZhaPinType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import universalelectricity.core.UEConfig;
import universalelectricity.core.vector.Vector2;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;

public class BaoHu
{
	/**
	 * File Structure: Tag: DimData -Tag: DimID -boolean: globalBan -Tag: RegionName -int: X -int: Z
	 * -int: R -int: TYPE
	 */
	public static NBTTagCompound nbtData;

	public static final String FIELD_GLOBAL_BAN = "globalBan";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_X = "X";
	public static final String FIELD_Z = "Z";
	public static final String FIELD_R = "R";

	public static final boolean DEFAULT_PROTECITON = UEConfig.getConfigData(ZhuYao.CONFIGURATION, "Protect Worlds by Default", false);

	public static boolean shiWeiZhiBaoHu(World worldObj, Vector2 position, ZhaPinType type)
	{
		if (!worldObj.isRemote)
		{
			NBTTagCompound dimData = nbtData.getCompoundTag("dim" + worldObj.provider.dimensionId);

			if (nengQuanQiuBaoHu(dimData)) { return true; }

			// Regions check
			Iterator i = dimData.getTags().iterator();
			while (i.hasNext())
			{
				try
				{
					NBTTagCompound region = (NBTTagCompound) i.next();

					if (Vector2.distance(position, new Vector2(region.getInteger(FIELD_X), region.getInteger(FIELD_Z))) <= region.getInteger(FIELD_R)) { return (ZhaPinType.get(region.getInteger(FIELD_TYPE)) == ZhaPinType.QUAN_BU || ZhaPinType.get(region.getInteger(FIELD_TYPE)) == type); }
				}
				catch (Exception e)
				{
				}
			}
		}

		return false;
	}

	public static boolean nengDanBaoHu(World worldObj, Vector2 position)
	{
		return !shiWeiZhiBaoHu(worldObj, position, ZhaPinType.ZHA_DAN);
	}

	public static boolean nengShouLiuDanBaoHu(World worldObj, Vector2 position)
	{
		return !shiWeiZhiBaoHu(worldObj, position, ZhaPinType.SHOU_LIU_DAN);
	}

	public static boolean nengDaoDanBaoHu(World worldObj, Vector2 position)
	{
		return !shiWeiZhiBaoHu(worldObj, position, ZhaPinType.DAO_DAN);
	}

	public static boolean nengQuanQiuBaoHu(NBTTagCompound dimData)
	{
		return ((!dimData.hasKey(FIELD_GLOBAL_BAN) && DEFAULT_PROTECITON) || dimData.getBoolean(FIELD_GLOBAL_BAN));
	}

	public static boolean saveData(NBTTagCompound data, String filename)
	{
		String folder;

		if (MinecraftServer.getServer().isDedicatedServer())
		{
			folder = MinecraftServer.getServer().getFolderName();
		}
		else
		{
			folder = "saves" + File.separator + MinecraftServer.getServer().getFolderName();
		}
		
		try
		{
			File var3 = new File(Minecraft.getMinecraftDir(), folder + File.separator + filename + "_tmp.dat");
			File var4 = new File(Minecraft.getMinecraftDir(), folder + File.separator + filename + ".dat");

			CompressedStreamTools.writeCompressed(data, new FileOutputStream(var3));

			if (var4.exists())
			{
				var4.delete();
			}

			var3.renameTo(var4);

			FMLLog.fine("Saved ICBM data successfully.");
			return true;
		}
		catch (Exception var5)
		{
			FMLLog.severe("Failed to save " + filename + ".dat!");
			return false;
		}
	}

	public static NBTTagCompound loadData(String filename)
	{
		String folder;

		if (MinecraftServer.getServer().isDedicatedServer())
		{
			folder = MinecraftServer.getServer().getFolderName();
		}
		else
		{
			folder = "saves" + File.separator + MinecraftServer.getServer().getFolderName();
		}

		try
		{
			File var2 = new File(Minecraft.getMinecraftDir(), folder + File.separator + filename + ".dat");

			if (var2.exists())
			{
				return CompressedStreamTools.readCompressed(new FileInputStream(var2));
			}
			else
			{
				return new NBTTagCompound();
			}
		}
		catch (Exception var3)
		{
			FMLLog.severe("Failed to load " + filename + ".dat!");
			return null;
		}
	}
}
