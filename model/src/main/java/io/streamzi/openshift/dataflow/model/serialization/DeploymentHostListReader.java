package io.streamzi.openshift.dataflow.model.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.streamzi.openshift.dataflow.model.deployment.DeploymentHost;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Reads a list of deployment hosts from JSON
 * @author hhiden
 */
public class DeploymentHostListReader {
    public List<DeploymentHost> readFromJsonString(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<HashMap> hosts = mapper.readValue(json, List.class);      
        ArrayList<DeploymentHost> results = new ArrayList<>();
        for(HashMap hm : hosts){
            results.add(new DeploymentHost(
                    hm.get("id").toString(),
                    hm.get("url").toString(),
                    hm.get("secretId").toString()));
                    
        }
        return results;
    }
}
