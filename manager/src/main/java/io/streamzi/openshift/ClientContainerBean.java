package io.streamzi.openshift;

import com.sun.prism.impl.PrismSettings;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.streamzi.openshift.dataflow.model.deployment.DeploymentHost;
import io.streamzi.openshift.dataflow.model.serialization.DeploymentHostListReader;
import io.streamzi.openshift.dataflow.model.serialization.DeploymentHostListWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import java.util.logging.Logger;


/**
 * Contains a Openshift client
 *
 * @author hhiden
 */
@Singleton(name = "ClientContainerBean")
public class ClientContainerBean implements ClientContainer {

    private static final Logger logger = Logger.getLogger(ClientContainerBean.class.getName());
    private OpenShiftClient osClient;

    @PostConstruct
    public void init() {

        logger.info("Starting ClientContainer");
        
        // Create a client
        osClient = new DefaultOpenShiftClient();
        logger.info("URL:" + osClient.getOpenshiftUrl().toString());
        logger.info("Namespace: " + osClient.getNamespace());
        
        // Create a config map with the supported "clouds"
        // TODO: This will need to be updated when we can 
        // register actual deployment targets
        ConfigMap cm = osClient.configMaps().inNamespace(osClient.getNamespace()).withName("hosts.cm").get();
        try {
            if(cm==null){
                // No default hosts
                logger.info("Creating a new hosts.cm list");
                List<DeploymentHost> hosts = new ArrayList<>();
                hosts.add(new DeploymentHost("local", osClient.getOpenshiftUrl().toString(), ""));
                DeploymentHostListWriter writer = new DeploymentHostListWriter(hosts);
                cm = new ConfigMapBuilder()
                        .withNewMetadata()
                        .withName("hosts.cm")
                        .withNamespace(osClient.getNamespace())
                        .addToLabels("streamzi.io/kind", "hostlist")
                        .endMetadata()
                        .addToData("hosts", writer.writeToIndentedJsonString())
                        .build();
                osClient.configMaps().createOrReplace(cm);
            } else {
                logger.info("Hosts list already exists");
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Cannot create list of deployment hosts: " + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void cleanup() {
        logger.info("Stopping ClientContainer");
    }

    @Override
    public String getNamespace() {
        return getOSClient().getNamespace();
    }

    @Override
    public OpenShiftClient getOSClient() {
        return osClient;
    }

    @Override
    public List<DeploymentHost> listDeploymentHosts() {
        try {
            ConfigMap cm = osClient.configMaps().inNamespace(osClient.getNamespace()).withName("hosts.cm").get();
            if(cm!=null){
                String listJson = cm.getData().get("hosts");
                DeploymentHostListReader reader = new DeploymentHostListReader();
                return reader.readFromJsonString(listJson);
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error getting host list: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    
}
