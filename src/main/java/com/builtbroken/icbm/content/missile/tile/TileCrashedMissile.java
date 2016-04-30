package com.builtbroken.icbm.content.missile.tile;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.blast.IBlastTileMissile;
import com.builtbroken.icbm.api.blast.IExHandlerTileMissile;
import com.builtbroken.icbm.api.missile.ICustomMissileRender;
import com.builtbroken.icbm.api.missile.IMissileItem;
import com.builtbroken.icbm.api.missile.ITileMissile;
import com.builtbroken.icbm.client.Assets;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.api.edit.IWorldChangeAction;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileEnt;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2016.
 */
public class TileCrashedMissile extends TileEnt implements IPacketIDReceiver, ITileMissile
{
    public Missile missile;
    float yaw = 0;
    float pitch = -90;
    Pos renderTranslationOffset = new Pos();

    IBlastTileMissile blast;
    boolean doBlast = false;
    TriggerCause cause;

    //TODO add engine flames for a few seconds after landing
    //TODO generate smoke for a few mins after landing

    public TileCrashedMissile()
    {
        super("missile", Material.iron);
        this.hardness = 10f;
        this.resistance = 10f;
        this.itemBlock = ItemBlockMissile.class;
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
        this.isOpaque = false;
        this.bounds = new Cube(0.3, 0, 0.3, .7, .7, .7);
    }

    @Override
    public Tile newTile()
    {
        return new TileCrashedMissile();
    }

    /**
     * Called to place this tile into the world from a missile entity
     *
     * @param missile
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public static void placeFromMissile(EntityMissile missile, World world, int x, int y, int z)
    {
        if (world.setBlock(x, y, z, ICBM.blockCrashMissile))
        {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileCrashedMissile)
            {
                if (missile.getMissile() != null)
                {
                    ((TileCrashedMissile) tile).missile = missile.getMissile();
                }
                ((TileCrashedMissile) tile).yaw = missile.rotationYaw;
                ((TileCrashedMissile) tile).pitch = missile.rotationPitch;
                ((TileCrashedMissile) tile).cause = new TriggerCause.TriggerCauseEntity(missile);
            }
        }
        missile.setDead();
    }

    @Override
    public void update()
    {
        super.update();
        //TODO implement gravity
        //TODO check if block inside moves
        //TODO allow to be pushed a little bit
        //TODO on punched by entity push a little
        if (blast == null && ticks % 20 == 0)
        {
            if (missile != null && missile.getWarhead() != null && missile.getWarhead().explosive != null)
            {
                IExplosiveHandler handler = ExplosiveRegistry.get(missile.getWarhead().explosive);

                if (doBlast || handler instanceof IExHandlerTileMissile && ((IExHandlerTileMissile) handler).doesSpawnMissileTile(missile, null))
                {
                    IWorldChangeAction action = handler.createBlastForTrigger(world(), xi() + 0.5, yi() + 0.5, zi() + 0.5, cause, missile.getWarhead().getExplosiveSize(), missile.getWarhead().getAdditionalExplosiveData());
                    if (action != null)
                    {
                        if (action instanceof IBlastTileMissile)
                        {
                            blast = (IBlastTileMissile) action;
                        }
                        else
                        {
                            missile.getWarhead().trigger(cause, world(), xi() + 0.5, yi() + 0.5, zi() + 0.5);
                        }
                    }
                }
            }
        }

        if (blast != null)
        {
            blast.tickBlast(this, missile);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("missile"))
        {
            ItemStack stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("missile"));
            missile = MissileModuleBuilder.INSTANCE.buildMissile(stack);
        }
        yaw = nbt.getFloat("yaw");
        pitch = nbt.getFloat("pitch");
        if (nbt.hasKey("offset"))
        {
            renderTranslationOffset = new Pos(nbt.getCompoundTag("offset"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (missile != null)
        {
            ItemStack stack = missile.toStack();
            nbt.setTag("missile", stack.writeToNBT(new NBTTagCompound()));
        }
        nbt.setFloat("yaw", yaw);
        nbt.setFloat("pitch", pitch);
        if (renderTranslationOffset != null)
        {
            nbt.setTag("offset", renderTranslationOffset.toNBT());
        }
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        ItemStack stack = ByteBufUtils.readItemStack(buf);
        missile = stack.getItem() instanceof IMissileItem ? MissileModuleBuilder.INSTANCE.buildMissile(stack) : null;
        yaw = buf.readFloat();
        pitch = buf.readFloat();
        renderTranslationOffset = new Pos(buf);
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        if (missile != null)
        {
            ByteBufUtils.writeItemStack(buf, missile.toStack());
        }
        else
        {
            ByteBufUtils.writeItemStack(buf, new ItemStack(Items.stone_axe)); //random sync data, set the missile to null
        }
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        renderTranslationOffset.writeByteBuf(buf);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(pos.x() + 0.5, pos.y() + (float) (missile.getHeight() / 2.0) - (float) (missile.getHeight() / 3.0), pos.z() + 0.5);
        GL11.glTranslated(renderTranslationOffset.x(), renderTranslationOffset.y(), renderTranslationOffset.z());

        if (!(missile instanceof ICustomMissileRender) || !((ICustomMissileRender) missile).renderMissileInWorld(yaw - 90, pitch - 90, frame))
        {
            GL11.glTranslated(0.5, 2, 0.5);
            GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(pitch - 90, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(.5f, .5f, .5f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.CLASSIC_MISSILE_TEXTURE);
            if (missile == null || missile.getWarhead() != null)
            {
                Assets.CLASSIC_MISSILE_MODEL.renderOnly("WARHEAD 1", "WARHEAD 2", "WARHEAD 3", "WARHEAD 4");
            }
            Assets.CLASSIC_MISSILE_MODEL.renderAllExcept("WARHEAD 1", "WARHEAD 2", "WARHEAD 3", "WARHEAD 4");
        }
        GL11.glPopMatrix();
    }

    @Override
    public ArrayList<ItemStack> getDrops(int metadata, int fortune)
    {
        ArrayList<ItemStack> drops = new ArrayList();
        if (missile != null)
        {
            ItemStack m = missile.toStack();
            if (m != null && m.getItem() != null)
            {
                //TODO implement dropping scrap parts instead of full missile
                drops.add(m);
            }
        }
        return drops;
    }
}
