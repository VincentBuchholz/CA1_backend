package facades;

import dtos.*;
import entities.*;
import errorhandling.MissingInputException;
import errorhandling.NotFoundException;

import javax.persistence.*;
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

    public PersonDTO getPersonByPhone(String phone) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<PersonDTO> query = em.createQuery("SELECT new dtos.PersonDTO (p) from Person p join Phone ph where ph.number = :phone and p.id=ph.person.id", PersonDTO.class);
            query.setParameter("phone",phone);
            query.setMaxResults(1);
            PersonDTO personDTO;
            try {
                personDTO = query.getSingleResult();
            } catch (Exception e){
            throw new NotFoundException("Person with provided phone number not found");
            }
        return personDTO;
        } finally {
            em.close();
        }
    }

    public List<PersonDTO> getAllPersonsByZip(int zip) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<PersonDTO> query = em.createQuery("SELECT new dtos.PersonDTO(p) FROM Person p join Address a join CityInfo c WHERE c.zipCode=:zip and c.id=a.cityInfo.id and p.address.id=a.id", PersonDTO.class);
            query.setParameter("zip", zip);
            List<PersonDTO> personDTOs = query.getResultList();
                if(personDTOs.size()==0) {
                    throw new NotFoundException("Provided Zipcode not found");
                }
                return personDTOs;
        }finally {
            em.close();
        }
    }

    public int getNumberOfPersonsWithHobby(int hobbyId) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Hobby> query = em.createQuery("select h FROM Hobby h WHERE h.id=:hobbyId", Hobby.class);
            query.setParameter("hobbyId", hobbyId);
            try{
            Hobby hobby = query.getSingleResult();
            return hobby.getPersons().size();
        } catch (Exception e){
            throw new NotFoundException("Hobby with provieded id not found");
        }
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
        address = checkIfAddressExists(address,personDTO.getAddress().getCityInfo().getId());
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
            if(address.getId() < 1) {
                em.persist(address);
            } else {
                em.merge(address);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(person);
    }

    public PersonDTO editPerson(PersonDTO personDTO) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        try{
            Person person = em.find(Person.class,personDTO.getId());
            if(person == null){
                throw new NotFoundException("Person with provided id not found");
            }
            if(personDTO.getfName() != null) {
                person.setFirstName(personDTO.getfName());
            }
            if(personDTO.getlName() != null) {
                person.setLastName(personDTO.getlName());
            }
            if(personDTO.getEmail() != null) {
                person.setEmail(personDTO.getEmail());
            }

            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();

            return personDTO;

        } finally {
            em.close();
        }
    }
    public PersonDTO getPersonById(int id) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            Person person = em.find(Person.class,id);
            if (person == null) {
                throw new NotFoundException("Person with provied id not found");
            }
            return new PersonDTO(person);
        }finally {
            em.close();
        }
    }

    public PersonDTO editPersonAddress(int userId, AddressDTO addressDTO) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        try{
            Person person = em.find(Person.class,userId);
            if(person == null){
                throw new NotFoundException("Person with provided id not found");
            }

            Address oldAddress = em.find(Address.class,person.getAddress().getId());
            Address newAddress = new Address(addressDTO.getStreet(), addressDTO.getInfo());

            newAddress = checkIfAddressExists(newAddress,addressDTO.getCityInfo().getId());

            person.addAddress(newAddress);

            em.getTransaction().begin();
            if(newAddress.getId() < 1) {
                em.persist(newAddress);
            } else {
                em.merge(newAddress);
            }
            em.merge(person);
            em.getTransaction().commit();

            oldAddress.removePerson(person);
            if(oldAddress.getPersons().size()<1){
                deleteAddress(oldAddress.getId());
            }
            return new PersonDTO(person);
        }finally {
            em.close();
        }
    }
    public Address checkIfAddressExists(Address address, int cityInfoId){
        EntityManager em = emf.createEntityManager();
        Address addressFound;
        try{
            TypedQuery<Address> query = em.createQuery("SELECT a from Address a WHERE a.street=:street and a.additionalInfo=:info and a.cityInfo.id =:city",Address.class);
            query.setParameter("street",address.getStreet());
            query.setParameter("info",address.getAdditionalInfo());
            query.setParameter("city",cityInfoId);
            try{
                 addressFound = query.getSingleResult();
            }catch (NoResultException e){
                e.printStackTrace();
                address.addCityInfo(em.find(CityInfo.class,cityInfoId));
                return address;
            }
        }finally {
            em.close();
        }
        return addressFound;
    }
    public void deleteAddress(int id){
        EntityManager em = emf.createEntityManager();
        try {
            Address a = em.find(Address.class, id);
            em.getTransaction().begin();
            em.remove(a);
            em.getTransaction().commit();
        }finally {
            em.close();
        }

    }

    public PersonDTO addNewPhoneToPerson(int userId, PhoneDTO phoneDTO) throws NotFoundException {
        Phone phone = new Phone(phoneDTO.getNr(), phoneDTO.getDesc());
        EntityManager em = getEntityManager();
        Person person = em.find(Person.class,userId);
        if(person == null){
            throw new NotFoundException("Person with provided id not found");
        }
        person.addPhone(phone);
        try {
            em.getTransaction().begin();
            em.persist(phone);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(person);

    }

    public void deletePhoneFromPerson(int phoneId) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            Phone phone = em.find(Phone.class, phoneId);
            if(phone == null){
                throw new NotFoundException("Phone with provided id not found");
            }
            Person person = em.find(Person.class, phone.getPerson().getId());
            person.removePhone(phone);
            em.getTransaction().begin();
            em.remove(phone);
            em.getTransaction().commit();

        }finally {
            em.close();
        }
    }

    public PersonDTO addHobbyToPerson(int personId, int hobbyId) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        try{
            Person person = em.find(Person.class,personId);
            if(person == null){
                throw new NotFoundException("Person with provided id not found");
            }
            Hobby hobby = em.find(Hobby.class,hobbyId);
            if(hobby == null){
                throw new NotFoundException("Hobby with provided id not found");
            }
            person.addHobby(hobby);
            PersonDTO updated = new PersonDTO(person);
            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();
            return updated;
        } finally {
            em.close();
        }
    }

    public PersonDTO removeHobbyFromPerson(int personId, int hobbyId)throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        try{
            Person person = em.find(Person.class,personId);
            if(person == null){
                throw new NotFoundException("Person with provided id not found");
            }
            Hobby hobby = em.find(Hobby.class,hobbyId);
            if(hobby == null){
                throw new NotFoundException("Hobby with provided id not found");
            }
            person.removeHobby(hobby);
            PersonDTO updated = new PersonDTO(person);
            em.getTransaction().begin();
            em.merge(person);
            em.merge(hobby);
            em.getTransaction().commit();
            return updated;
        } finally {
            em.close();
        }

    }

    public List<HobbyDTO> getAllHobbies(){
        EntityManager em = emf.createEntityManager();
        try{
            TypedQuery<HobbyDTO> query = em.createQuery("select new dtos.HobbyDTO(h) FROM Hobby h",HobbyDTO.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

}
