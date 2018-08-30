package cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.VersionType;
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
        request(new SetupRequest("type", "Which UpdateChannel how to use?", "This Channel not exsists", SetupResponseType.STRING, new Catcher<Boolean, String>() {
            @Override
            public Boolean doCatch(String s) {
                if(s.equalsIgnoreCase(VersionType.DEVELOPMENT.getType())){
                   return true;
                }else if(s.equalsIgnoreCase(VersionType.RELEASE.getType())){
                   return true;
                }else if(s.equalsIgnoreCase(VersionType.SNAPSHOT.getType())){
                   return true;
                }
                return false;
            }
        }));
    }
}
