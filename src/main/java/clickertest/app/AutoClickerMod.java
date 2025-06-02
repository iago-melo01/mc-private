package clickertest.app;


import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Random;


@Mod(modid = "autoclicker", name = "AutoClickerMod", version = "1.0")
public class AutoClickerMod {

        C02PacketUseEntity packet = new C02PacketUseEntity();

        private static final Minecraft mc = Minecraft.getMinecraft();
        private static boolean enabled = false;
        private static long lastClickTime = 0;
        private static int cps = 10;
        private static Random rand = new Random();
        private static KeyBinding toggleKey;
        private static boolean inPause = false;
        private static double pauseEndTime = 0;
        private static int count = 0;
        private static int jitterDellayMs = rand.nextInt(4);
        private static int jitterGroupSize = 3 + rand.nextInt(3);
        private static double range = 3 + rand.nextDouble() * 3;


        Robot robot;
        @Mod.EventHandler
        public void init(FMLInitializationEvent event) {
            toggleKey = new KeyBinding("Toggle AutoClicker", Keyboard.KEY_F8, "AutoClicker");
            net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(toggleKey);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
            
            try{
                robot = new Robot();
            }catch(AWTException e){
                e.printStackTrace();
            }
        }

        @SubscribeEvent
        public void onKeyInput(InputEvent.KeyInputEvent event) {
            if (toggleKey.isPressed()) {
                enabled = !enabled;
                mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText("AutoClicker: " + (enabled ? "ON" : "OFF")));
            }
        }

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (enabled && mc.theWorld != null && mc.thePlayer != null ) { // && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null
                long now = System.currentTimeMillis();


                if (inPause) {
                    if (now >= pauseEndTime) {
                        inPause = false; // fim da pausa
                    } else {
                        return; // ainda pausando, não clicar
                    }
                }

                if(rand.nextDouble() <= 0.02 ){
                        inPause = true;
                        pauseEndTime = now + rand.nextInt(100);
                        return;

                }


                if(Mouse.isButtonDown(0) && mc.currentScreen == null){
                    if(now - lastClickTime >= 1000 / cps + jitterDellayMs){
                        if(count >= jitterGroupSize){
                            jitterDellayMs = rand.nextInt(4);
                            jitterGroupSize = 3 +   rand.nextInt(3);
                            count = 0;
                        }

                        if(mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null ){
                            mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
                            mc.thePlayer.swingItem();
                            lastClickTime = now;
                            count++;
                        }
                        else {
                            mc.thePlayer.swingItem();
                            lastClickTime = now;
                            count++;
                        }

                    }
                }


            }
        }

        public static void setCPS(int newCPS) {
            cps = newCPS;
        }

        public static boolean isEnabled() {
            return enabled;
        }

        public static void setEnabled(boolean state) {
            enabled = state;
        }
    }


