package me.madfix.cloudnet.webinterface;

import com.google.gson.Gson;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.CoreModule;
import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import me.madfix.cloudnet.webinterface.api.group.GroupProvider;
import me.madfix.cloudnet.webinterface.api.permission.PermissionProvider;
import me.madfix.cloudnet.webinterface.api.rest.RestfulAPIService;
import me.madfix.cloudnet.webinterface.api.setup.SetupHandler;
import me.madfix.cloudnet.webinterface.api.update.UpdateHandler;
import me.madfix.cloudnet.webinterface.api.user.UserProvider;
import me.madfix.cloudnet.webinterface.logging.WebInterfaceLogger;
import me.madfix.cloudnet.webinterface.services.CloudNetService;
import me.madfix.cloudnet.webinterface.services.ConfigurationService;
import me.madfix.cloudnet.webinterface.services.DatabaseService;
import me.madfix.cloudnet.webinterface.updates.Update_1_9;


/**
 * Entry class for CloudNet v2 module system
 * @version 1.0.0
 * @since 1.11.5
 */
public final class WebInterface extends CoreModule {

    private ConfigurationService configurationService;
    private DatabaseService databaseService;
    private CloudNetService cloudNetService;
    private RestfulAPIService restfulAPIService;

    private final Gson gson = new Gson();
    private WebInterfaceLogger logger;

    private SetupHandler setupHandler;
    private UpdateHandler updateHandler;

    private UserProvider userProvider;
    private PermissionProvider permissionProvider;
    private GroupProvider groupProvider;


    @Override public void onLoad() {
        Sentry.init("https://08a4da2c621c4b8f9f16f345d829825b@o419044.ingest.sentry.io/5327070");
        Sentry.getContext()
              .setUser(new UserBuilder().setIpAddress(CloudNet.getInstance()
                                                              .getConfig()
                                                              .getConfig()
                                                              .getString("server.hostaddress"))
                                        .setId(CloudNet.getInstance()
                                                       .getConfig()
                                                       .getConfig()
                                                       .getString("cloudnet-statistics.uuid"))
                                        .build());
        this.logger = new WebInterfaceLogger();
        this.configurationService = new ConfigurationService(this);
        if (!this.configurationService.loadConfigurationFile()) {
            this.logger.severe("[100] No configuration file was found with the name: interface.json.");
            this.logger.severe("[100] Web interface will not start!");
            this.logger.severe(
                    "[100] Please create your configuration file under X and follow the instructions on the website. ");
        }
        if (this.configurationService.getOptionalInterfaceConfiguration().isPresent()) {
            this.databaseService = new DatabaseService(this);
        }
    }

    @Override public void onBootstrap() {
        if (this.configurationService.getOptionalInterfaceConfiguration().isPresent()) {
            this.setupHandler = new SetupHandler(this);
            this.setupHandler.setupPreSql();
            this.setupHandler.setupPostSql();
            this.permissionProvider = new PermissionProvider(this);
            this.userProvider = new UserProvider(this);
            this.setupHandler.setupPreAdminUser();
            this.updateHandler = new UpdateHandler(this);
            this.updateHandler.addTask(1, new Update_1_9());
            this.updateHandler.callUpdates();
            this.groupProvider = new GroupProvider(this);
            this.setupHandler.setupPreAdminGroup();
            this.cloudNetService = new CloudNetService(this);
            this.restfulAPIService = new RestfulAPIService(this);
            this.restfulAPIService.startRestApi();
        }


    }

    @Override public void onShutdown() {
    }

    public WebInterfaceLogger getLogger() {
        return logger;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public Gson getGson() {
        return gson;
    }

    public CloudNetService getCloudNetService() {
        return cloudNetService;
    }

    public PermissionProvider getPermissionProvider() {
        return permissionProvider;
    }

    public UserProvider getUserProvider() {
        return userProvider;
    }

    public GroupProvider getGroupProvider() {
        return groupProvider;
    }

}
