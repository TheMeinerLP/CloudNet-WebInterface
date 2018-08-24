package cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;

public class CommandVersion extends Command {
    private final ProjectMain projectMain;
    public CommandVersion(ProjectMain projectMain) {
        super("mdwi_version", "cloudnet.webinterface.version", "mdwi_v");
        this.projectMain = projectMain;
    }

    @Override
    public void onExecuteCommand(CommandSender commandSender, String[] strings) {
        commandSender.sendMessage("Your current extension Version: " +this.projectMain.getVersion());
    }
}
