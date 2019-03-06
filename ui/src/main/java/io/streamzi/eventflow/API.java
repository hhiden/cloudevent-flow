package io.streamzi.eventflow;


import io.streamzi.eventflow.providers.DummyFlowCRDBackedProvider;
import io.streamzi.eventflow.providers.KnativeProvider_0_2;
import io.streamzi.eventflow.serialization.SerializedFlow;
import io.streamzi.eventflow.serialization.SerializedTemplate;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

/**
 * @author hhiden
 */
@ApplicationScoped
@Path("/api")
public class API {
    private EventFlowAPIProvider provider = new KnativeProvider_0_2();
    
    private static final Logger logger = Logger.getLogger(API.class.getName());
    
    @Path("/test")
    @GET
    @Produces("text/plain")
    public String test(){
        return "Hello";
    }
    
    @Path("/dataflows")
    @GET
    @Produces("application/json")
    public List<String> dataflows(){
        return provider.getFlows();
    }
 
    @Path("/flows")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public SerializedFlow postFlow(SerializedFlow flow){
        return provider.postFlow(flow);
    }
    
    @Path("/dataflows/{name}")
    @GET
    @Produces("application/json")
    public SerializedFlow getFlow(@PathParam("name")String name){
        return provider.getFlow(name);
    }
    
    @Path("/processors")
    @GET
    @Produces("application/json")
    public List<SerializedTemplate> processors(){
        return provider.getTemplates();
    }
}
