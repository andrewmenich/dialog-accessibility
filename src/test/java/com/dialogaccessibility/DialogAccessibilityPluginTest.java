package com.dialogaccessibility;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class DialogAccessibilityPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(DialogAccessibilityPlugin.class);
		RuneLite.main(args);
	}
}