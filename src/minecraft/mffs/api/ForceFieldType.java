package mffs.api;

public enum ForceFieldType
{
	Default(1), Camouflage(2), Zapper(3), Area(1), Containment(1);

	int cost;

	private ForceFieldType(int cost)
	{
		this.cost = cost;
	}
}
