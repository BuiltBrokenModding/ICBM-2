package mffs.api.fortron;

public interface IFortronStorage
{
	/**
	 * Sets the amount of fortron energy.
	 * 
	 * @param joules
	 */
	public void setFortronEnergy(int joules);

	/**
	 * @return Gets the amount of fortron stored.
	 */
	public int getFortronEnergy();

	/**
	 * 
	 * @return Gets the maximum possible amount of fortron that can be stored.
	 */
	public int getFortronCapacity();

	/**
	 * Called to use and consume fortron energy from this storage unit.
	 * 
	 * @param joules - Amount of fortron energy to use.
	 * @param doUse - True if actually using, false if just simulating.
	 * @return joules - The amount of energy that was actually provided.
	 */
	public int requestFortron(int joules, boolean doUse);

	/**
	 * Called to use and give fortron energy from this storage unit.
	 * 
	 * @param joules - Amount of fortron energy to give.
	 * @param doUse - True if actually using, false if just simulating.
	 * 
	 * @return joules - The amount of energy that was actually injected.
	 */
	public int provideFortron(int joules, boolean doUse);
}
