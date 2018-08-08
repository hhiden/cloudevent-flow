package io.streamzi.openshift.dataflow.model.deployment;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class holds a deployment for a single processor node.
 * @author hhiden
 */
public class ProcessorDeployment {
    /** ID of the "cloud" where this deployment is targetted */
    private String hostId;
    
    /** Number of replicaas to deploy */
    private Integer replicas = 1;

    public ProcessorDeployment() {

    }
    
    public ProcessorDeployment(String hostId, int replicas) {
        this.hostId = hostId;
        this.replicas = replicas;
    }

    @JsonIgnore
    public boolean isConfigured() {
        return (hostId!=null && replicas!=null);
    }

    public String getHostId() {
        return hostId;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }
    
    public int incrementReplicas(int increase){
        return replicas+=increase;
    }
}
