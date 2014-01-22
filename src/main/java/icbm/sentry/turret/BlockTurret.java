package icbm.sentry.turret;

import icbm.Reference;
import icbm.api.sentry.ISentry;
import icbm.api.sentry.SentryRegistry;
import icbm.core.CreativeTabICBM;
import icbm.core.prefab.BlockICBM;
import icbm.sentry.ICBMSentry;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import universalelectricity.api.UniversalElectricity;
import calclavia.lib.multiblock.fake.IBlockActivate;
import calclavia.lib.prefab.block.BlockAdvanced;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Block turret is a class used by all turrets. Each type of turret will have a different tile
 * entity.
 * 
 * @author Calclavia */
public class BlockTurret extends BlockICBM
{

    public BlockTurret(int par1)
    {
        super(par1, "turret", UniversalElectricity.machine);
        this.setCreativeTab(CreativeTabICBM.INSTANCE);
        this.setHardness(100f);
        this.setResistance(50f);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        TileEntity ent = world.getBlockTileEntity(x, y, z);
        if (ent instanceof TileSentry)
        {
            EntityTileDamagable dEnt = ((TileSentry) ent).getDamageEntity();
            if (dEnt != null)
            {
                this.setBlockBounds(.2f, 0, .2f, .8f, .4f, .8f);
            }
            else
            {
                this.setBlockBounds(.2f, 0, .2f, .8f, .8f, .8f);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        this.blockIcon = iconRegister.registerIcon(Reference.PREFIX + "machine");
    }

    @Override
    public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof IBlockActivate)
        {
            return ((IBlockActivate) tileEntity).onActivated(entityPlayer);
        }

        return false;
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        /** Checks the TileEntity if it can activate. If not, then try to activate the turret
         * platform below it. */
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof IBlockActivate)
        {
            return ((IBlockActivate) tileEntity).onActivated(entityPlayer);
        }

        int id = world.getBlockId(x, y - 1, z);
        Block block = Block.blocksList[id];

        if (block instanceof BlockAdvanced)
        {
            return ((BlockAdvanced) block).onMachineActivated(world, x, y - 1, z, entityPlayer, side, hitX, hitY, hitZ);
        }

        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int side)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof TileSentry)
        {
            if (!this.canBlockStay(world, x, y, z))
            {
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 1);
            }
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int meta)
    {
        return new TileSentry();
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z)
    {
        return world.getBlockId(x, y - 1, z) == ICBMSentry.blockPlatform.blockID;
    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List list)
    {
        for (Entry<String, ISentry> entry : SentryRegistry.getMap().entrySet())
        {
            if (entry.getValue() != null)
            {
                ItemStack stack = new ItemStack(this);
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setString("sentryID", entry.getKey());
                list.add(stack);
            }
        }
    }
}
