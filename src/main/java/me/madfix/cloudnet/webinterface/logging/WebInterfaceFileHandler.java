package me.madfix.cloudnet.webinterface.logging;


import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.XMLFormatter;

public class WebInterfaceFileHandler extends FileHandler {

    public WebInterfaceFileHandler() throws IOException {
        super("wi/log/module.%g.xml", 2, 1, false);
        setFormatter(new XMLFormatter());
        setLevel(Level.ALL);
    }
}
