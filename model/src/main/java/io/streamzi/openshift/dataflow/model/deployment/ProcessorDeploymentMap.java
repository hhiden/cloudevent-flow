package io.streamzi.openshift.dataflow.model.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class holds a map describing where the ProcessorNodes for a ProcessorFlow
 * should be deployed.
 * @author hhiden
 */
public class ProcessorDeploymentMap {


    private Map<String, ProcessorDeploymentGroup> processorGroups = new HashMap<>();
    private List<String> hosts = new ArrayList<>();    
    
    
    public Collection<ProcessorDeploymentGroup> getDeployments() {
        return processorGroups.values();
    }

    public void setDeployments(Collection<ProcessorDeploymentGroup> processorGroups) {
        for(ProcessorDeploymentGroup g : processorGroups){
            this.processorGroups.put(g.getProcessorUuid(), g);
        }
    }

    public List<String> getHosts() {
        return hosts;
    }

    public boolean hostContainsDeployments(String hostId){
        for(ProcessorDeploymentGroup g : processorGroups.values()){
            for(ProcessorDeployment d : g.getLocations()){
                if(d.getHostId().equals(hostId)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }
    
    

    public void removeDeploymentsForProcessor(String processorUuid){
        processorGroups.remove(processorUuid);
    }
    
    public ProcessorDeploymentGroup getDeploymentsForProcessor(String processorUuid){
        return processorGroups.get(processorUuid);
    }
    
    public void addDeploymentsForProcessor(String processorUuid, ProcessorDeploymentGroup group){
        processorGroups.put(processorUuid, group);
    }
    
    public Iterator<ProcessorDeploymentGroup> groupsIterator(){
        return processorGroups.values().iterator();
    }
}