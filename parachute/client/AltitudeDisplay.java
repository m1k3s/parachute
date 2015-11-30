//  
//  =====GPL=============================================================
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; version 2 dated June, 1991.
// 
//  This program is distributed in the hope that it will be useful, 
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
// 
//  You should have received a copy of the GNU General Public License
//  along with this program;  if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave., Cambridge, MA 02139, USA.
//  =====================================================================
//
//
// Copyright 2011-2015 Michael Sheppard (crackedEgg)
//
package com.parachute.client;

import com.parachute.common.ParachuteCommonProxy;
import java.text.DecimalFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AltitudeDisplay {

//	private final DecimalFormat df = new DecimalFormat();
	public static double altitude = 0.0;
	private final Minecraft mc = Minecraft.getMinecraft();
	private int screenX;
	private int screenY;
	private final int colorWhite = 0xffffffff;
	private final int colorYellow = 0xffffff00;
	private final int colorRed = 0xffcc0000; // format: alpha.red.green.blue
	private final int colorGreen = 0xff00cc00;

	private final String altitudeLabel = "Altitude: ";
	private final int titleWidth = mc.fontRendererObj.getStringWidth(altitudeLabel);
	private final int fieldWidth = mc.fontRendererObj.getStringWidth("000.0");
	private final int totalWidth = titleWidth + fieldWidth;

	public AltitudeDisplay()
	{
		super();
		ScaledResolution sr = new ScaledResolution(mc);
		screenX = sr.getScaledWidth();
		screenY = sr.getScaledHeight();
		
//		char sep = df.getDecimalFormatSymbols().getDecimalSeparator();
//		df.applyPattern("###" + sep + "#"); // for the alitude display
	}

	// the altitudeStr display is placed in the food bar space because
	// the food bar is removed when riding boats, parachutes, etc.
	// when in creativemode we lower the display a bit
	public void updateWindowScale()
	{
		ScaledResolution sr = new ScaledResolution(mc);
		screenX = (sr.getScaledWidth() / 2) + 10;
		if (mc.thePlayer.capabilities.isCreativeMode) {
			screenY = sr.getScaledHeight() - 30;
		} else {
			screenY = sr.getScaledHeight() - 38;
		}
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent event)
	{
		if (event.isCancelable() || mc.gameSettings.showDebugInfo || mc.thePlayer.onGround) {
			return;
		}

		if (mc.inGameHasFocus && event.type == RenderGameOverlayEvent.ElementType.ALL) {
			if (ParachuteCommonProxy.onParachute(mc.thePlayer)) {
				updateWindowScale();
				String altitudeStr = format(altitude);
				int stringWidth = mc.fontRendererObj.getStringWidth(altitudeStr);
				int nextX = totalWidth - stringWidth;
				mc.fontRendererObj.drawStringWithShadow(altitudeLabel, screenX, screenY, colorWhite);
				mc.fontRendererObj.drawStringWithShadow(altitudeStr, screenX + nextX, screenY, colorString());
			}
		}
	}
	
	public String format(double d)
	{
//		double dstr = new Double(df.format(d));
		return String.format("%.1f", d);
	}
	
	public static void setAltitudeDouble(double alt)
	{
		altitude = alt;
	}

	private int colorString()
	{
		return (altitude <= 8.0 && altitude >= 0.0) ? colorRed : altitude < 0.0 ? colorYellow : colorGreen;
	}
}
