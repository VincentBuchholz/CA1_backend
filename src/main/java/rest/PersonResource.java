package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.CityInfoDTO;
import dtos.PersonDTO;
import errorhandling.MissingInputException;
import errorhandling.PersonNotFoundException;
import utils.EMF_Creator;
import facades.Facade;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
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
    public Response getPersonByPhone(@PathParam("phone") String phone) throws PersonNotFoundException {
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

    @GET
    @Path("/zip/all")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllZipCodes() {
        List<CityInfoDTO> cityInfoDTOs = FACADE.getAllZipCodes();
        return Response.ok().entity(GSON.toJson(cityInfoDTOs)).build();
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createPerson(String content) throws MissingInputException {
        PersonDTO pdto = GSON.fromJson(content, PersonDTO.class);
        PersonDTO newPdto = FACADE.createPerson(pdto);
        return Response.ok().entity(GSON.toJson(newPdto)).build();
    }

    @PUT
    @Path("/edit/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(@PathParam("id") int id, String content) throws PersonNotFoundException {
        PersonDTO pdto = GSON.fromJson(content, PersonDTO.class);
        pdto.setId(id);
        PersonDTO updated = FACADE.editPerson(pdto);
        return Response.ok().entity(GSON.toJson(updated)).build();
    }

}
