package icbm.jiqi;

import icbm.ICBM;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import universalelectricity.implement.ITier;

public class IBJiQi extends ItemBlock
{
    public static final String[] names = new String[] {
    	"Short-Range Platform",
    	"Medium-Range Platform",
    	"Intercontinental Platform",
    	"Short-Range Computer",
    	"Medium-Range Computer",
    	"Intercontinental Computer",
    	"Short-Range Frane",
    	"Medium-Range Frane",
    	"Intercontinental Frane",
    	"Radar Station",
    	"EMP Tower",
    	"Railgun",
    	"Cruise Launcher",
    	"Laser Turret"};
    
    private static final int spawnID = ICBM.blockJiQi.blockID;
    
    public IBJiQi(int id)
    {
        super(id);
        this.setIconIndex(48);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
	public int getMetadata(int damage)
    {
        return damage;
    }
    
    /**
     * Gets an icon index based on an item's damage value
     */
    @Override
    public int getIconFromDamage(int par1)
    {
        return this.iconIndex+par1;
    }

    @Override
	public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder())
                .append(super.getItemName())
                .append(".")
                .append(names[itemstack.getItemDamage()])
                .toString();
    }
    
    @Override
   	public String getTextureFile()
	{
       return ICBM.ITEM_TEXTURE_FILE;
	}
        
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS !
     */
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        int var8 = par3World.getBlockId(x, y, z);

        if (var8 == Block.snow.blockID)
        {
            par7 = 1;
        }
        else if (var8 != Block.vine.blockID && var8 != Block.tallGrass.blockID && var8 != Block.deadBush.blockID)
        {
            if (par7 == 0)
            {
                --y;
            }

            if (par7 == 1)
            {
                ++y;
            }

            if (par7 == 2)
            {
                --z;
            }

            if (par7 == 3)
            {
                ++z;
            }

            if (par7 == 4)
            {
                --x;
            }

            if (par7 == 5)
            {
                ++x;
            }
        }

        if (!par2EntityPlayer.canPlayerEdit(x, y, z))
        {
            return false;
        }
        else if (par1ItemStack.stackSize == 0)
        {
            return false;
        }
        else
        {
        	int metadata;
             
            if(par1ItemStack.getItemDamage() < 3)
         	{
         		metadata = 0;
         	}
            else if(par1ItemStack.getItemDamage() < 6)
         	{
         		metadata = 1;
         	}
            else if(par1ItemStack.getItemDamage() < 9)
			{
				metadata = 2;
			}
            else
			{
				metadata = par1ItemStack.getItemDamage() - 6;
			}
             
            if(BJiQi.canBePlacedAt(par3World, x, y, z, metadata))
            {
                Block var9 = Block.blocksList[IBJiQi.spawnID];
                
                if(par3World.setBlockAndMetadataWithNotify(x, y, z, this.spawnID, metadata))
                {
                    if (par3World.getBlockId(x, y, z) == this.spawnID)
                    {
                    	if(par1ItemStack.getItemDamage() < 9)
                    	{
    	            		ITier tileEntity = (ITier)par3World.getBlockTileEntity(x, y, z);
    	
    	            		if(tileEntity != null)
    	            		{
	    	                	if(par1ItemStack.getItemDamage() < 3)
	    	                	{
	    	                		tileEntity.setTier(par1ItemStack.getItemDamage());
	    	                	}
	    	                	else if(par1ItemStack.getItemDamage() < 6)
	    	                	{
	    	                		tileEntity.setTier(par1ItemStack.getItemDamage()-3);
	    	                	}
	    	                	else if(par1ItemStack.getItemDamage() < 9)
	    	                	{
	    	                		tileEntity.setTier(par1ItemStack.getItemDamage()-6);
	    	                	}
    	            		}
                    	}
                    	
                        Block.blocksList[IBJiQi.spawnID].onBlockPlacedBy(par3World, x, y, z, par2EntityPlayer);
                    }

                    --par1ItemStack.stackSize;
                }
            }

            return true;
        }
    }
}
