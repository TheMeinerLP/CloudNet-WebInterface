package me.madfix.cloudnet.webinterface.logging;

import de.dytanic.cloudnetcore.CloudNet;
import io.sentry.jul.SentryHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

public class WebInterfaceLogger extends Logger {
    public WebInterfaceLogger() {
        super("WebInterfaceLogger", null);
        setLevel(Level.INFO);
        setParent(CloudNet.getLogger());
        Path path = Paths.get("wi", "log");
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                log(Level.SEVERE, "An unexpected error occurred while creating the file logger folder", e);
            }
        }
        try {
            WebInterfaceFileHandler webInterfaceFileHandler = new WebInterfaceFileHandler();
            webInterfaceFileHandler.setLevel(Level.ALL);
            webInterfaceFileHandler.setFormatter(new XMLFormatter());
            webInterfaceFileHandler.setEncoding(StandardCharsets.UTF_8.name());
            addHandler(webInterfaceFileHandler);
        } catch (IOException e) {
            log(Level.SEVERE, "An unexpected error occurred while adding the file logger", e);
        }
        addHandler(new SentryHandler());
    }

    @Override
    public void log(LogRecord record) {
        record.setMessage(String.format("[%s] %s", "WI", record.getMessage()));
        super.log(record);
    }
}
