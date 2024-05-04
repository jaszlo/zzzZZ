package de.jasper.zzzzz;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;


/**
 * Playerautoma option screen to configure settings
 */
public class ZzzzzSettings extends GameOptionsScreen {

    public static final File PROLOG_FILE = new File(String.valueOf(Path.of(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "zzzzProlog")));
    public static final File EPILOGUE_FILE = new File(String.valueOf(Path.of(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "zzzzEpilog")));

    public static OptionButton<Boolean> usePrologText = new OptionButton<>(
        false,
        OptionButton.BOOLEAN_VALUES,
        "zzzzz.option.usePrologText",
        Object::toString,
        Boolean::parseBoolean,
        (bool) -> (bool ? ScreenTexts.ON : ScreenTexts.OFF)
    );

    public static OptionButton<Boolean> useEpilogueText = new OptionButton<>(
            false,
            OptionButton.BOOLEAN_VALUES,
            "zzzzz.option.useEpilogueText",
            Object::toString,
            Boolean::parseBoolean,
            (bool) -> (bool ? ScreenTexts.ON : ScreenTexts.OFF)
    );

    public static TextFieldWidget prologText;
    public static TextFieldWidget epilogueText;

    public ZzzzzSettings(String title, Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.of(title));
    }

    private static void storeText(boolean isProlog) {
        File file = isProlog ? PROLOG_FILE : EPILOGUE_FILE;
        String toStore = isProlog ? prologText.getText() : epilogueText.getText();
        try {
            FileWriter fw = new FileWriter(file, false);
            fw.write(toStore);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        assert this.client != null : "Closing screen of which this.client is null";
        // Use close from parent but also store new prolog/epilog
        for (Element element : this.children()) {
            if (!(element instanceof OptionListWidget optionListWidget)) continue;
            optionListWidget.applyAllPendingValues();
        }
        this.client.setScreen(this.parent);
        storeText(true);
        storeText(false);
    }

    public void init() {
        super.init();
        assert this.client != null;

        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().marginX(5).marginBottom(4).alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(2);

        prologText = new TextFieldWidget(client.textRenderer, 200, 20, Text.of("Text before sleeping"));
        epilogueText = new TextFieldWidget(client.textRenderer, 200, 20, Text.of("Text after sleeping"));


        ButtonWidget usePrologTextButton = ButtonWidget.builder(
                Text.translatable(usePrologText.key),
                (_b) -> {
                    usePrologText.next();
                    prologText.active = usePrologText.getValue();
                }
        ).build();
        usePrologText.setButton(usePrologTextButton);

        ButtonWidget useEpilogueTextButton = ButtonWidget.builder(
                Text.translatable(useEpilogueText.key),
                (_b) -> {
                    useEpilogueText.next();
                    epilogueText.active = useEpilogueText.getValue();
                }
        ).build();
        useEpilogueText.setButton(useEpilogueTextButton);

        adder.add(prologText);
        adder.add(usePrologTextButton);
        adder.add(epilogueText);
        adder.add(useEpilogueTextButton);
        adder.add(EmptyWidget.ofHeight(16), 2);
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, this.height / 6 - 12, this.width, this.height, 0.5f, 0.0f);
        gridWidget.forEachChild(this::addDrawableChild);
    }
}