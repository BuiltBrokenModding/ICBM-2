package com.builtbroken.icbm.content.crafting.station.small;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.crafting.IModularMissileItem;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.engine.RocketEngine;
import com.builtbroken.icbm.content.crafting.missile.guidance.Guidance;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.IModuleItem;
import com.builtbroken.mc.api.tile.IRotatable;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.recipe.item.RecipeTool;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.module.TileModuleInventory;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Workstation for the small missile
 * Created by DarkCow on 3/12/2015.
 */
public class TileSmallMissileWorkstation extends TileSmallMissileStationBase implements IPacketIDReceiver, IRotatable, IPostInit
{
    protected IMissile missile; //TODO change assemble and disassemble to use this object to reduce object creation

    public TileSmallMissileWorkstation()
    {
        super("missileWorkstation", Material.iron);
        this.resistance = 10f;
        this.hardness = 10f;
        this.itemBlock = ItemBlockMissileStation.class;
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
        this.isOpaque = false;
    }

    @Override
    protected IInventory createInventory()
    {
        return new TileModuleInventory(this, 1);
    }

    @Override
    public void onPostInit()
    {
        //TODO make recipe more complex, involving crafting each armature
        GameRegistry.addRecipe(new RecipeTool(new ItemStack(ICBM.blockMissileWorkstation), "RRR", "HCD", "PPP", 'R', "rodIron", 'P', "plateIron", 'H', Engine.itemSimpleCraftingTools.getHammer(), 'D', Engine.itemSimpleCraftingTools.getDrill(), 'C', "circuitBasic"));
    }

