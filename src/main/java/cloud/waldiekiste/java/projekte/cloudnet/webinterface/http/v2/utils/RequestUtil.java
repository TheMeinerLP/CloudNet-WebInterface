/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

public class RequestUtil {
    public static boolean hasHeader(HttpRequest request,String... headers){
        for (String header : headers) {
            if(request.headers().contains(header)){
                return true;
            }
        }
        return false;
    }
    public static String getHeaderValue(HttpRequest request,String header){
        if(hasHeader(request,header)){
            return request.headers().get(header);
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
}
