/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.listener;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.ProjectMain;
import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnetcore.api.event.server.ScreenInfoEvent;

import java.util.ArrayList;

public class ScreenSessionEvent implements IEventListener<ScreenInfoEvent> {
    private final ProjectMain projectMain;
    ScreenSessionEvent(ProjectMain projectMain) {
        this.projectMain = projectMain;
    }

    @Override
    public void onCall(ScreenInfoEvent screenInfoEvent) {
        screenInfoEvent.getScreenInfos().forEach(t->{
            if (this.projectMain.getScreenInfos().containsKey(t.getServiceId().getServerId())) {
                this.projectMain.getScreenInfos().get(t.getServiceId().getServerId()).add(t.getLine());
            }else{
                this.projectMain.getScreenInfos().put(t.getServiceId().getServerId(),new ArrayList<>());
                this.projectMain.getScreenInfos().get(t.getServiceId().getServerId()).add(t.getLine());
            }
        });
    }

}
