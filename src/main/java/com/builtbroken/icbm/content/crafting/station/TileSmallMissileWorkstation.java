package com.builtbroken.icbm.content.crafting.station;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.crafting.IModularMissileItem;
import com.builtbroken.icbm.content.Assets;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.IRotatable;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.multiblock.EnumMultiblock;
import com.builtbroken.mc.prefab.tile.multiblock.MultiBlockHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

/**
 * Workstation for the small missile
 * Created by DarkCow on 3/12/2015.
 */
public class TileSmallMissileWorkstation extends TileAbstractWorkstation implements IGuiTile, IPacketIDReceiver, IRotatable, ISimpleItemRenderer
{
    //Static values
    public static final int INPUT_SLOT = 0;
    public static final int WARHEAD_SLOT = 1;
    public static final int ENGINE_SLOT = 2;
    public static final int GUIDANCE_SLOT = 3;
    public static final int OUTPUT_SLOT = 4;

    public static final int[] INPUTS = new int[]{INPUT_SLOT, WARHEAD_SLOT, ENGINE_SLOT, GUIDANCE_SLOT};
    public static final int[] OUTPUTS = new int[]{OUTPUT_SLOT};

    public static final int MAX_PROGRESS = 200;

    /** Multi-block set for up-down row */
    public static HashMap<IPos3D, String> upDownMap;
    /** Multi-block set for east-west row */
    public static HashMap<IPos3D, String> eastWestMap;
    /** Multi-block set for north-south row */
    public static HashMap<IPos3D, String> northSouthMap;

    static
    {
        //Only 3 actual multi-block sets used 4 times each to create the 12 different rotation cases
        upDownMap = new HashMap();
        upDownMap.put(new Pos(0, 1, 0), EnumMultiblock.INVENTORY.getName() + "#RenderBlock=false");
        upDownMap.put(new Pos(0, -1, 0), EnumMultiblock.INVENTORY.getName() + "#RenderBlock=false");

        eastWestMap = new HashMap();
        eastWestMap.put(new Pos(1, 0, 0), EnumMultiblock.INVENTORY.getName() + "#RenderBlock=false");
        eastWestMap.put(new Pos(-1, 0, 0), EnumMultiblock.INVENTORY.getName() + "#RenderBlock=false");

        northSouthMap = new HashMap();
        northSouthMap.put(new Pos(0, 0, 1), EnumMultiblock.INVENTORY.getName() + "#RenderBlock=false");
        northSouthMap.put(new Pos(0, 0, -1), EnumMultiblock.INVENTORY.getName() + "#RenderBlock=false");
    }

    //Machine vars
    protected boolean automated = false;
    protected boolean assemble = true;
    protected boolean enforce_complete = false;
    protected int progress = 0;
    protected ForgeDirection rotation = ForgeDirection.NORTH;
    protected ForgeDirection side = ForgeDirection.UP;


    public TileSmallMissileWorkstation()
    {
        super("missileworkstation", Material.iron);
        this.addInventoryModule(5);
        this.itemBlock = ItemBlockMissileStation.class;
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        this.side = ForgeDirection.getOrientation(world().getBlockMetadata(xi(), yi(), zi()));
        System.out.println("First Tick");
        MultiBlockHelper.buildMultiBlock(world(), this, true);
    }

    @Override
    public void update()
    {
        super.update();
        //TODO add progress timer for manual building as well
        if (automated)
        {
            if (getMissileItem() != null && getStackInSlot(OUTPUT_SLOT) == null && getMissileItem().getItem() instanceof IModularMissileItem)
            {
                if (progress++ >= MAX_PROGRESS && assemble() == "")
                {
                    progress = 0;
                }
            }
        }
    }

