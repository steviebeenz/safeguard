package intentions.modules.movement;

import intentions.Client;
import intentions.modules.Module;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.util.MovementUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Step extends Module {
	
	public NumberSetting height = new NumberSetting("Height", 1, 1, 30, 1);
	public ModeSetting mode = new ModeSetting("Mode", "V1", new String[] {"V1", "V2", "AAC4"});
	
	public Step() {
		super("Step", 0, Category.MOVEMENT, "Steps up blocks when you run into them", true);
		this.addSettings(height, mode);
	}
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public boolean shouldstep = false;
	public double cacheY;
	
	public void onUpdate() {
		
		if(this.toggled) {
			
			if(mc.thePlayer.isCollidedHorizontally && !mode.getMode().equalsIgnoreCase("AAC4")) {
				
				if(mode.getMode().equalsIgnoreCase("V2")) 
					for(int i=0;i<height.getValue();i++) {
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ, mc.thePlayer.onGround));
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.75, mc.thePlayer.posZ, mc.thePlayer.onGround));
					}
				else if (mode.getMode().equalsIgnoreCase("V1")) {
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (0.42 * (float) height.getValue()), mc.thePlayer.posZ, mc.thePlayer.onGround));
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (0.75 * (float) height.getValue()), mc.thePlayer.posZ, mc.thePlayer.onGround));
				}
				
				mc.thePlayer.stepHeight = (float) height.getValue();
				
			} else {
				if (shouldstep) {
					mc.timer.timerSpeed = (float) (0.9 + mc.thePlayer.ticksExisted % 4 / 20);
				}
				if (mc.thePlayer.onGround) {
					shouldstep = false;
					mc.timer.timerSpeed = 1;
				}
				if (mc.thePlayer.isCollidedHorizontally && MovementUtils.isMoving() && mc.thePlayer.onGround) {
					cacheY = mc.thePlayer.posY;
					mc.thePlayer.isAirBorne = true;
					shouldstep = true;
					mc.timer.timerSpeed = 4;
					mc.thePlayer.motionY += 0.47;
					mc.thePlayer.motionY += 0.55; //for 1.5 block step
				}
			}
			
		} else {
			mc.thePlayer.stepHeight = 0.5f;
		}
		
	}
}
