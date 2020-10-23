package me.madfix.cloudnet.webinterface.logging;


import java.io.IOException;
import java.util.logging.FileHandler;

/**
 * Handle some log events into files
 * @version 1.0.0
 * @since 1.11.5
 */
public class WebInterfaceFileHandler extends FileHandler {

    public WebInterfaceFileHandler() throws IOException {
        super("wi/log/module.%g.xml", 80000, 10, false);
    }
}
