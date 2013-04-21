package icbm.zhapin;

import icbm.zhapin.zhapin.EZhaPin;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

public class MingLing extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "icbm";
	}

	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender)
	{
		return "/icbm lag <radius>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		try
		{
			EntityPlayer entityPlayer = (EntityPlayer) sender;
			int dimension = entityPlayer.worldObj.getWorldInfo().getDimension();

			if (args[0].equalsIgnoreCase("lag"))
			{
				int radius = parseInt(sender, args[1]);

				if (radius > 0 && radius < 10000)
				{
					EntityPlayer player = (EntityPlayer) sender;

					AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(player.posX - radius, player.posY - radius, player.posZ - radius, player.posX + radius, player.posY + radius, player.posZ + radius);
					List<Entity> entitiesNearby = player.worldObj.getEntitiesWithinAABB(Entity.class, bounds);

					for (Entity entity : entitiesNearby)
					{
						if (entity instanceof EFeiBlock)
						{
							((EFeiBlock) entity).setBlock();
						}
						else if (entity instanceof EZhaPin)
						{
							entity.setDead();
						}
					}

					sender.sendChatToPlayer("Removed all ICBM lag sources within " + radius + " radius.");
					return;
				}
				else
				{
					throw new WrongUsageException("Radius not within range.");
				}
			}
		}
		catch (Exception e)
		{
		}

		throw new WrongUsageException(this.getCommandUsage(sender));
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] { "lag" }) : null;
	}

}
