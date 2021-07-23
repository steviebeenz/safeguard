package intentions.waypoints;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class Waypoint {

	/*
	 * Called every tick
	 */
	
	private static Minecraft mc = Minecraft.getMinecraft();
	
	public static void onTick() {
		
		if(mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null) {
			String server = mc.getCurrentServerData().serverIP;
			
			
		} else {
			
		}
	}
	
}
