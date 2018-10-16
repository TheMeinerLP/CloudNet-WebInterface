package cloud.waldiekiste.java.projekte.cloudnet.webinterface.services;

public class ErrorService {
    public static void error(int code){
        System.err.println("Error Code: "+code);
        switch (code){
            case 101:
                reason("Config Permission System cannot init");
        }
    }
    private static void reason(String s) {
        System.err.println("Reason: "+s);
    }
}
