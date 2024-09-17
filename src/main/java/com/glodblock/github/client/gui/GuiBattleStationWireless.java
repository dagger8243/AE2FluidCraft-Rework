package com.glodblock.github.client.gui;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.PanelManager;
import com.cleanroommc.modularui.widgets.CycleButtonWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.glodblock.github.inventory.item.IWirelessTerminal;
import net.minecraft.entity.player.InventoryPlayer;

import javax.annotation.Nullable;

public class GuiBattleStationWireless {
    public ModularPanel mainPanel = new ModularPanel("Battle Station Main");

    private final ModularScreen screen = new ModularScreen(mainPanel);
    private final IWirelessTerminal it;

    public GuiBattleStationWireless(@Nullable InventoryPlayer inventory, IWirelessTerminal it) {
        this.it = it;
        /*
        if (this.state == State.CLOSED) throw new IllegalStateException("Can't init in closed state!");
        if (this.state == State.INIT || this.state == State.DISPOSED) {
            setState(State.OPEN);
            openPanel(this.mainPanel, false);
            checkDirty();
            return true;
        }
        return false;
         */
//        this.onResize(500, 500);
        createBaseGUI();
        if (inventory != null) screen.getMainPanel().bindPlayerInventory();
    }

//    @Override
//    public void onResize(int width, int height) {
//        super.onResize(width, height);
//    }

    private void createBaseGUI() {
        ModularPanel search = new ModularPanel("Battle Station Main - Search");
        ModularPanel itemsDisplay = new ModularPanel("Battle station Main - Items Display");
        ModularPanel craftArea = new ModularPanel("Battle Station Main - Craft Area");

        TextFieldWidget searchBar = new TextFieldWidget();
        CycleButtonWidget searchBoxMode = new CycleButtonWidget();
        ToggleButton saveString = new ToggleButton();

        search.addChild(searchBar, 0);
        search.addChild(searchBoxMode,1);
        search.addChild(saveString, 2);

        PanelManager panelManager = screen.getPanelManager();

        panelManager.openPanel(search, new PanelHandler(panelManager, search));
        panelManager.openPanel(itemsDisplay, new PanelHandler(panelManager, itemsDisplay));
        panelManager.openPanel(craftArea, new PanelHandler(panelManager, craftArea));
    }

    private class PanelHandler implements IPanelHandler {
        public final PanelManager manager;
        public final ModularPanel panel;

        public PanelHandler(PanelManager manager, ModularPanel panel) {
            this.manager = manager;
            this.panel = panel;
        }

        @Override
        public void openPanel() {
            panel.onOpen(screen);
        }

        @Override
        public void closePanel() {
            panel.onClose();
        }

        @Override
        public void closeSubPanels() {
            for (IWidget child : panel.getChildren()) {
                if (child instanceof ModularPanel) {
                    ((ModularPanel) child).closeIfOpen(false);
                }
            }
        }

        @Override
        public void closePanelInternal() {

        }

        @Override
        public void deleteCachedPanel() {

        }

        @Override
        public boolean isSubPanel() {
            return panel != manager.getMainPanel();
        }
    }
}
