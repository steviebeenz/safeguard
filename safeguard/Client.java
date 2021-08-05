package intentions;

// Java IO
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
// Java Net
import java.net.Proxy;
// Arrays and lists (in the *)
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Display
import org.lwjgl.opengl.Display;

import com.ibm.icu.text.MessagePatternUtil.ComplexArgStyleNode;
// Logins
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

// Command manager
import intentions.command.CommandManager;
// Events
import intentions.events.Event;
import intentions.events.listeners.*;
// Modules
import intentions.modules.Module;
import intentions.modules.Module.Category;
import intentions.modules.chat.*;
import intentions.modules.combat.*;
import intentions.modules.movement.*;
import intentions.modules.player.*;
import intentions.modules.render.*;
import intentions.modules.world.*;
// UI
import intentions.ui.HUD;
// Waypoints
import intentions.waypoints.Waypoint;
// Minecraft
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Session;

public class Client {
	
	public static String name = "SafeGuard", version = "b3.3.2";
	public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>();
	public static CopyOnWriteArrayList<Module> toggledModules = new CopyOnWriteArrayList<Module>();
	public static HUD hud = new HUD();
	public static CommandManager commandManager = new CommandManager();
	public static String fullName = name + " " + version;
	
	public static void a(Module m) {
		if(modules.contains(m))return;
		modules.add(m);
		System.out.println("[" + Client.name + "] Loaded " + m.name);
	}

	
	public static void startup() {
		System.out.println("[" + name + "] Starting " + name + " " + version);
		Display.setTitle(name + " " + version);
		
		// World
		a(new Scaffold());
		
		a(new TimerHack());
		
		a(new ChestStealer());
		
		// Movement
		a(new Flight());
		
		a(new Sprint());
		
		a(new Speed());
		
		a(new Hover());
		
		a(new AirHop());
		
		a(new BHop());
		
		a(new BPS());
		
		a(new Step());
		
		a(new LongJump());
		
		a(new ClickTP());
		
		// Player
		a(new Blink());
		
		a(new NoFall());
		
		a(new NoSlowdown());
		
		a(new AntiCactus());
		
		a(new Jesus());
		
		a(new Rotation());
		
		a(new AntiVoid());
		
		a(new AutoSoup());
		
		a(new AutoTool());
		
		a(new LiquidInteract());
		
		a(new InvMove());
		
		a(new Team());
		
		a(new PingSpoof());
		
		// Render
		a(new FullBright());
		
		a(new ESP());
		
		// Combat
		a(new KillAura());
		
		a(new MultiAura());
		
		a(new Velocity());
		
		a(new Criticals());
		
		a(new AutoArmor());
		
		a(new AutoClicker());
		
		a(new AimAssist());
		
		// Chat
		a(new Spammer());
		
		a(new Watermark());
		
		// TabGUI
		a(new TabGUI());
		
		File file = new File(System.getProperty("user.dir") + "\\SafeGuard\\alt.txt");
		
		if(file.exists()) {
			StringBuilder f = new StringBuilder();
			try {
				  
				  BufferedReader br = new BufferedReader(new FileReader(file));
				  
				  String st;
				  while ((st = br.readLine()) != null) {
					   f.append(st);
				  }
				  br.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			
			String d = f.toString();
			
			final String args[] = d.split(":");
			if(args[0].contains("@") && args[0].contains(".")) {
				final YggdrasilUserAuthentication authentication = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
				authentication.setUsername(args[0]);
				authentication.setPassword(args[1]);
				try {
					authentication.logIn();
					
					Minecraft.getMinecraft().session = new Session(authentication.getSelectedProfile().getName(), authentication.getSelectedProfile().getId().toString(), authentication.getAuthenticatedToken(), "mojang");	
				} catch (AuthenticationException e) {
					e.printStackTrace();
				}
			} else {
				Minecraft.getMinecraft().session = new Session(d, "", "", "mojang");
			}
		}
		
		file = new File(System.getProperty("user.dir") + "\\SafeGuard\\settings.txt");
		
		if(file.exists()) {
			StringBuilder f = new StringBuilder();
			try {
				  
				  BufferedReader br = new BufferedReader(new FileReader(file));
				  
				  String st;
				  while ((st = br.readLine()) != null) {
					   f.append(st);
				  }
				  br.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			
			String d = f.toString();
			
			String[] args = d.split(";");
			
			
			
			try {
				
				for(Module m : modules) {
					
					if(m.name.equalsIgnoreCase(args[1])) {
						
						boolean tof = args[0].equalsIgnoreCase("true");
						
						m.toggled = tof;
						
						break;
						
					}
					
				}
				
			}catch(Exception e) {}
			
		}
		
	}
	
	public static void onEvent(Event e) {
		if(e instanceof EventChat) {
			commandManager.handleChat((EventChat)e);
		}
		
		for(Module m : modules) {
			if (!m.toggled && !m.name.equalsIgnoreCase("TabGUI"))
				continue;
			m.onEvent(e);
		}
	}
	
	public static void keyPress(int key) {
		Client.onEvent(new EventKey(key));
		
		for(Module m : modules) {
			if (m.getKey() == key && TabGUI.openTabGUI) {
				m.toggle();
				break;
			}
		}
	}
	
	public static List<Module> getModulesByCategory(Category c){
		List<Module> modules = new ArrayList<Module>();
		
		for(Module m : Client.modules) {
			if (m.category == c) {
				modules.add(m);
			}
		}
		
		return modules;
	}
	
	public static void addChatMessage(Object b) {
		b = "\2478[\247c" + name + "\2478] \2477" + b;
		
		if(b.toString().contains("Disabled ")) {
			if (!TabGUI.openTabGUI) {
				return;
			};
		};
		
		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(b.toString()));
	}
	
	public static void onRender() {
		for(Module module : Client.toggledModules) {
			module.onRender();
		}
	}
	
	public static void onTick() {
		if(!TabGUI.openTabGUI) return;
		for (Module module : Client.toggledModules) {
			module.onTick();
		}
		Waypoint.onTick();
	}
	
	public static void onUpdate() {
		if(!TabGUI.openTabGUI) return;
		for(Module module : Client.modules) {
			if(module.name == "Flight" && (!module.toggled || !((Flight)module).type.getMode().equalsIgnoreCase("Redesky"))) {
				Minecraft.getMinecraft().thePlayer.speedInAir = 0.02f;
			}
			((Module)module).onUpdate();
		}
		
	}
}
