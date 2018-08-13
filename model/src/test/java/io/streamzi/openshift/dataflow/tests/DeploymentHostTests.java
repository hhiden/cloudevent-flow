package io.streamzi.openshift.dataflow.tests;

import io.streamzi.openshift.dataflow.model.deployment.DeploymentHost;
import io.streamzi.openshift.dataflow.model.serialization.DeploymentHostListReader;
import io.streamzi.openshift.dataflow.model.serialization.DeploymentHostListWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests reading / writing lists of deployment hosts
 * @author hhiden
 */

public class DeploymentHostTests {
    private static final Logger logger = Logger.getLogger(DeploymentBuilderTest.class.getName());
    
    @Test
    public void test() throws Exception {
        List<DeploymentHost> hosts = new ArrayList<>();
        hosts.add(new DeploymentHost("openshift", "127.0.0.1:8441/api", "credentials"));
        hosts.add(new DeploymentHost("azure", "10.5.1.3:1235/azure", "azcredentials"));
        DeploymentHostListWriter writer = new DeploymentHostListWriter(hosts);
        String json = writer.writeToIndentedJsonString();
        logger.info(json);
        
        DeploymentHostListReader reader = new DeploymentHostListReader();
        List<DeploymentHost> retreived = reader.readFromJsonString(json);
        Assert.assertEquals(retreived.size(), 2);
    }
}
