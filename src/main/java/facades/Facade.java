package facades;

import dtos.CityInfoDTO;
import dtos.HobbyDTO;
import dtos.PersonDTO;
import dtos.PhoneDTO;
import entities.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.reflect.Type;
import java.util.*;

//import errorhandling.RenameMeNotFoundException;


/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class Facade {

    private static Facade instance;
    private static EntityManagerFactory emf;
    
    //Private Constructor to ensure Singleton
    private Facade() {}
    
    
    /**
     * 
     * @param _emf
     * @return an instance of this facade class.
     */
    public static Facade getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new Facade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
//    public RenameMeDTO create(RenameMeDTO rm){
//        RenameMe rme = new RenameMe(rm.getDummyStr1(), rm.getDummyStr2());
//        EntityManager em = getEntityManager();
//        try {
//            em.getTransaction().begin();
//            em.persist(rme);
//            em.getTransaction().commit();
//        } finally {
//            em.close();
//        }
//        return new RenameMeDTO(rme);
//    }
//    public RenameMeDTO getById(long id) { //throws RenameMeNotFoundException {
//        EntityManager em = emf.createEntityManager();
//        RenameMe rm = em.find(RenameMe.class, id);
////        if (rm == null)
////            throw new RenameMeNotFoundException("The RenameMe entity with ID: "+id+" Was not found");
//        return new RenameMeDTO(rm);
//    }
//
//    public long getRenameMeCount(){
//        EntityManager em = getEntityManager();
//        try{
//            long renameMeCount = (long)em.createQuery("SELECT COUNT(r) FROM RenameMe r").getSingleResult();
//            return renameMeCount;
//        }finally{
//            em.close();
//        }
//    }
    
    public List<PersonDTO> getAllPersons(){
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p", Person.class);
            List<Person> persons = query.getResultList();
            return PersonDTO.getDtos(persons);
        } finally {
            em.close();
        }
    }

    public PersonDTO getPersonByPhone(String phone) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<PersonDTO> query = em.createQuery("SELECT new dtos.PersonDTO (p) from Person p join Phone ph where ph.number = :phone and p.id=ph.person.id", PersonDTO.class);
            query.setParameter("phone",phone);
            query.setMaxResults(1);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<PersonDTO> getAllPersonsByZip(int zip) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<PersonDTO> query = em.createQuery("SELECT new dtos.PersonDTO(p) FROM Person p join Address a join CityInfo c WHERE c.zipCode=:zip and c.id=a.cityInfo.id and p.address.id=a.id", PersonDTO.class);
            query.setParameter("zip", zip);
            return query.getResultList();
        }finally {
            em.close();
        }
    }

    public int getNumberOfPersonsWithHobby(int hobbyId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Hobby> query = em.createQuery("select h FROM Hobby h WHERE h.id=:hobbyId", Hobby.class);
            query.setParameter("hobbyId", hobbyId);
            Hobby hobby = query.getSingleResult();
            return hobby.getPersons().size();
        } finally {
            em.close();
        }
    }

    public List<CityInfoDTO> getAllZipCodes() {
        EntityManager em = emf.createEntityManager();
        try{
            TypedQuery<CityInfoDTO> query = em.createQuery("select new dtos.CityInfoDTO(ci) FROM CityInfo ci",CityInfoDTO.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public PersonDTO createPerson(PersonDTO personDTO){
        Person person = new Person(personDTO.getEmail(),personDTO.getfName(),personDTO.getlName());

        Address address = new Address(personDTO.getAddress().getStreet(),personDTO.getAddress().getInfo());
        person.addAddress(address);

        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            for (HobbyDTO hobby: personDTO.getHobbiesDTOs()) {
                person.addHobby(em.find(Hobby.class,hobby.getId()));
            }
            address.addCityInfo(em.find(CityInfo.class,personDTO.getAddress().getCityInfo().getId()));
            for (PhoneDTO phoneDTO : personDTO.getPhones()) {
                Phone phone = new Phone(phoneDTO.getNr(),phoneDTO.getDesc());
                person.addPhone(phone);
                em.persist(phone);
            }
            em.persist(person);
            em.persist(address);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(person);
    }

    public PersonDTO editPerson(PersonDTO personDTO) {
        EntityManager em = emf.createEntityManager();
        try{
            Person person = em.find(Person.class,personDTO.getId());
            if(personDTO.getfName() != null) {
                person.setFirstName(personDTO.getfName());
            }
            if(personDTO.getlName() != null) {
                person.setLastName(personDTO.getlName());
            }

            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();

            return personDTO;

        } finally {
            em.close();
        }
    }

//
//    public static void main(String[] args) {
//        emf = EMF_Creator.createEntityManagerFactory();
//        FacadeExample fe = getFacadeExample(emf);
//        fe.getAll().forEach(dto->System.out.println(dto));
//    }

}
