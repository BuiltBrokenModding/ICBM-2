package com.builtbroken.icbm.content.crafting.station.warhead;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.api.warhead.ITrigger;
import com.builtbroken.icbm.api.warhead.ITriggerAccepter;
import com.builtbroken.icbm.api.warhead.IWarheadItem;
import com.builtbroken.mc.api.automation.IAutomatedCrafter;
import com.builtbroken.mc.api.automation.IAutomation;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.logic.TileMachineNode;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles crafting and configuration of warheads
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/6/2016.
 */
@TileWrapped(className = "TileWrapperWarheadStation", wrappers = "ExternalInventory")
public class TileWarheadStation extends TileMachineNode<ExternalInventory> implements IPacketIDReceiver, IGuiTile, IAutomatedCrafter<ExternalInventory>, IRotation
{
    public static final int WARHEAD_SLOT = 0;
    public static final int EXPLOSIVE_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int TRIGGER_SLOT = 3;
    public static final int[] INPUT_SLOTS = new int[]{WARHEAD_SLOT, EXPLOSIVE_SLOT, TRIGGER_SLOT};
    public static final int[] OUTPUT_SLOTS = new int[]{OUTPUT_SLOT};

    /** Is the machine setup to autocraft */
    protected boolean isAutocrafting = false;
    /** Forced auto explosive to check for trigger before crafting */
    protected boolean requireExplosive = true;
    /** Forced auto crafter to check for trigger before crafting */
    protected boolean requireTrigger = false;
    /** Triggers crafting logic. */
    protected boolean checkForCraft = false;
    /** Number of explosives to craft */
    protected int explosiveStackSizeRequired = 1;

    private ForgeDirection rotationCache;


    public TileWarheadStation()
    {
        super("worktable.warhead", ICBM.DOMAIN);
    }

    @Override
    protected ExternalInventory createInventory()
    {
        return new ExternalInventory(this, 4);
    }

