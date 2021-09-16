package intentions.modules.movement;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.modules.Module;
import intentions.settings.NumberSetting;
import intentions.util.BlockUtils;
import intentions.util.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class LongJump extends Module{
	
	public NumberSetting ZSpeed = new NumberSetting("Z Speed", 3.5f, 1f, 5f, 0.5f);
	public NumberSetting XSpeed = new NumberSetting("X Speed", 3.5f, 1f, 5f, 0.5f);
	public NumberSetting YSpeed = new NumberSetting("Y Speed", 1.2f, 1f, 3f, 0.1f);
	
	public LongJump() {
		super("LongJump", 0, Category.MOVEMENT, "Makes you jump very far", false);
		this.addSettings(XSpeed, YSpeed, ZSpeed);
	}
	public boolean lastOnGround = true;
	private float startY = 0, ticksOn = 0;
	
	public void onEnable() {
		Client.addChatMessage("Jumped long");
		mc.thePlayer.motionY = 0;
	}
	public void onDisable() {
		mc.thePlayer.capabilities.isFlying = false;
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
			ticksOn++;
			if(mc.thePlayer.onGround) {
				mc.thePlayer.jump();
				ticksOn = 0;
			}
			
			double speed = 0.65f;
			double motionXZ = 1.85f;
			double motionY = 0.055f;
			float airSpeed = 20f;
			float timer = 1.1f;
			
			
			
			
			motionY = motionY + (ticksOn / 10000) - (PlayerUtil.getPlayerHeightDouble() / 300);
			if(motionY > 0.07f) motionY = 0.07f;
			if(motionY < 0.04f) motionY = 0.04f;
			speed = speed + (ticksOn / 100);
			if(speed > 4.65f) speed = 4.65f;
			
			mc.thePlayer.speedInAir = 0.02f;
			if(PlayerUtil.getPlayerHeightDouble() > 1) {
				mc.thePlayer.motionY += motionY;
				mc.thePlayer.speedInAir = airSpeed;
				double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);
		        mc.thePlayer.motionX = speed * -Math.sin(playerYaw);
		        mc.thePlayer.motionZ = speed * Math.cos(playerYaw);
				mc.thePlayer.motionX *= motionXZ;
				mc.thePlayer.motionZ *= motionXZ;
				mc.timer.timerSpeed = timer;
			}
		}
	}
	
}
