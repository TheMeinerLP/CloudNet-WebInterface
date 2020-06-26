package me.madfix.cloudnet.webinterface.http.v2.utils;

import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.charset.StandardCharsets;

public final class Http {

  /**
   * Do the simple check of a http response and check if the user authorized.
   * @param httpRequest The request to check
   * @return The edited response for the web server
   */
  public static FullHttpResponse simpleCheck(HttpRequest httpRequest) {
    FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.protocolVersion(),
        HttpResponseStatus.OK);
    Response.setHeader(fullHttpResponse, "Content-Type",
        "application/json; charset=utf-8");
    if (!Request
        .hasHeader(httpRequest, "-xcloudnet-user",
            "-Xcloudnet-token", "-xcloudnet-message")) {
      return Response.cloudFieldNotFound(fullHttpResponse);
    }
    if (!Request.checkAuth(httpRequest)) {
      return Http.failedAuthorization(fullHttpResponse);
    }
    return fullHttpResponse;
  }

  public static User getUser(HttpRequest httpRequest) {
    return CloudNet.getInstance()
        .getUser(Request.headerValue(httpRequest, "-xcloudnet-user"));
  }

  /**
   * Send a fail authorization.
   * @param response The response to edit
   * @return Return the response they are edited
   */
  public static FullHttpResponse failedAuthorization(FullHttpResponse response) {
    Document dataDocument = new Document("success", false);
    dataDocument.append("reason", "failed authorization!");
    dataDocument.append("response", new Document());
    response.setStatus(HttpResponseStatus.UNAUTHORIZED);
    response.content()
        .writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
    return response;
  }
}
