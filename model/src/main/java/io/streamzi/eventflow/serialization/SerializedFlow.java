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
    
    public SerializedNode findNode(String nodeUUID){
        for(SerializedNode n : nodes){
            if(n.getUuid().equals(nodeUUID)){
                return n;    
            }
        }
        return null;
    }

    public List<SerializedLink> findOutputLinksFromNode(String nodeUUID){
        SerializedNode n = findNode(nodeUUID);
        if(n!=null){
            List<SerializedLink> results = new ArrayList<>();
            for(String name : n.getOutputs()){
                for(SerializedLink l : links){
                    if(l.getSourceUuid().equals(nodeUUID)){
                        results.add(l);
                    }
                }
            }
            return results;
        } else {
            return new ArrayList<>();
        }
    }
    
    
    public String findSingleConnectedOutputFromNode(String nodeUUID){
        SerializedNode n = findNode(nodeUUID);
        List<SerializedLink> links = findOutputLinksFromNode(nodeUUID);
        if(links.size()>0 && n.getOutputs().size()==1){
            return n.getOutputs().get(0);
        } else {
            return null;
        }
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