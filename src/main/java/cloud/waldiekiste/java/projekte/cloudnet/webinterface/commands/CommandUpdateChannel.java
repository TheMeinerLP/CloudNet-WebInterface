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

public class CommandUpdateChannel extends Command {
    private ProjectMain projectMain;
    public CommandUpdateChannel(ProjectMain projectMain) {
        super("updateI", "cloudnet.webinterface.update",new String[0]);
        this.projectMain = projectMain;
    }

    @Override
    public void onExecuteCommand(CommandSender commandSender, String[] strings) {
        if(strings.length > 0){
            String subcommand = strings[0];
            switch (subcommand.toLowerCase()){
                case "manual":{
                    if (strings.length > 1) {
                        //String version = strings[1];
                        System.out.println("Still work in Progress");
                    }else{
                        Document document = CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().get();
                        if (document.contains("mdwi.updateChannel")) {
                            VersionType type = VersionType.valueOf(document.get("mdwi.updateChannel").getAsString());
                            try {
                                System.out.println("[Updater] Available versions for update channel "+type.getType());
                                this.projectMain.getUpdates(type).forEach(t-> System.out.printf("Version: %s",t.getVersion()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return;
                }
                case "channel":{
                    if (strings.length > 1){
                        String channel = strings[1];
                        VersionType type = VersionType.valueOf(channel);
                        Document document = CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().get();
                        document.append("mdwi.updateChannel",type.getType());
                        CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().set(document);
                        System.out.println("[Updater] Update Channel now "+type.getType());
                    }else{
                        System.out.println("[Updater] Please insert a Update Channel(DEVELOPMENT,RELEASE,SNAPSHOT)");
                    }
                    return;
                }
            }

        }else{
            this.projectMain.run();
        }
    }
}
