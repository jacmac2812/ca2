package facades;

import dto.CityInfosDTO;
import dto.HobbiesDTO;
import dto.HobbyDTO;
import dto.PersonDTO;
import dto.PersonsDTO;
import dto.PhoneDTO;
import dto.PhonesDTO;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import exceptions.CityNotFoundException;
import exceptions.HobbyNotFoundException;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
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
    public PersonDTO addPerson(String firstName, String lastName, String email, String street, String zipCode, String city, List<PhoneDTO> phones, List<HobbyDTO> hobbies) throws MissingInputException {
        if (firstName.length() == 0 || lastName.length() == 0) {
            throw new MissingInputException("First name and/or last name is missing");
        }
        if (email.length() == 0 || email.contains("@") == false) {
            throw new MissingInputException("Email missing and/or does not contain @");
        }
        if (street.length() == 0 || zipCode.length() == 0 || zipCode.length() > 4 || city.length() == 0) {
            throw new MissingInputException("Street, zipcode or city is missing");
        }

        EntityManager em = emf.createEntityManager();
        try {

            Person p = new Person(firstName, lastName, email);
            Address a = new Address(street);

            for (PhoneDTO phDTO : phones) {
                Phone ph = new Phone(phDTO.getNumber(), phDTO.getDescription());
                p.addPhone(ph);
            }
            for (HobbyDTO hDTO : hobbies) {
                Hobby h = new Hobby(hDTO.getName(), hDTO.getDescription());
                p.addHobby(h);
            }
            a.addPerson(p);
            em.getTransaction().begin();

            TypedQuery<CityInfo> query
                    = em.createQuery("SELECT z FROM CityInfo z WHERE z.zipcode =:zipCode", CityInfo.class);
            query.setParameter("zipCode", zipCode);
            List<CityInfo> cityInfos = query.getResultList();

            if (cityInfos.size() > 0) {
                CityInfo ci2 = cityInfos.get(0);
                ci2.addAddress(a);
                em.persist(ci2);
            } else {
                CityInfo ci = new CityInfo(zipCode, city);
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
    public PersonDTO deletePerson(int id) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();

        try {
            Person person = em.find(Person.class, id);
            if (person == null) {
                throw new PersonNotFoundException("Could not delete. Person not found");
            } else {
                em.getTransaction().begin();

                for (Phone ph : person.getPhones()) {
                    em.remove(ph);
                }
                em.remove(person);
                em.remove(person.getAddress());

                em.getTransaction().commit();
                PersonDTO pDTO = new PersonDTO(person);
                return pDTO;
            }

        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) throws MissingInputException, PersonNotFoundException {
        if (p.getFirstName().length() == 0 || p.getLastName().length() == 0) {
            throw new MissingInputException("First name and/or last name is missing");
        }
        if (p.getEmail().length() == 0 || p.getEmail().contains("@") == false) {
            throw new MissingInputException("Email missing and/or does not contain @");
        }
        if (p.getStreet().length() == 0 || p.getZipCode().length() == 0 || p.getZipCode().length() > 4 || p.getCity().length() == 0) {
            throw new MissingInputException("Street, zipcode or city is missing");
        }

        EntityManager em = emf.createEntityManager();

        try {
            Person person = em.find(Person.class, p.getId());
            if (person == null) {
                throw new PersonNotFoundException("Could not find person");
            } else {

                person.setFirstName(p.getFirstName());
                person.setLastName(p.getLastName());
                person.setEmail(p.getEmail());
                person.getAddress().setStreet(p.getStreet());
                person.getAddress().getCityInfo().setZipcode(p.getZipCode());
                person.getAddress().getCityInfo().setCity(p.getCity());

                for (HobbyDTO hDTO : p.getHobbies()) {
                    Hobby h = new Hobby(hDTO.getName(), hDTO.getDescription());
                    person.addHobby(h);
                }
                for (PhoneDTO phDTO : p.getPhones()) {
                    Phone ph = new Phone(phDTO.getNumber(), phDTO.getDescription());
                    person.addPhone(ph);
                }
                PersonDTO pDTO = new PersonDTO(person);
                return pDTO;
            }

        } finally {
            em.close();
        }

    }

    @Override
    public PersonDTO getPerson(String phoneNumber) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Person> query
                    = em.createQuery("SELECT p FROM Person p JOIN p.phones ph WHERE ph.number = :phoneNumber", Person.class);
            query.setParameter("phoneNumber", phoneNumber);
            Person p = query.getSingleResult();

            return new PersonDTO(p);

        } catch (NoResultException ex) {
            throw new PersonNotFoundException("Could not find person");
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
    public PersonsDTO getAllPersonsHobbies(String hobby) throws HobbyNotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Person> query
                    = em.createQuery("SELECT p FROM Person p JOIN p.hobbies h WHERE h.name =:hobby", Person.class);
            query.setParameter("hobby", hobby);
            List<Person> personEntities = query.getResultList();
            if (personEntities.isEmpty()) {
                throw new HobbyNotFoundException("Could not find any persons with the given hobby");
            } else {
                PersonsDTO all = new PersonsDTO(personEntities);
                return all;
            }

        } finally {
            em.close();
        }
    }

    @Override
    public PersonsDTO getAllPersonsCity(String city) throws CityNotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Person> query
                    = em.createQuery("SELECT p FROM Person p JOIN p.address a JOIN a.cityInfo ci WHERE ci.city =:city", Person.class);
            query.setParameter("city", city);
            List<Person> personEntities = query.getResultList();
            if (personEntities.isEmpty()) {
                throw new CityNotFoundException("Could not find any persons living in the given city");
            } else {
                PersonsDTO all = new PersonsDTO(personEntities);
                return all;
            }

        } finally {
            em.close();
        }
    }

    @Override
    public long getHobbyCount(String hobbyName) throws HobbyNotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            Query personCount = em.createQuery("SELECT COUNT(p) FROM Person p JOIN p.hobbies h WHERE h.name=:hobbyName");
            personCount.setParameter("hobbyName", hobbyName);
            long result = (long) personCount.getSingleResult();
            if (result == 0) {
                throw new HobbyNotFoundException("There are no persons with the given hobby");
            } else {
                return result;
            }

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