    @Override
    public Tile newTile()
    {
        return new TileSmallMissileWorkstation();
    }

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        if(slot == INPUT_SLOT)
        {
            if (item == null)
            {
                missile = null;
            }
            else if (item.getItem() instanceof IModularMissileItem && !InventoryUtility.stacksMatchExact(getStackInSlot(slot), item))
            {
                missile = ((IModularMissileItem) item.getItem()).toMissile(item);
            }
            sendDescPacket();
        }
    }

    @Override
    public void ejectCraftingItems()
    {
        //Can't eject the only item we have
    }

    @Override
    public boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            //TODO add translations
            //Find slot to place or removes items from
            if (player.getHeldItem() != null)
            {
                if (getMissileItem() == null)
                {
                    if (isItemValidForSlot(INPUT_SLOT, player.getHeldItem()))
                    {
                        if (InventoryUtility.addItemToSlot(player, this, INPUT_SLOT))
                        {
                            updateMissile();
                        }
                        //Shouldn't happen as slot is empty and the item is valid
                        else
                        {
                            player.addChatComponentMessage(new ChatComponentText("hmm, something seems wrong."));
                        }
                    }
                    else
                    {
                        player.addChatComponentMessage(new ChatComponentText("I don't think that goes into here."));
                    }
                }
                else if (getMissile() != null)
                {
                    IModule module = null;

                    if (player.getHeldItem().getItem() instanceof IModuleItem)
                    {
                        module = ((IModuleItem) player.getHeldItem().getItem()).getModule(player.getHeldItem());
                    }

                    if (module instanceof Guidance)
                    {
                        if (getMissile().getGuidance() == null)
                        {
                            getMissile().setGuidance((Guidance) module);
                            player.getHeldItem().stackSize--;
                            if (player.getHeldItem().stackSize <= 0)
                            {
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                            }
                            player.inventoryContainer.detectAndSendChanges();
                            updateMissileItem();
                        }
                        else
                        {
                            player.addChatComponentMessage(new ChatComponentText("Guidance already set, right click with empty hand to remove."));
                        }
                    }
                    else if (module instanceof RocketEngine)
                    {
                        player.addChatComponentMessage(new ChatComponentText("Try clicking the back of the missile"));
                    }
                    else if (module instanceof Warhead)
                    {
                        player.addChatComponentMessage(new ChatComponentText("Try clicking the front of the missile"));
                    }
                    else
                    {
                        player.addChatComponentMessage(new ChatComponentText("That doesn't go onto a missile"));
                    }
                }
            }
            else if (player.isSneaking())
            {
                //TODO add sound effect when adding parts
                InventoryUtility.removeItemFromSlot(player, this, INPUT_SLOT);
                updateMissile();
            }
            else if (getMissile() != null && getMissile().getGuidance() != null)
            {
                ItemStack stack = getMissile().getGuidance().toStack();
                player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
                getMissile().setGuidance(null);
                updateMissileItem();
                player.inventoryContainer.detectAndSendChanges();
            }
        }
        return true;
    }

    @Override
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, int side, IPos3D hit)
    {
        if (isServer())
        {
            //Get offset from center
            Pos pos = new Pos((TileEntity) tile).sub(xi(), yi(), zi());
            //Ensure pos is in our layout
            if (getLayoutOfMultiBlock().containsKey(pos))
            {
                //Find slot to place or removes items from
                if (getMissile() != null)
                {
                    IModule module = null;

                    if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IModuleItem)
                    {
                        module = ((IModuleItem) player.getHeldItem().getItem()).getModule(player.getHeldItem());
                    }

                    if (isWarheadSide(pos))
                    {
                        if (module == null)
                        {
                            if (player.getHeldItem() == null && getMissile().getWarhead() != null)
                            {
                                ItemStack stack = getMissile().getWarhead().toStack();
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
                                getMissile().setWarhead(null);
                                updateMissileItem();
                                player.inventoryContainer.detectAndSendChanges();
                            }
                        }
                        else if (module instanceof Warhead)
                        {
                            if (missile.getWarhead() == null)
                            {
                                getMissile().setWarhead((Warhead) module);
                                reducePlayerHeldItem(player);
                                updateMissileItem();
                            }
                            else
                            {
                                player.addChatComponentMessage(new ChatComponentText("Warhead already installed."));
                            }
                        }
                        else
                        {
                            player.addChatComponentMessage(new ChatComponentText("Only warheads can fit on the tip."));
                        }
                    }
                    else
                    {
                        //slot = ENGINE_SLOT;
                        if (module == null)
                        {
                            if (player.getHeldItem() == null && getMissile().getEngine() != null)
                            {
                                ItemStack stack = getMissile().getEngine().toStack();
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
                                getMissile().setEngine(null);
                                updateMissileItem();
                                player.inventoryContainer.detectAndSendChanges();
                            }
                        }
                        else if (module instanceof RocketEngine)
                        {
                            if (missile.getEngine() == null)
                            {
                                getMissile().setEngine((RocketEngine) module);
                                reducePlayerHeldItem(player);
                                updateMissileItem();
                            }
                            else
                            {
                                player.addChatComponentMessage(new ChatComponentText("Engine already installed."));
                            }
                        }
                        else
                        {
                            player.addChatComponentMessage(new ChatComponentText("Only engines can fit in the end."));
                        }
                    }
                }
                else if (player.getHeldItem() != null)
                {
                    return onPlayerRightClick(player, side, new Pos(hit));
                }
            }
        }
        return true;
    }

    private boolean isWarheadSide(Pos pos)
    {
        if (facing == ForgeDirection.NORTH || facing == ForgeDirection.SOUTH)
        {
            return pos.toForgeDirection() == getDirection().getOpposite();
        }
        return pos.toForgeDirection() == getDirection();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack != null)
        {
            //Missile slots
            if (stack.getItem() instanceof IModularMissileItem && slot == INPUT_SLOT)
            {
                IMissile missile = ((IModularMissileItem) stack.getItem()).toMissile(stack);
                return missile.getMissileSize() == MissileCasings.SMALL.ordinal();
            }
        }
        return false;
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
    public PacketTile getDescPacket()
    {
        if (getMissileItem() != null)
        {
            return new PacketTile(this, 1, (byte) facing.ordinal(), getMissileItem());
        }
        return new PacketTile(this, 1, (byte) facing.ordinal(), new ItemStack(Items.apple));
    }

    public void updateMissile()
    {
        if (getMissileItem() != null)
        {
            this.missile = MissileModuleBuilder.INSTANCE.buildMissile(getMissileItem());
        }
        else
        {
            this.missile = null;
        }
        sendDescPacket();
    }

    public void updateMissileItem()
    {
        if (getMissile() != null)
        {
            setInventorySlotContents(INPUT_SLOT, getMissile().toStack());
        }
    }
}
