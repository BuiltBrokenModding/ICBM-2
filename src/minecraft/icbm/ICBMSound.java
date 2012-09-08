package icbm;

import java.net.URL;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class ICBMSound
{
	public static final ICBMSound INSTANCE = new ICBMSound();
	
	public static final String[] SOUND_FILES = {
		"airstrike.ogg",
		"missilelaunch.ogg",
		"emp.ogg",
		"sonicwave.ogg",
		"machinehum.ogg",
		"alarm.ogg",
		"explosion1.ogg",
		"explosionfire.ogg",
		"gasleak.ogg",
		"beamcharging.ogg",
		"powerdown.ogg",
		"targetlocked.ogg",
		"redmatter.ogg",
		"railgun.ogg",
		"antigravity.ogg"};

	@ForgeSubscribe
	public void loadSoundEvents(SoundLoadEvent event)
	{
		for(int i = 0; i < SOUND_FILES.length; i++)
		{
			URL url = this.getClass().getResource("/icbm/"+SOUND_FILES[i]);
			
			event.manager.soundPoolSounds.addSound("icbm/"+SOUND_FILES[i], url);
			
			if(url == null)
		    {
		    	System.out.println("Invalid sound file: " + SOUND_FILES[i]+".ogg");
		    }
		}
	}
}
