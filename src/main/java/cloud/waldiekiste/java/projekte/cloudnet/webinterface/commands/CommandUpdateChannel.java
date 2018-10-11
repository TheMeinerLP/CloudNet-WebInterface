/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils.UpdateData;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils.VersionType;
import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.command.TabCompletable;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CommandUpdateChannel extends Command implements TabCompletable {
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
                    Document document = CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().get();
                    VersionType type = VersionType.valueOf(document.get("mdwi.updateChannel").getAsString());
                    if (strings.length > 1) {
                        String version = strings[1];
                        System.out.println("Search for Version...");
                        UpdateData data = null;
                        try {
                            Stream<UpdateData> updateDataStream = this.projectMain.getUpdateService().getUpdates(type).stream();
                            if (updateDataStream.anyMatch(t->t.getVersion().equalsIgnoreCase(version.toLowerCase()))) {
                                document.append("mdwi.downgrade",true);
                                CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().set(document);
                                System.out.println("Old Version found. Doing Downgrade...");
                                this.projectMain.onShutdown();
                                CloudNet.getInstance().getModuleManager().disableModule(this.projectMain);
                                System.out.println("Downgrade finish. Reloading Cloud...");
                                this.projectMain.getUpdateService().update(data);
                                CloudNet.getInstance().reload();
                            }else{
                                System.out.println("Old Version not found!");
                            }
                        } catch (Exception e) {}
                    }else{

                        if (document.contains("mdwi.updateChannel")) {
                            try {
                                System.out.println("[Updater] Available versions for update channel "+type.getType());
                                this.projectMain.getUpdateService().getUpdates(type).forEach(t-> System.out.printf("Version: %s",t.getVersion()));
                            } catch (Exception e) {}
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
            this.projectMain.getUpdateService().checkUpdate(this.projectMain);
        }
    }

    @Override
    public List<String> onTab(long l, String s) {
        switch ((int) l){
            case 0:{
                return Arrays.asList("manual","channel");
            }
            case 1:{
                switch (s.toLowerCase()){
                    case "manual":{
                        Document document = CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().get();
                        VersionType type = VersionType.valueOf(document.get("mdwi.updateChannel").getAsString());
                        List<String> versions = new ArrayList<>();
                        try {
                            this.projectMain.getUpdateService().getUpdates(type).forEach(t->versions.add(t.getVersion()));
                        } catch (Exception e){}

                        return versions;
                    }
                    case "channel":{
                        return Arrays.asList("DEVELOPMENT","RELEASE","SNAPSHOT");
                    }
                }
                break;
            }
        }
        return Arrays.asList("manual","channel");
    }
}
