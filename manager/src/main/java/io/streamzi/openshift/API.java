package io.streamzi.openshift;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.api.model.Pod;
import io.streamzi.openshift.dataflow.model.ProcessorConstants;
import io.streamzi.openshift.dataflow.model.ProcessorFlow;

import io.streamzi.openshift.dataflow.model.deployment.DeploymentHost;
import io.streamzi.openshift.dataflow.model.deployment.ProcessorDeploymentMap;
import io.streamzi.openshift.dataflow.model.deployment.ProcessorDeploymentMapBuilder;
import io.streamzi.openshift.dataflow.model.serialization.ProcessorDeploymentMapReader;
import io.streamzi.openshift.dataflow.model.serialization.ProcessorDeploymentMapWriter;

import io.streamzi.openshift.dataflow.model.template.ProcessorNodeTemplate;
import io.streamzi.openshift.dataflow.model.serialization.ProcessorFlowReader;
import io.streamzi.openshift.dataflow.model.serialization.ProcessorFlowWriter;
import io.streamzi.openshift.dataflow.model.serialization.ProcessorTemplateYAMLReader;
import io.streamzi.openshift.dataflow.model.serialization.ProcessorTemplateYAMLWriter;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author hhiden
 */
@ApplicationScoped
@Path("/api")
public class API {

    private static final Logger logger = Logger.getLogger(API.class.getName());

    @EJB(beanInterface = ClientContainer.class)
    private ClientContainer container;

    private final String bootstrapServersDefault = "my-cluster-kafka:9092";
    private final String brokerUrlDefault = "amqp://dispatch.myproject.svc:5672";

    @GET
    @Path("/pods")
    @Produces("application/json")
    public List<String> listPods() {
        List<Pod> pods = container.getOSClient().pods().inNamespace(container.getNamespace()).list().getItems();
        List<String> results = new ArrayList<>();
        for (Pod p : pods) {
            results.add(p.getMetadata().getName());
        }
        return results;
    }

    @GET
    @Path("/dataflows/{name}")
    @Produces("application/json")
    public String getProcessorFlow(@PathParam("name") String name) {
        ConfigMap map = container.getOSClient().configMaps().withName(name).get();
        if (map != null) {
            return map.getData().get("flow");
        } else {
            return "";
        }
    }
    

    @GET
    @Path("/dataflows")
    @Produces("application/json")
    public List<String> listFlows() {
        List<String> results = new ArrayList<>();

        // Find all of the config maps with the streamzi/kind flow labels
        ConfigMapList configMapList = container.getOSClient().configMaps().inNamespace(container.getNamespace()).withLabel("streamzi.io/kind", "flow").list();

        for (ConfigMap cm : configMapList.getItems()) {
            results.add(cm.getMetadata().getName());
        }

        return results;
    }

    @GET
    @Path("/processors")
    @Produces("application/json")
    public List<String> listProcessors() {
        List<String> results = new ArrayList<>();

        ConfigMapList configMapList = container.getOSClient().configMaps().inNamespace(container.getNamespace()).withLabel("streamzi.io/kind", "processor").list();
        List<ConfigMap> processorConfigMaps = configMapList.getItems();
        for (ConfigMap cm : processorConfigMaps) {
            try {
                String templateYaml = cm.getData().get("template");
                final ProcessorNodeTemplate template = ProcessorTemplateYAMLReader.readTemplateFromString(templateYaml);
                final ProcessorTemplateYAMLWriter writer = new ProcessorTemplateYAMLWriter(template);
                results.add(writer.writeToYAMLString());
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error reading template from config map: " + e.getMessage());
            }
        }
        return results;
    }

