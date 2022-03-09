package facades;

import dtos.PersonDTO;
import entities.Person;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

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
        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p", Person.class);
        List<Person> persons = query.getResultList();
        return PersonDTO.getDtos(persons);
    }
//
//    public static void main(String[] args) {
//        emf = EMF_Creator.createEntityManagerFactory();
//        FacadeExample fe = getFacadeExample(emf);
//        fe.getAll().forEach(dto->System.out.println(dto));
//    }

}
