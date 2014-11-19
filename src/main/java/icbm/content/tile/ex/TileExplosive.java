package icbm.content.tile.ex;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import icbm.Reference;
import icbm.explosion.Explosive;
import icbm.explosion.ExplosiveRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.api.IRotatable;
import resonant.api.explosion.ExplosionEvent;
import resonant.api.explosion.IExplosive;
import resonant.api.explosion.IExplosiveContainer;

import resonant.content.prefab.java.TileAdvanced;
import resonant.engine.ResonantEngine;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketReceiver;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.transform.vector.VectorWorld;
import resonant.lib.utility.WrenchUtility;
import resonant.lib.utility.inventory.InventoryUtility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

public class TileExplosive extends TileAdvanced implements IExplosiveContainer, IPacketReceiver, IRotatable
{
    public boolean exploding = false;
    public int explosiveID = 0;
    public NBTTagCompound nbtData = new NBTTagCompound();

    public static final IIcon[] ICON_TOP = new IIcon[100];
    public static final IIcon[] ICON_SIDE = new IIcon[100];
    public static final IIcon[] ICON_BOTTOM = new IIcon[100];

    public TileExplosive()
    {
        super(Material.cloth);
        this.blockHardness(0);
        this.normalRender(true);
        this.itemBlock(ItemBlockExplosive.class);
    }

