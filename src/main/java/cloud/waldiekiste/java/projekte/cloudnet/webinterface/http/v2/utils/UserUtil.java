package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils;

import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.nio.charset.StandardCharsets;

public class UserUtil {

    public static boolean hasPermission(User user, String... permissions){
        for (String permission : permissions) {
            //if(permission.matches("^.+?.*([.0-9]\\1$)")){ } Check permission end with number
            if (user.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }
    public  static FullHttpResponse failedAuthorization(FullHttpResponse response){
        Document dataDocument = new Document("success", false);
        dataDocument.append("reason", "failed authorization!");
        dataDocument.append("response", new Document());
        response.setStatus(HttpResponseStatus.UNAUTHORIZED);
        response.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
        return response;
    }

}
