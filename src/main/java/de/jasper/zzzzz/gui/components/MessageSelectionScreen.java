package de.jasper.zzzzz.gui.components;


import de.jasper.zzzzz.ModInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

/**
 * Screen copied from language-selection. Allows to select stored recordings.
 */
public class MessageSelectionScreen extends Screen {

    private MessageSelectionListWidget messageSelectionList;
    private final Screen parent;
    private final MinecraftClient client;

    // Should no longer use a singleton when used in general
    public static String FILE_PATH = ModInit.MESSAGES_FOLDER_PATH;

    private final List<String> messageList;

    public MessageSelectionScreen(Text title, Screen parent, String filename, List<String> messageList) {
        super(title);
        FILE_PATH = Path.of(ModInit.MESSAGES_FOLDER_PATH, filename).toString();
        this.parent = parent;
        this.client = MinecraftClient.getInstance();
        this.messageList = messageList;
        this.init();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }


    private TextFieldWidget tfw;

    protected void init() {
        this.messageSelectionList = new
                MessageSelectionListWidget(MinecraftClient.getInstance(), FILE_PATH);
        this.addSelectableChild(this.messageSelectionList);

        // Button placement:
        //   [Delete] [TextField] [Add] [Done]
        this.addDrawableChild(ButtonWidget.builder(
                        Text.translatable("zzzzz.messageSelectionScreen.delete"),
                        (button) -> this.onDelete()
                )
                .dimensions(this.width / 2 - 280, this.height - 38, 130, 20)
                .build());

        this.tfw = new TextFieldWidget(
                this.client.textRenderer,
                this.width / 2 - 140,
                this.height - 38,
                130,
                20,
                Text.of("")
        );
        this.tfw.setEditable(true);
        this.tfw.active = true;

        this.addDrawableChild(this.tfw);

        this.addDrawableChild(ButtonWidget.builder(
                        Text.translatable("zzzzz.messageSelectionScreen.add"),
                        (button) -> this.messageSelectionList.add(this.tfw.getText())
                )
                .dimensions(this.width / 2, this.height - 38, 130, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(
                        ScreenTexts.DONE,
                        (button) -> this.onDone()
                )
                .dimensions(this.width / 2 + 140, this.height - 38, 130, 20)
                .build());

        super.init();
    }

    private void onDone() {
        MinecraftClient.getInstance().setScreen(this.parent);
        this.messageSelectionList.writeCommands();
    }


    private static void writeToActionBar(Text message) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.inGameHud == null) {
                return;
            }

            client.inGameHud.setOverlayMessage(message, false);
    }

    private void onDelete() {

        MessageSelectionListWidget.
                MessageEntry recEntry = this.messageSelectionList.getSelectedOrNull();
        if (recEntry != null) {
            boolean deleteSuccess = this.messageSelectionList.remove(recEntry);
            if (!deleteSuccess) {
                ModInit.LOGGER.warn("Could not delete command to ignore {}", recEntry.command);
                this.close();
                writeToActionBar(Text.translatable("zzzzz.messageSelectionScreen.deletionFailed"));
            }
            this.messageSelectionList.writeCommands();
            this.messageSelectionList.updateCommands();
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyCodes.isToggle(keyCode)) {

            MessageSelectionListWidget.
                    MessageEntry languageEntry = this.messageSelectionList.getSelectedOrNull();
            if (languageEntry != null) {
                languageEntry.onPressed();
                this.onDone();
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.messageSelectionList.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 16, 16777215);
    }

    private class
    MessageSelectionListWidget extends AlwaysSelectedEntryListWidget<
            MessageSelectionListWidget.
                    MessageEntry> {
        final String filePath;
        public
        MessageSelectionListWidget(MinecraftClient client, String filePath) {
            // EntryListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight)
            super(client,
                    MessageSelectionScreen.this.width,
                    MessageSelectionScreen.this.height - 93, 32, 18);
            this.filePath = filePath;
            this.updateCommands();

            if (this.getSelectedOrNull() != null) {
                this.centerScrollOn(this.getSelectedOrNull());
            }
        }

        public void updateCommands() {
            this.clearEntries();
            messageList.clear();

            // Iterate over each line in file and add it as an entry
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // Process the line
                    addEntry(new
                            MessageEntry(line));
                    messageList.add(line);
                }
            } catch (IOException e) {
                // Don't know when this should happen
                ModInit.LOGGER.info(e.toString());
            }
        }

        public void writeCommands() {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                for (
                        MessageEntry e : this.children()) {
                    bw.write(e.command);
                    bw.newLine(); // Add a new line after each line
                }
            } catch (IOException e) {
                // Don't know when this should happen
                ModInit.LOGGER.info(e.toString());
            }
        }

        public boolean remove(
                MessageEntry toRemove) {
            return this.children().remove(toRemove);
        }

        public void add(String command) {

            MessageEntry toAdd = new
                    MessageEntry(command);
            if (this.children().contains(toAdd) || command.isEmpty()) {
                return;
            }
            this.children().add(toAdd);
            this.writeCommands();
            this.updateCommands();
        }

        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }

        public class
        MessageEntry extends Entry<
                MessageEntry> {
            final String command;
            private long clickTime;

            public
            MessageEntry(String command) {
                this.command = command;
            }

            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                context.drawCenteredTextWithShadow(
                        MessageSelectionScreen.this.textRenderer, this.command,
                        MessageSelectionListWidget.this.width / 2, y + 1, 16777215);
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                this.onPressed();
                if (Util.getMeasuringTimeMs() - this.clickTime < 250L) {

                    MessageSelectionScreen.this.onDone();
                }

                this.clickTime = Util.getMeasuringTimeMs();
                return true;
            }

            void onPressed() {

                MessageSelectionListWidget.this.setSelected(this);
            }

            @Override
            public Text getNarration() {
                return Text.of(this.command);
            }
        }
    }
}
