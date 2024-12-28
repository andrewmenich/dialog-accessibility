package com.dialogaccessibility;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;

import java.awt.Font;
import java.util.Objects;

@Slf4j
@PluginDescriptor(
	name = "Dialog Accessibility"
)
public class DialogAccessibilityPlugin extends Plugin
{
	private static final Logger log = LoggerFactory.getLogger(DialogAccessibilityPlugin.class);

	@Inject
	private Client client;

	@Inject
	private DialogAccessibilityConfig config;

	@Inject
	private NPCDialogOverlay npcOverlay;

	@Inject
	private PlayerDialogOverlay playerOverlay;

	@Inject
	private OverlayManager overlayManager;

	private Font customFont;

	public int npcDialogId = ComponentID.DIALOG_NPC_TEXT;

	public int playerDialogId = ComponentID.DIALOG_PLAYER_TEXT;

	public Widget npcDialogWidget;
	public Widget playerDialogWidget;

	public String dialogBoxBounds;

    @Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}


	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if(this.client.getWidget(ComponentID.DIALOG_NPC_TEXT) != null){
			this.npcDialogWidget = this.client.getWidget(ComponentID.DIALOG_NPC_TEXT);
            assert this.npcDialogWidget != null;
            this.dialogBoxBounds = this.npcDialogWidget.getBounds().toString();
			processWidgetOverlay(npcDialogId, npcOverlay);
		}

		if(this.client.getWidget(ComponentID.DIALOG_PLAYER_TEXT) != null){
			this.playerDialogWidget = this.client.getWidget(ComponentID.DIALOG_PLAYER_TEXT);

			if(config.playerDialog()){
				processWidgetOverlay(playerDialogId, playerOverlay);
			}
		}

		if(!Objects.equals(this.npcDialogWidget.getBounds().toString(), this.dialogBoxBounds)){
			this.hideWidgetOverlay(npcDialogId, npcOverlay);
			this.processWidgetOverlay(npcDialogId, npcOverlay);

			if(config.playerDialog()){
				this.hideWidgetOverlay(playerDialogId, playerOverlay);
				this.processWidgetOverlay(playerDialogId, playerOverlay);
			}
		}
	}

	public Font getCustomFont()
	{
		FontManager fontManager = new FontManager(config);
		String configFont = config.fontOptions().toString();
		return fontManager.findFont(configFont);
	}

	private void processWidgetOverlay(int dialogId, Overlay overlay)
	{
		Widget dialog = this.client.getWidget(dialogId);

		if(dialog != null){
			String text = dialog.getText();

			if(!text.isEmpty()){
				dialog.setHidden(true);
				overlayManager.add(overlay);
			} else {
				overlayManager.remove(overlay);
			}
		}
	}

	private void showWidgetOverlay(int dialogId, Overlay overlay)
	{
		Widget dialog = this.client.getWidget(dialogId);

		if(dialog != null){
			String text = dialog.getText();

			if(!text.isEmpty()){
				dialog.setHidden(true);
				overlayManager.add(overlay);
			}
		}
	}

	private void hideWidgetOverlay(int dialogId, Overlay overlay)
	{
		Widget dialog = this.client.getWidget(dialogId);

		if(dialog != null){
			dialog.setHidden(false);
			overlayManager.remove(overlay);
		}
	}


	@Provides
	DialogAccessibilityConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DialogAccessibilityConfig.class);
	}
}