    @Override
    public void update(long ticks)
    {
        super.update(ticks);
        if (isServer() && ticks % 10 == 0)
        {
            if (checkForCraft && isAutocrafting)
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
        final ItemStack warheadStack = getWarheadStack();
        final ItemStack explosiveStack = getExplosiveStack();
        final ItemStack outputStack = getOutputStack();
        ItemStack triggerStack = getTriggerStack();

        if (warheadStack != null && warheadStack.getItem() instanceof IWarheadItem && ExplosiveRegistry.get(explosiveStack) != null && (outputStack == null || outputStack.stackSize < outputStack.getMaxStackSize()))
        {
            final IWarhead warhead = ((IWarheadItem) warheadStack.getItem()).getModule(warheadStack);
            if (warhead != null && warhead.hasSpaceForExplosives(explosiveStack))
            {
                int insert = Math.min(explosiveStackSizeRequired, Math.min(explosiveStack.stackSize, warhead.getSpaceForExplosives()));
                //Update warhead's explosive stack
                if (warhead.getExplosiveStack() == null)
                {
                    ItemStack insertStack = explosiveStack.copy();
                    insertStack.stackSize = insert;
                    warhead.setExplosiveStack(insertStack);
                }
                else
                {
                    //Increase explosive stack
                    warhead.getExplosiveStack().stackSize += insert;
                    //Trigger any events for warhead change
                    warhead.setExplosiveStack(warhead.getExplosiveStack());
                }

                //Install trigger
                if (triggerStack != null && warhead instanceof ITriggerAccepter && ((ITriggerAccepter) warhead).getTrigger() == null)
                {
                    triggerStack = ((ITriggerAccepter) warhead).setTrigger(triggerStack);
                }

                final ItemStack newWarhead = warhead.toStack();

                if (outputStack == null || InventoryUtility.stacksMatch(newWarhead, outputStack))
                {
                    //Decrease explosive stack
                    explosiveStack.stackSize -= insert;
                    if (explosiveStack.stackSize <= 0)
                    {
                        getInventory().setInventorySlotContents(EXPLOSIVE_SLOT, null);
                    }
                    else
                    {
                        //Update inventory
                        getInventory().setInventorySlotContents(EXPLOSIVE_SLOT, explosiveStack);
                    }

                    //Decrease warhead stack (INPUT)
                    warheadStack.stackSize--;
                    if (warheadStack.stackSize <= 0)
                    {
                        getInventory().setInventorySlotContents(WARHEAD_SLOT, null);
                    }
                    else
                    {
                        //Update inventory
                        getInventory().setInventorySlotContents(WARHEAD_SLOT, warheadStack);
                    }
                    //Update trigger slot inventory
                    getInventory().setInventorySlotContents(TRIGGER_SLOT, triggerStack);

                    //Increase output
                    if (getOutputStack() == null)
                    {
                        getInventory().setInventorySlotContents(OUTPUT_SLOT, newWarhead);
                    }
                    else
                    {
                        outputStack.stackSize += 1;
                        getInventory().setInventorySlotContents(OUTPUT_SLOT, outputStack);
                    }
                }
            }
        }
    }

    /**
     * Checks if it is possible to process a crafting recipe
     *
     * @return true if possible
     */
    public boolean canCraft()
    {
        if (getWarheadStack() != null)
        {
            IWarhead result = getCraftResultAsWarhead();
            if (result != null)
            {
                if (isAutocrafting)
                {
                    if (requireTrigger && result instanceof ITriggerAccepter && ((ITriggerAccepter) result).getTrigger() == null)
                    {
                        return false;
                    }
                    if (requireExplosive)
                    {
                        if (result.getExplosiveStack() == null)
                        {
                            return false;
                        }
                        else if (result.getExplosiveStack().stackSize < explosiveStackSizeRequired)
                        {
                            return false;
                        }
                    }
                }
                //Check if we have space
                return getOutputStack() == null || InventoryUtility.stacksMatch(getCraftResult(), result.toStack()) && InventoryUtility.roomLeftInSlot(getInventory(), OUTPUT_SLOT) > 0;
            }
        }
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
            if (stack.getItem() instanceof IModuleItem)
            {
                final IModule module = ((IModuleItem) stack.getItem()).getModule(stack);
                if (module instanceof IWarhead)
                {
                    if (getWarheadStack() == null)
                    {
                        ItemStack insert = stack.copy();
                        insert.stackSize = 1;
                        getInventory().setInventorySlotContents(WARHEAD_SLOT, insert);
                        stack.stackSize -= 1;
                        checkForCraft = true;
                        return stack;
                    }
                }
                else if (module instanceof ITrigger)
                {
                    if (getTriggerStack() == null)
                    {
                        ItemStack insert = stack.copy();
                        insert.stackSize = 1;
                        getInventory().setInventorySlotContents(TRIGGER_SLOT, insert);
                        stack.stackSize -= 1;
                        checkForCraft = true;
                        return stack;
                    }
                }
            }
            else if (ExplosiveRegistry.get(stack) != null)
            {
                if (getTriggerStack() == null)
                {
                    ItemStack insert = stack.copy();
                    insert.stackSize = 1;
                    getInventory().setInventorySlotContents(EXPLOSIVE_SLOT, insert);
                    stack.stackSize -= 1;
                    checkForCraft = true;
                    return stack;
                }
            }
        }
        return stack;
    }

