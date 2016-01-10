package parachute.common;

import net.minecraft.src.NetworkManager;

public class PlayerInfo
{
	public String Name;
	public int mode; // 0 = drift, 1 = ascend, 2 = descend
    public NetworkManager networkManager;
    
    public PlayerInfo(String name, NetworkManager nm) {
        Name = name;
        networkManager = nm;
    }
    
    public void setLiftMode(int m) {
    	mode = m;
    }
}
