package xyz.emirdev.hypixeloverlay;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    TextFieldWidget textFieldWidget;

    public ConfigScreen() {
        super(Text.literal("Hypixel Overlay"));
    }

    @Override
    protected void init() {
        textFieldWidget = new TextFieldWidget(this.textRenderer, 40, 40, 225, 20, Text.literal(""));
        textFieldWidget.setMaxLength(36);
        textFieldWidget.setText(HypixelOverlayClient.getInstance().hypixelAPIKey);

        this.addDrawableChild(textFieldWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawText(this.textRenderer, "Hypixel API Key", 40, 40 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
    }

    @Override
    public void close() {
        super.close();
        HypixelOverlayClient.getInstance().hypixelAPIKey = textFieldWidget.getText();
    }
}