package com.builtbroken.icbm.server;

import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.icbm.content.missile.tracking.MissileTracker;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CommandICBM extends CommandBase
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
        if (sender instanceof EntityPlayer)
        {
            EntityPlayer entityPlayer = (EntityPlayer) sender;
            int dimension = entityPlayer.worldObj.provider.dimensionId;
            if (args == null || args.length == 0 || args[0].equalsIgnoreCase("help"))
            {
                ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("/ICBM lag <radius>"));
                ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("/ICBM remove <All/Missile/Explosion> <radius>"));
                ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("/ICBM emp <radius>"));
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
                        /**
                         if (entity instanceof EntityFlyingBlock)
                         {
                         ((EntityFlyingBlock) entity).setBlock();
                         } */
                        if (entity instanceof EntityMissile)
                        {
                            entity.setDead();
                        }
                        /**
                         if (entity instanceof EntityExplosion)
                         {
                         entity.setDead();
                         } */
                    }

                    ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("Removed all ICBM lag sources within " + radius + " radius."));
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
                        if ((all || missile) && entity instanceof EntityMissile)
                        {
                            entity.setDead();
                        }
                        /**
                         if ((all || explosion) && entity instanceof EntityFlyingBlock)
                         {
                         ((EntityFlyingBlock) entity).setBlock();
                         }
                         else
                         else if ((all || explosion) && entity instanceof EntityExplosion)
                         {
                         entity.setDead();
                         } */
                    }

                    ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("Removed all ICBM " + str + " within " + radius + " radius."));
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
                    //new BlastEMP(entityPlayer.worldObj, null, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, radius).setEffectBlocks().setEffectEntities().doExplode();
                    switch (entityPlayer.worldObj.rand.nextInt(20))
                    {
                        case 0:
                            ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("Did you pay the power bill?"));
                            return;
                        case 1:
                            ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("See them power their toys now!"));
                            return;
                        case 2:
                            ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("Hey who turned the lights out."));
                            return;
                        case 3:
                            ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("Ha! I run on steam power!"));
                            return;
                        case 4:
                            ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("The power of lighting at my finger tips!"));
                            return;
                        default:
                            ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("Zap!"));
                            return;
                    }
                }
                else
                {
                    throw new WrongUsageException("Radius needs to be higher than zero");
                }
            }
            else if (args[0].equalsIgnoreCase("missile"))
            {
                if (args.length >= 3)
                {
                    try
                    {
                        Location loc = new Location(entityPlayer.worldObj, Integer.parseInt(args[1]), 0, Integer.parseInt(args[2]));
                        Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.SMALL, ExplosiveRegistry.get("TNT"));
                        MissileTracker.spawnMissileOverTarget(missile, loc, entityPlayer);
                        ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("Firing missile at " + loc.toString()));

                    }
                    catch (NumberFormatException e)
                    {
                        ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("X and Z coords need to be integers"));
                    }
                }
                else
                {
                    ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("X and Z coords are needed"));
                }
            }
            else
            {
                ((EntityPlayer) sender).addChatComponentMessage(new ChatComponentText("/ICBM help"));
            }
        }
        else
        {
            sender.addChatMessage(new ChatComponentText("ICBM commands can only be used by players currently."));
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "lag") : null;
    }

    @Override
    public int compareTo(Object par1Obj)
    {
        return this.compareTo((ICommand) par1Obj);
    }
}
