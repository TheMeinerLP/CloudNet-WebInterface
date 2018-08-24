package cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetcore.CloudNet;

public class CommandSetupConfig extends Command {
    private final ProjectMain projectMain;
    public CommandSetupConfig(ProjectMain projectMain) {
        super("setup_interface", "cloudnet.webinterface.setup", "mdwi");
        this.projectMain = projectMain;
    }

    @Override
    public void onExecuteCommand(CommandSender commandSender, String[] strings) {
        this.projectMain.getConfigSetup().start(CloudNet.getLogger().getReader());
    }
}
