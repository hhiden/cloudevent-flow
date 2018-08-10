package io.streamzi.openshift.dataflow.model.serialization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.streamzi.openshift.dataflow.model.ProcessorFlow;
import io.streamzi.openshift.dataflow.model.ProcessorLink;
import io.streamzi.openshift.dataflow.model.ProcessorNode;
import io.streamzi.openshift.dataflow.model.ProcessorOutputPort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serialized form of flow
 * @author hhiden
 */
public class SerializedFlow {
    @JsonIgnore
    protected ProcessorFlow flow;
    
    protected String name;
    protected List<SerializedNode> nodes = new ArrayList<>();
    protected List<SerializedLink> links = new ArrayList<>();
    protected Map<String, String> settings = new HashMap<>();
    protected Map<String, String> globalSettings = new HashMap<>();
    protected Map<String, String> deployments = new HashMap<>();

    public SerializedFlow() {
    }

    public SerializedFlow(ProcessorFlow flow) {
        this.flow = flow;
        this.name = flow.getName();
        for(ProcessorNode n : flow.getNodes()){
            nodes.add(new SerializedNode(n));
            
            for(ProcessorOutputPort output : n.getOutputs().values()){
                for(ProcessorLink link : output.getLinks()){
                    links.add(new SerializedLink(link));
                }
            }
        }
        
        for(String key : flow.getSettings().keySet()){
            settings.put(key, flow.getSettings().get(key));
        }
        
        for(String key : flow.getGlobalSettings().keySet()){
            globalSettings.put(key, flow.getGlobalSettings().get(key));
        }
        
        for(String key : flow.getDeployments().keySet()){
            deployments.put(key, flow.getDeployments().get(key));
        }
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

    public Map<String, String> getGlobalSettings() {
        return globalSettings;
    }

    public void setGlobalSettings(Map<String, String> globalSettings) {
        this.globalSettings = globalSettings;
    }

    public List<SerializedNode> getNodes(){
        return nodes;
    }

    public List<SerializedLink> getLinks() {
        return links;
    }

    public void setLinks(List<SerializedLink> links) {
        this.links = links;
    }

    public Map<String, String> getDeployments() {
        return deployments;
    }

    public void setDeployments(Map<String, String> deployments) {
        this.deployments = deployments;
    }
    
    
}