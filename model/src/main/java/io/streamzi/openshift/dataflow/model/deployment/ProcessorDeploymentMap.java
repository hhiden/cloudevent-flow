package io.streamzi.openshift.dataflow.model.deployment;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds a map describing where the ProcessorNodes for a ProcessorFlow
 * should be deployed.
 * @author hhiden
 */
public class ProcessorDeploymentMap {
    /** A group of deployment locations for each processor in a flow */
    private Map<String, ProcessorDeploymentGroup> processorGroups = new HashMap<>();
    
}