package icbm.explosions;

import icbm.EntityExplosive;
import net.minecraft.src.Entity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.Vector3;
import universalelectricity.recipe.RecipeManager;

public class ExplosiveBreaching extends Explosive
{
	public ExplosiveBreaching(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setFuse(10);
	}

	@Override
	public void doExplosion(World worldObj, Vector3 position, Entity explosionSource)
	{
		Condensed.doExplosion(worldObj, position, explosionSource);

		Vector3 difference = new Vector3();
		
		if(explosionSource instanceof EntityExplosive)
		{
			difference.modifyPositionFromSide(((EntityExplosive)explosionSource).getDirection());
		}
		else
		{
			difference.modifyPositionFromSide(ForgeDirection.DOWN);
		}
		
		for(int i = 0; i < 3; i ++)
		{
			position.add(difference);
			position.add(difference);
			Condensed.doExplosion(worldObj, position, explosionSource);
		}
    }
	
	@Override
	public void addCraftingRecipe()
	{
        RecipeManager.addRecipe(this.getItemStack(2), new Object [] {"@", "@", "@", '@', Condensed.getItemStack()});
	}
}
