package icbm;

import icbm.zhapin.EZhaPin;

import java.util.Arrays;
import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.CommandBase;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.WrongUsageException;
import cpw.mods.fml.common.FMLCommonHandler;

public class CommandICBM extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "ICBM";
	}

	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/" + getCommandName() + " <command> <parameters>";
    }

	@Override
	public List getCommandAliases()
	{
    	return Arrays.asList(new String[] {"/icbm"});
	}

	@Override
    public void processCommand(ICommandSender sender, String[] args)
	{
		try
		{
			if(args[0].contains("lag"))
			{
				int radius = parseInt(sender, args[1]);
				
				if(radius > 0 && radius < 1000)
				{
					EntityPlayer player = (EntityPlayer)sender;
					
					AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(player.posX - radius, player.posY - radius, player.posZ - radius, player.posX + radius, player.posY + radius, player.posZ + radius);
			        List<Entity> entitiesNearby = player.worldObj.getEntitiesWithinAABB(Entity.class, bounds);
			        
			        for(Entity entity : entitiesNearby)
			        {
			        	if(entity instanceof EFeiBlock)
			        	{
			        		((EFeiBlock)entity).setBlock();
			        	}
			        	else if(entity instanceof EZhaPin)
			        	{
			        		entity.setDead();
			        	}
			        }
				}
				else
				{
					 throw new WrongUsageException("/" + getCommandName() + " lag <radius>");
				}
			}
		}
		catch (Exception e)
    	{
			 throw new WrongUsageException("/" + getCommandName() + " <command> <parameters>");
    	}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if(sender instanceof EntityPlayer)
		{
			return FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().areCommandsAllowed(sender.getCommandSenderName());
		}
		
		return false;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2)
	{
		return null;
	}
	
}
