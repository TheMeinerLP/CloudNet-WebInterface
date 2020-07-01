package me.madfix.cloudnet.webinterface.http.v2.utils;

import de.dytanic.cloudnetcore.CloudNet;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class RequestHelper {

    /**
     * Check has header set.
     *
     * @param request The request to edit
     * @param headers The headers to add
     * @return Return true if the header set
     */
    public static boolean hasHeader(HttpRequest request, String... headers) {
        for (String header : headers) {
            if (request.headers().contains(header.toLowerCase(Locale.ENGLISH))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check and get the header.
     *
     * @param request The request to edit
     * @param header  The header to get the value
     * @return The value of the header
     */
    public static String headerValue(HttpRequest request, String header) {
        if (hasHeader(request, header.toLowerCase(Locale.ENGLISH))) {
            return request.headers().get(header.toLowerCase(Locale.ENGLISH));
        } else {
            throw new RuntimeException("Header-Field " + header + " not found!");
        }
    }

    /**
     * Get the content of the request.
     *
     * @param request The request to get content
     * @return The content of the request
     */
    public static String content(HttpRequest request) {
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

    /**
     * Check if the user authorized to login.
     *
     * @param httpRequest The request with information's about authorization
     * @return Return true if user authorized
     */
    public static boolean checkAuth(HttpRequest httpRequest) {
        String username = RequestHelper.headerValue(httpRequest, "-xcloudnet-user");
        String token = RequestHelper.headerValue(httpRequest, "-xcloudnet-token");
        return CloudNet.getInstance().authorization(username, token);
    }
}