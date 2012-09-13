package icbm;
 
import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.Vector3;
 
/**
 * @author CovertJaguar, Modified by Calclavia
 */

public class GenLiu extends WorldGenerator
{
    private static int blockID;
    private static int branchAmount;
    
    public GenLiu(ItemStack itemStack, int amount)
    {
        super();
        this.blockID = itemStack.itemID;
        this.branchAmount = amount;
    }
 
    @Override
    public boolean generate(World world, Random rand, int x, int y, int z)
    {
        if(nearLava(world, x, y, z))
        {
            placeOre(world, rand, x, y, z);
            return true;
        }
        return false;
    }
 
    private void placeOre(World world, Random rand, int x, int y, int z)
    {
        Vector3 position = new Vector3(x, y, z);

        for(int amount = 0; amount < branchAmount; amount++)
        {
            Block block = Block.blocksList[world.getBlockId(x, y, z)];
            
            if(block != null && block.isGenMineableReplaceable(world, x, y, z))
            {
                world.setBlock(x, y, z, blockID);
            }

            ForgeDirection dir = ForgeDirection.values()[rand.nextInt(6)];
            
            position.modifyPositionFromSide(dir);
        }
    }
 
    private boolean nearLava(World world, int x, int y, int z)
    {
        for(int side = 2; side < 6; side++) 
        {
			Vector3 position = new Vector3(x, y, z);
			 
			ForgeDirection s = ForgeDirection.values()[side];
			 
			position.modifyPositionFromSide(s);
 
            if(world.blockExists(position.intX(), position.intY(), position.intZ()))
            {
                int id = world.getBlockId(position.intX(), position.intY(), position.intZ());
                
                if(id == Block.lavaStill.blockID || id == Block.lavaMoving.blockID)
                {
                    return true;
                }
            }
        }
        
        for(int j = 0; j < 4; j++)
        {
            int id = world.getBlockId(x, y - j, z);
 
            if(id == Block.lavaStill.blockID || id == Block.lavaMoving.blockID)
            {
                return true;
            }
            else if(id != 0)
            {
                return false;
            }
        }
        
        return false;
    }
}