    @Override
    public ItemStack insertRequiredItem(ItemStack stack, int slot, IAutomation entity, ForgeDirection side)
    {
        if (isAutocrafting && stack != null)
        {
            if ((slot == WARHEAD_SLOT || slot == TRIGGER_SLOT) && stack.getItem() instanceof IModuleItem)
            {
                final IModule module = ((IModuleItem) stack.getItem()).getModule(stack);
                if (slot == WARHEAD_SLOT && module instanceof IWarhead)
                {
                    if (getWarheadStack() == null)
                    {
                        ItemStack insert = stack.copy();
                        insert.stackSize = 1;
                        getInventory().setInventorySlotContents(WARHEAD_SLOT, insert);
                        stack.stackSize -= 1;
                        checkForCraft = true;
                        return stack.stackSize <= 0 ? null : stack;
                    }
                }
                else if (slot == TRIGGER_SLOT && module instanceof ITrigger)
                {
                    if (getTriggerStack() == null)
                    {
                        ItemStack insert = stack.copy();
                        insert.stackSize = 1;
                        getInventory().setInventorySlotContents(TRIGGER_SLOT, insert);
                        stack.stackSize -= 1;
                        checkForCraft = true;
                        return stack.stackSize <= 0 ? null : stack;
                    }
                }
            }
            else if (slot == EXPLOSIVE_SLOT && ExplosiveRegistry.get(stack) != null)
            {
                if (getExplosiveStack() == null)
                {
                    ItemStack insert = stack.copy();
                    insert.stackSize = 1;
                    getInventory().setInventorySlotContents(EXPLOSIVE_SLOT, insert);
                    stack.stackSize -= 1;
                    checkForCraft = true;
                    return stack.stackSize <= 0 ? null : stack;
                }
                else if (InventoryUtility.stacksMatch(stack, getExplosiveStack()))
                {
                    final ItemStack explosiveStack = getExplosiveStack();
                    //Limit number of items to minimal required
                    if (explosiveStack.stackSize >= explosiveStackSizeRequired)
                    {
                        return stack;
                    }
                    //Figure out out insert limit from space left, require items, and stack size of insert item
                    int insert = Math.min(explosiveStackSizeRequired, InventoryUtility.roomLeftInSlot(getInventory(), EXPLOSIVE_SLOT));
                    insert = Math.min(stack.stackSize, insert);
                    //Increase explosive stack
                    explosiveStack.stackSize += insert;
                    //Update inventory
                    getInventory().setInventorySlotContents(EXPLOSIVE_SLOT, explosiveStack);
                    //Decrease insert stack
                    stack.stackSize -= insert;
                    //Return
                    checkForCraft = true;
                    return stack.stackSize <= 0 ? null : stack;
                }
            }
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
        if (stack.getItem() instanceof IModuleItem)
        {
            final IModule module = ((IModuleItem) stack.getItem()).getModule(stack);
            return module instanceof IWarhead || module instanceof ITrigger;
        }
        return ExplosiveRegistry.get(stack) != null;
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
    public IWarhead getCraftResultAsWarhead()
    {
        //Grab items from inventory
        final ItemStack warheadStack = getWarheadStack();
        final ItemStack explosiveStack = getExplosiveStack();
        final ItemStack triggerStack = getTriggerStack();

        //Check that the warhead stack contains a warhead
        if (warheadStack != null && warheadStack.getItem() instanceof IWarheadItem)
        {
            //Get warhead from stack
            final IWarhead warhead = ((IWarheadItem) warheadStack.getItem()).getModule(warheadStack);
            //Ensure it's not null
            if (warhead != null)
            {
                //Insert explosive
                if ((!isAutocrafting || requireExplosive) && ExplosiveRegistry.get(explosiveStack) != null && warhead.hasSpaceForExplosives(explosiveStack))
                {
                    //Figure out how many explosives are needed
                    int insert = Math.min(explosiveStackSizeRequired, Math.min(explosiveStack.stackSize, warhead.getSpaceForExplosives()));
                    //Update warhead's explosive stack
                    if (warhead.getExplosiveStack() == null)
                    {
                        ItemStack insertStack = explosiveStack.copy();
                        insertStack.stackSize = insert;
                        warhead.setExplosiveStack(insertStack);
                    }
                    else
                    {
                        //Increase explosive stack
                        warhead.getExplosiveStack().stackSize += insert;
                        //Trigger any events for warhead change
                        warhead.setExplosiveStack(warhead.getExplosiveStack());
                    }
                }

                //Insert trigger
                if ((!isAutocrafting || requireTrigger) && triggerStack != null && warhead instanceof ITriggerAccepter && ((ITriggerAccepter) warhead).getTrigger() == null)
                {
                    ((ITriggerAccepter) warhead).setTrigger(triggerStack);
                }
                return warhead;
            }
        }
        return null;
    }

    /**
     * Gets the expected output of a crafting recipe
     *
     * @return ItemStack, or null if not possible to craft
     */
    public ItemStack getCraftResult()
    {
        final IWarhead result = getCraftResultAsWarhead();
        return result != null ? result.toStack() : null;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        if (stack != null)
        {
            if (WARHEAD_SLOT == slot)
            {
                return stack.getItem() instanceof IModuleItem && ((IModuleItem) stack.getItem()).getModule(stack) instanceof IWarhead;
            }
            else if (EXPLOSIVE_SLOT == slot)
            {
                return ExplosiveRegistry.get(stack) != null;
            }
            else if (TRIGGER_SLOT == slot)
            {
                return stack.getItem() instanceof IModuleItem && ((IModuleItem) stack.getItem()).getModule(stack) instanceof ITrigger;
            }
        }
        return false;
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        return slot == OUTPUT_SLOT;
    }

    protected ItemStack getWarheadStack()
    {
        return getInventory().getStackInSlot(WARHEAD_SLOT);
    }

    protected ItemStack getExplosiveStack()
    {
        return getInventory().getStackInSlot(EXPLOSIVE_SLOT);
    }

    protected ItemStack getOutputStack()
    {
        return getInventory().getStackInSlot(OUTPUT_SLOT);
    }

    protected ItemStack getTriggerStack()
    {
        return getInventory().getStackInSlot(TRIGGER_SLOT);
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (isServer())
            {
                //Auto craft call
                if (id == 1)
                {
                    doCrafting();
                    return true;
                }
                //Tab switch call
                else if (id == 2)
                {
                    int guiID = buf.readInt();
                    player.openGui(ICBM.INSTANCE, guiID, world().unwrap(), xi(), yi(), zi());
                    return true;
                }
                //Gui updated some settings
                else if (id == 3)
                {
                    isAutocrafting = buf.readBoolean();
                    explosiveStackSizeRequired = Math.min(Math.max(1, buf.readInt()), 64);
                    requireExplosive = buf.readBoolean();
                    requireTrigger = buf.readBoolean();
                    _doUpdateGuiUsers();
                    return true;
                }
            }
            else
            {
                //GUI packet
                if (id == 5)
                {
                    isAutocrafting = buf.readBoolean();
                    explosiveStackSizeRequired = buf.readInt();
                    requireExplosive = buf.readBoolean();
                    requireTrigger = buf.readBoolean();
                    //Reload GUI
                    final GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                    if (screen instanceof GuiWarheadStation)
                    {
                        screen.initGui();
                    }
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
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
    }

    public void sendCraftingPacket()
    {
        sendPacketToServer(getHost().getPacketForData(1));
    }

    public void sendGUIDataUpdate()
    {
        sendPacketToServer(getHost().getPacketForData(3, isAutocrafting, explosiveStackSizeRequired, requireExplosive, requireTrigger));
    }

    public void switchTab(int tab)
    {
        sendPacketToServer(getHost().getPacketForData(2, tab));
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        isAutocrafting = getBoolean(nbt, "isAutocrafting", false);
        requireExplosive = getBoolean(nbt, "requireExplosive", true);
        requireTrigger = getBoolean(nbt, "requireTrigger", false);
        checkForCraft = getBoolean(nbt, "checkForCraft", false);
        if (nbt.hasKey("explosiveStackSizeRequired"))
        {
            explosiveStackSizeRequired = nbt.getInteger("explosiveStackSizeRequired");
        }
    }

    private boolean getBoolean(NBTTagCompound nbt, String key, boolean b)
    {
        return nbt.hasKey(key) ? nbt.getBoolean(key) : b;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.save(nbt);
        nbt.setBoolean("isAutocrafting", isAutocrafting);
        nbt.setBoolean("requireExplosive", requireExplosive);
        nbt.setBoolean("requireTrigger", requireTrigger);
        nbt.setBoolean("checkForCraft", checkForCraft);
        nbt.setInteger("explosiveStackSizeRequired", explosiveStackSizeRequired);

        return nbt;
    }

    public void _doUpdateGuiUsers()
    {
        sendPacketToGuiUsers(getHost().getPacketForData(5, isAutocrafting, explosiveStackSizeRequired, requireExplosive, requireTrigger));
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerWarheadStation(player, this, ID);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiWarheadStation(player, this, ID);
    }

    @Override
    public ForgeDirection getDirection()
    {
        if (rotationCache == null)
        {
            rotationCache = ForgeDirection.getOrientation(getHost().getHostMeta()).getOpposite();
        }
        return rotationCache;
    }
}
