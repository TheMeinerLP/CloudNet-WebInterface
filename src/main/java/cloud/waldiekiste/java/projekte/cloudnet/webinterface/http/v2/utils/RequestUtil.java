/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils;

import de.dytanic.cloudnetcore.CloudNet;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

public class RequestUtil {
    public static boolean hasHeader(HttpRequest request,String... headers){
        for (String header : headers) {
            if(request.headers().contains(header.toLowerCase())){
                return true;
            }
        }
        return false;
    }
    public static String getHeaderValue(HttpRequest request,String header){
        if(hasHeader(request,header.toLowerCase())){
            return request.headers().get(header.toLowerCase());
        }else{
            throw new NullPointerException("Header Field "+header+" not found!");

        }
    }
    public static String getContent(HttpRequest request){
        FullHttpRequest fullHttpRequest = (FullHttpRequest) request;
        if (fullHttpRequest.content().readableBytes() != 0) {
            ByteBuf buf = fullHttpRequest.content();
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            return new String(bytes);
        }else {
            throw new NullPointerException("No Content found found!");
        }
    }
    public static boolean checkAuth(HttpRequest httpRequest){
        String username = RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-user");
        String token = RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-token");
        if (!CloudNet.getInstance().authorization(username, token)) {
            return false;
        }
        return true;
    }
}
