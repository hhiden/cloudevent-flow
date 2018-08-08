package io.streamzi.openshift.dataflow.tests;

import io.streamzi.openshift.dataflow.model.ProcessorConstants;
import io.streamzi.openshift.dataflow.model.ProcessorFlow;
import io.streamzi.openshift.dataflow.model.ProcessorInputPort;
import io.streamzi.openshift.dataflow.model.ProcessorNode;
import io.streamzi.openshift.dataflow.model.ProcessorOutputPort;
import io.streamzi.openshift.dataflow.model.deployment.ProcessorDeploymentMap;
import io.streamzi.openshift.dataflow.model.deployment.ProcessorDeploymentMapBuilder;
import io.streamzi.openshift.dataflow.model.serialization.ProcessorDeploymentMapWriter;
import io.streamzi.openshift.dataflow.model.serialization.ProcessorDeploymentMapReader;
import io.streamzi.openshift.dataflow.model.serialization.ProcessorDeploymentMapWriter;
import io.streamzi.openshift.dataflow.model.serialization.ProcessorFlowWriter;
import io.streamzi.openshift.dataflow.partitioning.ProcessorFlowPartitioner;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests building a deployment config map
 *
 * @author hhiden
 */
public class DeploymentBuilderTest {

    private static final Logger logger = Logger.getLogger(DeploymentBuilderTest.class.getName());

    @BeforeClass
    public static void setupClass() {

    }

    @Before
    public void setup() {

    }

    @Test
    public void testDeploymentBuilder() throws Exception {
        logger.info("Testing deployment builder");
        ProcessorFlow flow = new ProcessorFlow();
        flow.getGlobalSettings().put(ProcessorConstants.KAFKA_BOOTSTRAP_SERVERS, "my-cluster-kafka:9092");
        flow.getGlobalSettings().put(ProcessorConstants.INTERCONNECT_BROKER, "interconnect-broker:1234");

        ProcessorNode kafkaInput = new ProcessorNode();
        kafkaInput.setTemplateName("kafka-input");
        kafkaInput.addOutput(new ProcessorOutputPort("events", "kafka"));

        ProcessorNode kafkaFilter = new ProcessorNode();
        kafkaFilter.setTemplateName("kafka-filter");
        kafkaFilter.addInput(new ProcessorInputPort("input", "kafka"));
        kafkaFilter.addOutput(new ProcessorOutputPort("output", "amq"));

        ProcessorNode kafkaPublish = new ProcessorNode();
        kafkaPublish.setTemplateName("kafka-publish");
        kafkaPublish.addInput(new ProcessorInputPort("events", "amq"));

        flow.setName("FilterFlow");
        flow.addProcessorNode(kafkaInput);
        flow.addProcessorNode(kafkaFilter);
        flow.addProcessorNode(kafkaPublish);

        flow.linkNodes(kafkaInput, "events", kafkaFilter, "input");
        flow.linkNodes(kafkaFilter, "output", kafkaPublish, "events");
        
        // Build a no-deployment map
        ProcessorDeploymentMap noDeploymentMap = new ProcessorDeploymentMapBuilder(flow).build();
        logger.info(new ProcessorDeploymentMapWriter(noDeploymentMap).writeToIndentedJsonString());
        
        // Build a default map
        ProcessorDeploymentMap map = new ProcessorDeploymentMapBuilder(flow)
                .addDeploymentHost("openshift")
                .addDeploymentHost("azure")
                .addDeployment(kafkaInput, "openshift")
                .addDeployment(kafkaFilter, "openshift")
                .addDeployment(kafkaFilter, "azure")
                .addDeployment(kafkaPublish, "openshift")
                .build();
        
        // Print
        ProcessorDeploymentMapWriter writer = new ProcessorDeploymentMapWriter(map);
        String json = writer.writeToIndentedJsonString();
        logger.info(json);
        
        ProcessorDeploymentMapReader reader = new ProcessorDeploymentMapReader();
        ProcessorDeploymentMap recreatedMap = reader.read(json);
        String recreatedJson = new ProcessorDeploymentMapWriter(recreatedMap).writeToIndentedJsonString();
        logger.info(recreatedJson);
        Assert.assertEquals(json, recreatedJson);
        
        // Build the deployment flows
        ProcessorFlowPartitioner partitioner = new ProcessorFlowPartitioner(flow, map);
        Collection<ProcessorFlow> deployments = partitioner.partition();
        for(ProcessorFlow f : deployments){
            logger.info(new ProcessorFlowWriter(f).writeToIndentedJsonString());
        }
    }
}
