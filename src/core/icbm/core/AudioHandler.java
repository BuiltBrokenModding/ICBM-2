package icbm.core;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class AudioHandler
{
	public static final AudioHandler INSTANCE = new AudioHandler();

	public static final String[] SOUND_FILES = { "interface1.ogg", "lasershot.ogg", "aagun.ogg", "collapse1.ogg", "collapse2.ogg", "collapse3.ogg", "collapse4.ogg", "collapse5.ogg", "collapse6.ogg", "collapse7.ogg", "collapse8.ogg", "railgun.ogg", "machinegun1.ogg", "machinegun2.ogg", "debilitation.ogg", "airstrike.ogg", "hypersonic.ogg", "missileinair.ogg", "missilelaunch1.ogg", "missilelaunch2.ogg", "missilelaunch2.ogg", "emp.ogg", "sonicwave.ogg", "machinehum.ogg", "alarm.ogg", "explosion1.ogg", "explosionfire.ogg", "gasleak.ogg", "beamcharging.ogg", "powerdown.ogg", "targetlocked.ogg", "redmatter.ogg", "railgun.ogg", "antigravity.ogg", "antimatter.ogg", "laser.ogg" };

	@ForgeSubscribe
	public void loadSoundEvents(SoundLoadEvent event)
	{
		for (int i = 0; i < SOUND_FILES.length; i++)
		{
			event.manager.addSound(ICBMCore.PREFIX + SOUND_FILES[i]);
		}
	}
}
