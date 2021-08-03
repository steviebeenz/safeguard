package intentions.modules.player;

import intentions.Client;
import intentions.events.Event;
import intentions.modules.Module;
import intentions.settings.BooleanSetting;
import intentions.settings.ModeSetting;
import intentions.settings.Setting;
import intentions.util.PlayerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Module {
  
  public static boolean shouldWork = true;
  private BooleanSetting steppatch = new BooleanSetting("StepPatch", false);
  private ModeSetting mode = new ModeSetting("Mode", "Default", new String[] {"ACD", "Default"});
  public boolean NoFall;
  
  public NoFall() {
    super("NoFall", 38, Module.Category.PLAYER, "Prevents fall damage", true);
    addSettings(new Setting[] { (Setting)this.steppatch, (Setting)this.mode });
  }
  
  public void onEnable() {
    NoFall = true;
  }
  
  public void onDisable() {
    NoFall = false;
  }
  
  public void onEvent(Event e) {
    if (e instanceof intentions.events.listeners.EventUpdate && 
      e.isPre()
    	&& NoFall && shouldWork) {
    	if(mode.getMode().equalsIgnoreCase("Default")) {
	      boolean stepEnabled = false;
	      for(Module module : Client.modules) {
	    	  if(module.name == "Step" && module.isEnabled()) {
	    		  stepEnabled = true;
	    		  break;
	    	  }
	      }
	      if (this.mc.thePlayer.fallDistance >= 2 || (this.steppatch.isEnabled() && stepEnabled)) {
	        this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer(true));
	      }
    	} else if (mode.getMode().equalsIgnoreCase("ACD")) {
    		if(this.mc.thePlayer.fallDistance >= 2) {
    			mc.thePlayer.motionY += 0.05f;
	    		if(this.mc.thePlayer.fallDistance >= 5 && mc.thePlayer.ticksExisted % 20 == 0) {
	    			mc.thePlayer.motionY = 0;
	    		}
    		}
    	}
    }
  }
}
