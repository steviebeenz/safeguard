package intentions.command.impl;

import intentions.Client;
import intentions.command.Command;
import intentions.modules.Module;
import intentions.modules.combat.MultiAura;
import intentions.modules.movement.Flight;
import intentions.modules.movement.Speed;
import intentions.modules.world.ChestStealer;

public class Config extends Command {
	
  public Config() {
    super("Config", "Load hack config", "config <config>", new String[] {"c", "config"});
  }
  
  public void onCommand(String[] args, String command) {
    if (args.length > 0) {
      if (args[0].equalsIgnoreCase("Redesky")) {
        while (!Flight.type.getMode().equalsIgnoreCase("Redesky"))
          Flight.type.cycle(); 
        ChestStealer.autoClose.enabled = true;
        ChestStealer.autoSteal.enabled = true;
        ChestStealer.stealSpeed.setValue(10.0D);
        Speed.movementSpeed.setValue(1.3D);
        MultiAura.cps.setValue(16.0D);
        for (Module module : Client.modules) {
          if (module.name.equalsIgnoreCase("Sprint") || module.name.equalsIgnoreCase("FullBright") && !module.toggled)
            module.toggle();
        }
        while (!MultiAura.priority.getMode().equalsIgnoreCase("Players"))
          MultiAura.priority.cycle(); 
        MultiAura.range.setValue(4.8D);
        Client.addChatMessage("Loaded config \"Redesky\"");
      } else {
        Client.addChatMessage("Can not find config \"" + args[0] + "\"");
      } 
    } else {
      Client.addChatMessage("Enter config");
    } 
  }
}
