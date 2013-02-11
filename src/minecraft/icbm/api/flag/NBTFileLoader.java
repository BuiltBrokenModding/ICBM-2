package icbm.api.flag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;

public class NBTFileLoader
{
	/**
	 * Saves NBT data in the world folder.
	 */
	public static boolean saveData(NBTTagCompound data, String filename, String minecraftDir)
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
			File tempFile;
			File file;

			if (minecraftDir != "")
			{
				tempFile = new File(minecraftDir, folder + File.separator + filename + "_tmp.dat");
				file = new File(minecraftDir, folder + File.separator + filename + ".dat");
			}
			else
			{
				tempFile = new File(folder + File.separator + filename + "_tmp.dat");
				file = new File(folder + File.separator + filename + ".dat");
			}

			CompressedStreamTools.writeCompressed(data, new FileOutputStream(tempFile));

			if (file.exists())
			{
				file.delete();
			}

			tempFile.renameTo(file);

			FMLLog.fine("Saved ICBM data successfully.");
			return true;
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to save " + filename + ".dat!");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Reads NBT data from the world folder.
	 * 
	 * @param filename
	 * @param minecraftDir
	 * @return
	 */
	public static NBTTagCompound loadData(String filename, String minecraftDir)
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
			File file;

			if (minecraftDir != "")
			{
				file = new File(minecraftDir, folder + File.separator + filename + ".dat");
			}
			else
			{
				file = new File(folder + File.separator + filename + ".dat");
			}

			if (file.exists())
			{
				return CompressedStreamTools.readCompressed(new FileInputStream(file));
			}
			else
			{
				return new NBTTagCompound();
			}
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to load " + filename + ".dat!");
			e.printStackTrace();
			return null;
		}
	}

	public File getSaveDirectory()
	{
		if (FMLCommonHandler.instance().getSide().isClient())
		{
			FMLClientHandler.instance().getClient();
			return Minecraft.getMinecraftDir();
		}
		else
		{
			return new File(".");
		}
	}
}
