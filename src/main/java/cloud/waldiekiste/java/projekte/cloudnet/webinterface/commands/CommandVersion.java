package cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;

public final class CommandVersion extends Command {

  private final String version;

  /**
   * Shows the version of the module.
   * @param version The version of the module
   */
  public CommandVersion(String version) {
    super("versionI", "cloudnet.webinterface.version", "vI");
    this.description = "Show you the Module Version of the Material Design Web Interface";
    this.version = version;
  }

  @Override
  public void onExecuteCommand(CommandSender commandSender, String[] strings) {
    commandSender.sendMessage("Your current extension Version: " + version);
  }
}