package intentions.modules.combat;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.modules.Module;
import intentions.modules.player.AutoSoup;
import intentions.settings.BooleanSetting;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.settings.Setting;
import intentions.util.PlayerUtil;
import intentions.util.RenderUtils;
import intentions.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;

public class KillAura extends Module {
  public Timer timer = new Timer();
  
  public NumberSetting range = new NumberSetting("Range", 4.0D, 1.0D, 6.0D, 0.1D), cps = new NumberSetting("CPS", 10.0D, 1.0D, 20.0D, 1.0D);
  
  public BooleanSetting noSwing = new BooleanSetting("No Swing", false), death = new BooleanSetting("Death", true), invisibles = new BooleanSetting("Invisibles", true), esp = new BooleanSetting("ESP", true);
  
  public ModeSetting sort = new ModeSetting("Sort", "Closest", new String[] { "Furthest", "Health", "Closest" }), rotation = new ModeSetting("Rotation", "Server", new String[] { "Client", "Server" }), priority = new ModeSetting("Priority", "Passive", new String[] { "Passive", "None", "Mobs", "Players" });
  
  public KillAura() {
    super("KillAura", 45, Module.Category.COMBAT, "Attacks entities around you", true);
    addSettings(new Setting[] { (Setting)this.range, (Setting)this.cps, (Setting)this.sort, (Setting)this.noSwing, (Setting)this.rotation, (Setting)this.priority, (Setting)this.invisibles, (Setting)this.death, (Setting)this.esp });
  }

  private EntityLivingBase target;
  private int timeSinceLastAtk;

  
  public void onRender() {
	  if(esp.isEnabled() && timeSinceLastAtk < 10 && target != null) {
		if(!target.isEntityAlive()) { target = null; return; }
		float red = 0.10588235294f;
		float green = 0.56470588235f;
		float blue = 0.75294117647f;
		
		double xPos = (target.lastTickPosX + (target.posX - target.lastTickPosX) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosX;
		double yPos = (target.lastTickPosY + (target.posY - target.lastTickPosY) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosY;
		double zPos = (target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosZ;    		
		render(red, green, blue, xPos, yPos, zPos, target.width, target.height);
      }
  }
  
  public void onEvent(Event e) {
    if (e instanceof EventMotion && 
      e.isPre()) {
    
      if (mc.thePlayer.deathTime > 1 && death.isEnabled()) {
    	  if(this.toggled) {
    		  this.toggle();
    	  }
    	  return;
      }
      
      if(mc.thePlayer == null) return;
      
      if(mc.thePlayer.getCurrentEquippedItem() != null) {
	      Item held = mc.thePlayer.getCurrentEquippedItem().getItem();
	      if(held == Items.bowl || held == Items.mushroom_stew && AutoSoup.enabled) return;
      }
      
      timeSinceLastAtk++;
      
      EventMotion event = (EventMotion)e;
      List<EntityLivingBase> targets = (List<EntityLivingBase>)this.mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
      targets = (List<EntityLivingBase>)targets.stream().filter(entity -> (entity.getDistanceToEntity((Entity)this.mc.thePlayer) < this.range.getValue() && entity != this.mc.thePlayer && !entity.isDead && entity.getHealth() > 0.0F)).collect(Collectors.toList());
      if (this.priority.getMode() == "Passive") {
        targets = (List<EntityLivingBase>)targets.stream().filter(EntityAnimal.class::isInstance).collect(Collectors.toList());
      } else if (this.priority.getMode() == "Monsters") {
        targets = (List<EntityLivingBase>)targets.stream().filter(EntityMob.class::isInstance).collect(Collectors.toList());
      } else if (this.priority.getMode() == "Players") {
        targets = (List<EntityLivingBase>)targets.stream().filter(EntityPlayer.class::isInstance).collect(Collectors.toList());
      } 
      if (this.sort.getMode() == "Closest") {
        targets.sort(Comparator.comparingDouble(entity -> entity.getDistanceToEntity((Entity)this.mc.thePlayer)));
      } else if (this.sort.getMode() == "Furthest") {
        targets.sort(Comparator.<EntityLivingBase>comparingDouble(entity -> ((EntityLivingBase)entity).getDistanceToEntity((Entity)this.mc.thePlayer)).reversed());
      } 
      targets = PlayerUtil.removeNotNeeded(targets);
      if (!targets.isEmpty()) {
        EntityLivingBase target = targets.get(0);
        
        this.target = target;
        timeSinceLastAtk = 0;
        if (this.timer.hasTimeElapsed((long)(1000.0D / this.cps.getValue()), true)) {
        
          if(!invisibles.isEnabled() && target.isInvisible()) return;
        
          if (this.rotation.getMode() == "Server") {
              event.setYaw((float) ((float) getRotations((Entity)target)[0] + Math.floor(Math.random() * 10)));
              event.setPitch((float) (getRotations((Entity)target)[1] + Math.floor(Math.random() * 10)));
            } else {
              this.mc.thePlayer.rotationYaw = getRotations((Entity)target)[0];
              this.mc.thePlayer.rotationPitch = getRotations((Entity)target)[1];
            } 
          
          if (this.noSwing.isEnabled()) {
            this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0APacketAnimation());
          } else {
            this.mc.thePlayer.swingItem();
          } 
          
          if(Criticals.c) {
        	  
        	  this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.2958195819595185918, mc.thePlayer.posZ, false));
        	  this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
         
          }
          
          
          this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C02PacketUseEntity((Entity)target, C02PacketUseEntity.Action.ATTACK));
        } 
      } 
    } 
  }
  
  public void render(float red, float green, float blue, double x, double y, double z, float width, float height) {
	  RenderUtils.drawEntityESP(x, y, z, width - (width / 4), height, red, green, blue, 0.2F, 0F, 0F, 0F, 1F, 1F);
  }
  
  
  public float[] getRotations(Entity e) {
    double deltaX = e.posX + e.posX - e.lastTickPosX - this.mc.thePlayer.lastTickPosX;
    double deltaY = e.posY - 3.5D + e.getEyeHeight() - this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight();
    double deltaZ = e.posZ + e.posZ - e.lastTickPosZ - this.mc.thePlayer.lastTickPosZ;
    double distance = Math.sqrt(Math.pow(deltaX, 2.0D) + Math.pow(deltaZ, 2.0D));
    float yaw = (float)Math.toDegrees(-Math.atan(deltaX / deltaZ));
    float pitch = (float)-Math.toDegrees(Math.atan(deltaY / distance));
    if (deltaX < 0.0D && deltaZ < 0.0D) {
      yaw = (float)(90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX)));
    } else if (deltaX > 0.0D && deltaZ < 0.0D) {
      yaw = (float)(-90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX)));
    } 
    return new float[] { yaw, pitch };
  }
}
