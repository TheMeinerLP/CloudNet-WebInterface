/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.sign;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.serverselectors.sign.SearchingAnimation;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignGroupLayouts;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayout;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayoutConfig;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("deprecation")
public class ConfigSignLayout {

    private final Path path;

    public ConfigSignLayout() {
        this.path = Paths.get("local/signLayout.json");
        if (!Files.exists(this.path)) {
            new Document().append("layout_config", new SignLayoutConfig(true, false, 1.0, 0.8, Collections.singletonList(new SignGroupLayouts("default", (Collection) Arrays.asList(new SignLayout("empty", new String[]{"%server%", "&e%state%", "%online_players%/%max_players%", "%motd%"}, 159, 0), new SignLayout("online", new String[]{"%server%", "&e%state%", "%online_players%/%max_players%", "%motd%"}, 159, 0), new SignLayout("full", new String[]{"%server%", "&ePREMIUM", "%online_players%/%max_players%", "%motd%"}, 159, 0), new SignLayout("maintenance", new String[]{"§8§m---------", "maintenance", "§cmode", "§8§m---------"}, 159, 0)))), new SearchingAnimation(33, 11, (Collection)Arrays.asList(new SignLayout("loading1", new String[] { "", "server loads...", "o                ", "" }, 159, 14), new SignLayout("loading2", new String[] { "", "server loads...", " o               ", "" }, 159, 14), new SignLayout("loading3", new String[] { "", "server loads...", "  o              ", "" }, 159, 14), new SignLayout("loading4", new String[] { "", "server loads...", "   o             ", "" }, 159, 14), new SignLayout("loading5", new String[] { "", "server loads...", "    o            ", "" }, 159, 14), new SignLayout("loading6", new String[] { "", "server loads...", "o    o           ", "" }, 159, 14), new SignLayout("loading7", new String[] { "", "server loads...", " o    o          ", "" }, 159, 14), new SignLayout("loading8", new String[] { "", "server loads...", "  o    o         ", "" }, 159, 14), new SignLayout("loading9", new String[] { "", "server loads...", "   o    o        ", "" }, 159, 14), new SignLayout("loading10", new String[] { "", "server loads...", "    o    o       ", "" }, 159, 14), new SignLayout("loading11", new String[] { "", "server loads...", "o    o    o      ", "" }, 159, 14), new SignLayout("loading12", new String[] { "", "server loads...", " o    o    o     ", "" }, 159, 14), new SignLayout("loading13", new String[] { "", "server loads...", "  o    o    o    ", "" }, 159, 14), new SignLayout("loading14", new String[] { "", "server loads...", "   o    o    o   ", "" }, 159, 14), new SignLayout("loading15", new String[] { "", "server loads...", "    o    o    o  ", "" }, 159, 14), new SignLayout("loading16", new String[] { "", "server loads...", "o    o    o    o ", "" }, 159, 14), new SignLayout("loading17", new String[] { "", "server loads...", " o    o    o    o", "" }, 159, 14), new SignLayout("loading18", new String[] { "", "server loads...", "  o    o    o    ", "" }, 159, 14), new SignLayout("loading19", new String[] { "", "server loads...", "   o    o    o   ", "" }, 159, 14), new SignLayout("loading20", new String[] { "", "server loads...", "    o    o    o   ", "" }, 159, 14), new SignLayout("loading21", new String[] { "", "server loads...", "     o    o    o ", "" }, 159, 14), new SignLayout("loading22", new String[] { "", "server loads...", "      o    o    o", "" }, 159, 14), new SignLayout("loading23", new String[] { "", "server loads...", "       o    o    ", "" }, 159, 14), new SignLayout("loading24", new String[] { "", "server loads...", "        o    o   ", "" }, 159, 14), new SignLayout("loading25", new String[] { "", "server loads...", "         o    o  ", "" }, 159, 14), new SignLayout("loading26", new String[] { "", "server loads...", "          o    o ", "" }, 159, 14), new SignLayout("loading27", new String[] { "", "server loads...", "           o    o", "" }, 159, 14), new SignLayout("loading28", new String[] { "", "server loads...", "            o    ", "" }, 159, 14), new SignLayout("loading29", new String[] { "", "server loads...", "             o   ", "" }, 159, 14), new SignLayout("loading30", new String[] { "", "server loads...", "              o  ", "" }, 159, 14), new SignLayout("loading31", new String[] { "", "server loads...", "               o ", "" }, 159, 14), new SignLayout("loading32", new String[] { "", "server loads...", "                o", "" }, 159, 14), new SignLayout("loading33", new String[] { "", "server loads...", "                 ", "" }, 159, 14))))).saveAsConfig(this.path);
        }
    }

    public ConfigSignLayout saveLayout(final SignLayoutConfig signLayoutConfig) {
        final Document document = Document.loadDocument(this.path);
        document.append("layout_config", (Object)signLayoutConfig);
        document.saveAsConfig(this.path);
        return this;
    }

    public SignLayoutConfig loadLayout() {
        final Document document = Document.loadDocument(this.path);
        if (!document.getDocument("layout_config").contains("knockbackOnSmallDistance")) {
            final Document document2 = document.getDocument("layout_config").append("knockbackOnSmallDistance", false);
            document.append("layout_config", document2);
            document.saveAsConfig(this.path);
        }
        if (!document.getDocument("layout_config").contains("distance")) {
            final Document document2 = document.getDocument("layout_config").append("distance", (Number)1.0);
            document.append("layout_config", document2);
            document.saveAsConfig(this.path);
        }
        if (!document.getDocument("layout_config").contains("strength")) {
            final Document document2 = document.getDocument("layout_config").append("strength", (Number)0.8);
            document.append("layout_config", document2);
            document.saveAsConfig(this.path);
        }
        final SignLayoutConfig signLayoutConfig = (SignLayoutConfig)document.getObject("layout_config", new TypeToken<SignLayoutConfig>() {}.getType());
        boolean injectable = false;
        for (final SignGroupLayouts groupLayouts : signLayoutConfig.getGroupLayouts()) {
            final SignLayout signLayout = (SignLayout)CollectionWrapper.filter(groupLayouts.getLayouts(), (Acceptable)new Acceptable<SignLayout>() {
                public boolean isAccepted(final SignLayout signLayout) {
                    return signLayout.getName().equalsIgnoreCase("empty");
                }
            });
            if (signLayout == null) {
                groupLayouts.getLayouts().add(new SignLayout("empty", new String[] { "%server%", "&6%state%", "%online_players%/%max_players%", "%motd%" }, 159, 1));
                injectable = true;
            }
        }
        if (injectable) {
            document.append("layout_config", (Object)signLayoutConfig).saveAsConfig(this.path);
        }
        return signLayoutConfig;
    }
}
