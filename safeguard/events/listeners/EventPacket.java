package intentions.events.listeners;

import net.minecraft.network.Packet;

public class EventPacket {

	public Packet packet;
	
	public EventPacket(Packet packet) {
		this.packet = packet;
	}
	
	private boolean cancelled = false;
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public boolean getCancelled() {
		return cancelled;
	}
	public Packet getPacket() {
		return packet;
	}
	
}
