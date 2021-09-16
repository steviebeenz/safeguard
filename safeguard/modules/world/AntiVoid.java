package intentions.modules.world;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.modules.Module;
import intentions.modules.movement.Flight;
import intentions.settings.ModeSetting;
import intentions.util.PlayerUtil;
import intentions.util.TeleportUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class AntiVoid extends Module {

	public static ModeSetting mode = new ModeSetting("Mode", "NCP", new String[] {"NCP", "AAC(WIP)"});
	
	public AntiVoid() {
		super("AntiVoid", 0, Category.WORLD, "Prevents you from falling into the void", true);
		this.addSettings(mode);
	}
	
	Vec3 lastOnGround = null;
	
	public void onEvent(Event e) {
		if (e instanceof EventMotion) {
			if(!mode.getMode().equalsIgnoreCase("NCP"))return;
			if(!mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ))) {
				lastOnGround = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
				return;
			}
			if(mc.thePlayer.fallDistance > 3 && mc.thePlayer.motionY < 0 && PlayerUtil.getPlayerHeight() > 1 && lastOnGround != null){
			    for(int i = 0;i<30;i++){
			        if(!mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - i, mc.thePlayer.posZ)))return;
			    }
			    mc.thePlayer.setPosition(lastOnGround.xCoord, lastOnGround.yCoord, lastOnGround.zCoord);
			    mc.thePlayer.fallDistance = 0f;
			}
		}
	}
	
}
