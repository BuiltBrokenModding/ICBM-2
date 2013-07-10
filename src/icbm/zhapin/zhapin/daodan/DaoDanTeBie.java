package icbm.zhapin.zhapin.daodan;

public abstract class DaoDanTeBie extends DaoDan
{
	public DaoDanTeBie(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.hasBlock = false;
		this.hasGrenade = false;
		this.hasMinecart = false;
	}
}
