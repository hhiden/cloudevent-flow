package io.streamzi.openshift.dataflow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a connection "port" that can send / receive events
 * @author hhiden
 */
public abstract class ProcessorPort {
    protected String name;
    protected ProcessorNode parent;
    protected List<ProcessorLink> links = new ArrayList<>();
    protected String transportType = "default";

    public ProcessorPort() {
    }

    public ProcessorPort(String name, String transportType){
        this.name = name;
        this.transportType = transportType;
    }
    
    public ProcessorPort(String name) {
        this.name = name;
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProcessorLink> getLinks() {
        return links;
    }

    public void setLinks(List<ProcessorLink> links) {
        this.links = links;
    }

    public ProcessorNode getParent() {
        return parent;
    }

    public void setParent(ProcessorNode parent) {
        this.parent = parent;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }
    
    
    public abstract void addLink(ProcessorLink link);
    
}
