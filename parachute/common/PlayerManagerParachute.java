package parachute.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.EntityPlayer;

public class PlayerManagerParachute
{
	public List<PlayerInfo> Players;
    
    private static final PlayerManagerParachute instance = new PlayerManagerParachute();
    
    public static final PlayerManagerParachute getInstance() {
        return instance;
    }
    
    private PlayerManagerParachute() {
        Players = new ArrayList();
    }
    
    // must test for null EntityPlayer before calling this method
    public PlayerInfo getPlayerInfoFromPlayer(EntityPlayer player) {
        for(PlayerInfo pi : Players) {
            if(pi.Name.equals(player.username))
                return pi;
        }
    	return null;
    }
}
