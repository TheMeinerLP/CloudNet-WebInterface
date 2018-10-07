/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils.VersionType;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.setup.Setup;
import de.dytanic.cloudnet.setup.SetupRequest;
import de.dytanic.cloudnet.setup.SetupResponseType;
import de.dytanic.cloudnetcore.CloudNet;

public class UpdateChannelSetup extends Setup {
    public UpdateChannelSetup() {
        setupComplete(t->{
            final String type = t.get("type").getAsString();
            Document document = CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().get();
            document.append("mdwi.updateChannel",VersionType.valueOf(type).getType());
            CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().set(document);
        });
        request(new SetupRequest("type", "Which UpdateChannel you want to use?(RELEASE,DEVELOPMENT,SNAPSHOT)", "This Channel not exsists", SetupResponseType.STRING, s -> {
            if(s.length() == 0 || s == null || s.isEmpty()){
                return false;
            }
            if(s.equalsIgnoreCase(VersionType.DEVELOPMENT.getType())){
               return true;
            }else if(s.equalsIgnoreCase(VersionType.RELEASE.getType())){
               return true;
            }else if(s.equalsIgnoreCase(VersionType.SNAPSHOT.getType())){
               return true;
            }
            return false;
        }));
    }
}
