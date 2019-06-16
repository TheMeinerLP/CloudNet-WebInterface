package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils;

import de.dytanic.cloudnetcore.CloudNet;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class RequestUtil {

  public static boolean hasHeader(HttpRequest request, String... headers) {
    for (String header : headers) {
      if (request.headers().contains(header.toLowerCase(Locale.ENGLISH))) {
        return true;
      }
    }
    return false;
  }

  public static String getHeaderValue(HttpRequest request, String header) {
    if (hasHeader(request, header.toLowerCase(Locale.ENGLISH))) {
      return request.headers().get(header.toLowerCase(Locale.ENGLISH));
    } else {
      throw new NullPointerException("Header-Field " + header + " not found!");
    }
  }

  public static String getContent(HttpRequest request) {
    FullHttpRequest fullHttpRequest = (FullHttpRequest) request;
    if (fullHttpRequest.content().readableBytes() != 0) {
      ByteBuf buf = fullHttpRequest.content();
      byte[] bytes = new byte[buf.readableBytes()];
      buf.readBytes(bytes);
      return new String(bytes, StandardCharsets.UTF_8);
    } else {
      throw new NullPointerException("No content found!");
    }
  }

  public static boolean checkAuth(HttpRequest httpRequest) {
    String username = RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-user");
    String token = RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-token");
    return CloudNet.getInstance().authorization(username, token);
  }
}