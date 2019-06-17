package cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils.VersionType;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.setup.Setup;
import de.dytanic.cloudnet.setup.SetupRequest;
import de.dytanic.cloudnet.setup.SetupResponseType;
import de.dytanic.cloudnetcore.CloudNet;

/*
In this class, the user chooses the Updatechannel
 */
public final class UpdateChannelSetup extends Setup {

  /**
   * Start the setup process to set the update channel type
   */
  public UpdateChannelSetup() {
    setupComplete(t -> {
      final String type = t.get("type").getAsString().toUpperCase();
      Document document = CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase()
          .get();
      document.append("mdwi.updateChannel", VersionType.valueOf(type).getType().toUpperCase());
      CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().set(document);
    });
    /*
    Here the user will get a message in which he get asked which UpdateChannel he wants to use RELEASE
    | DEVELOPMENT | SNAPSHOT
    If he types a other Message or something other, he gets a ErrorMessage.
    */
    request(new SetupRequest("type",
        "Which Update Channel you want to use?(RELEASE,DEVELOPMENT,SNAPSHOT)",
        "This Channel not exists", SetupResponseType.STRING, s -> {

      if (s.length() == 0 || s.isEmpty()) {
        return false;
      } else {
        VersionType type = VersionType.valueOf(s.toUpperCase());
        return type != null;
      }
    }));
  }
}