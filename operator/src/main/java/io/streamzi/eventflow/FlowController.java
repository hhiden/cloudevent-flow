package io.streamzi.eventflow;

import io.fabric8.openshift.client.OpenShiftClient;
import io.streamzi.eventflow.crds.Flow;
import io.streamzi.eventflow.serialization.SerializedFlow;



import java.util.logging.Level;
import java.util.logging.Logger;

public class FlowController {
    private Class targetStateClass;
    private static Logger logger = Logger.getLogger(FlowController.class.getName());
    private OpenShiftClient client;
    
    public FlowController(Class targetStateClass) {
        this.targetStateClass = targetStateClass;
    }

    public void setClient(OpenShiftClient client) {
        this.client = client;
    }

    public OpenShiftClient getClient() {
        return client;
    }

    public void onAdded(Flow flow) {
        createOrUpdate(flow);
    } 

    public void onModified(Flow flow) {
        createOrUpdate(flow);
    }

    public void onDeleted(Flow flow) {
        delete(flow);
    }

    private void createOrUpdate(Flow customResource) {
        try {
            SerializedFlow flow = customResource.getSpec();
            logger.info("Flow Parsed OK");

            TargetStateProvider target = (TargetStateProvider)targetStateClass.getConstructor().newInstance();
            target.setFlow(flow);
            target.setClient(client);
            target.build();
            logger.info("Done");

        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Error parsing JSON flow data: " + e.getMessage(), e);
        }
    }

    private void delete(Flow customResource) {

        try {
            SerializedFlow flow = customResource.getSpec();
            logger.info("Flow Parsed OK");
           
            // Remove everything from the local namespace
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error parsing JSON flow data: " + e.getMessage(), e);
        }
    }
}