    /**
     * Upload a definition for a processor node
     */
    @POST
    @Path("/processors")
    @Consumes("application/json")
    public void postYaml(String yamlData) {
        logger.info(yamlData);
        try {
            ProcessorNodeTemplate template = ProcessorTemplateYAMLReader.readTemplateFromString(yamlData);
            logger.info("Valid template for image: " + template.getImageName());
            ProcessorTemplateYAMLWriter writer = new ProcessorTemplateYAMLWriter(template);

            // Write this to a config map
            ConfigMap cm = new ConfigMapBuilder()
                    .withNewMetadata()
                    .withName(template.getId() + ".cm")
                    .withNamespace(container.getNamespace())
                    .addToLabels("streamzi.io/kind", "processor")
                    .endMetadata()
                    .addToData("template", writer.writeToYAMLString())
                    .build();
            container.getOSClient().configMaps().createOrReplace(cm);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error parsing YAML data: " + e.getMessage(), e);
        }

    }

    /**
     * Upload a new flow to a ConfigMap
     */
    @POST
    @Path("/flows")
    @Consumes("application/json")
    public void postFlow(String flowJson) {
        logger.info(flowJson);
        try {
            ProcessorFlowReader reader = new ProcessorFlowReader();
            ProcessorFlow flow = reader.readFromJsonString(flowJson);
            logger.info("Flow Parsed OK");

            // Write this to a ConfigMap
            ProcessorFlowWriter writer = new ProcessorFlowWriter(flow);

            ConfigMap cm = new ConfigMapBuilder()
                    .withNewMetadata()
                    .withName(flow.getName() + ".cm")
                    .withNamespace(container.getNamespace())
                    .addToLabels("streamzi.io/kind", "flow")
                    .addToLabels("app", flow.getName())
                    .endMetadata()
                    .addToData("flow", writer.writeToIndentedJsonString())
                    .build();

            container.getOSClient().configMaps().createOrReplace(cm);

            logger.info("Flow written OK");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error parsing JSON flow data: " + e.getMessage(), e);
        }
    }

    @GET
    @Path("flows/{name}/deployment")
    @Produces("application/json")
    public String getDeploymentMap(@PathParam("name") String flowName) {
        String flowJson = getProcessorFlow(flowName + ".cm");
        if(flowJson!=null && !flowJson.isEmpty()){
            try {
                ConfigMap cm = container.getOSClient().configMaps().withName(flowName + "-deployment-map.cm").get();
                if (cm == null) {
                    // Need to create an empty one
                    ProcessorFlow flow = new ProcessorFlowReader().readFromJsonString(flowJson);
                    ProcessorDeploymentMapBuilder builder = new ProcessorDeploymentMapBuilder(flow);
                    cm = storeDeploymentMap(flowName, builder.build());
                } 

                return cm.getData().get("deployment");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error getting deployment map: " + e.getMessage(), e);
                return "";
            }
        } else {
            logger.log(Level.SEVERE, "No flow: " + flowName);
            return "";
        }
    }
    
    @GET
    @Path("/flows/{name}/defaultdeploymentmap")
    public String createDefaultDeploymentMap(@PathParam("name")String flowName){
        String flowJson = getProcessorFlow(flowName + ".cm");
        if(flowJson!=null && !flowJson.isEmpty()){
            try {
                ProcessorFlow flow = new ProcessorFlowReader().readFromJsonString(flowJson);
                
                ProcessorDeploymentMapBuilder builder = new ProcessorDeploymentMapBuilder(flow);
                Iterator<DeploymentHost> hosts = container.listDeploymentHosts().iterator();
                boolean firstHost = true;
                while(hosts.hasNext()){
                    DeploymentHost host = hosts.next();
                    builder.addDeploymentHost(host.getId());
                    if(firstHost){
                        builder.setDefaultDeploymentHost(host.getId());
                        firstHost = false;
                    }
                }
                
                builder.deployAllToDefaultHost();
                ProcessorDeploymentMap map = builder.build();
                storeDeploymentMap(flowName, map);
                
                
                return "";
            } catch (Exception e){
                logger.log(Level.SEVERE, "Error creating default deployment map: " + e.getMessage(), e);
                return "";
            }
        } else {
            logger.log(Level.SEVERE, "No flow found: " + flowName);
            return "";
        }  
    }
    

