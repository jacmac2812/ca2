package facades;

import dto.CityInfosDTO;
import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public PersonDTO addPerson(String firstName, String lastName, String email, String street, String zipCode, String city, List<Phone> phones, List<Hobby> hobbies) {
        EntityManager em = emf.createEntityManager();
        try {

            Person p = new Person(firstName, lastName, email);
            Address a = new Address(street);
            CityInfo ci = new CityInfo(zipCode, city);

            for (Phone ph : phones) {
                p.addPhone(ph);
            }
            for (Hobby h : hobbies) {
                p.addHobby(h);
            }
            a.addPerson(p);
            em.getTransaction().begin();

            TypedQuery<CityInfo> query
                    = em.createQuery("SELECT z FROM CityInfo z WHERE z.zipcode =:zipCode", CityInfo.class);
            query.setParameter("zipCode", zipCode);
            CityInfo cityInfo = query.getSingleResult();

            if (cityInfo != null) {
                cityInfo.addAddress(a);
                em.persist(cityInfo);
            } else {
                ci.addAddress(a);
                em.persist(ci);
            }
            em.getTransaction().commit();
            return new PersonDTO(p);

        } finally {
            em.close();
        }

    }

    @Override
    public PersonDTO deletePerson(int id) {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, id);

        try {
            em.getTransaction().begin();
//            
//            for(Hobby h : person.getHobbies()){
//            person.removeHobby(h);
//            }
            for (Phone ph : person.getPhones()) {
                em.remove(ph);
            }
            em.remove(person);
            em.remove(person.getAddress());

            em.getTransaction().commit();
            PersonDTO pDTO = new PersonDTO(person);
            return pDTO;
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) {
        EntityManager em = emf.createEntityManager();

        try {
            Person person = em.find(Person.class, p.getId());

            person.setFirstName(p.getFirstName());
            person.setLastName(p.getLastName());
            person.setEmail(p.getEmail());
            person.getAddress().setStreet(p.getStreet());
            person.getAddress().getCityInfo().setZipcode(p.getZipCode());
            person.getAddress().getCityInfo().setCity(p.getCity());

            for (Hobby h : p.getHobbies()) {
                person.addHobby(h);
            }
            for (Phone ph : p.getPhones()) {
                person.addPhone(ph);
            }
            PersonDTO pDTO = new PersonDTO(person);
            return pDTO;
        } finally {
            em.close();
        }

    }

    @Override
    public PersonDTO getPerson(String phoneNumber) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Person> query
                    = em.createQuery("SELECT p FROM Person p JOIN p.phones ph WHERE ph.number = :phoneNumber", Person.class);
            query.setParameter("phoneNumber", phoneNumber);
            Person p = query.getSingleResult();
            return new PersonDTO(p);

        } finally {
            em.close();
        }

    }

    @Override
    public PersonsDTO getAllPersons() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Person> query
                    = em.createQuery("SELECT p FROM Person p", Person.class);
            List<Person> personEntities = query.getResultList();
            PersonsDTO all = new PersonsDTO(personEntities);
            return all;
        } finally {
            em.close();
        }
    }

    @Override
    public CityInfosDTO getAllZipcodes() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<CityInfo> query
                    = em.createQuery("SELECT z FROM CityInfo z", CityInfo.class);
            List<CityInfo> zipCodes = query.getResultList();
            CityInfosDTO all = new CityInfosDTO(zipCodes);
            return all;
        } finally {
            em.close();
        }
    }

    @Override
    public PersonsDTO getAllPersonsHobbies(String hobby) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Person> query
                    = em.createQuery("SELECT p FROM Person p JOIN p.hobbies h WHERE h.name =:hobby", Person.class);
            query.setParameter("hobby", hobby);
            List<Person> personEntities = query.getResultList();
            PersonsDTO all = new PersonsDTO(personEntities);
            return all;
        } finally {
            em.close();
        }
    }

    @Override
    public PersonsDTO getAllPersonsCity(String city) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Person> query
                    = em.createQuery("SELECT p FROM Person p JOIN p.address a JOIN a.cityInfo ci WHERE ci.city =:city", Person.class);
            query.setParameter("city", city);
            List<Person> personEntities = query.getResultList();
            PersonsDTO all = new PersonsDTO(personEntities);
            return all;
        } finally {
            em.close();
        }
    }

    @Override
    public long getHobbyCount(String hobbyName) {
        EntityManager em = emf.createEntityManager();
        try {
            Query personCount = em.createQuery("SELECT COUNT(p) FROM Person p JOIN p.hobbies h WHERE h.name=:hobbyName");
            personCount.setParameter("hobbyName", hobbyName);
            long result = (long) personCount.getSingleResult();
            return result;
        } finally {
            em.close();
        }
    }

    @Override
    public long personCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long personCount = (long) em.createQuery("SELECT COUNT(p) FROM Person p").getSingleResult();
            return personCount;
        } finally {
            em.close();
        }
    }
}
