package me.madfix.projects.java.cloudnet.webinterface.setup;

import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.setup.Setup;
import de.dytanic.cloudnet.setup.SetupRequest;
import de.dytanic.cloudnet.setup.SetupResponseType;
import de.dytanic.cloudnetcore.CloudNet;

public final class DomainSslSetup extends Setup {


  /**
   * Setup the ssl for the web interface.
   */
  public DomainSslSetup() {
    setupComplete(document -> {
      Document updateDatabase = CloudNet.getInstance().getDbHandlers()
          .getUpdateConfigurationDatabase()
          .get();
      updateDatabase.append("mdwi.domain", document.getString("domain"));
      CloudNet.getInstance().getDbHandlers().getUpdateConfigurationDatabase().set(updateDatabase);
      System.out.println("The domain for ssl successfully set!");
    });
    setupCancel(() -> {
      /*
      If the user cancels the setup, he gets a Message
      */
      System.out.println("The setup was cancel from user!");
    });
    request(new SetupRequest("domain",
        "Please insert the domain witch you use for the web interface!",
        "Only domains names allowed", SetupResponseType.STRING, c ->
        !c.matches("([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])")));
  }
}
