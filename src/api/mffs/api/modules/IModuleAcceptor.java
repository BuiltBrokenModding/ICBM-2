package mffs.api.modules;

import java.util.Set;

import net.minecraft.item.ItemStack;

public interface IModuleAcceptor
{
	public ItemStack getModule(IModule module);

	public int getModuleCount(IModule module, int... slots);

	public Set<ItemStack> getModuleStacks(int... slots);

	public Set<IModule> getModules(int... slots);

	public int getFortronCost();
}
