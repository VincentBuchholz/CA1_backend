package facades;

import entities.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class FacadeTest {

    private static EntityManagerFactory emf;
    private static Facade facade;

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

        Person person = new Person("test@test.dk","Karl","Larsen");
        Phone phone = new Phone("2314121","DK");
        Hobby hobby = new Hobby("BasketBall","play ball");
        Address address = new Address("Ulrikkenborg Alle","33");
        CityInfo cityInfo = new CityInfo(2800,"Kgs. Lyngby");


        try{
            em.getTransaction().begin();
            em.persist(person);
            em.persist(phone);
            em.persist(hobby);
            em.persist(address);
            em.persist(cityInfo);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }


    //Delete this test
    @Test
    public void databaseInsertTest(){
        System.out.println("testing");
    }

}
