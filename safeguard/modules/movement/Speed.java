package intentions.modules.movement;

import org.apache.http.conn.ClientConnectionRequest;
import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.modules.Module;
import intentions.settings.BooleanSetting;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Speed extends Module {
	
	public static BooleanSetting waitOnGround = new BooleanSetting("Ground", true);
	public static NumberSetting movementSpeed = new NumberSetting("Speed", 1.4, 1.1, 1.5, 0.05);
	public static BooleanSetting jump = new BooleanSetting("Jump", true);
	public static ModeSetting bypass = new ModeSetting("Mode", "None", new String[] {"Default", "Panda","Strafe"});

	public Speed() {
		super("Speed", Keyboard.KEY_Y, Category.MOVEMENT, "Makes you go faster", true);
		this.addSettings(waitOnGround, movementSpeed, jump, bypass);
	}
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public void onEnable() {
		y = mc.thePlayer.posY;
	}
	
	double y=0;
	boolean onGround = false;
	
	public void onEvent(Event e) {
		if (e instanceof EventMotion) {
		}
	}
	
	public void onTick(){
		if (mc.thePlayer == null) {
			return;
		} else if(this.toggled) {
			if((mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) && !mc.thePlayer.isSneaking()) {
				if (bypass.getMode().equalsIgnoreCase("Strafe")) {
					if(mc.thePlayer.onGround && jump.isEnabled()) mc.thePlayer.jump();
					mc.thePlayer.motionX *= movementSpeed.getValue();
		    		mc.thePlayer.motionZ *= movementSpeed.getValue();
					mc.thePlayer.onGround = true;
		    		return;
				}
				if (mc.thePlayer.onGround && waitOnGround.isEnabled()) {
					if (jump.isEnabled()) mc.thePlayer.jump(); 
					mc.thePlayer.motionX *= (movementSpeed.getValue());
					mc.thePlayer.motionZ *= (movementSpeed.getValue());
				} else if (!waitOnGround.isEnabled()) {
					if (mc.thePlayer.onGround && jump.isEnabled()) mc.thePlayer.jump(); 
					if (!mc.thePlayer.onGround) {
			    		mc.thePlayer.motionX *= 1.08432;
			    		mc.thePlayer.motionZ *= 1.08432;
					} else {
						mc.thePlayer.motionX *= movementSpeed.getValue();
			    		mc.thePlayer.motionZ *= movementSpeed.getValue();
					}
				}
				if(bypass.getMode().equalsIgnoreCase("Panda") && mc.thePlayer.motionY > 0) {
					mc.thePlayer.motionY -= 0.1;
				}
			} 
		}
	}
	
	public void onDisable() {
		mc.timer.timerSpeed = 1f;
	}
}
