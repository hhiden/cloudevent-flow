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

import io.streamzi.eventflow.serialization.SerializedTemplate;
import java.util.ArrayList;
import java.util.List;

/**
 * Dummy provider that uses Flow CRDs to store data
 */
public class DummyFlowCRDBackedProvider extends FlowCRDBackedProvider {

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
