/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetcore.CloudNet;

public final class CommandSetupConfig extends Command {
    private final ProjectMain projectMain;

    public CommandSetupConfig(ProjectMain projectMain) {
        super("setupI", "cloudnet.webinterface.setup", "sI");
        this.description = "Setup the CloudNet Material Design Interface";
        this.projectMain = projectMain;
    }

    @Override
    public void onExecuteCommand(CommandSender commandSender, String[] strings) {
        this.projectMain.getConfigSetup().start(CloudNet.getLogger().getReader());
    }
}
