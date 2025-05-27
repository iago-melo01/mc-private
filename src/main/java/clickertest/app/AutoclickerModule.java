package clickertest.app;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class AutoclickerModule {

    @Mod(modid = AutoClickerMod.MODID, name = AutoClickerMod.NAME, version = AutoClickerMod.VERSION)
    public class AutoClickerMod {
        public static final String MODID = "autoclicker";
        public static final String NAME = "AutoClicker Mod";
        public static final String VERSION = "1.0";

        private static final Minecraft mc = Minecraft.getMinecraft();
        private static boolean enabled = false;
        private static long lastClickTime = 0;
        private static int cps = 10;

        private static KeyBinding toggleKey;

        @Mod.EventHandler
        public void init(FMLInitializationEvent event) {
            toggleKey = new KeyBinding("Toggle AutoClicker", Keyboard.KEY_R, "AutoClicker");
            net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(toggleKey);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
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
            if (enabled && mc.theWorld != null && mc.thePlayer != null && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
                long now = System.currentTimeMillis();
                if (now - lastClickTime >= 1000 / cps) {
                    mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
                    mc.thePlayer.swingItem();
                    lastClickTime = now;
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

}
