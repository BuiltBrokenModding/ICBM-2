package com.builtbroken.icbm.content.crafting.station.small.auto;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IGuidance;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.api.modules.IWarhead;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.station.small.TileSmallMissileStationBase;
import com.builtbroken.mc.api.automation.IAutomatedCrafter;
import com.builtbroken.mc.api.automation.IAutomation;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
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
public class TileSMAutoCraft extends TileSmallMissileStationBase implements IPacketIDReceiver, IGuiTile, IAutomatedCrafter
{
    public static final int WARHEAD_SLOT = 1;
    public static final int GUIDANCE_SLOT = 2;
    public static final int ENGINE_SLOT = 3;
    public static final int OUTPUT_SLOT = 4;

    public static final int[] INPUT_SLOTS = new int[]{INPUT_SLOT, WARHEAD_SLOT, GUIDANCE_SLOT, ENGINE_SLOT};
    public static final int[] OUTPUT_SLOTS = new int[]{OUTPUT_SLOT};

    //TODO add tabs, crafting, configuration, reverse crafting
    //TODO add option to limit number of inserted explosives per craft (1-64 scale, all)

    /** Is the machine setup to autocraft */
    protected boolean isAutocrafting = false;
    protected boolean requiresWarhead = true;
    protected boolean requiresEngine = true;
    protected boolean requiresGuidance = true;
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
        if (getInputStack() != null)
        {
            final IModule module = toModule(getInputStack());
            if (module instanceof IMissile)
            {
                final IMissile prev = (IMissile) module;
                final IMissile result = getCraftedMissile();
                if (result != null)
                {
                    //Bellow checks assume that the modules can not change
                    // but can only be inserted... if this changes the code will break
                    if (prev.getWarhead() == null && result.getWarhead() != null)
                    {
                        decrStackSize(WARHEAD_SLOT, 1);
                    }
                    if (prev.getGuidance() == null && result.getGuidance() != null)
                    {
                        decrStackSize(GUIDANCE_SLOT, 1);
                    }
                    if (prev.getEngine() == null && result.getEngine() != null)
                    {
                        decrStackSize(ENGINE_SLOT, 1);
                    }
                    decrStackSize(INPUT_SLOT, 1);
                    setInventorySlotContents(OUTPUT_SLOT, result.toStack());
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
        final IMissile result = getCraftedMissile();
        if (result != null)
        {
            if (isAutocrafting)
            {
                if (requiresWarhead && missile.getWarhead() == null)
                {
                    return false;
                }

                if (requiresGuidance && missile.getGuidance() == null)
                {
                    return false;
                }

                if (requiresEngine && missile.getEngine() == null)
                {
                    return false;
                }
            }
            return getOutputStack() == null || InventoryUtility.stacksMatch(getOutputStack(), result.toStack()) && InventoryUtility.roomLeftInSlot(this, INPUT_SLOT) > 0;
        }
        return false;
    }

    @Override
    public float getCraftingProgress()
    {
        return 0; //TODO implement when less lazy
    }

    @Override
    public List<ItemStack> getRequiredItems()
    {
        return new ArrayList();
    }

    @Override
    public ItemStack insertRequiredItem(final ItemStack stack, final IAutomation entity, final ForgeDirection side)
    {
        return stack; //TODO implement when less lazy
    }

    @Override
    public ItemStack insertRequiredItem(final ItemStack stack, final int slot, final IAutomation entity, final ForgeDirection side)
    {
        if (isAutocrafting && stack != null)
        {
            final ItemStack insert = stack.copy();
            insert.stackSize = 1;

            final IModule module = toModule(stack);
            if (INPUT_SLOT == slot)
            {
                if (module instanceof IMissile && getInputStack() == null)
                {
                    setInventorySlotContents(INPUT_SLOT, insert);
                    stack.stackSize--;
                    return stack.stackSize <= 0 ? null : stack;
                }
            }
            else if (WARHEAD_SLOT == slot)
            {
                if (module instanceof IWarhead && getWarheadStack() == null)
                {
                    setInventorySlotContents(INPUT_SLOT, insert);
                    stack.stackSize--;
                    return stack.stackSize <= 0 ? null : stack;
                }
            }
            else if (GUIDANCE_SLOT == slot)
            {
                if (module instanceof IGuidance && getGuidanceStack() == null)
                {
                    setInventorySlotContents(INPUT_SLOT, insert);
                    stack.stackSize--;
                    return stack.stackSize <= 0 ? null : stack;
                }
            }
            else if (ENGINE_SLOT == slot)
            {
                if (module instanceof IRocketEngine && getEngineStack() == null)
                {
                    setInventorySlotContents(INPUT_SLOT, insert);
                    stack.stackSize--;
                    return stack.stackSize <= 0 ? null : stack;
                }
            }
        }
        return stack;
    }

    private IModule toModule(ItemStack stack)
    {
        IModule module = MissileModuleBuilder.INSTANCE.build(stack);
        if (module == null && stack.getItem() instanceof IModuleItem)
        {
            module = ((IModuleItem) stack.getItem()).getModule(stack);
        }
        return module;
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
        ItemStack inputStack = getInputStack();
        ItemStack warheadStack = getWarheadStack();
        ItemStack guidanceStack = getGuidanceStack();
        ItemStack engineStack = getEngineStack();

        if (inputStack != null)
        {
            IModule module = MissileModuleBuilder.INSTANCE.build(inputStack);
            if (module == null && inputStack.getItem() instanceof IModuleItem)
            {
                module = ((IModuleItem) inputStack.getItem()).getModule(inputStack);
            }
            if (module instanceof IMissile)
            {
                final IMissile missile = (IMissile) module;
                if ((!isAutocrafting || requiresWarhead) && warheadStack != null && missile.getWarhead() == null)
                {
                    warheadStack = warheadStack.copy();
                    warheadStack.stackSize = 1;
                    module = MissileModuleBuilder.INSTANCE.build(warheadStack);
                    if (module == null && inputStack.getItem() instanceof IModuleItem)
                    {
                        module = ((IModuleItem) inputStack.getItem()).getModule(warheadStack);
                    }
                    if (module instanceof IWarhead)
                    {
                        missile.setWarhead((IWarhead) module);
                    }
                }

                if ((!isAutocrafting || requiresGuidance) && guidanceStack != null && missile.getGuidance() == null)
                {
                    guidanceStack = guidanceStack.copy();
                    guidanceStack.stackSize = 1;
                    module = MissileModuleBuilder.INSTANCE.build(guidanceStack);
                    if (module == null && inputStack.getItem() instanceof IModuleItem)
                    {
                        module = ((IModuleItem) inputStack.getItem()).getModule(guidanceStack);
                    }
                    if (module instanceof IGuidance)
                    {
                        missile.setGuidance((IGuidance) module);
                    }
                }

                if ((!isAutocrafting || requiresEngine) && engineStack != null && missile.getEngine() == null)
                {
                    engineStack = engineStack.copy();
                    engineStack.stackSize = 1;
                    module = MissileModuleBuilder.INSTANCE.build(engineStack);
                    if (module == null && inputStack.getItem() instanceof IModuleItem)
                    {
                        module = ((IModuleItem) inputStack.getItem()).getModule(engineStack);
                    }
                    if (module instanceof IRocketEngine)
                    {
                        missile.setEngine((IRocketEngine) module);
                    }
                }
                return missile;
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
        final IMissile result = getCraftedMissile();
        return result != null ? result.toStack() : null;
    }

    @Override
    public boolean canStore(ItemStack stack, ForgeDirection side)
    {
        if (stack != null)
        {
            IModule module = MissileModuleBuilder.INSTANCE.build(stack);
            if (module == null && stack.getItem() instanceof IModuleItem)
            {
                module = ((IModuleItem) stack.getItem()).getModule(stack);
            }
            return module instanceof IMissile || module instanceof IWarhead || module instanceof IGuidance || module instanceof IRocketEngine;
        }
        return false;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        if (stack != null)
        {
            IModule module = MissileModuleBuilder.INSTANCE.build(stack);
            if (module == null && stack.getItem() instanceof IModuleItem)
            {
                module = ((IModuleItem) stack.getItem()).getModule(stack);
            }
            if (INPUT_SLOT == slot)
            {
                return module instanceof IMissile;
            }
            else if (WARHEAD_SLOT == slot)
            {
                return module instanceof IWarhead;
            }
            else if (GUIDANCE_SLOT == slot)
            {
                return module instanceof IGuidance;
            }
            else if (ENGINE_SLOT == slot)
            {
                return module instanceof IRocketEngine;
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

    protected ItemStack getGuidanceStack()
    {
        return getInventory().getStackInSlot(GUIDANCE_SLOT);
    }

    protected ItemStack getEngineStack()
    {
        return getInventory().getStackInSlot(ENGINE_SLOT);
    }

    protected ItemStack getInputStack()
    {
        return getInventory().getStackInSlot(INPUT_SLOT);
    }

    protected ItemStack getOutputStack()
    {
        return getInventory().getStackInSlot(OUTPUT_SLOT);
    }


    /** Missile object, create from the input slot stack */
    public IMissile getMissile()
    {
        if (getMissileItem() != null && getMissileItem().getItem() instanceof IMissileItem && missile == null)
        {
            missile = ((IMissileItem) getMissileItem().getItem()).toMissile(getMissileItem());
        }
        return missile;
    }


    public ItemStack getMissileItem()
    {
        return getStackInSlot(INPUT_SLOT);
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
                    requiresWarhead = buf.readBoolean();
                    requiresGuidance = buf.readBoolean();
                    requiresEngine = buf.readBoolean();
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
        PacketTile packet = new PacketTile(this, 5, isAutocrafting, requiresWarhead, requiresGuidance, requiresEngine);
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
