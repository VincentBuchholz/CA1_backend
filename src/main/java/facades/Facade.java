package facades;

import dtos.*;
import entities.*;
import errorhandling.MissingInputException;
import errorhandling.PersonNotFoundException;

import javax.persistence.*;
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

    public PersonDTO getPersonByPhone(String phone) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<PersonDTO> query = em.createQuery("SELECT new dtos.PersonDTO (p) from Person p join Phone ph where ph.number = :phone and p.id=ph.person.id", PersonDTO.class);
            query.setParameter("phone",phone);
            query.setMaxResults(1);
            PersonDTO personDTO;
            try {
                personDTO = query.getSingleResult();
            } catch (Exception e){
            throw new PersonNotFoundException("Person with provided phone number not found");
            }
        return personDTO;
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

    public PersonDTO createPerson(PersonDTO personDTO)throws MissingInputException {
        if(personDTO.getEmail() == null || personDTO.getfName() == null || personDTO.getlName() == null){
            throw new MissingInputException("Missing person values");
        }
        Person person = new Person(personDTO.getEmail(),personDTO.getfName(),personDTO.getlName());

        if(personDTO.getAddress().getStreet() == null || personDTO.getAddress().getInfo() == null){
            throw new MissingInputException("Missing address values");
        }
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
                if(phone.getNumber() == null || phone.getDescription() == null){
                    throw new MissingInputException("Missing phone values");
                }
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

    public PersonDTO editPerson(PersonDTO personDTO) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        try{
            Person person = em.find(Person.class,personDTO.getId());
            System.out.println(person);
            if(person == null){
                throw new PersonNotFoundException("Person with provided id not found");
            }
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
    public PersonDTO getPersonById(int id){
        EntityManager em = emf.createEntityManager();
        try {
            Person person = em.find(Person.class,id);
            if (person == null) {
                System.out.println("No person with provided id found");
            }
            return new PersonDTO(person);
        }finally {
            em.close();
        }
    }

    public PersonDTO editPersonAddress(int userId, AddressDTO addressDTO) {
        EntityManager em = emf.createEntityManager();
        try{
            Person person = em.find(Person.class,userId);
            Address oldAddress = em.find(Address.class,person.getAddress().getId());
            Address newAddress = new Address(addressDTO.getStreet(), addressDTO.getInfo());
            newAddress.addCityInfo(em.find(CityInfo.class,addressDTO.getCityInfo().getId()));
            newAddress = checkIfAddressExists(newAddress);
            if(newAddress.getId()>0){
                newAddress=em.find(Address.class,newAddress.getId());
            }else{
                newAddress.addCityInfo(em.find(CityInfo.class,addressDTO.getCityInfo().getId()));
            }
            person.addAddress(newAddress);
            em.getTransaction().begin();
            em.persist(newAddress);
            em.merge(person);
            em.getTransaction().commit();

            oldAddress.removePerson(person);
            if(oldAddress.getPersons().size()<1){
                deleteAddress(oldAddress.getId());
            }
            return new PersonDTO(person);
        } finally {
            em.close();
        }
    }
    public Address checkIfAddressExists(Address address){
        EntityManager em = emf.createEntityManager();
        try{
            TypedQuery<Address> query = em.createQuery("SELECT a from Address a WHERE a.street=:street and a.additionalInfo=:info and a.cityInfo.id =:city",Address.class);
            query.setParameter("street",address.getStreet());
            query.setParameter("info",address.getAdditionalInfo());
            query.setParameter("city",address.getCityInfo().getId());
            try{
                return query.getSingleResult();
            }catch (NoResultException e){
                e.printStackTrace();
                return address;
            }
        }finally {
            em.close();
        }
    }
    public void deleteAddress(int id){
        EntityManager em = emf.createEntityManager();
        try {
            Address a = em.find(Address.class, id);
            System.out.println(a.getStreet());
            em.getTransaction().begin();
            em.remove(a);
            em.getTransaction().commit();
        }finally {
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
