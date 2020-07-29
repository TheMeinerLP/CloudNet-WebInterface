package me.madfix.cloudnet.webinterface.api.rest.basic;

import io.javalin.core.security.BasicAuthCredentials;
import io.javalin.http.Context;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.api.permission.Permissions;

import java.util.concurrent.CompletableFuture;

public final class UtilityAPI {

    public static CompletableFuture<Boolean> hasPermission(WebInterface webInterface, Context context, Permissions permission) {
        return hasPermission(webInterface, context, permission.getPermissionString());
    }

    public static CompletableFuture<Boolean> hasPermission(WebInterface webInterface, Context context, String permission) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (context.basicAuthCredentialsExist()) {
            BasicAuthCredentials authCredentials = context.basicAuthCredentials();
            webInterface.getUserProvider()
                    .getUser(authCredentials.getUsername())
                    .thenCompose((user) -> webInterface.getPermissionProvider().getPermissionUser(user))
                    .thenAccept(permissionUser -> completableFuture.complete(permissionUser.hasPermission(permission)));
        } else {
            completableFuture.complete(false);
        }
        return completableFuture;
    }
}
