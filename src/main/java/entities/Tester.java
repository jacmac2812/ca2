/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author jacobsimonsen
 */
public class Tester {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();
        
        Person person = new Person("Hans", "Hansen", "Hans@hej.dk");
        Person person2 = new Person("Kaj", "Hansen", "Kaj@hej.dk");
        Person person1 = new Person("Bent", "Hansen", "Bent@hej.dk");
        
        
        Address address = new Address("Hovedgade 27");
        Address address2 = new Address("Lillegade 90");
        Address address3 = new Address("Storegade 110");
        
        Phone phone = new Phone("12345678", "nokia 3310");
        Phone phone2 = new Phone("87654321", "Samsung Galaxy A50");
        Phone phone3 = new Phone("99998888", "Iphone 11 Pro Max");
        
        Hobby hobby = new Hobby("Svømning", "100meter");
        Hobby hobby2 = new Hobby("Fodbold", "9 Mands");
        Hobby hobby3 = new Hobby("Håndbold", "For Senor");
        
        CityInfo cityInfo = new CityInfo(3000, "Helsingør");
        CityInfo cityInfo2 = new CityInfo(2240, "KBH");
        CityInfo cityInfo3 = new CityInfo(9000, "Aalborg");
        
        person.addHobby(hobby);
        person.addPhone(phone);
        address.addPerson(person);
        cityInfo.addAddress(address);
        
        em.getTransaction().begin();
        em.persist(person);
//        em.persist(address);
//        em.persist(cityInfo);
        
        em.getTransaction().commit();
        
    }
    
}
