package intentions.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import intentions.Client;
import intentions.events.Event;
import intentions.events.listeners.EventPacket;
import intentions.settings.KeybindSetting;
import intentions.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;

public class Module {

	public String name, description;
	
	public boolean toggled, expanded, toggleMessage;
	
	public int index;
	public KeybindSetting keyCode = new KeybindSetting(0);
	
	public Category category;
	public Minecraft mc = Minecraft.getMinecraft();
	
	public List<Setting> settings = new ArrayList<Setting>();
	
	public Module(String name, int key, Category c, String description, boolean toggleMessage) {;
		this.name = name;
		this.description = description;
		keyCode.code = key;
		this.category = c;
		if (!name.equals("ChestStealer") && !name.equals("TabGUI"))
			this.addSettings(keyCode);
		this.toggleMessage = toggleMessage;
	}
	
	public void addSettings(Setting...settings) {
		this.settings.addAll(Arrays.asList(settings));
		this.settings.sort(Comparator.comparingInt(s -> s == keyCode ? 1 : 0));
	}
	
	public boolean isEnabled() {
		return toggled;
	}
	
	public int getKey() {
		return keyCode.code;
	}
	
	public void onEvent(Event e) { }
	
	public void toggle() {
		toggled = !toggled;
		
		if (this.toggleMessage) Client.addChatMessage((toggled ? "Enabled" : "Disabled") + " " + this.name);
		
		if (toggled) { 
			onEnable();
			Client.toggledModules.add(this);
			return;
		}
		Client.toggledModules.remove(this);
		onDisable();
	}
	
	public void onEnable() { }
	
	public void onDisable() { }
	
	public void onRender() { }
	
	public void onTick() { }
	
	public void onUpdate() { }
	
	public enum Category
	{
		COMBAT("Combat"),
		MOVEMENT("Movement"),
		PLAYER("Player"),
		RENDER("Render"),
		CHAT("Chat"),
		WORLD("World");
		
		public String name;
		public int moduleIndex;
		
		Category(String name){
			this.name = name;
		}
	}

	public void onRightClick() { }
 
	public void onSendPacket(EventPacket eventPacket) { }
	
}
