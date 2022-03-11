package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.AddressDTO;
import dtos.CityInfoDTO;
import dtos.PersonDTO;
import entities.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;
import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

import io.restassured.parsing.Parser;

import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    Person person;
    Person person2;
    Person person3;
    Address address3;
    Hobby hobby;
    Hobby hobby2;
    CityInfo cityInfo;
    CityInfo cityInfo2;
    Phone phonetest;
    Phone phone1;
    Phone phone2;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
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

        em.getTransaction().begin();
        em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
        em.createNamedQuery("Person.deleteAllRows").executeUpdate();
        em.createNamedQuery("Address.deleteAllRows").executeUpdate();
        em.createNamedQuery("CityInfo.deleteAllRows").executeUpdate();
        em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
        em.getTransaction().commit();


        person = new Person("test@test.dk", "Karl", "Larsen");
        person2 = new Person("test@test.dk", "Hans", "Larsen");
        person3 = new Person("test@test.dk", "Viggo", "Hansen");
        phone1 = new Phone("2314121", "DK");
        phone2 = new Phone("9314121", "DK");
        phonetest = new Phone("999999", "DK");
        hobby = new Hobby("BasketBall", "play ball");
        hobby2 = new Hobby("Tennis", "play ball");
        Address address = new Address("Ulrikkenborg Alle", "33");
        Address address2 = new Address("Ulrikkenborg Alle", "230");
        address3 = new Address("Vægterparken", "193");
        cityInfo = new CityInfo(2800, "Kgs. Lyngby");
        cityInfo2 = new CityInfo(2770, "Kastrup");

        person.addHobby(hobby);
        person2.addHobby(hobby);
        person3.addHobby(hobby2);

        person.addPhone(phone1);

        person2.addPhone(phone2);

        person.addAddress(address);
        person2.addAddress(address2);
        person3.addAddress(address3);

        address.addCityInfo(cityInfo);
        address2.addCityInfo(cityInfo);
        address3.addCityInfo(cityInfo2);

        try {
            em.getTransaction().begin();
            em.persist(person);
            em.persist(person2);
            em.persist(person3);
            em.persist(phone1);
            em.persist(phone2);
            em.persist(hobby);
            em.persist(hobby2);
            em.persist(address);
            em.persist(address2);
            em.persist(address3);
            em.persist(cityInfo);
            em.persist(cityInfo2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }

    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/person/all").then().statusCode(200);
    }

    @Test
    public void getAllPersons() throws Exception {
        List<PersonDTO> personDTOS;
        personDTOS = given()
                .contentType("application/json")
                .when()
                .get("/person/all")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract().body().jsonPath().getList("", PersonDTO.class);
        PersonDTO p1DTO = new PersonDTO(person);
        PersonDTO p2DTO = new PersonDTO(person2);
        PersonDTO p3DTO = new PersonDTO(person3);
        assertThat(personDTOS, containsInAnyOrder(p1DTO, p2DTO, p3DTO));
    }

    @Test
    public void testGetPersonByPhone() {
        PersonDTO personDTO =
                given()
                        .contentType(ContentType.JSON)
                        .get("/person/phone/{phone}", "9314121" )
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.OK_200.getStatusCode())
                        .extract().body().jsonPath().getObject("", PersonDTO.class);
        assertThat(personDTO, equalTo(new PersonDTO(person2)));
    }

    @Test
    public void testGetPersonByPhoneNotFound() {
        given()
                .contentType(ContentType.JSON)
                .get("/person/phone/{phone}", "93283428478474")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetPersonByZip() {
        List<PersonDTO> personDTOS;
        personDTOS = given()
                .contentType("application/json")
                .when()
                .get("/person/zip/{zip}", cityInfo.getZipCode())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract().body().jsonPath().getList("", PersonDTO.class);
        PersonDTO p1DTO = new PersonDTO(person);
        PersonDTO p2DTO = new PersonDTO(person2);
        assertThat(personDTOS, containsInAnyOrder(p1DTO, p2DTO));
    }

    @Test
    public void testGetPersonByZipNotFound() {
        given()
                .contentType(ContentType.JSON)
                .get("/person/zip/{zip}", 3820)
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetAmountOfPersonsByHobby() {
        int amount = given()
                .contentType("application/json")
                .when()
                .get("/person/hobby/{hobbyid}", hobby.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract().body().jsonPath().getInt("amount");
        assertThat(amount, equalTo(2));
    }

    @Test
    public void testGetAmountOfPersonsByHobbyNotFound() {
        given()
                .contentType(ContentType.JSON)
                .get("/person/hobby/{hobbyid}", 3820)
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetAllZipcodes() {
        List<CityInfoDTO> cityInfoDTOS;
        cityInfoDTOS = given()
                .contentType("application/json")
                .when()
                .get("/person/zip/all")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .extract().body().jsonPath().getList("", CityInfoDTO.class);
        CityInfoDTO c1DTO = new CityInfoDTO(cityInfo);
        CityInfoDTO c2DTO = new CityInfoDTO(cityInfo2);
        assertThat(cityInfoDTOS, containsInAnyOrder(c1DTO, c2DTO));
    }

    @Test
    public void testCreatePerson() {
        Person person = new Person("Test@test.dk", "testf", "testl");
        Address address = new Address("Ulrikkenborg plads", "2nd door");
        address.addCityInfo(cityInfo);
        person.addAddress(address);
        person.addHobby(hobby2);
        Phone phone = new Phone("24341421", "dkk");
        person.addPhone(phone);

        PersonDTO personDTO = new PersonDTO(person);

        String requestBody = GSON.toJson(personDTO);

        given()
                .header("Content-type", ContentType.JSON)
                .and()
                .body(requestBody)
                .when()
                .post("/person")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", notNullValue())
                .body("fName", equalTo("testf"))
                .body("lName", equalTo("testl"))
                .body("email", equalTo("Test@test.dk"))
                .body("hobbies", hasItems(hasEntry("id", hobby2.getId())))
                .body("phones", hasItems(hasEntry("nr", "24341421")));
    }

    @Test
    public void testCreatePersonMissingInput() {
        Person person = new Person("Test@test.dk", null, "testl");
        Address address = new Address("Ulrikkenborg plads", "2nd door");
        address.addCityInfo(cityInfo);
        person.addAddress(address);
        person.addHobby(hobby2);
        Phone phone = new Phone("24341421", "dkk");
        person.addPhone(phone);
        PersonDTO personDTO = new PersonDTO(person);
        String requestBody = GSON.toJson(personDTO);

        given()
                .header("Content-type", ContentType.JSON)
                .and()
                .body(requestBody)
                .when()
                .post("/person")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void testEditPerson() {
        PersonDTO personDTO = new PersonDTO(person);
        personDTO.setfName("EditedF");
        personDTO.setlName("EditedL");
        personDTO.setEmail("Edited@mail.dk");
        String requestBody = GSON.toJson(personDTO);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .put("/person/edit/{id}", person.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(person.getId()))
                .body("fName", equalTo("EditedF"))
                .body("lName", equalTo("EditedL"))
                .body("email", equalTo("Edited@mail.dk"));
    }

    @Test
    public void testEditPersonNotFound() {
        PersonDTO personDTO = new PersonDTO(person);
        personDTO.setfName("EditedF");
        personDTO.setlName("EditedL");
        personDTO.setEmail("Edited@mail.dk");
        String requestBody = GSON.toJson(personDTO);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .put("/person/edit/{id}", 9999)
                .then()
                .statusCode(404);
    }

    @Test
    public void testEditAddress() {
        Address newAddress = new Address("Tests street", "none");
        newAddress.addCityInfo(cityInfo);

        person.addAddress(newAddress);
        PersonDTO personDTO = new PersonDTO(person);
        AddressDTO addressDTO = new AddressDTO(newAddress);

        String requestBody = GSON.toJson(personDTO);
        AddressDTO response = given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .put("/person/editaddress/{id}", person.getId())
                .then()
                .extract().body().jsonPath().getObject("address", AddressDTO.class);
        assertThat(response.getStreet(), equalTo(addressDTO.getStreet()));
        assertThat(response.getInfo(), equalTo(addressDTO.getInfo()));
        assertThat(response.getCityInfo(), equalTo(addressDTO.getCityInfo()));
    }

    @Test
    public void testEditAddressPersonNotFound() {
        Address newAddress = new Address("Tests street", "none");
        newAddress.addCityInfo(cityInfo);
        person.addAddress(newAddress);
        PersonDTO personDTO = new PersonDTO(person);
        String requestBody = GSON.toJson(personDTO);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .put("/person/editaddress/{id}", 9999)
                .then()
                .statusCode(404);
    }

    @Test
    public void testAddPhoneToPerson() {
        Phone phone = new Phone("9999999", "eng");
        person.addPhone(phone);

        PersonDTO personDTO = new PersonDTO(person);
        String requestBody = GSON.toJson(personDTO);
        given()
                .header("Content-type", ContentType.JSON)
                .and()
                .body(requestBody)
                .when()
                .post("/person/addphone/{id}", person.getId())
                .then()
                .assertThat()
                .statusCode(200)
                .body("phones", hasItems(hasEntry("nr", "9999999")))
                .body("phones", hasItems(hasEntry("nr", "2314121")));
    }

    @Test
    public void testAddPhoneToPersonNotFound() {
        Phone phone = new Phone("9999999", "eng");
        person.addPhone(phone);

        PersonDTO personDTO = new PersonDTO(person);
        String requestBody = GSON.toJson(personDTO);
        given()
                .header("Content-type", ContentType.JSON)
                .and()
                .body(requestBody)
                .when()
                .post("/person/addphone/{id}", 9999)
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void testDeletePhoneFromPerson() {
        given()
                .contentType(ContentType.JSON)
                .delete("/person/deletephone/{id}", phone1.getId())
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeletePhoneFromPersonNotFound() {
        given()
                .contentType(ContentType.JSON)
                .delete("/person/deletephone/{id}", 9999)
                .then()
                .statusCode(404);
    }

    @Test
    public void testAddHobbyToPerson() {
        given()
                .header("Content-type", "application/json")
                .pathParam("personid", person.getId())
                .pathParam("hobbyid", hobby2.getId())
                .put("/person/addhobby/{personid}/{hobbyid}")
                .then()
                .statusCode(200);
    }

    @Test
    public void testAddHobbyToPersonPersonNotFound() {
        given()
                .header("Content-type", "application/json")
                .pathParam("personid", 9999)
                .pathParam("hobbyid", hobby2.getId())
                .put("/person/addhobby/{personid}/{hobbyid}")
                .then()
                .statusCode(404);
    }

    @Test
    public void testAddHobbyToPersonHobbyNotFound() {
        given()
                .header("Content-type", "application/json")
                .pathParam("personid", person.getId())
                .pathParam("hobbyid", 9999)
                .put("/person/addhobby/{personid}/{hobbyid}")
                .then()
                .statusCode(404);
    }

    @Test
    public void testRemoveHobbyFromPerson() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("personid", person.getId())
                .pathParam("hobbyid", hobby.getId())
                .delete("/person/removehobby/{personid}/{hobbyid}")
                .then()
                .statusCode(200);
    }

    @Test
    public void testRemoveHobbyFromPersonPersonNotFound() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("personid", 9999)
                .pathParam("hobbyid", hobby.getId())
                .delete("/person/removehobby/{personid}/{hobbyid}")
                .then()
                .statusCode(404);
    }

    @Test
    public void testRemoveHobbyFromPersonHobbyNotFound() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("personid", person.getId())
                .pathParam("hobbyid", 9999)
                .delete("/person/removehobby/{personid}/{hobbyid}")
                .then()
                .statusCode(404);
    }

}
