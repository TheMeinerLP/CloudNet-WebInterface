package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils;

import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

public final class HttpUtil {

  /**
   * Do the simple check of a http response and check if the user authorized.
   * @param fullHttpResponse The response to check
   * @param httpRequest The request to check
   * @return The edited response for the web server
   */
  public static FullHttpResponse simpleCheck(FullHttpResponse fullHttpResponse,
      HttpRequest httpRequest) {
    ResponseUtil.setHeader(fullHttpResponse, "Content-Type", "application/json; charset=utf-8");
    if (!RequestUtil
        .hasHeader(httpRequest, "-xcloudnet-user", "-Xcloudnet-token", "-xcloudnet-message")) {
      return ResponseUtil.cloudFieldNotFound(fullHttpResponse);
    }
    if (!RequestUtil.checkAuth(httpRequest)) {
      return UserUtil.failedAuthorization(fullHttpResponse);
    }
    return fullHttpResponse;
  }

  public static User getUser(HttpRequest httpRequest) {
    return CloudNet.getInstance()
        .getUser(RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-user"));
  }
}
