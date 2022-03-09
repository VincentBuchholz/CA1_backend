package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.PersonDTO;
import utils.EMF_Creator;
import facades.Facade;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

//Todo Remove or change relevant parts before ACTUAL use
@Path("/person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
       
    private static final Facade FACADE =  Facade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            

    @GET
    @Path("/all")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllPersons() {
        List<PersonDTO> personDTOList = FACADE.getAllPersons();
        return Response.ok().entity(GSON.toJson(personDTOList)).build();
    }

    @GET
    @Path("/phone/{phone}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPersonByPhone(@PathParam("phone") String phone) {
        PersonDTO personDTO = FACADE.getPersonByPhone(phone);
        return Response.ok().entity(GSON.toJson(personDTO)).build();
    }
    @GET
    @Path("/zip/{zip}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllPersonsByZip(@PathParam("zip") int zip) {
        List<PersonDTO> personDTOList = FACADE.getAllPersonsByZip(zip);
        return Response.ok().entity(GSON.toJson(personDTOList)).build();
    }
    @GET
    @Path("/hobby/{hobbyID}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getNumberOfPersonsWithHobby(@PathParam("hobbyID") int hobbyId) {
        int amountOfPersons= FACADE.getNumberOfPersonsWithHobby(hobbyId);
        return Response.ok().entity("{\"amount\":"+amountOfPersons+"}").build();

    }

}
