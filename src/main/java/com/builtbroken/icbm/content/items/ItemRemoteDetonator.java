package com.builtbroken.icbm.content.items;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.api.items.hz.IItemFrequency;
import com.builtbroken.mc.api.items.listeners.IItemActivationListener;
import com.builtbroken.mc.codegen.annotations.ItemWrapped;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.framework.item.ItemNode;
import com.builtbroken.mc.lib.world.map.radio.RadioRegistry;
import com.builtbroken.mc.prefab.hz.FakeRadioSender;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.List;

/**
 * Remotely triggers missile launches on a set frequency, call back ID, and pass key. Will not funciton if any of those
 * data points is missing.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
@ItemWrapped(className = ".gen.ItemWrapperRemoteDet", wrappers = "EnergyUE")
public class ItemRemoteDetonator extends ItemNode implements IItemFrequency, IItemActivationListener
{
    public ItemRemoteDetonator(String owner, String id)
    {
        super(owner, id);
    }

    public ItemRemoteDetonator()
    {
        this(ICBM.DOMAIN, "icbmRemoteDet");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            int mode = getMode(stack);
            if (mode > 0)
            {
                float hz = getBroadCastHz(stack);
                if (mode == 1)
                {
                    RadioRegistry.popMessage(world, new FakeRadioSender(player, stack, 2000), hz, "fireMissile" + mode, getPassKey(stack), getGroupID(stack), getSiloName(stack));
                }
                else if (mode == 2)
                {
                    RadioRegistry.popMessage(world, new FakeRadioSender(player, stack, 2000), hz, "fireMissileGroup" + mode, getPassKey(stack), getGroupID(stack));
                    //TODO add selective first missile firing
                }
                //TODO add mode for detonating warhead tiles
                //TODO add mode for chain firing
                //TODO add mode for group firing
            }
            else
            {
                player.addChatComponentMessage(new ChatComponentText("Not encoded for launch data! Use Command Silo Interface to encode with launch data..."));
            }
        }
        return stack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            int mode = getMode(stack);
            if (mode > 0)
            {
                float hz = getBroadCastHz(stack);
                if (mode == 1)
                {
                    RadioRegistry.popMessage(world, new FakeRadioSender(player, stack, 2000), hz, "fireMissile" + mode, getPassKey(stack), getGroupID(stack), getSiloName(stack));
                }
                else if (mode == 2)
                {
                    RadioRegistry.popMessage(world, new FakeRadioSender(player, stack, 2000), hz, "fireMissile" + mode, getPassKey(stack), getGroupID(stack));
                    //TODO add selective first missile firing
                }
                //TODO add mode for detonating warhead tiles
                //TODO add mode for chain firing
                //TODO add mode for group firing
            }
            else
            {
                player.addChatComponentMessage(new ChatComponentText("Not encoded for launch data! Use Command Silo Interface to encode with launch data..."));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return true;
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
    {
        return true;
    }


    /**
     * Encoded launch data into the silo
     *
     * @param stack
     * @param hz
     * @param passKey
     * @param groupID
     * @param siloName
     */
    public void encode(ItemStack stack, float hz, short passKey, String groupID, String siloName)
    {
        setMode(stack, 1);
        setBroadCastHz(stack, hz);
        setPassKey(stack, passKey);
        setGroupID(stack, groupID);
        setSiloName(stack, siloName);
    }

    /**
     * Encoded launch data into the silo
     *
     * @param stack
     * @param hz
     * @param passKey
     * @param groupID
     */
    public void encode(ItemStack stack, float hz, short passKey, String groupID)
    {
        setMode(stack, 2);
        setBroadCastHz(stack, hz);
        setPassKey(stack, passKey);
        setGroupID(stack, groupID);
        setSiloName(stack, null);
    }

    public short getPassKey(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("passKey"))
        {
            return stack.getTagCompound().getShort("passKey");
        }
        return 0;
    }

    public void setPassKey(ItemStack stack, short passKey)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setShort("passKey", passKey);
    }

    public String getGroupID(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("groupID"))
        {
            return stack.getTagCompound().getString("groupID");
        }
        return null;
    }

    public void setGroupID(ItemStack stack, String id)
    {
        if (id != null)
        {
            if (stack.getTagCompound() == null)
            {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.getTagCompound().setString("groupID", id);
        }
        else if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("groupID"))
        {
            stack.getTagCompound().removeTag("groupID");
        }
    }

    public String getSiloName(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("siloName"))
        {
            return stack.getTagCompound().getString("siloName");
        }
        return null;
    }

    public void setSiloName(ItemStack stack, String name)
    {
        if (name != null)
        {
            if (stack.getTagCompound() == null)
            {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.getTagCompound().setString("siloName", name);
        }
        else if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("siloName"))
        {
            stack.getTagCompound().removeTag("siloName");
        }
    }

    public int getMode(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("mode"))
        {
            return stack.getTagCompound().getInteger("mode");
        }
        return 0;
    }

    public void setMode(ItemStack stack, int mode)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger("mode", mode);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        if (getMode(stack) == 0)
        {
            list.add("Fires missiles remotely");
            list.add("Use Silo Command Interface to encode");
        }
        else if (getMode(stack) == 1)
        {
            list.add("Mode: Single Silo");
            if (Engine.runningAsDev)
            {
                list.add("Pass: " + getPassKey(stack));
            }
            list.add("Group: " + getGroupID(stack));
            list.add("Silo: " + getSiloName(stack));
            list.add("Hz: " + getBroadCastHz(stack));
        }
        else if (getMode(stack) == 1)
        {
            list.add("Mode: Group First Silo");
            if (Engine.runningAsDev)
            {
                list.add("Pass: " + getPassKey(stack));
            }
            list.add("Group: " + getGroupID(stack));
            list.add("Hz: " + getBroadCastHz(stack));
        }
    }
}
