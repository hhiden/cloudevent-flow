package io.streamzi.openshift.dataflow.model.deployment;

/**
 * This class holds a deployment for a single processor node.
 * @author hhiden
 */
public class ProcessorDeployment {
    /** ID of the "cloud" where this deployment is targetted */
    private String hostId = "";
    
    /** Number of replicaas to deploy */
    private int replicas = 1;

    public ProcessorDeployment() {
    }
    
    public ProcessorDeployment(String hostId, int replicas) {
        this.hostId = hostId;
        this.replicas = replicas;
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
