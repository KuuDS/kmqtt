package me.kuuds.kmqtt.server.event;

import java.util.List;

import me.kuuds.kmqtt.common.state.Event;
import me.kuuds.kmqtt.server.pojo.Topic;

public class UnsubscribeEvent implements Event {

    private final List<Topic> topics;

    public UnsubscribeEvent(List<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public Object getObject() {
        return topics;
    }

    public List<Topic> getTopics() {
        return topics;
    }

}
