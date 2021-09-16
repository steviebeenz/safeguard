package intentions.modules.movement;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.events.listeners.EventPacket;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import intentions.modules.player.NoFall;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.util.PacketUtils;
import intentions.util.PlayerUtil;
import intentions.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class Flight extends Module {
	
	public static NumberSetting flightSpeed = new NumberSetting("Speed", 1, 0.2, 10, 0.1);
	public static ModeSetting type = new ModeSetting("Type","Vanilla", new String[] {"Glide", "Redesky", "Vanilla", "Bounce", "ACD", "AGC", "AAC3", "Verus","Flappy","Hypixel","Watcher"});

	public Flight() {
		super("Flight", Keyboard.KEY_G, Category.MOVEMENT, "Allows you to fly like a bird", true);
		this.addSettings(flightSpeed, type);
	}
	
	public static Minecraft mc = Minecraft.getMinecraft();
	public static boolean flight;
	private boolean Bounce, above = false;
	private Timer timer = new Timer();
	private double[] loc;
	
	public void onDisable() {
		if(type.getMode().equalsIgnoreCase("Verus")) {
            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY+1,mc.thePlayer.posZ, true));
            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ, true));
		} else if (type.getMode().equalsIgnoreCase("Watcher")) {
			mc.thePlayer.motionY = -(cacFly / 100);
		}
		mc.timer.timerSpeed = 1f;
		mc.thePlayer.capabilities.isFlying = false;
		mc.thePlayer.capabilities.setFlySpeed(0.05f);
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionY = 0;
		mc.thePlayer.motionZ = 0;
		flight = false;
		
		NoFall.shouldWork = true;
		pitch = 0f;
		yaw = 0f;
		cacFly = 0f;
		mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
		
	}
	
	
	private final ArrayList<Packet> packets = new ArrayList<>();
	
	
	public void onEnable() {
		flight = true;
		
		if(type.getMode().equalsIgnoreCase("Verus")) {
			mc.thePlayer.motionY = 0.01f;
		} else if(type.getMode().equalsIgnoreCase("Hypixel")) {
		} else if (type.getMode().equalsIgnoreCase("Watcher")) {
			loc = new double[] {mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};
			mc.thePlayer.motionY = 0.03f;
			//TeleportUtils.pathFinderTeleportTo(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new Vec3(mc.thePlayer.posX, 256, mc.thePlayer.posZ));
		} else if (type.getMode().equalsIgnoreCase("ACD")) {
			yaw = (float)mc.thePlayer.posY;
		} else if (type.getMode().equalsIgnoreCase("Flappy")) {
			mc.thePlayer.motionY = 0.2f;
		}
	}
	
	EventPacket lastC04 = null;
	boolean aac5nextFlag;
	ArrayList<C03PacketPlayer> aac5C03List = new ArrayList<C03PacketPlayer>();
	
	public void onSendPacket(EventPacket e) {
		if(mc.theWorld == null || mc.thePlayer == null || mc.thePlayer.onGround || !this.toggled)return;
		if(e.getPacket() instanceof C04PacketPlayerPosition) {
			lastC04 = e;
		}
		
		if(type.getMode().equalsIgnoreCase("Watcher")) {
			if(e.getPacket() instanceof C04PacketPlayerPosition) {
				e.setCancelled(true);
				C04PacketPlayerPosition p = (C04PacketPlayerPosition)e.getPacket();
				PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(p.x, p.y, p.z, p.yaw, p.pitch, mc.thePlayer.onGround));
			}
		} else if (type.getMode().equalsIgnoreCase("Redesky")) {
			if(e.getPacket() instanceof S08PacketPlayerPosLook) {
            
				C04PacketPlayerPosition l = (C04PacketPlayerPosition) lastC04.getPacket();
			} else if (e.getPacket() instanceof C03PacketPlayer) {
				double f=mc.thePlayer.width/2.0;
				final C03PacketPlayer packetPlayer = (C03PacketPlayer)e.getPacket();
                // need to no collide else will flag
                if(aac5nextFlag || !mc.theWorld.checkBlockCollision(new AxisAlignedBB(packetPlayer.getPositionX() - f, packetPlayer.getPositionY(), packetPlayer.getPositionZ() - f, packetPlayer.getPositionX() + f, packetPlayer.getPositionY() + mc.thePlayer.height, packetPlayer.getPositionZ() + f))){
                    aac5C03List.add(packetPlayer);
                    aac5nextFlag=false;
                    e.setCancelled(true);
                    if(!timer.hasTimeElapsed(1000, true) && aac5C03List.size() > 7) {
                        sendAAC5Packets();
                    }
                }
			}
		} else if (type.getMode().equalsIgnoreCase("Verus")) {
			e.setCancelled(mc.thePlayer.ticksExisted % 2 == 0);
		}
	}
	
	private void sendAAC5Packets(){
        float yaw=mc.thePlayer.rotationYaw;
        float pitch=mc.thePlayer.rotationPitch;
        for(C03PacketPlayer packet : aac5C03List){
            PacketUtils.sendPacketNoEvent(packet);
            if(packet.getRotating()){
                yaw=packet.getYaw();
                pitch=packet.getPitch();
            }
            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x,256,packet.z, true));
            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x,packet.y,packet.z, true));
        }
        
        aac5C03List.clear();
    }
	
	
	float pitch = 0,yaw = 0, cacFly = 0;
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if(e.isPre() && this.toggled) {
				
				if(type.getMode().equalsIgnoreCase("ACD")) {
                   
						if(loc != null && mc.thePlayer.posX == loc[0] && mc.thePlayer.posY == loc[1] && mc.thePlayer.posZ == loc[2]) {
							mc.thePlayer.motionY *= 0.05f;
							return;
						}
					
						if(mc.thePlayer.onGround) {
							cacFly = 0;
							yaw++;
							if(yaw < 5)return;
						}
						yaw = 0;
						cacFly++;
					    if(cacFly > 7) {;
					       mc.thePlayer.capabilities.isFlying = false;
					       mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
					       return;
					    }
					    mc.gameSettings.keyBindJump.pressed = true;
						mc.timer.timerSpeed = 1.1555324f;
						mc.thePlayer.motionY *= 1.93f;
						mc.thePlayer.motionY -= 0.08f;
						mc.thePlayer.motionY *= 0.98f;
						mc.thePlayer.motionX *= 1.05f;
						mc.thePlayer.motionZ *= 1.05f;
						mc.thePlayer.capabilities.isFlying = true;
						mc.thePlayer.capabilities.setFlySpeed(0.02f);
						loc = new double[] {mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};
						return;
					
					
				} else if(type.getMode().equalsIgnoreCase("Watcher")) {
					
					
					
					//for(int i=0;i<30;i++) {
					//	mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
					//}
				}else if(type.getMode().equalsIgnoreCase("Hypixel")) {
					
					// https://github.com/UnlegitMC/FDPClient/blob/master/src/main/java/net/ccbluex/liquidbounce/features/module/modules/movement/Fly.java
					
	                mc.timer.timerSpeed=0.7F;
	                mc.thePlayer.motionX=0;
	                mc.thePlayer.motionY=0;
	                mc.thePlayer.motionZ=0;
	                if(timer.hasTimeElapsed(1000, true)){
	                    // hclip LMFAO
	                    double yaw=Math.toRadians(mc.thePlayer.rotationYaw);
	                    double x = -Math.sin(yaw) * 0.7;
	                    double z = Math.cos(yaw) * 0.7;
	                    mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
	                }
	                mc.thePlayer.jumpMovementFactor = 0.00f;
					return;
				} else if(type.getMode().equalsIgnoreCase("Flappy")) {
					  if(mc.thePlayer.onGround || PlayerUtil.getPlayerHeightDouble() < 0.2)return;
				      mc.thePlayer.motionY = 0;
				      mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.000001f, mc.thePlayer.posZ);
                    return;
				}else if(type.getMode().equalsIgnoreCase("AAC3"))  {
					mc.timer.timerSpeed = 1.3f;
				}
				else if (type.getMode().equalsIgnoreCase("Redesky")) {
				}
				else if (type.getMode().equalsIgnoreCase("Bounce") || type.getMode().equalsIgnoreCase("Flappy")) {
					if(mc.thePlayer.onGround || PlayerUtil.getPlayerHeightDouble() <= 0.1)return;
					if(!Bounce)
					{
						Bounce = true;
						mc.thePlayer.motionY += 0.201;
					} else {
						Bounce = false;
						mc.thePlayer.motionY -= 0.2;
					}
				} else if (type.getMode().equalsIgnoreCase("Verus")) {
					if(PlayerUtil.getPlayerHeightDouble() < 0.2 || mc.thePlayer.onGround)return;
					mc.thePlayer.motionY = -0.0054f;
					mc.thePlayer.onGround = true;
					if(mc.thePlayer.isCollided)return;
					cacFly++;
					mc.timer.timerSpeed = 1.15f;
					if(cacFly % 3 == 0 && isMovingKB()) {
						mc.thePlayer.setSpeed(0.256f);
						mc.timer.timerSpeed = 2.3f;
					}
					if(cacFly % 4 == 0) {
						mc.thePlayer.motionY = 0.016f;
					}
					mc.thePlayer.fallDistance = 0f;
					return;
				} else if (type.getMode().equalsIgnoreCase("AGC")) {
					mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer(true));
	                if(Math.random()>0.5){
	                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(0,-1,0),
	                            0,mc.thePlayer.inventory.getCurrentItem(),0,0,0));
	                }
	                mc.thePlayer.motionY = 0;
					return;
				}
				mc.thePlayer.capabilities.isFlying = true;
				mc.thePlayer.fallDistance = 0f;
				mc.thePlayer.capabilities.setFlySpeed((float)flightSpeed.getValue() / 20f);
			}
		} else if (e instanceof EventMotion) {
			if(type.getMode().equalsIgnoreCase("ACD")) {
			} else if (type.getMode().equalsIgnoreCase("Watcher")) {
				if(!isMovingKB()) {
					mc.thePlayer.motionX = 0;
					mc.thePlayer.motionZ = 0;
				}
			}
		}
	}
	
	@Override
	public void onBB(AxisAlignedBB e) {
		if(type.getMode().equalsIgnoreCase("AGC"))
			e.setBoundingBox(AxisAlignedBB.fromBounds(e.minX, e.minY, e.minZ, e.minX + 1, mc.thePlayer.posY, e.minZ + 1));
	}

	public void onTick() {
		if(mc.thePlayer != null && mc.theWorld != null && (Flight.type.getMode().equalsIgnoreCase("Glide"))) {
			mc.thePlayer.motionY = -0.05f;
		}
	}
	
}
