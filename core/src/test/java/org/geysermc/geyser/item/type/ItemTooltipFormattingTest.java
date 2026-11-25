package org.geysermc.geyser.item.type;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.geysermc.geyser.item.TooltipOptions;
import org.geysermc.geyser.scoreboard.network.util.GeyserMockContext;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.translator.item.BedrockItemBuilder;
import org.geysermc.geyser.translator.item.ItemTranslator;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemTooltipFormattingTest {
    @Test
    void customNameRespectsExplicitAndInheritedItalics() {
        GeyserMockContext.mockContext(() -> {
            GeyserSession session = mock(GeyserSession.class);
            when(session.locale()).thenReturn("en_us");
            DataComponents components = new DataComponents(new HashMap<>());
            Component red = Component.text("test", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false);
            components.put(DataComponentTypes.CUSTOM_NAME, red);
            assertEquals("§r§ctest", ItemTranslator.getCustomName(session, components, null, 'f', false, false));

            components.put(DataComponentTypes.CUSTOM_NAME, Component.text("default"));
            assertEquals("default", ItemTranslator.getCustomName(session, components, null, 'f', false, false));

            components.put(DataComponentTypes.CUSTOM_NAME, Component.text("A").append(red).append(Component.text("B")));
            assertEquals("§r§e§oA§r§ctest§r§e§oB", ItemTranslator.getCustomName(session, components, null, 'e', false, false));
        });
    }

    @Test
    void loreCanDisableItalicsWhileKeepingItsDefaultColor() {
        GeyserMockContext.mockContext(() -> {
            GeyserSession session = mock(GeyserSession.class);
            when(session.locale()).thenReturn("en_us");
            DataComponents components = new DataComponents(new HashMap<>());
            components.put(DataComponentTypes.LORE, List.of(
                Component.text("default"),
                Component.text("upright").decoration(TextDecoration.ITALIC, false)
            ));
            Item item = new Item("dirt", Item.builder().components(components));
            BedrockItemBuilder builder = new BedrockItemBuilder();
            item.translateComponentsToBedrock(session, components, TooltipOptions.ALL_SHOWN, builder);
            assertEquals(List.of("default", "§r§5upright"), builder.getOrCreateLore());
        });
    }
}
