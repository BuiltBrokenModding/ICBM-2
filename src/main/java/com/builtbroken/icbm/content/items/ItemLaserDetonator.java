package com.builtbroken.icbm.content.items;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.launcher.ILauncher;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketReceiver;
import com.builtbroken.mc.core.network.packet.PacketPlayerItem;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.world.radio.RadioRegistry;
import com.builtbroken.mc.prefab.hz.FakeRadioSender;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;

/**
 * Extended version of {@link ItemRemoteDetonator} that can target blocks in a line of sight.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class ItemLaserDetonator extends ItemRemoteDetonator implements IRecipeContainer, IPacketReceiver
{
    public ItemLaserDetonator()
    {
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setUnlocalizedName(ICBM.PREFIX + "laserDetonator");
        this.setTextureName(ICBM.PREFIX + "laserDesignator");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            MovingObjectPosition objectMouseOver = player.rayTrace(200, 1);
            TileEntity tileEntity = world.getTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
            if (!(tileEntity instanceof ILauncher))
            {
                Engine.instance.packetHandler.sendToServer(new PacketPlayerItem(player, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
            }
        }
        return stack;
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

    public float getBroadCastHz(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("hz"))
        {
            return stack.getTagCompound().getFloat("hz");
        }
        return 0;
    }

    public void setBroadCastHz(ItemStack stack, float hz)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setFloat("hz", hz);
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

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        recipes.add(newShapedRecipe(this, "GLP", "BDW", "ECE", 'G', Items.glass_bottle, 'B', Items.blaze_rod, 'L', Blocks.redstone_lamp, 'P', OreNames.PLATE_STEEL, 'W', OreNames.WIRE_COPPER, 'E', Items.repeater, 'C', UniversalRecipe.CIRCUIT_T2.get(), 'D', ICBM.itemRemoteDetonator));
    }

    @Override
    public void read(ByteBuf buf, EntityPlayer player, PacketType packet)
    {
        ItemStack stack = player.inventory.getCurrentItem();
        if (stack != null && stack.getItem() == this)
        {
            if (!player.worldObj.isRemote)
            {
                Pos pos = new Pos(buf.readInt(), buf.readInt(), buf.readInt());
                int mode = getMode(stack);
                if (mode > 0)
                {
                    float hz = getBroadCastHz(stack);
                    if (mode == 1)
                    {
                        RadioRegistry.popMessage(player.worldObj, new FakeRadioSender(player, stack, 2000), hz, "fireMissileAtTarget", getPassKey(stack), getGroupID(stack), getSiloName(stack), pos);
                    }
                    else if (mode == 2)
                    {
                        RadioRegistry.popMessage(player.worldObj, new FakeRadioSender(player, stack, 2000), hz, "fireMissileGroupAtTarget", getPassKey(stack), getGroupID(stack), pos);
                        //TODO add selective first missile firing
                    }
                    //TODO add mode for chain firing
                    //TODO add mode for group firing
                }
                else
                {
                    player.addChatComponentMessage(new ChatComponentText("Not encoded for launch data! Use Command Silo Interface to encode with launch data..."));
                }
            }
        }
    }
}