    // Adds all components to the missile and sends it to the output slot
    public String assemble()
    {
        if (getStackInSlot(OUTPUT_SLOT) == null)
        {
            ItemStack missile_stack = getMissileItem();
            if (missile_stack == null)
                return "slot.input.empty";
            if (getStackInSlot(OUTPUT_SLOT) != null)
                return "slot.output.full";
            if (missile_stack.getItem() instanceof IModularMissileItem)
            {
                IModularMissileItem missile = (IModularMissileItem) missile_stack.getItem();

                //Insert engine
                if (getEngineItem() != null && missile.getEngine(missile_stack) == null)
                    if (missile.setEngine(missile_stack, getEngineItem(), true))
                    {
                        missile.setEngine(missile_stack, getEngineItem(), false);
                        setInventorySlotContents(ENGINE_SLOT, null);
                    }

                //Insert warhead
                if (getWarheadItem() != null && missile.getWarhead(missile_stack) == null)
                    if (missile.setWarhead(missile_stack, getWarheadItem(), true))
                    {
                        missile.setWarhead(missile_stack, getWarheadItem(), false);
                        setInventorySlotContents(WARHEAD_SLOT, null);
                    }

                //Insert guidance
                if (getGuidanceItem() != null && missile.getGuidance(missile_stack) == null)
                    if (missile.setGuidance(missile_stack, getGuidanceItem(), true))
                    {
                        missile.setGuidance(missile_stack, getGuidanceItem(), false);
                        setInventorySlotContents(GUIDANCE_SLOT, null);
                    }
                if (enforce_complete)
                {
                    if (missile.getEngine(missile_stack) == null || missile.getWarhead(missile_stack) == null || missile.getGuidance(missile_stack) == null)
                        return "slot.output.incomplete";
                }
                //Move missile to output slot even if not finished
                setInventorySlotContents(OUTPUT_SLOT, missile_stack);
                setInventorySlotContents(INPUT_SLOT, null);
                return "";
            }
            return "slot.input.invalid";
        }
        return "slot.output.full";
    }

    /**
     * Breaks down the missile into its parts. Called from the GUI or automation.
     *
     * @return error message
     */
    public String disassemble()
    {
        ItemStack missile_stack = getMissileItem();
        if (missile_stack != null && missile_stack.getItem() instanceof IModularMissileItem)
        {
            IModularMissileItem missile = (IModularMissileItem) missile_stack.getItem();

            //Check if output slots are empty
            if (missile.getEngine(missile_stack) != null && getEngineItem() != null)
                return "slot.engine.full";
            if (missile.getWarhead(missile_stack) != null && getWarheadItem() != null)
                return "slot.warhead.full";
            if (missile.getGuidance(missile_stack) != null && getGuidanceItem() != null)
                return "slot.guidance.full";
            if (getStackInSlot(OUTPUT_SLOT) != null)
                return "slot.output.full";

            //Remove items from missile
            if (getWarheadItem() == null)
                setInventorySlotContents(WARHEAD_SLOT, missile.getWarhead(missile_stack));
            if (getEngineItem() == null)
                setInventorySlotContents(ENGINE_SLOT, missile.getEngine(missile_stack));
            if (getGuidanceItem() == null)
                setInventorySlotContents(GUIDANCE_SLOT, missile.getGuidance(missile_stack));

            //Clear items off of the missile
            if (missile.getEngine(missile_stack) != null && !missile.setEngine(missile_stack, null, false))
                return "missile.engine.error.set";
            if (missile.getWarhead(missile_stack) != null && !missile.setWarhead(missile_stack, null, false))
                return "missile.warhead.error.set";
            if (missile.getGuidance(missile_stack) != null && !missile.setGuidance(missile_stack, null, false))
                return "missile.guidance.error.set";

            //Move missile to output slot
            setInventorySlotContents(OUTPUT_SLOT, missile_stack);
            setInventorySlotContents(INPUT_SLOT, null);
            return "";
        }
        return "slot.input.invalid";
    }

    public ItemStack getWarheadItem()
    {
        return getStackInSlot(WARHEAD_SLOT);
    }

    public ItemStack getEngineItem()
    {
        return getStackInSlot(ENGINE_SLOT);
    }

    public ItemStack getGuidanceItem()
    {
        return getStackInSlot(GUIDANCE_SLOT);
    }

    public ItemStack getMissileItem()
    {
        return getStackInSlot(INPUT_SLOT);
    }

