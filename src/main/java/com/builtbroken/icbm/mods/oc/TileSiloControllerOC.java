package com.builtbroken.icbm.mods.oc;

import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.launcher.controller.direct.TileSiloController;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileSiloControllerOC extends TileSiloController implements Environment, Analyzable
{
    //TODO add detailed data about the missile and launcher
    //TODO allow setting launcher name
    //TODO allow detonating warhead in silo, hacking :P
    private Node node;

    @Override
    public Tile newTile(World world, int meta)
    {
        return new TileSiloControllerOC();
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        Network.joinOrCreateNetwork(this);

    }

    @Override
    public void update()
    {
        super.update();
        if (ticks % 5 == 0 && node != null && node.network() == null)
        {
            Network.joinOrCreateNetwork(this);
        }
    }

    @Override
    public Node[] onAnalyze(EntityPlayer entityPlayer, int i, float v, float v1, float v2)
    {
        return new Node[]{node};
    }

    @Override
    public Node node()
    {
        if(node == null)
        {
            node = Network.newNode(this, Visibility.Network).withComponent("icbmSiloConnector", Visibility.Network).withConnector().create();
        }
        return node;
    }

    @Override
    public void onConnect(Node node)
    {

    }

    @Override
    public void onDisconnect(Node node)
    {
    }

    @Override
    public void onMessage(Message message)
    {

    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("OCNode") && node != null && node.host() == this)
        {
            node.load(nbt.getCompoundTag("OCNode"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (node != null && node.host() == this)
        {
            NBTTagCompound tag = new NBTTagCompound();
            node.save(tag);
            nbt.setTag("OCNode", tag);
        }
    }

    @Override
    public void onChunkUnload()
    {
        super.onChunkUnload();
        if (node != null)
        {
            node.remove();
        }
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (node != null)
        {
            node.remove();
        }
    }

    @Callback
    public Object[] isConnectedToLauncher(Context context, Arguments args)
    {
        return new Object[]{launcher != null};
    }

    @Callback
    public Object[] hasMissile(Context context, Arguments args)
    {
        if (launcher == null)
        {
            throw new RuntimeException("Not connected to a launcher");
        }
        return new Object[]{launcher.getMissile() != null};
    }

    @Callback
    public Object[] doesMissileHaveWarhead(Context context, Arguments args)
    {
        if (launcher == null)
        {
            throw new RuntimeException("Not connected to a launcher");
        }
        Missile missile = launcher.getMissile();
        if (missile == null)
        {
            throw new RuntimeException("No missile loaded");
        }
        return new Object[]{missile.getWarhead() != null};
    }

    @Callback
    public Object[] doesMissileHaveGuidance(Context context, Arguments args)
    {
        if (launcher == null)
        {
            throw new RuntimeException("Not connected to a launcher");
        }
        Missile missile = launcher.getMissile();
        if (missile == null)
        {
            throw new RuntimeException("No missile loaded");
        }
        return new Object[]{missile.getGuidance() != null};
    }

    @Callback
    public Object[] doesMissileHaveEngine(Context context, Arguments args)
    {
        if (launcher == null)
        {
            throw new RuntimeException("Not connected to a launcher");
        }
        Missile missile = launcher.getMissile();
        if (missile == null)
        {
            throw new RuntimeException("No missile loaded");
        }
        return new Object[]{missile.getEngine() != null};
    }

    @Callback
    public Object[] fireMissile(Context context, Arguments args)
    {
        if (launcher == null)
        {
            throw new RuntimeException("Not connected to a launcher");
        }
        if (launcher.target == null)
        {
            throw new RuntimeException("No target set in the launcher");
        }
        return new Object[]{launcher.fireMissile()};
    }

    @Callback
    public Object[] canFireMissile(Context context, Arguments args)
    {
        if (launcher == null)
        {
            throw new RuntimeException("Not connected to a launcher");
        }
        if (launcher.target == null)
        {
            return new Object[]{false, "No target set"};
        }
        if (!launcher.canFireMissile())
        {
            Missile missile = launcher.getMissile();
            if (missile == null)
            {
                if (launcher.getMissileItem() != null)
                {
                    return new Object[]{false, "Invalid item in missile tube"};
                }
                return new Object[]{false, "No missile loaded"};
            }
            else if (!missile.canLaunch())
            {
                if (missile.getEngine() != null)
                {
                    return new Object[]{false, "Engine is invalid or has no fuel"};
                }
                return new Object[]{false, "Missile has no engine"};
            }
            return new Object[]{false, "Unknown reason"};
        }
        return new Object[]{true, "All green"};
    }

    @Callback
    public Object[] setTarget(Context context, Arguments args)
    {
        if (launcher == null)
        {
            throw new RuntimeException("Not connected to a launcher");
        }
        if (!args.isDouble(0) || !args.isDouble(1) || !args.isDouble(2))
        {
            throw new RuntimeException("3 doubles are expected");
        }
        launcher.setTarget(new Pos(args.checkDouble(0), args.checkDouble(1), args.checkDouble(2)));
        return new Object[]{true};
    }

    @Callback
    public Object[] getTarget(Context context, Arguments args) throws Exception
    {
        if (launcher == null)
        {
            throw new RuntimeException("Not connected to a launcher");
        }
        if (launcher.target == null)
        {
            throw new RuntimeException("No target set in the launcher");
        }
        return new Object[]{launcher.target.x(), launcher.target.y(), launcher.target.z()};
    }
}
