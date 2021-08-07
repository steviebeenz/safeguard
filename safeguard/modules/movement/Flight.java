package intentions.modules.movement;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.events.listeners.EventPacket;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import intentions.modules.player.NoFall;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.util.PlayerUtil;
import intentions.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

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
	private double[] loc;
	
	public void onDisable() {
		mc.timer.timerSpeed = 1f;
		mc.thePlayer.capabilities.isFlying = false;
		mc.thePlayer.capabilities.setFlySpeed(0.05f);
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionZ = 0;
		mc.thePlayer.motionY = 0;
		flight = false;
		
		NoFall.shouldWork = true;
		pitch = 0f;
		yaw = 0f;
		
		if(type.getMode().equalsIgnoreCase("Watcher")) {
			
	        for (Packet packet : packets) {
	            mc.thePlayer.sendQueue.addToSendQueue(packet);
	        }
	        packets.clear();
		    
		}
	}
	
	private final ArrayList<Packet> packets = new ArrayList<>();
	
	public void onEnable() {
		flight = true;
		
		if(type.getMode().equalsIgnoreCase("Hypixel")) {
			pitch = 0;
			mc.thePlayer.motionY = 3f;
		} else if (type.getMode().equalsIgnoreCase("Watcher")) {
			yaw = (float) (mc.thePlayer.motionX);
			pitch = (float) (mc.thePlayer.motionZ);
		} else if (type.getMode().equalsIgnoreCase("ACD")) {
			mc.thePlayer.motionY = 0.75D;
		}
	}
	
	public void onUpdate() {
		if(!type.getMode().equalsIgnoreCase("Redesky")) {
			mc.thePlayer.speedInAir = 0.02F;
		}
		if(mc.thePlayer.onGround) {
			loc = new double[]{mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};
		}
	}
	
	public void onSendPacket(EventPacket e) {
		if(mc.theWorld == null || mc.thePlayer == null || mc.thePlayer.onGround)return;
		if(type.getMode().equalsIgnoreCase("ACD")) {
			e.setCancelled(mc.thePlayer.ticksExisted % 2 == 0);
		}
		else if(type.getMode().equalsIgnoreCase("Watcher")) {
			e.setCancelled(true);
			if(e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
				packets.add(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ, mc.thePlayer.onGround));
				return;
			}
			packets.add(e.getPacket());
		}
	}
	
	
	float pitch = 0,yaw = 0;
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if(e.isPre() && this.toggled) {
				
				if(type.getMode().equalsIgnoreCase("ACD")) {
					if(mc.thePlayer.onGround)return;
					
					mc.thePlayer.motionY = -0.051;
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.051, mc.thePlayer.posZ, mc.thePlayer.onGround));
					return;
				}
				
				if(type.getMode().equalsIgnoreCase("Watcher"))return;
				
				if(type.getMode().equalsIgnoreCase("Hypixel")) {
					pitch++;
					
					NoFall.shouldWork = false;
					
					boolean beforeGround = mc.thePlayer.onGround;
					
					if(pitch < 10 && pitch > 3) {
						
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
						mc.thePlayer.setPosition(mc.thePlayer.posX, (Math.floor(mc.thePlayer.posY) - yaw) + 1.3f, mc.thePlayer.posZ);
					}
					
					if(pitch < 30) {
						mc.timer.timerSpeed = 2.2f;
					} else if (pitch < 80) {
						mc.timer.timerSpeed = 1.9f;
					} else if (pitch < 180) {
						mc.timer.timerSpeed = 0.9f;
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
				mc.thePlayer.motionX *= 0.7F;
				mc.thePlayer.motionZ *= 0.7F;
				
				e.setCancelled(mc.thePlayer.ticksExisted % 2 == 0);
				
			} else if (type.getMode().equalsIgnoreCase("Flappy")) {
				if(mc.thePlayer.onGround)return;
				((EventMotion) e).setY(Math.floor(mc.thePlayer.posY) - PlayerUtil.getPlayerHeight()+1);
				((EventMotion) e).setOnGround(true);
			} else if (type.getMode().equalsIgnoreCase("Watcher")) {
				e.setCancelled(true);
				mc.thePlayer.motionY = 0;
				if(mc.thePlayer.motionX < yaw && mc.thePlayer.motionX > -yaw && mc.thePlayer.motionX != 0)
					mc.thePlayer.motionX *= 1.1;
				if(mc.thePlayer.motionZ < pitch && mc.thePlayer.motionZ > -pitch && mc.thePlayer.motionZ != 0)
					mc.thePlayer.motionZ *= 1.1;
			}
		}
	}

	public void onTick() {
		if(mc.thePlayer != null && mc.theWorld != null && (Flight.type.getMode().equalsIgnoreCase("Glide"))) {
			mc.thePlayer.motionY = -0.05f;
		}
	}
	
}