    @Override
    public boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
            openGui(player, 0, ICBM.INSTANCE);
        return true;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerMissileWorkstation(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiMissileWorkstation(player, this);
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (id == 0)
        {
            this.automated = buf.readBoolean();
            this.enforce_complete = buf.readBoolean();
            return true;
        }
        else if (isServer())
        {
            if (id == 1)
            {
                this.automated = buf.readBoolean();
                return true;
            }
            else if (id == 2)
            {
                this.enforce_complete = buf.readBoolean();
                return true;
            }
            else if (id == 3)
            {
                this.assemble = buf.readBoolean();
                return true;
            }
            else if (id == 4)
            {
                String e = "";
                if (assemble)
                    e = assemble();
                else
                    e = disassemble();

                if (e != "")
                {
                    sendPacket(new PacketTile(this, 1, e));
                }
                return true;
            }
            else if (id == 5)
            {
                this.rotation = ForgeDirection.getOrientation(Math.min(0, Math.max(buf.readByte(), 5)));
                return true;
            }
        }
        else
        {
            if (id == 1)
            {
                String e = ByteBufUtils.readUTF8String(buf);
                if (Minecraft.getMinecraft().currentScreen instanceof GuiMissileWorkstation)
                {
                    ((GuiMissileWorkstation) Minecraft.getMinecraft().currentScreen).error_msg = e;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, 0, automated, enforce_complete);
    }

    public void setAutomated(boolean b)
    {
        this.automated = b;
        if (isClient())
            sendPacketToServer(new PacketTile(this, 1, b));
    }

    public void setEnforceComplete(boolean b)
    {
        this.enforce_complete = b;
        if (isClient())
            sendPacketToServer(new PacketTile(this, 2, b));
    }

    public void setAssemble(boolean b)
    {
        this.assemble = b;
        if (isClient())
            sendPacketToServer(new PacketTile(this, 3, b));
    }

    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock()
    {
        switch (side)
        {
            case UP:
            case DOWN:
                if (getDirection() == ForgeDirection.EAST || getDirection() == ForgeDirection.WEST)
                {
                    return eastWestMap;
                }
                else
                {
                    return northSouthMap;
                }
            case EAST:
            case WEST:
                if (getDirection() == ForgeDirection.DOWN || getDirection() == ForgeDirection.UP)
                {
                    return upDownMap;
                }
                else
                {
                    return northSouthMap;
                }
            case NORTH:
            case SOUTH:
                if (getDirection() == ForgeDirection.DOWN || getDirection() == ForgeDirection.UP)
                {
                    return upDownMap;
                }
                else
                {
                    return eastWestMap;
                }
        }
        return eastWestMap;
    }

    @Override
    public ForgeDirection getDirection()
    {
        return rotation;
    }

    @Override
    public void setDirection(ForgeDirection direction)
    {
        ForgeDirection prev = direction;
        switch (side)
        {
            case UP:
            case DOWN:
                if (direction != ForgeDirection.DOWN && direction != ForgeDirection.UP)
                {
                    rotation = direction;
                }
                break;
            case EAST:
            case WEST:
                if (direction != ForgeDirection.EAST && direction != ForgeDirection.WEST)
                {
                    rotation = direction;
                }
                break;
            case NORTH:
            case SOUTH:
                if (direction != ForgeDirection.NORTH && direction != ForgeDirection.SOUTH)
                {
                    rotation = direction;
                }
                break;
        }
        if (isServer() && prev != direction)
        {
            //Rotate 90 degrees meaning we need a structure update
            if (prev != rotation.getOpposite())
            {
                //TODO check if it can rotate without clipping blocks
            }
            sendPacket(new PacketTile(this, 5, (byte) side.ordinal()));
        }
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glScaled(.7f, .7f, .7f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        Assets.SMALL_MISSILE_STATION_MODEL.renderAll();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f, pos.zf() + 0.5f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.GREY_FAKE_TEXTURE);
        Assets.SMALL_MISSILE_STATION_MODEL.renderAll();
        GL11.glPopMatrix();

        //Render missile
        if (getMissileItem() != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f, pos.zf() + 0.5f);
            GL11.glScaled(.0015625f, .0015625f, .0015625f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.SMALL_MISSILE_TEXTURE);
            Assets.SMALL_MISSILE_MODEL.renderAll();
            GL11.glPopMatrix();
        }
    }
}
