package icbm.jiqi;

import icbm.ZhuYao;
import icbm.api.ICBM;
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
    	"Cruise Launcher",};
    
    private static final int spawnID = ZhuYao.bJiQi.blockID;
    
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
    
    @Override
    public boolean placeBlockAt(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
    	int metadata;
        
        if(itemStack.getItemDamage() < 3)
     	{
     		metadata = 0;
     	}
        else if(itemStack.getItemDamage() < 6)
     	{
     		metadata = 1;
     	}
        else if(itemStack.getItemDamage() < 9)
		{
			metadata = 2;
		}
        else
		{
			metadata = itemStack.getItemDamage() - 6;
		}
         
        if(BJiQi.canBePlacedAt(world, x, y, z, metadata, entityPlayer))
        {
            Block var9 = Block.blocksList[IBJiQi.spawnID];
            
            if(world.setBlockAndMetadataWithNotify(x, y, z, this.spawnID, metadata))
            {
                if (world.getBlockId(x, y, z) == this.spawnID)
                {
                	if(itemStack.getItemDamage() < 9)
                	{
	            		ITier tileEntity = (ITier)world.getBlockTileEntity(x, y, z);
	
	            		if(tileEntity != null)
	            		{
    	                	if(itemStack.getItemDamage() < 3)
    	                	{
    	                		tileEntity.setTier(itemStack.getItemDamage());
    	                	}
    	                	else if(itemStack.getItemDamage() < 6)
    	                	{
    	                		tileEntity.setTier(itemStack.getItemDamage()-3);
    	                	}
    	                	else if(itemStack.getItemDamage() < 9)
    	                	{
    	                		tileEntity.setTier(itemStack.getItemDamage()-6);
    	                	}
	            		}
                	}
                	
                    Block.blocksList[IBJiQi.spawnID].onBlockPlacedBy(world, x, y, z, entityPlayer);
                }

                --itemStack.stackSize;
            }
        }

        return true;
    }
}
