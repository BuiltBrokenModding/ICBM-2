package icbm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.ICommand;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.WrongUsageException;

public class CommandICBM implements ICommand
{

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName()
	{
		return "ICBM";
	}

	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/" + getCommandName() + " <COMMAND>";
    }

	@Override
	public List getCommandAliases()
	{
		List list =  new ArrayList();
		
		list.add("/icbm <COMMAND>");
		
    	return list;
	}

	@Override
    public void processCommand(ICommandSender sender, String[] args)
	{
    	if (args.length!=1) throw new WrongUsageException("/" + getCommandName() + " <COMMAND>");
    	
    	System.out.println("PROCESS COMMMAND");
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2)
	{
		return null;
	}
	
}
