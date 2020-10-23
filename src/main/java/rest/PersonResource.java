package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dto.CityInfosDTO;
import dto.PersonDTO;
import dto.PersonsDTO;
import dto.PhoneDTO;
import exceptions.CityNotFoundException;
import exceptions.HobbyNotFoundException;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import facades.FacadeExample;
import facades.PersonFacade;
import java.lang.reflect.Type;
import java.util.Collection;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

//Todo Remove or change relevant parts before ACTUAL use
@Path("persons")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    private static final PersonFacade FACADE = PersonFacade.getPersonFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }

    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPersonCount() {
        long count = FACADE.personCount();
        //System.out.println("--------------->"+count);
        return "{\"count\":" + count + "}";  //Done manually so no need for a DTO
    }

    @Path("{phoneNumber}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPerson(@PathParam("phoneNumber") String phoneNumber) throws PersonNotFoundException {
        PersonDTO pDTO = FACADE.getPerson(phoneNumber);
        return GSON.toJson(pDTO);
    }

    @Path("all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllPersons() {
        PersonsDTO psDTO = FACADE.getAllPersons();
        return GSON.toJson(psDTO);
    }

    @Path("hobbies/{hobby}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllPersonsHobbies(@PathParam("hobby") String hobby) throws HobbyNotFoundException {
        PersonsDTO psDTO = FACADE.getAllPersonsHobbies(hobby);
        return GSON.toJson(psDTO);
    }

    @Path("cities/{city}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllPersonsCity(@PathParam("city") String city) throws CityNotFoundException {
        PersonsDTO psDTO = FACADE.getAllPersonsCity(city);
        return GSON.toJson(psDTO);
    }

    @Path("hobbies/count/{hobby}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getHobbyCount(@PathParam("hobby") String hobby) throws HobbyNotFoundException {
        long hobbyCount = FACADE.getHobbyCount(hobby);
        return "{\"count\":" + hobbyCount + "}";
    }

    @Path("zips")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllZipcodes() {
        CityInfosDTO ciDTO = FACADE.getAllZipcodes();
        return GSON.toJson(ciDTO);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String addPerson(String person) throws MissingInputException {
//        Type collectionType = new TypeToken<Collection<PhoneDTO>>(){}.getType();
//        Collection<PhoneDTO> phDTOs = GSON.fromJson(person, collectionType);
        PersonDTO pDTO = GSON.fromJson(person, PersonDTO.class);
        PersonDTO pAdded
                = FACADE.addPerson(pDTO.getFirstName(), pDTO.getLastName(), pDTO.getEmail(), pDTO.getStreet(), pDTO.getZipCode(), pDTO.getCity(), pDTO.getPhones(), pDTO.getHobbies());
        return GSON.toJson(pAdded);
    }

    @Path("/{id}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String editPerson(@PathParam("id") int id, String person) throws MissingInputException, PersonNotFoundException {
        PersonDTO pDTO = GSON.fromJson(person, PersonDTO.class);
        pDTO.setId(id);
        PersonDTO pEdited = FACADE.editPerson(pDTO);
        return GSON.toJson(pEdited);
    }

    @Path("/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    public String deletePerson(@PathParam("id") int id) throws PersonNotFoundException {
        PersonDTO pDeleted = FACADE.deletePerson(id);
        return GSON.toJson(pDeleted);

    }
}
