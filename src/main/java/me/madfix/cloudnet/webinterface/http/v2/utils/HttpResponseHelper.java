package me.madfix.cloudnet.webinterface.http.v2.utils;

import de.dytanic.cloudnet.lib.utility.document.Document;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public final class HttpResponseHelper {

    /**
     * Send a permission denied message.
     *
     * @param response The response from web server to manipulate
     * @return The response to send to the web server
     */
    public static FullHttpResponse permissionDenied(FullHttpResponse response) {
        Document dataDocument = new Document("success", false)
                .append("reason", new ArrayList<>())
                .append("response", new Document());
        dataDocument.append("reason", "permission denied!");
        response.content()
                .writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
        response.setStatus(HttpResponseStatus.FORBIDDEN);
        return response;
    }

    /**
     * Send the response to web.
     *
     * @param response     The response from web server to manipulate
     * @param dataDocument The content of the response
     * @return The response to send to the web server
     */
    public static FullHttpResponse success(FullHttpResponse response,
                                           Document dataDocument) {
        dataDocument.append("success", true);
        dataDocument.append("reason", new ArrayList<>());
        response.setStatus(HttpResponseStatus.OK);

        response.content()
                .writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
        return response;
    }

    /**
     * Send the bad response to web.
     *
     * @param response     The response from web server to edit
     * @param dataDocument The content of the response
     * @return The edited response
     */
    public static FullHttpResponse badRequest(FullHttpResponse response, Document dataDocument) {
        dataDocument.append("success", false);
        dataDocument.append("reason", "Bad request");
        response.setStatus(HttpResponseStatus.BAD_REQUEST);
        response.content().writeBytes(dataDocument.convertToJsonString()
                .getBytes(StandardCharsets.UTF_8));
        return response;
    }


    public static void setHeader(FullHttpResponse response, String field, String value) {
        response.headers().set(field, value);
    }

    /**
     * Send to web the cloud field isn't exist.
     *
     * @param response The response from web server to manipulate
     * @return The response to send to the web server
     */
    public static FullHttpResponse cloudFieldNotFound(FullHttpResponse response) {
        Document dataDocument = new Document("success", false);
        dataDocument.append("reason", "-xcloudnet-user, -xcloudnet-password or -Xmessage not found!");
        response.content()
                .writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
        return response;
    }

    /**
     * Send to web the value field isn't exist.
     *
     * @param response The response from web server to manipulate
     * @return The response to send to the web server
     */
    public static FullHttpResponse valueFieldNotFound(FullHttpResponse response) {
        Document dataDocument = new Document();
        dataDocument.append("success", false);
        dataDocument.append("reason", "No available -Xvalue command found!");
        response.content()
                .writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
        response.setStatus(HttpResponseStatus.BAD_REQUEST);
        return response;
    }

    /**
     * Send to web the message field isn't exist.
     *
     * @param response The response from web server to manipulate
     * @return The response to send to the web server
     */
    public static FullHttpResponse messageFieldNotFound(FullHttpResponse response) {
        Document dataDocument = new Document();
        dataDocument.append("success", false);
        dataDocument.append("reason", "No available -Xmessage command found!");
        response.content()
                .writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
        response.setStatus(HttpResponseStatus.BAD_REQUEST);
        return response;
    }

    /**
     * Send to web the field isn't exist.
     *
     * @param response The response from web server to manipulate
     * @param message  The error message/reason message
     * @return The response to send to the web server
     */
    public static FullHttpResponse fieldNotFound(FullHttpResponse response, String message) {
        Document dataDocument = new Document();
        dataDocument.append("success", false);
        dataDocument.append("reason", message);
        response.content()
                .writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
        response.setStatus(HttpResponseStatus.BAD_REQUEST);
        return response;
    }

    /**
     * Send the cross to web site.
     *
     * @param request The response from web server to manipulate
     * @return The response to send to the web server
     */
    public static FullHttpResponse cross(HttpRequest request) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(request.protocolVersion(),
                HttpResponseStatus.OK);
        fullHttpResponse.headers().set("Content-Type", "application/json");
        fullHttpResponse.headers().set("Access-Control-Allow-Credentials", "true");
        fullHttpResponse.headers().set("Access-Control-Allow-Headers",
                "content-type, if-none-match, -Xcloudnet-token, -Xmessage, -Xvalue, -Xcloudnet-user,"
                        + " -Xcloudnet-password,-Xcount");
        fullHttpResponse.headers().set("Access-Control-Allow-Methods", "POST,GET,OPTIONS");
        fullHttpResponse.headers().set("Access-Control-Allow-Origin", "*");
        fullHttpResponse.headers().set("Access-Control-Max-Age", "3600");
        return fullHttpResponse;
    }
}