package com.builtbroken.icbm.content.blast.thaum;

import com.builtbroken.icbm.api.IWarheadHandler;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.api.edit.IWorldChangeAction;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.lib.transform.vector.Location;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileJarNode;
import thaumcraft.common.tiles.TileNode;

import java.util.List;

/**
 * Handles creating node placement blast.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/16/2015.
 */
public final class ExplosiveHandlerNode implements IWarheadHandler
{
    private String id;
    private String modID;

    @Override
    public IWorldChangeAction createBlastForTrigger(World world, double x, double y, double z, TriggerCause triggerCause, double size, NBTTagCompound tag)
    {
        BlastNode blast = new BlastNode(new Location(world, x, y, z), tag);
        if (triggerCause instanceof TriggerCause.TriggerCauseEntity)
        {
            Entity source = ((TriggerCause.TriggerCauseEntity) triggerCause).source;
            if (source instanceof EntityMissile)
            {
                //TODO set missile source into blast to allow directional handling
            }
        }
        return blast;
    }

    public static NBTTagCompound convertToNBT(Location location)
    {
        //Block block = location.getBlock();
        TileEntity tile = location.getTileEntity();
        if (tile instanceof TileJarNode)
        {
            NBTTagCompound tag = new NBTTagCompound();
            ((TileJarNode) tile).writeCustomNBT(tag);
            return tag;
        }
        else if (tile instanceof TileNode)
        {
            NBTTagCompound tag = new NBTTagCompound();
            ((TileNode) tile).writeCustomNBT(tag);
            return tag;
        }
        return null;
    }

    /**
     * Converts a node {@link TileNode} to a jar node {@link thaumcraft.common.tiles.TileJarNode}
     */
    public static boolean jarNode(World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof INode)
        {
            INode node = (INode) tile;

            //Get node data
            AspectList na = node.getAspects().copy();
            int nodeType = node.getNodeType().ordinal();
            int nodeModifier = -1;
            if (node.getNodeModifier() != null)
            {
                nodeModifier = node.getNodeModifier().ordinal();
            }

            //Apply chance to change modifier
            if (world.rand.nextFloat() < 0.75F)
            {
                if (node.getNodeModifier() == null)
                {
                    nodeModifier = NodeModifier.PALE.ordinal();
                }
                else if (node.getNodeModifier() == NodeModifier.BRIGHT)
                {
                    nodeModifier = -1;
                }
                else if (node.getNodeModifier() == NodeModifier.PALE)
                {
                    nodeModifier = NodeModifier.FADING.ordinal();
                }
            }

            //More data
            String nid = node.getId();
            node.setAspects(new AspectList());

            //Remove node and set block
            world.removeTileEntity(x, y, z); //TODO check if needed
            world.setBlock(x, y, z, ConfigBlocks.blockJar, 2, 3);

            //Get jar tile
            tile = world.getTileEntity(x, y, z);
            TileJarNode jar = (TileJarNode) tile;

            //Set jar data
            jar.setAspects(na);
            if (nodeModifier >= 0)
            {
                jar.setNodeModifier(NodeModifier.values()[nodeModifier]);
            }
            jar.setNodeType(NodeType.values()[nodeType]);
            jar.setId(nid);

            //Tick
            world.addBlockEvent(x, y, z, ConfigBlocks.blockJar, 9, 0);
            return true;
        }
        return false;
    }

    @Override
    public void addInfoToItem(EntityPlayer player, ItemStack stack, List<String> lines)
    {

    }

    @Override
    public void addInfoToItem(EntityPlayer player, IWarhead warhead, List<String> list)
    {
        NBTTagCompound nbt = warhead.getAdditionalExplosiveData();
        if (nbt != null && nbt.hasKey("thuamNode"))
        {
            nbt = nbt.getCompoundTag("thaumNode");

            //Add node description
            String desc = "§9" + StatCollector.translateToLocal("nodetype." + this.getNodeType(nbt) + ".name");
            if (this.getNodeModifier(nbt) != null)
            {
                desc = desc + ", " + StatCollector.translateToLocal("nodemod." + this.getNodeModifier(nbt) + ".name");
            }
            list.add(desc);

            //List aspects
            AspectList aspects = this.getAspects(nbt);
            if (aspects != null && aspects.size() > 0)
            {
                Aspect[] arr$ = aspects.getAspectsSorted();
                int len$ = arr$.length;

                for (int i$ = 0; i$ < len$; ++i$)
                {
                    Aspect tag = arr$[i$];
                    if (Thaumcraft.proxy.playerKnowledge.hasDiscoveredAspect(player.getCommandSenderName(), tag))
                    {
                        list.add(tag.getName() + " x " + aspects.getAmount(tag));
                    }
                    else
                    {
                        list.add(StatCollector.translateToLocal("tc.aspect.unknown"));
                    }
                }
            }
        }
    }

    public AspectList getAspects(NBTTagCompound tag)
    {
        AspectList aspects = new AspectList();
        aspects.readFromNBT(tag);
        return aspects.size() > 0 ? aspects : null;
    }

    public NodeType getNodeType(NBTTagCompound tag)
    {
        return NodeType.values()[tag.getInteger("nodetype")];
    }

    public NodeModifier getNodeModifier(NBTTagCompound tag)
    {
        return tag.hasKey("nodemod") ? NodeModifier.values()[tag.getInteger("nodemod")] : null;
    }

    public String getNodeId(NBTTagCompound tag)
    {
        return tag.getString("nodeid");
    }

    @Override
    public void onRegistered(String id, String modID)
    {
        this.id = id;
        this.modID = modID;
    }

    @Override
    public String getTranslationKey()
    {
        return "explosive." + modID + ":ThaumNode";
    }

    @Override
    public String getID()
    {
        return this.id;
    }
}
