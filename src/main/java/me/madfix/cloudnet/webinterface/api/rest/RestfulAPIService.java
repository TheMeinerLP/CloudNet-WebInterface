package me.madfix.cloudnet.webinterface.api.rest;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.javalin.Javalin;
import io.javalin.core.security.BasicAuthCredentials;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.ReDocOptions;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.api.permission.Permissions;
import me.madfix.cloudnet.webinterface.model.InterfaceConfiguration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class RestfulAPIService {

    private final WebInterface webInterface;
    private Javalin restServer;

    public RestfulAPIService(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    public void startRestApi() {
        Optional<InterfaceConfiguration> interfaceConfiguration =
                this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration();
        if (interfaceConfiguration.isPresent()) {
            final InterfaceConfiguration configuration = interfaceConfiguration.get();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(RestfulAPIService.class.getClassLoader());
            this.restServer = Javalin.create(config -> config.registerPlugin(new OpenApiPlugin(getOpenApiOptions())))
                                     .start(configuration.getHost(), configuration.getRestPort());
            this.restServer.routes(() -> {

            });
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    private void getUsers(Context context) {
        hasPermission(context, Permissions.GET_WEB_USERS).thenAccept(has -> {
            if (has) {
                context.json(this.webInterface.getUserProvider().getUsers()).status(200);
            }
        });
    }

    private CompletableFuture<Boolean> hasPermission(Context context, Permissions permission) {
        return hasPermission(context, permission.getPermissionString());
    }

    private CompletableFuture<Boolean> hasPermission(Context context, String permission) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (context.basicAuthCredentialsExist()) {
            BasicAuthCredentials authCredentials = context.basicAuthCredentials();
            this.webInterface.getUserProvider()
                             .getUser(authCredentials.getUsername())
                             .thenCompose((user) -> this.webInterface.getPermissionProvider().getPermissionUser(user))
                             .thenAccept(permissionUser -> completableFuture.complete(permissionUser.hasPermission(
                                     permission)));
        } else {
            completableFuture.complete(false);
        }
        return completableFuture;
    }

    private void auth(Context context) {
        if (context.basicAuthCredentialsExist()) {
            BasicAuthCredentials authCredentials = context.basicAuthCredentials();
            this.webInterface.getUserProvider().getUser(authCredentials.getUsername()).thenAccept(webInterfaceUser -> {
                BCrypt.Result result = BCrypt.verifyer()
                                             .verify(authCredentials.getPassword().getBytes(StandardCharsets.UTF_8),
                                                     webInterfaceUser.getPasswordHash());
                if (result.verified && result.validFormat) {
                    context.status(200);
                } else {
                    try {
                        context.res.sendError(401, result.formatErrorMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            try {
                context.res.sendError(401, "Unauthorized");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getUser(Context context) {
        hasPermission(context, Permissions.GET_WEB_USER).thenAccept(has -> {
            if (has) {
                String username = context.pathParam("username");
                context.json(this.webInterface.getUserProvider().getUser(username)).status(200);
            }
        });
    }

    private OpenApiOptions getOpenApiOptions() {
        Info applicationInfo = new Info().version("1.0").description("My Application");
        return new OpenApiOptions(applicationInfo).path("/swagger-docs")
                                                  .swagger(new SwaggerOptions("/swagger"))
                                                  .reDoc(new ReDocOptions("/redoc"));
    }

}
