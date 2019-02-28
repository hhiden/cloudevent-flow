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
package io.streamzi.eventflow.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.streamzi.eventflow.serialization.SerializedFlow;
import io.streamzi.eventflow.serialization.SerializedLink;
import io.streamzi.eventflow.serialization.SerializedNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author hugo
 */
public class SerializationTest {
    
    public SerializationTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void hello() throws Exception {
        SerializedFlow flow = new SerializedFlow();
        flow.setName("TEST FLOW");
        
        SerializedNode n1 = new SerializedNode();
        n1.setDisplayName("Reader");
        n1.getOutputs().add("IMPORTEDDATA");
        n1.getAttributes().put("TYPE", "KSVC");
        
        SerializedNode n2 = new SerializedNode();
        n2.setDisplayName("Writer");
        n2.getInputs().add("INPUTDATA");
        n2.getAttributes().put("TYPE", "KSVC");
        
        flow.getNodes().add(n1);
        flow.getNodes().add(n2);
        
        SerializedLink link = new SerializedLink();
        link.setSourceUuid(n1.getUuid());
        link.setTargetUuid(n2.getUuid());
        link.setSourcePortName("IMPORTEDDATA");
        link.setTargetPortName("INPUTDATA");
        flow.getLinks().add(link);
        
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(flow);
        
        System.out.println(json);
    
    }
}
