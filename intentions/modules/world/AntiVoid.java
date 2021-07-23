package intentions.modules.world;

import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.modules.Module;
import intentions.modules.movement.Flight;
import intentions.settings.ModeSetting;
import net.minecraft.util.BlockPos;

public class AntiVoid extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "NCP", new String[] {"NCP", "AAC(WIP)"});
	
	public AntiVoid() {
		super("AntiVoid", 0, Category.WORLD, "Prevents you from falling into the void", true);
		this.addSettings(mode);
	}
	
	public void onEvent(Event e) {
		if (e instanceof EventMotion) {
			if(!mode.getMode().equalsIgnoreCase("NCP"))return;
			if(mc.thePlayer.fallDistance > 3){
			    for(int i = 0;i<30;i++){
			        if(!mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - i, mc.thePlayer.posZ)))return;
			    }
			    mc.thePlayer.capabilities.isFlying = true;
			    mc.thePlayer.onGround = true;
			    mc.thePlayer.motionY = 0;
			} else {
				if(!Flight.flight) {
					mc.thePlayer.capabilities.isFlying = false;
				}
			}
		}
	}
	
}
