package me.madfix.cloudnet.webinterface.logging;

import de.dytanic.cloudnetcore.CloudNet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebInterfaceLogger extends Logger {
    public WebInterfaceLogger() {
        super("WebInterfaceLogger", "logger_messages");
        setLevel(Level.INFO);
        setParent(CloudNet.getLogger());
        Path path = Paths.get("wi", "log");
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                log(Level.SEVERE,"logger.handler.create.folder", e);
            }
        }
        try {
            addHandler(new WebInterfaceFileHandler());
        } catch (IOException e) {
            log(Level.SEVERE,"logger.handler.add.file", e);
        }
    }
}
