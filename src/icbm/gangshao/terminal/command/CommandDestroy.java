package icbm.gangshao.terminal.command;

import icbm.gangshao.platform.TileEntityTurretPlatform;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import dark.core.api.ISpecialAccess;
import dark.core.api.ITerminal;
import dark.library.access.AccessLevel;
import dark.library.machine.terminal.TerminalCommand;

public class CommandDestroy extends TerminalCommand
{
	@Override
	public String getCommandPrefix()
	{
		return "destroy";
	}

	@Override
	public boolean processCommand(EntityPlayer player, ITerminal terminal, String[] args)
	{
		if (terminal instanceof TileEntityTurretPlatform)
		{
			TileEntityTurretPlatform turret = (TileEntityTurretPlatform) terminal;

			if (args.length > 1)
			{
				turret.destroyTurret();
				terminal.addToConsole("Destroyed Turret");
				return true;
			}
			else
			{
				turret.destroy(false);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canPlayerUse(EntityPlayer var1, ISpecialAccess mm)
	{
		return mm.getUserAccess(var1.username).ordinal() >= AccessLevel.ADMIN.ordinal();
	}

	@Override
	public boolean showOnHelp(EntityPlayer player, ISpecialAccess mm)
	{
		return this.canPlayerUse(player, mm);
	}

	@Override
	public List<String> getCmdUses(EntityPlayer player, ISpecialAccess mm)
	{
		List<String> cmds = new ArrayList<String>();
		cmds.add("destroy");
		cmds.add("destroy turret");
		return cmds;
	}

	@Override
	public boolean canMachineUse(ISpecialAccess mm)
	{
		return mm instanceof TileEntityTurretPlatform;
	}

}
