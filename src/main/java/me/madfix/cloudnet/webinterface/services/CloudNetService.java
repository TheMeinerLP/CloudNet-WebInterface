package me.madfix.cloudnet.webinterface.services;

import me.madfix.cloudnet.webinterface.WebInterface;

public class CloudNetService {

    private final MobService mobService;
    private final SignService signService;
    private final ProxyGroupService proxyGroupService;
    private final ServerGroupService serverGroupService;

    public CloudNetService(WebInterface webInterface) {
        this.mobService = new MobService(webInterface);
        this.signService = new SignService(webInterface);
        proxyGroupService = new ProxyGroupService(webInterface);
        serverGroupService = new ServerGroupService(webInterface);
    }

    public MobService getMobService() {
        return mobService;
    }

    public SignService getSignService() {
        return signService;
    }

    public ProxyGroupService getProxyGroupService() {
        return proxyGroupService;
    }

    public ServerGroupService getServerGroupService() {
        return serverGroupService;
    }
}
