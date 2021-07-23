package intentions.util;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class PlayerUtil {

	private static Minecraft mc = Minecraft.getMinecraft();
	
	public static int getPlayerHeight() {
		int yHeight = (int)Math.floor(mc.thePlayer.posY);
		int height = 0;
		while(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, yHeight, mc.thePlayer.posZ)).getBlock() == Blocks.air) {
			height++;
			yHeight--;
		}
		return height;
	}
	
}
