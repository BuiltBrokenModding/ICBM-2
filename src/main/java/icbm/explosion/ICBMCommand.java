package icbm.explosion;

import icbm.explosion.explosive.EntityExplosion;
import icbm.explosion.explosive.blast.BlastEMP;
import icbm.explosion.missile.missile.EntityMissile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;

import java.util.List;

public class ICBMCommand extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "icbm";
    }

    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/icbm help";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        try
        {
            EntityPlayer entityPlayer = (EntityPlayer) sender;
            int dimension = entityPlayer.worldObj.provider.dimensionId;
            if (args == null || args.length == 0 || args[0].equalsIgnoreCase("help"))
            {
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/ICBM help"));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/ICBM lag <radius>"));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/ICBM remove <All/Missile/Explosion> <radius>"));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText("/ICBM emp <radius>"));
                return;
            }
            else if (args.length >= 2 && args[0].equalsIgnoreCase("lag"))
            {
                int radius = parseInt(sender, args[1]);

                if (radius > 0)
                {

                    AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(entityPlayer.posX - radius, entityPlayer.posY - radius, entityPlayer.posZ - radius, entityPlayer.posX + radius, entityPlayer.posY + radius, entityPlayer.posZ + radius);
                    List<Entity> entitiesNearby = entityPlayer.worldObj.getEntitiesWithinAABB(Entity.class, bounds);

                    for (Entity entity : entitiesNearby)
                    {
                        if (entity instanceof EntityFlyingBlock)
                        {
                            ((EntityFlyingBlock) entity).setBlock();
                        }
                        else if (entity instanceof EntityMissile)
                        {
                            entity.setDead();
                        }
                        else if (entity instanceof EntityExplosion)
                        {
                            entity.setDead();
                        }
                    }

                    sender.sendChatToPlayer(ChatMessageComponent.createFromText("Removed all ICBM lag sources within " + radius + " radius."));
                    return;
                }
                else
                {
                    throw new WrongUsageException("Radius needs to be higher than zero");
                }
            }
            else if (args.length >= 3 && args[0].equalsIgnoreCase("remove"))
            {
                int radius = parseInt(sender, args[2]);
                boolean all = args[1].equalsIgnoreCase("all");
                boolean missile = args[1].equalsIgnoreCase("missiles");
                boolean explosion = args[1].equalsIgnoreCase("explosion");
                String str = "entities";
                if (missile)
                {
                    str = "missiles";
                }
                if (explosion)
                {
                    str = "explosions";
                }

                if (radius > 0)
                {
                    EntityPlayer player = (EntityPlayer) sender;

                    AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(player.posX - radius, player.posY - radius, player.posZ - radius, player.posX + radius, player.posY + radius, player.posZ + radius);
                    List<Entity> entitiesNearby = player.worldObj.getEntitiesWithinAABB(Entity.class, bounds);

                    for (Entity entity : entitiesNearby)
                    {
                        if ((all || explosion) && entity instanceof EntityFlyingBlock)
                        {
                            ((EntityFlyingBlock) entity).setBlock();
                        }
                        else if ((all || missile) && entity instanceof EntityMissile)
                        {
                            entity.setDead();
                        }
                        else if ((all || explosion) && entity instanceof EntityExplosion)
                        {
                            entity.setDead();
                        }
                    }

                    sender.sendChatToPlayer(ChatMessageComponent.createFromText("Removed all ICBM " + str + " within " + radius + " radius."));
                    return;
                }
                else
                {
                    throw new WrongUsageException("Radius needs to be higher than zero");
                }
            }
            else if (args.length >= 2 && args[0].equalsIgnoreCase("emp"))
            {
                int radius = parseInt(sender, args[1]);
                if (radius > 0)
                {
                    new BlastEMP(entityPlayer.worldObj, null, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, radius).setEffectBlocks().setEffectEntities().doExplode();
                    switch (entityPlayer.worldObj.rand.nextInt(20))
                    {
                        case 0:
                            sender.sendChatToPlayer(ChatMessageComponent.createFromText("Did you pay the power bill?"));
                            return;
                        case 1:
                            sender.sendChatToPlayer(ChatMessageComponent.createFromText("See them power their toys now!"));
                            return;
                        case 2:
                            sender.sendChatToPlayer(ChatMessageComponent.createFromText("Hey who turned the lights out."));
                            return;
                        case 3:
                            sender.sendChatToPlayer(ChatMessageComponent.createFromText("Ha! I run on steam power!"));
                            return;
                        case 4:
                            sender.sendChatToPlayer(ChatMessageComponent.createFromText("The power of lighting at my finger tips!"));
                            return;
                        default:
                            sender.sendChatToPlayer(ChatMessageComponent.createFromText("Zap!"));
                            return;
                    }
                }
                else
                {
                    throw new WrongUsageException("Radius needs to be higher than zero");
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

    @Override
    public int compareTo(Object par1Obj)
    {
        return this.compareTo((ICommand) par1Obj);
    }
}
