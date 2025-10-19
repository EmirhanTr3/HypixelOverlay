package xyz.emirdev.hypixeloverlay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class HypixelOverlayClient implements ClientModInitializer {
    private static HypixelOverlayClient instance;
    public KeyBinding keybind;
    public KeyBinding configKeybind;

    public String hypixelAPIKey;

    public static HypixelOverlayClient getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        keybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hypixeloverlay.stats",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "HypixelOverlay"
        ));

        configKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hypixeloverlay.config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                "HypixelOverlay"
        ));

        AtomicLong usedAt = new AtomicLong();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keybind.wasPressed()) {
                if (!Objects.equals(MinecraftClient.getInstance().getNetworkHandler().getServerInfo().address, "hypixel.net")) {
                    return;
                }

                if (usedAt.get() + 2000 > System.currentTimeMillis()) {
                    return;
                }

                if (MinecraftClient.getInstance().options.sprintKey.wasPressed()) {
                    PlayerDataProvider.clearStats();
                } else {
                    BedwarsStats.get();
                }

                usedAt.set(System.currentTimeMillis());
            }

            if (configKeybind.wasPressed()) {
                if (MinecraftClient.getInstance().currentScreen != null) return;
                MinecraftClient.getInstance().setScreen(new ConfigScreen());
            }
        });

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.of("hypixeloverlay", "stats"),
                StatsHud::render
        );
    }
}
