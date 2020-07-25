package me.madfix.cloudnet.webinterface.api.rest;

import com.sun.tools.javac.util.DefinedBy;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.core.security.BasicAuthCredentials;
import io.javalin.http.Context;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.model.InterfaceConfiguration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class RestfulAPIService {

    private final WebInterface webInterface;
    private Javalin restServer;

    public RestfulAPIService(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    public void startRestApi() {
        Optional<InterfaceConfiguration> interfaceConfiguration = this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration();
        if (interfaceConfiguration.isPresent()) {
            final InterfaceConfiguration configuration = interfaceConfiguration.get();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(RestfulAPIService.class.getClassLoader());
            this.restServer = Javalin.create().start(configuration.getHost(),configuration.getRestPort());
            this.restServer.routes(() -> {
                ApiBuilder.before("user/*", this::auth);
                ApiBuilder.path("user", () -> {
                    ApiBuilder.path("get", () -> {
                        ApiBuilder.get("user/:username", this::getUser);
                    });
                    ApiBuilder.path("post", () -> {

                    });
                });


            });
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    private void auth(Context context) {
        if (context.basicAuthCredentialsExist()) {
            BasicAuthCredentials authCredentials = context.basicAuthCredentials();
            this.webInterface.getUserProvider().getUser(authCredentials.getUsername()).thenAccept(webInterfaceUser -> {
                String pwdHash = new String(webInterfaceUser.getPasswordHash(), StandardCharsets.UTF_8);
                System.out.println(authCredentials.getPassword());
                System.out.println(pwdHash);
                System.out.println(pwdHash.equals(authCredentials.getPassword()));

                boolean isSame = true;
                int i = 0;
                while(i < pwdHash.length() && isSame) {
                    isSame = pwdHash.charAt(i) == authCredentials.getPassword().charAt(i);
                    i++;
                }

                System.out.println("isSame: " + isSame + " Index is: " + i);

                if (pwdHash.equals(authCredentials.getPassword())) {
                    context.status(200);
                } else {
                    try {
                        context.res.sendError(401, "Unauthorized");
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
        String username = context.pathParam("username");
        context.json(this.webInterface.getUserProvider().getUser(username)).status(200);
    }

}
