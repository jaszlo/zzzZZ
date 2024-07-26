package de.jasper.zzzzz.gui;

import de.jasper.zzzzz.gui.components.MessageSelectionScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class EpilogueMessageSelectionScreen extends MessageSelectionScreen {

    public static final List<String> messageList = new ArrayList<>();

    public EpilogueMessageSelectionScreen(Text title, Screen parent, String filename) {
        super(title, parent, filename, messageList);
    }
}
