package cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;

import java.io.*;
import java.net.URL;

public class CommandWIChangelog extends Command {
    private Gson gson;
    public CommandWIChangelog() {
        super("wichangelog", "cloudnet.webinterface.changelog", "wilog");
        this.gson = new Gson();
    }

    @Override
    public void onExecuteCommand(CommandSender commandSender, String[] strings) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(CommandWIChangelog.class.getClassLoader().getResourceAsStream("changelog.json")));
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        JsonArray changes = json.getAsJsonArray("changes");
        StringBuilder builder = new StringBuilder();
        builder.append("\n╔══════════[Changelog]══════════\n");
        changes.forEach(x->{
            JsonElement date = x.getAsJsonObject().get("date");
            JsonArray fixes = x.getAsJsonObject().getAsJsonArray("changes");
            JsonArray adds = x.getAsJsonObject().getAsJsonArray("adds");
            JsonArray removes = x.getAsJsonObject().getAsJsonArray("removes");
            builder.append("║\n");
            builder.append("╠──────────────────────────────\n");
            builder.append("║\n");
            builder.append("╠─────["+date.getAsString()+"]\n");
            builder.append("║\n");
            builder.append("╠──────────[Changes]────────────\n");
            builder.append("║\n");
            fixes.forEach(y->builder.append(String.format("╠ -> %s\n",y.getAsString())));
            builder.append("║\n");
            builder.append("╠───────────[Adds]──────────────\n");
            builder.append("║\n");
            adds.forEach(z->builder.append(String.format("╠ -> %s\n",z.getAsString())));
            builder.append("║\n");
            builder.append("╠──────────[Removes]────────────\n");
            builder.append("║\n");
            removes.forEach(a->builder.append(String.format("╠ -> %s\n",a.getAsString())));
            builder.append("║\n");
            builder.append("╠──────────────────────────────\n");
            builder.append("║\n");

        });
        builder.append("╚══════════[Changelog]══════════\n");
        commandSender.sendMessage(builder.toString());
    }
}
