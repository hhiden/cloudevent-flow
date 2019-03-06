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
package io.streamzi.eventflow;

import io.streamzi.eventflow.serialization.SerializedFlow;
import io.streamzi.eventflow.serialization.SerializedTemplate;
import java.util.List;
import java.util.Set;

/**
 * This interface defines an object that can provide data to the API REST service
 * @author hugo
 */
public interface EventFlowAPIProvider {
    /** List the deployed flows */
    public List<String> getFlows();
    
    /** Get a specific flow */
    public SerializedFlow getFlow(String name);
    
    /** Sumbit / resubmit a flow */
    public SerializedFlow postFlow(SerializedFlow flow);
    
    /** List the possible flow processor objects */
    public List<SerializedTemplate> getTemplates();
}
