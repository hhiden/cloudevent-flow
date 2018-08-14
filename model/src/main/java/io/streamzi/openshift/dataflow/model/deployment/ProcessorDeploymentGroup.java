package io.streamzi.openshift.dataflow.model.deployment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds a group of deployment preferences for a ProcessorNode.
 * @author hhiden
 */
public class ProcessorDeploymentGroup {
    private String processorUuid;
    private Map<String, ProcessorDeployment> deployments = new HashMap<>();

    public ProcessorDeploymentGroup() {
    }


    public ProcessorDeploymentGroup(String processorUuid) {
        this.processorUuid = processorUuid;
    }

    public Collection<ProcessorDeployment> getLocations() {
        return deployments.values();
    }

    public void setLocations(Collection<ProcessorDeployment> deployments) {
        for(ProcessorDeployment d : deployments){
            this.deployments.put(d.getHostId(), d);
        }
    }

    public void setProcessorUuid(String processorUuid) {
        this.processorUuid = processorUuid;
    }
    
    public void clearDeployments(){
        deployments.clear();
    }
    
    public void addDeployment(String hostId, int replicas){
        if(!deployments.containsKey(hostId)){
            // Create a new deployment
            deployments.put(hostId, new ProcessorDeployment(hostId, replicas));
        } else {
            // Add to an existing deployment
            if(deployments.get(hostId).incrementReplicas(replicas)==0){
                // None left, remove
                deployments.remove(hostId);
            }
        }
    }
    
    public String getProcessorUuid(){
        return processorUuid;
    }
    
    @JsonIgnore
    public boolean isConfigured(){
        if(deployments.size()==0){
            return false;
        } else {
            // Not congigured if any deployments are not setup
            for(ProcessorDeployment d : deployments.values()){
                if(!d.isConfigured()){
                    return false;
                }
            }
            return true;
        }
    }
}
