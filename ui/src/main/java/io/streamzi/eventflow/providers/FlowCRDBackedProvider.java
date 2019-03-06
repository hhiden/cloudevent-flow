/*
 * Copyright 2019 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamzi.eventflow.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.streamzi.eventflow.APIStartup;
import io.streamzi.eventflow.EventFlowAPIProvider;
import io.streamzi.eventflow.crds.DoneableFlow;
import io.streamzi.eventflow.crds.Flow;
import io.streamzi.eventflow.crds.FlowList;
import io.streamzi.eventflow.serialization.SerializedFlow;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This basic abstract providers uses the Flow CRD to store flow data
 * in K8S
 * @author hugo
 */
public abstract class FlowCRDBackedProvider implements EventFlowAPIProvider {
    private static final Logger logger = Logger.getLogger(FlowCRDBackedProvider.class.getName());
    private ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public List<String> getFlows() {
        final CustomResourceDefinition flowCRD = APIStartup.client().customResourceDefinitions().withName("flows.streamzi.io").get();
        if (flowCRD == null) {
            logger.severe("Can't find Flow CRD");
            return Collections.emptyList();
        }

        return APIStartup.client().customResources(
                flowCRD,
                Flow.class,
                FlowList.class,
                DoneableFlow.class)
                .inNamespace(APIStartup.client().getNamespace()).list().getItems().stream()
                .map(flow -> flow.getMetadata().getName())
                .collect(Collectors.toList());
    }

    @Override
    public SerializedFlow getFlow(String name) {
        final CustomResourceDefinition flowCRD = APIStartup.client().customResourceDefinitions().withName("flows.streamzi.io").get();
        if (flowCRD == null) {
            logger.severe("Can't find Flow CRD");
            return new SerializedFlow();
        }

        SerializedFlow flow = APIStartup.client().customResources(
                flowCRD,
                Flow.class,
                FlowList.class,
                DoneableFlow.class)
                .inNamespace(APIStartup.client().getNamespace())
                .withName(name).get().getSpec();
        return flow;

    }

    @Override
    public SerializedFlow postFlow(SerializedFlow flow) {

        try {

            logger.info("Flow Parsed OK");

            flow.setName(flow.getName()
                    .toLowerCase()
                    .replace("_", "-")
                    .replace(" ", "-")
            );

            Flow customResource = new Flow();
            ObjectMeta metadata = new ObjectMeta();
            metadata.setName(flow.getName());
            customResource.setMetadata(metadata);
            customResource.setSpec(flow);

            final CustomResourceDefinition flowCRD = APIStartup.client().customResourceDefinitions().withName("flows.streamzi.io").get();

            APIStartup.client().customResources(flowCRD, Flow.class, FlowList.class, DoneableFlow.class).inNamespace(APIStartup.client().getNamespace()).createOrReplace(customResource);

            logger.info("Flow written OK");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error parsing JSON flow data: " + e.getMessage(), e);
        }
        return flow;
    }    
}