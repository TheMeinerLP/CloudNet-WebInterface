/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils.VersionType;
import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

public final class CommandUpdateChannel extends Command {

    private final ProjectMain projectMain;

    public CommandUpdateChannel(ProjectMain projectMain) {
        super("updateI", "cloudnet.webinterface.update","UI");
        this.description = "Help you to Update the Material Design Web Interface Module";
        this.projectMain = projectMain;
    }

    @Override
    public void onExecuteCommand(CommandSender commandSender, String[] strings) {
        if(strings.length > 0){
            String subCommand = strings[0];
            switch (subCommand.toLowerCase()){
                case "manual":{
                    Document document = CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().get();
                    VersionType type = VersionType.valueOf(document.get("mdwi.updateChannel").getAsString());
                    if (document.contains("mdwi.updateChannel")) {
                        try {
                            System.out.println("[Updater] Available versions for update channel "+type.getType());
                            this.projectMain.getUpdateService().getUpdates(type).forEach(t-> System.out.printf("Version: %s",t.getVersion()));
                        } catch (Exception exception) {
                            System.err.println("Something went wrong");
                            exception.printStackTrace();
                        }
                    }
                    return;
                }
                case "channel":{
                    if (strings.length > 1){
                        String channel = strings[1];
                        VersionType type = VersionType.valueOf(channel.toUpperCase());
                        Document document = CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().get();
                        document.append("mdwi.updateChannel", type.getType().toUpperCase());
                        CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().set(document);
                        System.out.println("[Updater] Update Channel now "+ type.getType());
                    } else {
                        System.out.println("[Updater] Please insert a Update Channel(BETA,RELEASE,SNAPSHOT)");
                    }
                }
            }
        } else {
            this.projectMain.getUpdateService().checkUpdate(this.projectMain);
        }
    }
}
