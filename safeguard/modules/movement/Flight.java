package intentions.modules.movement;

import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import intentions.modules.player.NoFall;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.util.PlayerUtil;
import intentions.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Flight extends Module {
	
	public NumberSetting flightSpeed = new NumberSetting("Speed", 1, 0.2, 4, 0.2);
	public static ModeSetting type = new ModeSetting("Type","Vanilla", new String[] {"Glide", "Vanilla", "Redesky", "Bounce", "ACD", "AAC3", "Verus","Flappy","Hypixel","Watcher"});
	
	

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
		
		NoFall.shouldWork = true;
	}
	
	public void onEnable() {
		flight = true;
		
		if(type.getMode().equalsIgnoreCase("Hypixel")) {
			pitch = 0;
			mc.thePlayer.motionY = 3f;
		}
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
				
				if(type.getMode().equalsIgnoreCase("Watcher")) {
					if(mc.thePlayer.onGround) mc.thePlayer.motionY += 0.3f;
					else {
						for(int i=0;i<30;i++) {
							mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
						}
						
						mc.timer.timerSpeed = (float) (flightSpeed.getValue());
					}
					
				}
				
				if(type.getMode().equalsIgnoreCase("Hypixel")) {
					pitch++;
					
					NoFall.shouldWork = false;
					
					boolean beforeGround = mc.thePlayer.onGround;
					
					if(pitch < 10) {
						
						mc.timer.timerSpeed = 10f;
						mc.thePlayer.onGround = false;
						
						if(pitch % 2 == 0) {
							mc.thePlayer.motionY = 1f;
						} else {
							mc.thePlayer.motionY = -1f;
						}
						
						
						return;
					} else {
						mc.thePlayer.onGround = true;
					}
					
					if(pitch < 30) {
						mc.timer.timerSpeed = 4.2f;
					} else if (pitch < 80) {
						mc.timer.timerSpeed = 2.9f;
					} else if (pitch < 180) {
						mc.timer.timerSpeed = 1.9f;
					} else if (pitch < 250) {
						mc.timer.timerSpeed = 1f;
						this.toggle();
					}
					if(beforeGround) {
						mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.4f, mc.thePlayer.posZ);
					}
					
					mc.thePlayer.motionY = 0;
					return;
				}
				if(type.getMode().equalsIgnoreCase("Flappy")) {
					if(mc.thePlayer.onGround)return;
					mc.thePlayer.motionY = 0;
					for(int i=0;i<4;i++)
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
					return;
				}else
				if(type.getMode().equalsIgnoreCase("AAC3"))  {
					mc.timer.timerSpeed = 1.3f;
				}
				else if (type.getMode().equalsIgnoreCase("Redesky")) {
		            this.mc.thePlayer.cameraYaw = -0.2F;
		            this.mc.thePlayer.sendQueue.addToSendQueue(new C00PacketKeepAlive(50000));
		            this.mc.thePlayer.cameraYaw = -0.2F;
		            if (this.timer.hasTimeElapsed(200L, false)) {
		               this.mc.timer.timerSpeed = 0.05F;
		               this.mc.thePlayer.motionY = 0.0D;
		               this.mc.thePlayer.capabilities.isFlying = true;
		               this.mc.thePlayer.jumpMovementFactor = 0.06F;
		               this.mc.thePlayer.speedInAir = 0.08F;
		               if (this.timer.hasTimeElapsed(800L, true)) {
		                  if (this.yaw < 2.0F) {
		                     this.mc.timer.timerSpeed = 2.0F;
		                  } else {
		                     this.mc.timer.timerSpeed = 1.0F;
		                  }

		                  ++this.yaw;
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
			} else if (type.getMode().equalsIgnoreCase("Flappy")) {
				if(mc.thePlayer.onGround)return;
				((EventMotion) e).setY(Math.floor(mc.thePlayer.posY) - PlayerUtil.getPlayerHeight());
				((EventMotion) e).setOnGround(false);
			}
		}
	}

	public void onTick() {
		if(mc.thePlayer != null && mc.theWorld != null && (Flight.type.getMode().equalsIgnoreCase("Glide"))) {
			mc.thePlayer.motionY = -0.05f;
		}
	}
	
}
