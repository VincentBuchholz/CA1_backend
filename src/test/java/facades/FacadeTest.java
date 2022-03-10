package facades;

import dtos.PersonDTO;
import entities.*;
import errorhandling.MissingInputException;
import errorhandling.PersonNotFoundException;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Uncomment the line below, to temporarily disable this test
@Disabled
public class FacadeTest {

    private static EntityManagerFactory emf;
    private static Facade facade;
    Person person;
    Person person2;
    Person person3;
    Address address3;
    Hobby hobby;
    Hobby hobby2;
    CityInfo cityInfo;

    public FacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = Facade.getFacadeExample(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {

        EntityManager em = emf.createEntityManager();

        person = new Person("test@test.dk","Karl","Larsen");
        person2 = new Person("test@test.dk","Hans","Larsen");
        person3 = new Person("test@test.dk","Viggo","Hansen");
        Phone phone1 = new Phone("2314121","DK");
        Phone phone2 = new Phone("9314121","DK");
        hobby = new Hobby("BasketBall","play ball");
        hobby2 = new Hobby("Tennis","play ball");
        Address address = new Address("Ulrikkenborg Alle","33");
        Address address2 = new Address("Ulrikkenborg Alle","230");
        address3 = new Address("Vægterparken","193");
        cityInfo = new CityInfo(2800,"Kgs. Lyngby");
        CityInfo cityInfo2 = new CityInfo(2770,"Kastrup");

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

        try{
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

    @AfterEach
    public void tearDown() {
    }


    @Test
    public void getAllPersonsTest(){
        System.out.println("Get all persons test!");
        assertEquals(3,facade.getAllPersons().size());

    }
    @Test
    public void getPersonByPhoneTest() throws PersonNotFoundException {
        System.out.println("Get person by phone test!");
        assertEquals(person.getId(),facade.getPersonByPhone("2314121").getId());
        assertEquals(person2.getId(),facade.getPersonByPhone("9314121").getId());

    }
    @Test
    public void getAllPersonsByZipTest(){
        System.out.println("Get all persons by zip test!");
        assertEquals(2,facade.getAllPersonsByZip(2800).size());
        assertEquals(1,facade.getAllPersonsByZip(2770).size());

    }

    @Test
    public void getNumberOfPersonsWithHobbyTest(){
        System.out.println("Get number of persons by hobby test!");
        assertEquals(1,facade.getNumberOfPersonsWithHobby(hobby2.getId()));

    }

    @Test
    public void getAllZipCodesTest(){
        System.out.println("get all zipCodes test");
        assertEquals(2,facade.getAllZipCodes().size());

    }

    @Test
    public void createPersonTest() throws MissingInputException {
        System.out.println("Create person test!");
        Person person = new Person("Test@test.dk","test","test");
        Address address = new Address("Ulrikkenborg plads","2nd door");
        address.addCityInfo(cityInfo);
        person.addAddress(address);
        person.addHobby(hobby2);
        Phone phone = new Phone("24341421","dkk");
        person.addPhone(phone);
        PersonDTO testPerson = new PersonDTO(person);
        facade.createPerson(testPerson);

        assertEquals(4,facade.getAllPersons().size());

    }

    @Test
    public void editPersonTest() throws PersonNotFoundException{
        System.out.println("edit person test");
        PersonDTO personDTO = new PersonDTO(person);
        personDTO.setfName("Edited name");

        facade.editPerson(personDTO);

        assertEquals("Edited name",facade.getPersonByPhone("2314121").getfName());

    }



}
