package cloud.waldiekiste.java.projekte.cloudnet.webinterface.services;

/**
 * In this class, the errors are listed and there messages are specified.
 */

public class ErrorService {

    /**
     * Getting the error code and sending a specified ErrorMessage for the ErrorCode.
     */
    public static void error(int code){
        System.err.println("Error Code: "+code);
        switch (code){
            case 101:
                reason("Config Permission System cannot init");
        }
    }

    /**
     * Here the Reason is getting name and sended.
     */
    private static void reason(String s) {
        System.err.println("Reason: "+s);
    }
}
