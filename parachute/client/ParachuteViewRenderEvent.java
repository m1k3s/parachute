package com.parachute.client;

import com.parachute.common.Parachute;
import com.parachute.common.ParachuteCommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ParachuteViewRenderEvent {

//    private final Minecraft mc = Minecraft.getMinecraft();

    public ParachuteViewRenderEvent()
    {
        Parachute.proxy.info("ParachuteViewRenderEvent ctor");
    }

    @SubscribeEvent()
    public void onEvent(EntityViewRenderEvent.CameraSetup event)
    {
        Parachute.proxy.info("**** Caught onCameraSetup");
//        EntityParachute entityParachute = (EntityParachute) event.entity;
        if (ParachuteCommonProxy.onParachute(Minecraft.getMinecraft().thePlayer)) {
            Parachute.proxy.info("*** Player is riding the Parachute");
            // calculate the delta yaw (rate of turn)
            float deltaYaw = event.entity.prevRotationYaw + (event.entity.rotationYaw - event.entity.prevRotationYaw) + 180.0f;
            // calulate forward velocity
            double velocity = Math.sqrt(event.entity.motionX * event.entity.motionX + event.entity.motionZ * event.entity.motionZ);
            event.roll = MathHelper.sin(deltaYaw * (float) Math.PI * (float) velocity);
        }
    }

//    @SubscribeEvent(priority= EventPriority.NORMAL, receiveCanceled=true)
//    public void onEvent(EntityViewRenderEvent.FogDensity event)
//    {
//        event.density = (float) Math.abs(Math.pow(((event.entity.posY - 63) / (255 - 63)), 4));
//        event.setCanceled(true); // must be canceled to affect the fog density
//    }
}