    @Override
    public boolean activate(EntityPlayer entityPlayer, int side, Vector3 hit)
    {
        if (entityPlayer.getCurrentEquippedItem() != null)
        {
            if (entityPlayer.getCurrentEquippedItem().getItem() == Items.flint_and_steel)
            {
                explode(0);
                return true;
            }
            else if (WrenchUtility.isUsableWrench(entityPlayer, entityPlayer.getCurrentEquippedItem(), xi(), yi(), zi()))
            {
                byte change = 3;

                // Reorient the block
                switch (getBlockMetadata())
                {
                    case 0:
                        change = 2;
                        break;
                    case 2:
                        change = 5;
                        break;
                    case 5:
                        change = 3;
                        break;
                    case 3:
                        change = 4;
                        break;
                    case 4:
                        change = 1;
                        break;
                    case 1:
                        change = 0;
                        break;
                }

                setMeta(ForgeDirection.getOrientation(change).ordinal());

                world().notifyBlockChange(xi(), yi(), zi(), this.getBlockType());
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPlaced(EntityLivingBase entityLiving, ItemStack itemStack)
    {
        super.onPlaced(entityLiving, itemStack);
        if (!world().isRemote)
        {
            //Set explosive id
            this.explosiveID = itemStack.getItemDamage();

            //Allow things to cancle the placement
            ExplosionEvent.ExplosivePreDetonationEvent evt = new ExplosionEvent.ExplosivePreDetonationEvent(world(), xi(), yi(), zi(), ExplosiveRegistry.get(explosiveID));
            MinecraftForge.EVENT_BUS.post(evt);

            //If canceled drop the item TODO return to inventory
            if (evt.isCanceled())
            {
                InventoryUtility.dropItemStack(world(), x(), y(), z(), getPickBlock(null), 20, 0f);
                world().setBlockToAir(xi(), yi(), zi());
                return;
            }

            //Set rotation for direction based explosives
            setMeta(determineOrientation(entityLiving));

            //If powered blow up
            if (world().isBlockIndirectlyGettingPowered(xi(), yi(), zi()))
            {
                explode(0);
            }

            // If anything can set it on fire blow up
            for (byte i = 0; i < 6; i++)
            {
                VectorWorld position = toVectorWorld();
                position.add(ForgeDirection.getOrientation(i));

                Block b = position.getBlock();

                if (b == Blocks.fire || b == Blocks.lava || b == Blocks.flowing_lava)
                {
                    explode(2);
                }
            }

            //Log the placement for Anti-Grief TODO add config to reduce console spam, add support for Anti-Grief mods/plugins
            if (entityLiving != null)
            {
                Reference.LOGGER.info(entityLiving.getCommandSenderName() + " placed " + ExplosiveRegistry.get(explosiveID) + " in: " + xi() + ", " + yi() + ", " + zi() + ".");
            }
        }
    }

    @Override
    public void onWorldJoin()
    {
        super.onWorldJoin();
        world().markBlockForUpdate(xi(), yi(), zi());
    }

    @Override
    public void onNeighborChanged(Block block)
    {
        super.onNeighborChanged(block);
        if (world().isBlockIndirectlyGettingPowered(xi(), yi(), zi()))
        {
            explode(0);
        }
        else if (block == Blocks.fire || block == Blocks.lava || block == Blocks.flowing_lava)
        {
            explode(2);
        }
    }

    /*
     * Called to detonate the TNT. Args: world, x, y, z, metaData, CauseOfExplosion (0, intentional,
     * 1, exploded, 2 burned)
     */
    public void explode(int causeOfExplosion)
    {
        ExplosionEvent.ExplosivePreDetonationEvent evt = new ExplosionEvent.ExplosivePreDetonationEvent(world(), xi(), yi(), zi(), ExplosiveRegistry.get(explosiveID));
        MinecraftForge.EVENT_BUS.post(evt);

        if (!evt.isCanceled())
        {
            //exploding = true;
           // EntityExplosive eZhaDan = new EntityExplosive(world(), toVector3().add(0.5), explosiveID, (byte) getBlockMetadata(), nbtData);

            //switch (causeOfExplosion)
            //{
                //case 2:
                    //eZhaDan.setFire(100);
                    //break;
            //}

            //world().spawnEntityInWorld(eZhaDan);
            world().setBlockToAir(xi(), yi(), zi());
        }
    }

    @Override
    public void onDestroyedByExplosion(Explosion explosion)
    {
        explode(1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int meta, int side)
    {
        if (side == 0)
        {
            return ICON_BOTTOM[meta];
        }
        else if (side == 1)
        {
            return ICON_TOP[meta];
        }
        return ICON_SIDE[meta];
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        /** Register every single texture for all explosives. */
        for (Explosive zhaPin : ExplosiveRegistry.getExplosives())
        {
            //ICON_TOP[zhaPin.getID()] = this.getIcon(iconRegister, zhaPin, "_top");
            //ICON_SIDE[zhaPin.getID()] = this.getIcon(iconRegister, zhaPin, "_side");
            //ICON_BOTTOM[zhaPin.getID()] = this.getIcon(iconRegister, zhaPin, "_bottom");
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IIconRegister iconRegister, Explosive zhaPin, String suffix)
    {
        String iconName = "explosive_" + zhaPin.getUnlocalizedName() + suffix;

        try
        {
            ResourceLocation resourcelocation = new ResourceLocation(Reference.DOMAIN, Reference.BLOCK_PATH + iconName + ".png");
            InputStream inputstream = Minecraft.getMinecraft().getResourceManager().getResource(resourcelocation).getInputStream();
            BufferedImage bufferedimage = ImageIO.read(inputstream);

            if (bufferedimage != null)
            {
                return iconRegister.registerIcon(Reference.PREFIX + iconName);
            }
        }
        catch (Exception e)
        {
        }

        if (suffix.equals("_bottom"))
        {
            //return iconRegister.registerIcon(Reference.PREFIX + "explosive_bottom_" + zhaPin.getTier());
        }

        //return iconRegister.registerIcon(Reference.PREFIX + "explosive_base_" + zhaPin.getTier());
        return null;
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.explosiveID = par1NBTTagCompound.getInteger("explosiveID");
        this.nbtData = par1NBTTagCompound.getCompoundTag("data");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("explosiveID", this.explosiveID);
        par1NBTTagCompound.setTag("data", this.nbtData);
    }

    @Override
    public void read(ByteBuf data, EntityPlayer player, PacketType type)
    {
        final byte ID = data.readByte();

        if (ID == 1)
        {
            explosiveID = data.readInt();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return ResonantEngine.instance.packetHandler.toMCPacket(new PacketTile(this, (byte) 1, this.explosiveID));
    }

    @Override
    public ForgeDirection getDirection()
    {
        return ForgeDirection.getOrientation(this.getBlockMetadata());
    }

    @Override
    public void setDirection(ForgeDirection facingDirection)
    {
        this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, facingDirection.ordinal(), 2);
    }

    @Override
    public IExplosive getExplosiveType()
    {
        return ExplosiveRegistry.get(this.explosiveID);
    }

    @Override
    public NBTTagCompound getTagCompound()
    {
        return this.nbtData;
    }

    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (Explosive zhaPin : ExplosiveRegistry.getExplosives())
        {
            //if (zhaPin.hasBlockForm())
            //{
            //    par3List.add(new ItemStack(par1, 1, zhaPin.getID()));
            //}
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target)
    {
        return new ItemStack(this.getBlockType(), 1, explosiveID);
    }
}
