package io.streamzi.eventflow.serialization;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.api.model.KubernetesResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serialized form of flow
 *
 * @author hhiden
 */
@JsonDeserialize(
        using = JsonDeserializer.None.class
)
public class SerializedFlow implements KubernetesResource {
    private String name;

    private List<SerializedNode> nodes = new ArrayList<>();
    private List<SerializedLink> links = new ArrayList<>();
    private Map<String, String> settings = new HashMap<>();

    public SerializedFlow() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    public List<SerializedNode> getNodes() {
        return nodes;
    }

    public List<SerializedLink> getLinks() {
        return links;
    }

    public void setLinks(List<SerializedLink> links) {
        this.links = links;
    }

    public void setNodes(List<SerializedNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "SerializedFlow{" +
                ", name='" + name + '\'' +
                ", nodes=" + nodes +
                ", links=" + links +
                ", settings=" + settings +
                '}';
    }
}