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

import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.streamzi.eventflow.APIStartup;
import io.streamzi.eventflow.providers.knative.DoneableKChannelCR;
import io.streamzi.eventflow.providers.knative.DoneableKServiceCR;
import io.streamzi.eventflow.providers.knative.KChannelCR;
import io.streamzi.eventflow.providers.knative.KChannelCRList;
import io.streamzi.eventflow.providers.knative.KServiceCR;
import io.streamzi.eventflow.providers.knative.KServiceCRList;
import io.streamzi.eventflow.serialization.SerializedTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class provides access to flows based on KNative primitives. This provider targets KNative eventing 0.2.1
 * @author hugo
 */
public class KnativeProvider_0_2 extends FlowCRDBackedProvider {
    private static final Logger logger = Logger.getLogger(KnativeProvider_0_2.class.getName());
    
    /** This method lists the KNative Serving functions that can be linked together */
    @Override
    public List<SerializedTemplate> getTemplates() {
        // Get the services
        final CustomResourceDefinition kSvcCRD = APIStartup.client().customResourceDefinitions().withName("services.serving.knative.dev").get();
        if (kSvcCRD == null) {
            logger.severe("Can't find KSVC CRD");
            return Collections.emptyList();
        }
        
        List<KServiceCR> crs = APIStartup.client().customResources(
                kSvcCRD,
                KServiceCR.class,
                KServiceCRList.class,
                DoneableKServiceCR.class)
                .inNamespace(APIStartup.client().getNamespace()).list().getItems().stream()
                //.map(flow -> flow.getMetadata().getName())
                .collect(Collectors.toList());
         
        List<SerializedTemplate> results = new ArrayList<>();
        for(KServiceCR cr : crs){
            SerializedTemplate template = new SerializedTemplate();
            findEnvParameters(template.getSettings(), cr.getSpec());
            template.setDisplayName(cr.getMetadata().getName());
            template.getInputs().add("input");
            template.getOutputs().add("output");
            template.getAttributes().put("type", "KService");
            template.setProcessorType("Services");
            
            results.add(template);
        }
        
        // Get the channels
        final CustomResourceDefinition kChannelCRD = APIStartup.client().customResourceDefinitions().withName("channels.eventing.knative.dev").get();
        if (kChannelCRD == null) {
            logger.severe("Can't find KCHannel CRD");
            return Collections.emptyList();
        }        
        
        List<KChannelCR> channelCrs = APIStartup.client().customResources(
                kChannelCRD,
                KChannelCR.class,
                KChannelCRList.class,
                DoneableKChannelCR.class)
                .inNamespace(APIStartup.client().getNamespace()).list().getItems().stream()
                //.map(flow -> flow.getMetadata().getName())
                .collect(Collectors.toList());


        for(KChannelCR cr : channelCrs) {
            SerializedTemplate targetTemplate = new SerializedTemplate();
            targetTemplate.setDisplayName(cr.getMetadata().getName() + "_in");
            targetTemplate.getInputs().add("input");
            targetTemplate.setProcessorType("Outputs");
            targetTemplate.getAttributes().put("type", "KOutputChannel");
            results.add(targetTemplate);
            
            SerializedTemplate inputTemplate = new SerializedTemplate();
            inputTemplate.setDisplayName(cr.getMetadata().getName() + "_out");
            inputTemplate.getOutputs().add("output");
            inputTemplate.setProcessorType("Sources");
            inputTemplate.getAttributes().put("type", "KInputChannel");
            results.add(inputTemplate);            
        }
        
        return results;
    }
    
    private void findEnvParameters(Map<String, String> results, HashMap current){
        for(Object key : current.keySet()){
            if(key.toString().equals("env")){
                // Add all of the properties here
                logger.info("Found env entry: " + current.get(key).getClass().getName());
                if(current.get(key) instanceof ArrayList){
                    pullEnvFromArrayList(results, (ArrayList)current.get(key));
                }
            } else {
                if(current.get(key) instanceof HashMap){
                // Carry on adding
                    findEnvParameters(results, (HashMap)current.get(key));
                }
            }
        }
    }
    
    private void pullEnvFromArrayList(Map<String, String> results, ArrayList env){
        for(Object o : env){
            if(o instanceof HashMap){
                HashMap map = (HashMap)o;
                if(map.containsKey("name") && map.containsKey("value")){
                    results.put(map.get("name").toString(), map.get("value").toString());
                }
            }
        }
    }
}