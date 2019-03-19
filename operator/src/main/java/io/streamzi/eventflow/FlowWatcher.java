package io.streamzi.eventflow;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.streamzi.eventflow.crds.DoneableFlow;
import io.streamzi.eventflow.crds.Flow;
import io.streamzi.eventflow.crds.FlowList;
import io.streamzi.eventflow.providers.knative.v02.KNativeTargetState;

import java.util.logging.Logger;

public class FlowWatcher implements Watcher<Flow>, Runnable {
    private static OpenShiftClient osClient;    
    
    private static final Logger logger = Logger.getLogger(FlowWatcher.class.getName());

    private FlowController controller;

    public FlowWatcher(FlowController controller) {
        logger.info("FlowWatcher startup");
        osClient = new DefaultOpenShiftClient();        
        this.controller = controller;
        controller.setClient(osClient);
        
        try {
            KNativeTargetState ts = new KNativeTargetState();
            ts.setClient(osClient);
            ts.debugPrint();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void eventReceived(Action action, Flow flow) {
        final ObjectMeta metadata = flow.getMetadata();

        final String name = metadata.getName();

        logger.fine("Flow watch received event " + action + " on Custom Resource " + name);

        try {
            switch (action) {
                case ADDED:
                    controller.onAdded(flow);
                    break;
                case MODIFIED:
                    controller.onModified(flow);
                    break;
                case DELETED:
                    controller.onDeleted(flow);
                    break;
                case ERROR:
                    logger.warning("Watch received action=ERROR for Flow " + name);
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }


    /**
     * Thread that's running the Flow Watcher
     */
    @Override
    public void run() {
        logger.info("Starting FlowWatcher");
        final CustomResourceDefinition flowCRD = osClient.customResourceDefinitions().withName("flows.streamzi.io").get();
        osClient.customResources(flowCRD, Flow.class, FlowList.class, DoneableFlow.class).inNamespace(osClient.getNamespace()).watch(this);
    }

    @Override
    public void onClose(KubernetesClientException e) {
        logger.info("Closing Watcher: " + this);
        logger.info(e.getMessage());
    }

}
