package io.streamzi.openshift;


import io.fabric8.openshift.client.OpenShiftClient;
import io.streamzi.openshift.dataflow.model.deployment.DeploymentHost;
import java.util.List;

import javax.ejb.Local;

/**
 * Methods for accessing the openshift client, looking up storage dirs etc.
 *
 * @author hhiden
 */
@Local
public interface ClientContainer {
    public String getNamespace();
    public OpenShiftClient getOSClient();
    public List<DeploymentHost> listDeploymentHosts();
    
}
