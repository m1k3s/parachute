//
// This work is licensed under the Creative Commons
// Attribution-ShareAlike 3.0 Unported License. To view a copy of this
// license, visit http://creativecommons.org/licenses/by-sa/3.0/
//

package parachute.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;


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
