package rest;

import dto.CityInfoDTO;
import dto.HobbyDTO;
import dto.PersonDTO;
import dto.PhoneDTO;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author Acer
 */
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Person p1;
    private static Person p2;
    private static Person p3;
    private static Address a1;
    private static Address a2;
    private static Address a3;
    private static Phone ph1;
    private static Phone ph2;
    private static Phone ph3;
    private static CityInfo ci1;
    private static CityInfo ci2;
    private static CityInfo ci3;
    private static Hobby h1;
    private static Hobby h2;
    private static Hobby h3;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() throws IOException {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        httpServer.start();
        while (!httpServer.isStarted()) {
        }
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        //System.in.read();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            p1 = new Person("Hans", "Karlsen", "hans@hansen.dk");
            p2 = new Person("Bente", "Karlsen", "Bente@hansen.dk");
            p3 = new Person("Kurt", "Karlsen", "Kurt@hansen.dk");

            a1 = new Address("Hovedgade 27");
            a2 = new Address("Lillegade 90");
            a3 = new Address("Storegade 110");

            a1.addPerson(p1);
            a2.addPerson(p2);
            a3.addPerson(p3);

            ph1 = new Phone("12345678", "nokia 3310");
            ph2 = new Phone("87654321", "Samsung Galaxy A50");
            ph3 = new Phone("99998888", "Iphone 11 Pro Max");

            p1.addPhone(ph1);
            p2.addPhone(ph2);
            p3.addPhone(ph3);

            ci1 = new CityInfo("3000", "Helsingør");
            ci2 = new CityInfo("2240", "KBH");
            ci3 = new CityInfo("9000", "Aalborg");

            ci1.addAddress(a1);
            ci2.addAddress(a2);
            ci3.addAddress(a3);

            h1 = new Hobby("Boksning", "Junior");
            h2 = new Hobby("Svømning", "Junior");
            h3 = new Hobby("Boksning", "Senior");

            p1.addHobby(h1);
            p2.addHobby(h2);
            p3.addHobby(h3);

            em.getTransaction().begin();

            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();

            em.persist(ci1);
            em.persist(ci2);
            em.persist(ci3);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/persons").then().statusCode(200);
    }

    @Test
    public void testDummyMsg() throws Exception {
        given()
                .contentType("application/json")
                .get("/persons/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("msg", equalTo("Hello World"));
    }

    @Test
    public void testCount() throws Exception {
        given()
                .contentType("application/json")
                .get("/persons/count").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("count", equalTo(3));
    }

    @Test
    public void testGetHobbyCount() throws Exception {
        given()
                .contentType("application/json")
                .get("/persons/hobbies/count/" + h3.getName()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("count", equalTo(2));
    }

    @Test
    public void testGetAllPersons() throws Exception {
        List<PersonDTO> personsDTO;
        personsDTO = given()
                .contentType("application/json")
                .when()
                .get("/persons/all").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract().body().jsonPath().getList("all", PersonDTO.class);

        assertThat(personsDTO, iterableWithSize(3));

        PersonDTO p1DTO = new PersonDTO(p1);
        PersonDTO p2DTO = new PersonDTO(p2);
        PersonDTO p3DTO = new PersonDTO(p3);

        assertThat(personsDTO, containsInAnyOrder(p1DTO, p2DTO, p3DTO));

    }

    @Test
    public void testGetPerson() throws Exception {
        given()
                .contentType("application/json")
                .get("/persons/" + ph2.getNumber()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstName", equalTo(p2.getFirstName()))
                .body("lastName", equalTo(p2.getLastName()))
                .body("email", equalTo(p2.getEmail()));
    }
    
    @Test
    public void testGetPersonException() {
        given()
                .contentType("application/json")
                .get("/persons/" + 999).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("message", equalTo("Could not find person"));
    }

    @Test
    public void testGetAllPersonsHobbies() throws Exception {
        List<PersonDTO> personsDTO;
        personsDTO = given()
                .contentType("application/json")
                .get("/persons/hobbies/" + h1.getName()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract().body().jsonPath().getList("all", PersonDTO.class);

        assertThat(personsDTO, iterableWithSize(2));

        PersonDTO p1DTO = new PersonDTO(p1);
        PersonDTO p3DTO = new PersonDTO(p3);

        assertThat(personsDTO, containsInAnyOrder(p1DTO, p3DTO));
    }
    
    @Test
    public void testGetAllPersonsHobbiesException() throws Exception {
        given()
                .contentType("application/json")
                .get("/persons/hobbies/" + "skak").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("message", equalTo("Could not find any persons with the given hobby"));

        
    }

    @Test
    public void testGetAllPersonsCity() throws Exception {
        List<PersonDTO> personsDTO;
        personsDTO = given()
                .contentType("application/json")
                .get("/persons/cities/" + ci2.getCity()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract().body().jsonPath().getList("all", PersonDTO.class);

        assertThat(personsDTO, iterableWithSize(1));

        PersonDTO p2DTO = new PersonDTO(p2);

        assertThat(personsDTO, contains(p2DTO));
    }
    
    @Test
    public void testGetAllPersonsCityException() throws Exception {
        given()
                .contentType("application/json")
                .get("/persons/cities/" + "svalbard").then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("message", equalTo("Could not find any persons living in the given city"));

        
    }

    @Test
    public void testGetAllZipcodes() throws Exception {
        List<CityInfoDTO> cityInfosDTO;
        cityInfosDTO = given()
                .contentType("application/json")
                .get("/persons/zips/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract().body().jsonPath().getList("all", CityInfoDTO.class);

        assertThat(cityInfosDTO, iterableWithSize(3));

        CityInfoDTO ci1DTO = new CityInfoDTO(ci1);
        CityInfoDTO ci2DTO = new CityInfoDTO(ci2);
        CityInfoDTO ci3DTO = new CityInfoDTO(ci3);

        assertThat(cityInfosDTO, containsInAnyOrder(ci1DTO, ci2DTO, ci3DTO));
    }    
    
    @Test
    public void testAddPerson() throws Exception {

        Phone phAdd = new Phone("768453621", "Samsung Galaxy A3");
        PhoneDTO phDTOAdd = new PhoneDTO(phAdd);
        List<PhoneDTO> phones = new ArrayList<>();
        phones.add(phDTOAdd);

        Hobby hAdd = new Hobby("Make-up", "Skilled");
        HobbyDTO hDTOAdd = new HobbyDTO(hAdd);
        List<HobbyDTO> hobbies = new ArrayList<>();
        hobbies.add(hDTOAdd);

        given()
                .contentType("application/json")
                .body(new PersonDTO(5, "Gurli", "Gris", "gurli@gris.dk", "Bygade 99", "9999", "Tarm", phones, hobbies))
                .when()
                .post("persons")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstName", equalTo("Gurli"))
                .body("lastName", equalTo("Gris"))
                .body("email", equalTo("gurli@gris.dk"))
                .body("street", equalTo("Bygade 99"))
                .body("zipCode", equalTo("9999"))
                .body("city", equalTo("Tarm"))
                .body("id", notNullValue());
    }
    @Test
    public void testAddPersonException() throws Exception {

        Phone phAdd = new Phone("768453621", "Samsung Galaxy A3");
        PhoneDTO phDTOAdd = new PhoneDTO(phAdd);
        List<PhoneDTO> phones = new ArrayList<>();
        phones.add(phDTOAdd);

        Hobby hAdd = new Hobby("Make-up", "Skilled");
        HobbyDTO hDTOAdd = new HobbyDTO(hAdd);
        List<HobbyDTO> hobbies = new ArrayList<>();
        hobbies.add(hDTOAdd);

        given()
                .contentType("application/json")
                .body(new PersonDTO(5, "", "Gris", "gurli@gris.dk", "Bygade 99", "9999", "Tarm", phones, hobbies))
                .when()
                .post("persons")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode())
                .body("message", equalTo("First name and/or last name is missing"));
    }

    @Test
    public void testEditPerson() throws Exception {
        PersonDTO p3DTO = new PersonDTO(p3);
        p3DTO.setFirstName("Morten");

        given()
                .contentType("application/json")
                .body(p3DTO)
                .when()
                .put("persons/" + p3DTO.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstName", equalTo("Morten"))
                .body("lastName", equalTo(p3.getLastName()))
                .body("email", equalTo(p3.getEmail()))
                .body("street", equalTo(p3.getAddress().getStreet()))
                .body("zipCode", equalTo(p3.getAddress().getCityInfo().getZipcode()))
                .body("city", equalTo(p3.getAddress().getCityInfo().getCity()))
                .body("id", equalTo(p3DTO.getId()));
    }

    @Test
    public void testEditPersonExceptionNotFound() {
        PersonDTO p3DTO = new PersonDTO(p3);
        p3DTO.setLastName("Larsen");

        given()
                .contentType("application/json")
                .body(p3DTO)
                .when()
                .put("persons/" + 9999)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("message", equalTo("Could not find person"));
    }

    @Test
    public void testEditPersonExceptionMissingInput() {
        PersonDTO p3DTO = new PersonDTO(p3);
        p3DTO.setFirstName("");

        given()
                .contentType("application/json")
                .body(p3DTO)
                .when()
                .put("persons/" + p3DTO.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode())
                .body("message", equalTo("First name and/or last name is missing"));
    }

    @Test
    public void testDeletePerson() throws Exception {
        PersonDTO p1DTO = new PersonDTO(p1);

        given()
                .contentType("application/json")
                .delete("persons/" + p1DTO.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());

        List<PersonDTO> personsDTO;
        personsDTO = given()
                .contentType("application/json")
                .when()
                .get("/persons/all").then()
                .extract().body().jsonPath().getList("all", PersonDTO.class);

        assertThat(personsDTO, iterableWithSize(2));
    }

    @Test
    public void testDeletePersonException() {

        given()
                .contentType("application/json")
                .delete("persons/" + 9999)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
                .body("message", equalTo("Could not delete. Person not found"));
    }

}
