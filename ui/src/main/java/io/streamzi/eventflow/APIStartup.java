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

import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author hugo
 */
@Singleton
@Startup
public class APIStartup {
    private static final Logger logger = Logger.getLogger(APIStartup.class.getName());
    private static OpenShiftClient osClient;
    @PostConstruct
    public void startup(){
        logger.info("API Startup");
        osClient = new DefaultOpenShiftClient();
        
        
        try {
            logger.info("Checking for CRDs at: " + osClient.getOpenshiftUrl());
            final CustomResourceDefinition flowCRD = osClient.customResourceDefinitions().withName("flows.streamzi.io").get();
            if (flowCRD == null) {
                logger.info("Can't find Flow CRDs - Local OpenShift only");
            }        
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error getting CRDs: " + e.getMessage(), e);
        }
    }
    
    public static OpenShiftClient client(){
        return osClient;
    }
}
