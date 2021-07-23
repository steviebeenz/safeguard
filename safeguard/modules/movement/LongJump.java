package intentions.modules.movement;

import intentions.Client;
import intentions.modules.Module;
import intentions.settings.NumberSetting;

public class LongJump extends Module{
	
	public NumberSetting ZSpeed = new NumberSetting("Z Speed", 3.5f, 1f, 5f, 0.5f);
	public NumberSetting XSpeed = new NumberSetting("X Speed", 3.5f, 1f, 5f, 0.5f);
	public NumberSetting YSpeed = new NumberSetting("Y Speed", 1.2f, 1f, 3f, 0.1f);
	
	public LongJump() {
		super("LongJump", 0, Category.MOVEMENT, "Makes you jump very far", false);
		this.addSettings(XSpeed, YSpeed, ZSpeed);
	}
	
	public void onEnable() {
		Client.addChatMessage("Jumped long");
		
		if(!mc.thePlayer.onGround) {
			this.toggle();
			return;
		}
		mc.timer.timerSpeed = 1.3f;
		mc.thePlayer.jump();
		mc.thePlayer.motionX *= XSpeed.getValue();
		mc.thePlayer.motionZ *= ZSpeed.getValue();
		mc.thePlayer.motionY *= YSpeed.getValue();
		mc.timer.timerSpeed = 1.0f;
		
		this.toggle();
	}
	
	public void onDisable() {
	}
	
}
