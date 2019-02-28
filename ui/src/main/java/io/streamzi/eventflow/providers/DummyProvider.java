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

import io.streamzi.eventflow.EventFlowAPIProvider;
import io.streamzi.eventflow.serialization.SerializedFlow;
import io.streamzi.eventflow.serialization.SerializedLink;
import io.streamzi.eventflow.serialization.SerializedNode;
import io.streamzi.eventflow.serialization.SerializedTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Simple provider that does not back onto anything
 * @author hugo
 */
public class DummyProvider implements EventFlowAPIProvider {
    private HashMap<String, SerializedFlow> flows = new HashMap<>();

    public DummyProvider() {
        SerializedFlow flow = new SerializedFlow();
        flow.setName("TEST FLOW");
        
        SerializedNode n1 = new SerializedNode();
        n1.setDisplayName("Reader");
        n1.getOutputs().add("importeddata");
        n1.getAttributes().put("TYPE", "KSVC");
        n1.setTemplateName("IMPORT");
        n1.setProcessorType("KSVC");
        
        SerializedNode n2 = new SerializedNode();
        n2.setDisplayName("Writer");
        n2.getInputs().add("inputdata");
        n2.setTemplateName("WRITER");
        n2.setProcessorType("KSVC");
        
        flow.getNodes().add(n1);
        flow.getNodes().add(n2);
        
        SerializedLink link = new SerializedLink();
        link.setSourceUuid(n1.getUuid());
        link.setTargetUuid(n2.getUuid());
        link.setSourcePortName("importeddata");
        link.setTargetPortName("inputdata");
        flow.getLinks().add(link);
        flows.put(flow.getName(), flow);        
    }

    @Override
    public SerializedFlow getFlow(String name) {
        return flows.get(name);
    }
    
    @Override
    public Set<String> getFlows() {
        return flows.keySet();
    }

    @Override
    public SerializedFlow postFlow(SerializedFlow flow) {
        flows.put(flow.getName(), flow);
        return flow;
    }

    @Override
    public List<SerializedTemplate> getTemplates() {
        List<SerializedTemplate> results = new ArrayList<>();
        
        SerializedTemplate t1 = new SerializedTemplate();
        t1.setDisplayName("IMPORT");
        t1.getOutputs().add("importeddata");
        t1.getSettings().put("Frequency", "1,0");
        t1.setProcessorType("KSVC");
        
        results.add(t1);
        
        SerializedTemplate t2 = new SerializedTemplate();
        t2.setDisplayName("WRITER");
        t2.getInputs().add("inputdata");
        t2.setProcessorType("KSVC");
        results.add(t2);        
        return results;
    }
}