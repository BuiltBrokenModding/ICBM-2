package com.builtbroken.icbm.content.crafting.station.small.auto;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.mc.api.automation.IAutomatedCrafter;
import com.builtbroken.mc.api.automation.IAutomation;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import com.builtbroken.mc.prefab.tile.module.TileModuleInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles crafting and configuration of missile
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/6/2016.
 */
public class TileSMAutoCraft extends TileModuleMachine implements IPacketIDReceiver, IGuiTile, IAutomatedCrafter
{
    public static final int BODY_SLOT = 0;
    public static final int WARHEAD_SLOT = 1;
    public static final int GUIDANCE_SLOT = 2;
    public static final int ENGINE_SLOT = 3;
    public static final int OUTPUT_SLOT = 4;
    public static final int[] INPUT_SLOTS = new int[]{BODY_SLOT, WARHEAD_SLOT, GUIDANCE_SLOT, ENGINE_SLOT};
    public static final int[] OUTPUT_SLOTS = new int[]{OUTPUT_SLOT};

    //TODO add tabs, crafting, configuration, reverse crafting
    //TODO add option to limit number of inserted explosives per craft (1-64 scale, all)

    /** Is the machine setup to autocraft */
    protected boolean isAutocrafting = false;
    /** Triggers crafting logic. */
    protected boolean checkForCraft = false;


    public TileSMAutoCraft()
    {
        super("warheadStation", Material.iron);
        this.resistance = 10f;
        this.hardness = 10f;
        this.renderTileEntity = true;
        this.renderNormalBlock = false;
    }

    @Override
    public Tile newTile()
    {
        return new TileSMAutoCraft();
    }

    @Override
    public void update()
    {
        super.update();
        if (isServer() && ticks % 10 == 0)
        {
            if (checkForCraft)
            {
                checkForCraft = false;
                //TODO add crafting timer
                if (canCraft())
                {
                    doCrafting();
                }
            }
        }
    }

    /** Called to process a crafting request */
    public void doCrafting()
    {
        //TODO
    }

    /**
     * Checks if it is possible to process a crafting recipe
     *
     * @return true if possible
     */
    public boolean canCraft()
    {
        //TODO
        return false;
    }

    @Override
    public float getCraftingProgress()
    {
        return 0;
    }

    @Override
    public List<ItemStack> getRequiredItems()
    {
        return new ArrayList();
    }

    @Override
    public ItemStack insertRequiredItem(ItemStack stack, IAutomation entity, ForgeDirection side)
    {
        if (isAutocrafting && stack != null)
        {
            //TODO
        }
        return stack;
    }

    @Override
    public ItemStack insertRequiredItem(ItemStack stack, int slot, IAutomation entity, ForgeDirection side)
    {
        if (isAutocrafting && stack != null)
        {
            //TODO
        }
        return stack;
    }

    @Override
    public int[] getCraftingOutputSlots(IAutomation entity, ForgeDirection side)
    {
        return OUTPUT_SLOTS;
    }

    @Override
    public int[] getCraftingInputSlots(IAutomation entity, ForgeDirection side)
    {
        return INPUT_SLOTS;
    }

    @Override
    public boolean canStore(ItemStack stack, ForgeDirection side)
    {
        //TODO
        return false;
    }

    @Override
    public boolean canRemove(ItemStack stack, ForgeDirection side)
    {
        return true;
    }

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        checkForCraft = true;
        sendDescPacket();
    }

    /**
     * Gets the expected output of a crafting recipe
     *
     * @return warhead
     */
    public IMissile getCraftedMissile()
    {
        //TODO
        return null;
    }

    /**
     * Gets the expected output of a crafting recipe
     *
     * @return ItemStack, or null if not possible to craft
     */
    public ItemStack getCraftResult()
    {
        final IMissile result = getCraftedMissile();
        return result != null ? result.toStack() : null;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        if (stack != null)
        {
            if (WARHEAD_SLOT == slot)
            {
                //TODO
            }
            else if (BODY_SLOT == slot)
            {
                //TODO
            }
            else if (ENGINE_SLOT == slot)
            {
                //TODO
            }
        }
        return false;
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        return slot == OUTPUT_SLOT;
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            openGui(player, ICBM.INSTANCE);
        }
        return true;
    }


    @Override
    public TileModuleInventory getInventory()
    {
        if (super.getInventory() == null)
        {
            //lazy init of inventory
            addInventoryModule(4);
        }
        return (TileModuleInventory) super.getInventory();
    }

    protected ItemStack getWarheadStack()
    {
        return getInventory().getStackInSlot(WARHEAD_SLOT);
    }

    protected ItemStack getExplosiveStack()
    {
        return getInventory().getStackInSlot(BODY_SLOT);
    }

    protected ItemStack getOutputStack()
    {
        return getInventory().getStackInSlot(OUTPUT_SLOT);
    }

    protected ItemStack getTriggerStack()
    {
        return getInventory().getStackInSlot(ENGINE_SLOT);
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        super.doUpdateGuiUsers();
        if (!super.read(buf, id, player, type))
        {
            if (isServer())
            {
                if (id == 1)
                {
                    doCrafting();
                    return true;
                }
                else if (id == 2)
                {
                    openGui(player, buf.readInt(), ICBM.INSTANCE);
                    return true;
                }
                //Gui updated some settings
                else if (id == 3)
                {
                    isAutocrafting = buf.readBoolean();
                    //TODO
                    _doUpdateGuiUsers();
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
    }

    @Override
    public void doUpdateGuiUsers()
    {
        if (ticks % 5 == 0)
        {
            _doUpdateGuiUsers();
        }
    }

    public void _doUpdateGuiUsers()
    {
        PacketTile packet = new PacketTile(this, 5, isAutocrafting);  //TODO
        sendPacketToGuiUsers(packet);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerSMAutoCraft(player, this, ID);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return null;
    }
}
