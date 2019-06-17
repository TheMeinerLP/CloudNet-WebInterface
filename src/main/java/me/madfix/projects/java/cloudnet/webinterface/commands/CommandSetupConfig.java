package me.madfix.projects.java.cloudnet.webinterface.commands;

import me.madfix.projects.java.cloudnet.webinterface.ProjectMain;
import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetcore.CloudNet;

public final class CommandSetupConfig extends Command {

  private final ProjectMain projectMain;

  /**
   * Start the setup process for the config.
   * @param projectMain The main class of the project
   */
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