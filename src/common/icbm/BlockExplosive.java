package icbm;

import icbm.explosions.Explosive;

import java.util.List;
import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityTNTPrimed;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.Vector3;
import universalelectricity.extend.IRotatable;
import buildcraft.api.tools.IToolWrench;

public class BlockExplosive extends BlockContainer
{
    public BlockExplosive(int par1, int par2)
    {
        super(par1, par2, Material.tnt);
        this.setHardness(0.0F);
        this.setBlockName("Explosives");
        this.setStepSound(soundGrassFootstep);
        this.setRequiresSelfNotify();
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    
    /**
     * gets the way this piston should face for that entity that placed it.
     */
    private static byte determineOrientation(World world, int x, int y, int z, EntityLiving entityLiving)
    {
        if (MathHelper.abs((float)entityLiving.posX - (float)x) < 2.0F && MathHelper.abs((float)entityLiving.posZ - (float)z) < 2.0F)
        {
            double var5 = entityLiving.posY + 1.82D - (double)entityLiving.yOffset;

            if (var5 - (double)y > 2.0D)
            {
                return 1;
            }

            if ((double)y - var5 > 0.0D)
            {
                return 0;
            }
        }

        int rotation = MathHelper.floor_double((double)(entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return (byte) (rotation == 0 ? 2 : (rotation == 1 ? 5 : (rotation == 2 ? 3 : (rotation == 3 ? 4 : 0))));
    }
    
    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLiving par5EntityLiving)
    {
        ((IRotatable)par1World.getBlockTileEntity(x, y, z)).setDirection(Vector3.getOrientationFromSide(ForgeDirection.getOrientation(determineOrientation(par1World, x, y, z, par5EntityLiving)), ForgeDirection.NORTH));
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    @Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata)
    {
    	//Get the tier of the explosive and find the row of it
    	int displacement = -1;
    	if(Explosive.list[metadata].getTier() == 4) displacement = 0;
    	
    	int rowPrefix = 16 + 16*(Explosive.list[metadata].getTier() + displacement);

    	int columnPrefix = metadata;
    	
    	switch(Explosive.list[metadata].getTier())
    	{
	    	case 2: columnPrefix -= Explosive.MAX_TIER_ONE; break;
	    	case 3: columnPrefix -= Explosive.MAX_TIER_TWO; break;
	    	case 4: columnPrefix -= 14; break;
    	}
    	
    	columnPrefix *= 3;
    	
    	return side == 0 ? rowPrefix + columnPrefix: (side == 1 ? rowPrefix + columnPrefix + 1 : rowPrefix + columnPrefix + 2);
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    @Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);
        int metadata = par1World.getBlockMetadata(par2, par3, par4);

        if (par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
        {
            BlockExplosive.detonateTNT(par1World, par2, par3, par4, metadata, 0);
        }

        Vector3 position = new Vector3(par2, par3, par4);

        //Check to see if there is fire nearby. If so, then detonate.
        for (Vector3 side : Vector3.side)
        {
            Vector3 currentSide = Vector3.add(position, side);

            int blockId = par1World.getBlockId((int)currentSide.x, (int)currentSide.y, (int)currentSide.z);
            
            if (blockId == Block.fire.blockID  || blockId == Block.lavaMoving.blockID || blockId == Block.lavaStill.blockID)
            {
                BlockExplosive.detonateTNT(par1World, par2, par3, par4, metadata, 2);
            }
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    @Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int blockId)
    {
        int metadata = par1World.getBlockMetadata(par2, par3, par4);

        if ((blockId > 0 && Block.blocksList[blockId].canProvidePower() && par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)))
        {
            BlockExplosive.detonateTNT(par1World, par2, par3, par4, metadata, 0);
        }
        else if (blockId == Block.fire.blockID || blockId == Block.lavaMoving.blockID || blockId == Block.lavaStill.blockID)
        {
            BlockExplosive.detonateTNT(par1World, par2, par3, par4, metadata, 2);
        }
    }

    /*
     * Called to detonate the TNT.  Args: world, x, y, z, metaData, CauseOfExplosion (0, intentional, 1, exploded, 2 burned)
     */
    public static void detonateTNT(World par1World, int x, int y, int z, int metadata, int causeOfExplosion)
    {
    	if(!par1World.isRemote)
    	{
    		if(par1World.getBlockTileEntity(x, y, z) != null)
    		{
    			Explosive.list[metadata].spawnExplosive(par1World, new Vector3(x, y, z), ((IRotatable)par1World.getBlockTileEntity(x, y, z)).getDirection(), (byte)causeOfExplosion);
    		}
    		else
    		{
    			Explosive.list[metadata].spawnExplosive(par1World, new Vector3(x, y, z), ForgeDirection.DOWN, (byte)causeOfExplosion);
    		}
    	}
    	
    	par1World.setBlockWithNotify(x, y, z, 0);
    }

    /**
     * Called upon the block being destroyed by an explosion
     */
    @Override
	public void onBlockDestroyedByExplosion(World par1World, int x, int y, int z)
    {
    	int metadata = par1World.getBlockMetadata(x, y, z);
        BlockExplosive.detonateTNT(par1World, x, y, z, metadata, 1);
    }

    /**
     * Called upon block activation (left or right click on the block.). The three integers represent x,y,z of the
     * block.
     */
    @Override
	public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        if (par5EntityPlayer.getCurrentEquippedItem() != null)
        {
        	if(par5EntityPlayer.getCurrentEquippedItem().itemID == Item.flintAndSteel.shiftedIndex)
        	{
        		int metadata = par1World.getBlockMetadata(x, y, z);
                BlockExplosive.detonateTNT(par1World, x, y, z, metadata, 0);
                return true;
        	}
        	else if(par5EntityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench)
        	{
        		byte change = 3;
        		
        		//Reorient the block
                switch(((IRotatable)par1World.getBlockTileEntity(x, y, z)).getDirection().ordinal())
                {
                	case 0: change = 2; break;
                    case 2: change = 5; break;
                    case 5: change = 3; break;
                    case 3: change = 4; break;
                    case 4: change = 1; break;
                    case 1: change = 0; break;
                }
                
                ((IRotatable)par1World.getBlockTileEntity(x, y, z)).setDirection(ForgeDirection.getOrientation(change));

                par1World.notifyBlockChange(x, y, z, this.blockID);
                return true;
        	}
        }

        return false;
    }
	
	@Override
	public String getTextureFile()
    {
        return ICBM.BLOCK_TEXTURE_FILE;
    }

    @Override
	protected int damageDropped(int metadata)
    {
        return metadata;
    }
    
    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    { 
    	for(int i = 0; i < Explosive.MAX_EXPLOSIVE_ID; i++)
        {
    		par3List.add(new ItemStack(this, 1, i));
        }
    }

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
    	return new TileEntityExplosive();
	}
}
