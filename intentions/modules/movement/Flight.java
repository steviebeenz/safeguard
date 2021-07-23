package intentions.modules.movement;

import org.lwjgl.input.Keyboard;

import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Flight extends Module {
	
	public NumberSetting flightSpeed = new NumberSetting("Speed", 1, 0.2, 4, 0.2);
	public static ModeSetting type = new ModeSetting("Type","Vanilla", new String[] {"Glide", "Vanilla", "Redesky", "Bounce", "ACD", "AAC3", "Verus","Flappy"});
	
	

	public Flight() {
		super("Flight", Keyboard.KEY_G, Category.MOVEMENT, "Allows you to fly like a bird", true);
		this.addSettings(flightSpeed, type);
	}
	
	public static Minecraft mc = Minecraft.getMinecraft();
	public static boolean flight;
	private boolean Bounce;
	private Timer timer = new Timer();
	
	public void onDisable() {
		mc.timer.timerSpeed = 1f;
		mc.thePlayer.capabilities.isFlying = false;
		mc.thePlayer.capabilities.setFlySpeed(0.05f);
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionZ = 0;
		mc.thePlayer.motionY = 0;
		flight = false;
	}
	
	public void onEnable() {
		flight = true;
	}
	
	public void onUpdate() {
		if(!type.getMode().equalsIgnoreCase("Redesky")) {
			mc.thePlayer.speedInAir = 0.02F;
		}
	}
	
	float pitch = 0,yaw = 0;
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if(e.isPre() && this.toggled) {
				if(type.getMode().equalsIgnoreCase("Flappy")) {
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
					if(!Bounce)
					{
						Bounce = true;
						mc.thePlayer.motionY += 1/50;
					} else {
						Bounce = false;
						mc.thePlayer.motionY -= 1/50;
					}
				}
				if(type.getMode().equalsIgnoreCase("AAC3"))  {
					mc.timer.timerSpeed = 1.3f;
				}
				else if (type.getMode().equalsIgnoreCase("Redesky")) {
		            if (this.mc.thePlayer.onGround) {
		               this.mc.thePlayer.setSprinting(true);
		               this.mc.thePlayer.jump();
		               mc.thePlayer.motionY *= 1.1f;
		            }

		            if (this.mc.thePlayer.motionY < 0.0D) {
		               this.mc.timer.timerSpeed = 0.3F;
		               mc.thePlayer.motionY = 0;
		               this.mc.thePlayer.speedInAir = 0.2F;
		               
		               if(mc.thePlayer.ticksExisted % 8 == 0) {
		            	   this.mc.timer.timerSpeed = 25f;
		               }
		            }
				}
				else if (type.getMode().equalsIgnoreCase("Bounce") || type.getMode().equalsIgnoreCase("Flappy")) {
					if(!Bounce)
					{
						Bounce = true;
						mc.thePlayer.motionY += 1 / 20;
					} else {
						Bounce = false;
						mc.thePlayer.motionY -= 1 / 20;
					}
				} else if (type.getMode().equalsIgnoreCase("Verus")) {
					mc.thePlayer.motionY = 0;
					mc.thePlayer.onGround = true;
					return;
				}
				if(!type.getMode().equalsIgnoreCase("Redesky"))
					mc.thePlayer.capabilities.isFlying = true;
				mc.thePlayer.capabilities.setFlySpeed((float)flightSpeed.getValue() / 20f);
			}
		} else if (e instanceof EventMotion) {
			if(type.getMode().equalsIgnoreCase("ACD")) {
				mc.thePlayer.motionX *= 0.9F;
				mc.thePlayer.motionZ *= 0.9F;
				((EventMotion) e).setY(((EventMotion) e).getY() + 0.3);
				mc.thePlayer.motionY -= 0.008f;
			}
		}
	}

	public void onTick() {
		if(mc.thePlayer != null && mc.theWorld != null && (Flight.type.getMode().equalsIgnoreCase("Glide"))) {
			mc.thePlayer.motionY = -0.05f;
		}
	}
	
}
