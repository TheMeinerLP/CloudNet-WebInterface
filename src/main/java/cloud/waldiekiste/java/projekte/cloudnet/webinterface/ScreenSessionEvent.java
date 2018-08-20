package cloud.waldiekiste.java.projekte.cloudnet.webinterface;

import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnet.lib.server.screen.ScreenInfo;
import de.dytanic.cloudnetcore.api.event.server.ScreenInfoEvent;

import java.util.ArrayList;

public class ScreenSessionEvent implements IEventListener<ScreenInfoEvent> {
    private ProjectMain projectMain;
    public ScreenSessionEvent(ProjectMain projectMain) {
        this.projectMain = projectMain;
    }

    @Override
    public void onCall(ScreenInfoEvent screenInfoEvent) {
        screenInfoEvent.getScreenInfos().forEach(t->{
            ScreenInfo info = t;
            if (getProjectMain().getScreenInfos().containsKey(info.getServiceId().getServerId())) {
                getProjectMain().getScreenInfos().get(info.getServiceId().getServerId()).add(info.getLine());
            }else{
                getProjectMain().getScreenInfos().put(info.getServiceId().getServerId(),new ArrayList<>());
                getProjectMain().getScreenInfos().get(info.getServiceId().getServerId()).add(info.getLine());
            }
        });
    }

    public ProjectMain getProjectMain() {
        return projectMain;
    }
}
