package me.madfix.projects.java.cloudnet.webinterface.listener;

import me.madfix.projects.java.cloudnet.webinterface.ProjectMain;
import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnetcore.api.event.server.ScreenInfoEvent;
import java.util.ArrayList;

public final class ScreenSessionListener implements IEventListener<ScreenInfoEvent> {

  private final ProjectMain projectMain;

  public ScreenSessionListener(ProjectMain projectMain) {
    this.projectMain = projectMain;
  }

  @Override
  public void onCall(ScreenInfoEvent screenInfoEvent) {
    screenInfoEvent.getScreenInfos().forEach(t -> {
      if (this.projectMain.getScreenInfos().containsKey(t.getServiceId().getServerId())) {
        this.projectMain.getScreenInfos().get(t.getServiceId().getServerId()).add(t.getLine());
      } else {
        this.projectMain.getScreenInfos().put(t.getServiceId().getServerId(), new ArrayList<>());
        this.projectMain.getScreenInfos().get(t.getServiceId().getServerId()).add(t.getLine());
      }
    });
  }
}