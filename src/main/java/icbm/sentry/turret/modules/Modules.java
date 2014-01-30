package icbm.sentry.turret.modules;

public enum Modules
{
    AA, CLASSIC, LASER, RAILGUN, VOID;
    
    
    private Modules()
    {
        
    }
    
    public static Modules getModuleForID(int ordinal)
    {
        if (Modules.AA.ordinal() == ordinal)
            return Modules.AA;
        
        if (Modules.CLASSIC.ordinal() == ordinal)
            return Modules.CLASSIC;

        if (Modules.LASER.ordinal() == ordinal)
            return Modules.LASER;

        if (Modules.RAILGUN.ordinal() == ordinal)
            return Modules.RAILGUN;

        return Modules.VOID;
    }
}