    private ConfigMap storeDeploymentMap(String flowName, ProcessorDeploymentMap map) throws Exception {
        ProcessorDeploymentMapWriter writer = new ProcessorDeploymentMapWriter(map);

        ConfigMap cm = new ConfigMapBuilder()
                .withNewMetadata()
                .withName(flowName + "-deployment-map.cm")
                .withNamespace(container.getNamespace())
                .addToLabels("streamzi.io/kind", "deploymentmap")
                .addToLabels("app", flowName)
                .endMetadata()
                .addToData("deployment", writer.writeToIndentedJsonString())
                .build();
        return container.getOSClient().configMaps().createOrReplace(cm);
    }

    @GET
    @Path("/flows/{name}/repartition")
    @Produces("application/json")
    public String repartitionFlow(@PathParam("name")String flowName) {
        // Get the flow
        
        // Get the deployment map
        
        // Drop the existing partitions
        
        // Build the partitions
        
        
        return "";
    }
    
    @POST
    @Path("/flows/{name}/deployment")
    @Consumes("application/json")
    public void postDeploymentMap(@PathParam("name") String flowName, String deploymentMapJson) {
        logger.info(deploymentMapJson);
        try {
            // Parse this to see if it's ok
            ProcessorDeploymentMapReader reader = new ProcessorDeploymentMapReader();
            ProcessorDeploymentMap deploymentMap = reader.read(deploymentMapJson);
            logger.info("Map Parsed OK");

            // Create a config map with this in
            storeDeploymentMap(flowName, deploymentMap);
            
            // Re-oartition
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error parsing deployment map: " + e.getMessage(), e);
        }

    }

    @GET
    @Path("/globalproperties")
    @Produces("application/json")
    public String getGlobalProperties() {
        final Properties props = new Properties();

        String bootstrapServers = EnvironmentResolver.get(ProcessorConstants.KAFKA_BOOTSTRAP_SERVERS);
        if (bootstrapServers != null && !bootstrapServers.equals("")) {
            props.put(ProcessorConstants.KAFKA_BOOTSTRAP_SERVERS, bootstrapServers);
        } else {
            props.put(ProcessorConstants.KAFKA_BOOTSTRAP_SERVERS, bootstrapServersDefault);
        }

        String brokerUrl = EnvironmentResolver.get("broker.url");
        if (brokerUrl != null && !brokerUrl.equals("")) {
            props.put(ProcessorConstants.INTERCONNECT_BROKER, brokerUrl);
        } else {
            props.put(ProcessorConstants.INTERCONNECT_BROKER, brokerUrlDefault);
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(props);
        } catch (JsonProcessingException e) {
            logger.severe(e.getMessage());
            return "{}";
        }
    }
    
    @GET
    @Path("/topics")
    @Produces("application/json")
    public List<String> listTopics(){
        ConfigMapList list = container.getOSClient().configMaps().withLabel("strimzi.io/kind", "topic").list();
        
        ArrayList<String> results = new ArrayList<>();
        for(ConfigMap cm : list.getItems()){
            if(cm.getMetadata().getLabels().containsKey("streamzi.io/source")){
                // This is one of ours - add it if it wasn't autocreated
                String source = cm.getMetadata().getLabels().get("streamzi.io/source");
                if(source==null || source.isEmpty() || !source.equalsIgnoreCase("autocreated")){
                    results.add(cm.getMetadata().getName());
                }
            } else {
                results.add(cm.getData().get("name"));
            }
        }
        return results;

    }
    
    @Path("/deploymenttargets")
    @Produces("application/json")
    public String[] listDeploymentTargets(){
        return null;
    }

    @POST
    @Path("deploymenttargets")
    @Consumes("application/json")
    public void addDeploymentTarget(String target){
        // Add the target to the deployments map 

    }
}
