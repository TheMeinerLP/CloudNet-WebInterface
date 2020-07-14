package me.madfix.cloudnet.webinterface.logging;


import java.io.IOException;
import java.util.logging.FileHandler;

public class WebInterfaceFileHandler extends FileHandler {

    public WebInterfaceFileHandler() throws IOException {
        super("wi/log/module.%g.xml", 80000, 10, false);
    }
}
