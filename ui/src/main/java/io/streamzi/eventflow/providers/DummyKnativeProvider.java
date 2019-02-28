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

import io.streamzi.eventflow.serialization.SerializedFlow;
import io.streamzi.eventflow.serialization.SerializedTemplate;
import java.util.List;
import java.util.Set;

/**
 * This class provides access to flows based on KNative primitives
 * @author hugo
 */
public class DummyKnativeProvider extends FlowCRDBackedProvider {

    /** This method lists the KNative Serving functions that can be linked together */
    @Override
    public List<SerializedTemplate> getTemplates() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}