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
import io.streamzi.eventflow.TargetStateProvider;
import java.util.logging.Logger;

/**
 * Deployer / state manager for KNative 0.2
 */
public class KNative_0_2_TargetState extends TargetStateProvider {
    private static final Logger logger = Logger.getLogger(KNative_0_2_TargetState.class.getName());
    
    @Override
    public void build() throws Exception {
        logger.info("Flow build");
        ObjectMapper mapper = new ObjectMapper();
        logger.info(mapper.writeValueAsString(getFlow()));
    }

}