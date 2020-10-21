/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.PersonDTO;
import dto.PersonsDTO;
import entities.CityInfo;
import entities.Hobby;
import entities.Phone;
import java.util.List;

/**
 *
 * @author jacobsimonsen
 */
public interface IPersonFacade {
  public PersonDTO addPerson(String firstName, String lastName, String email, String street, String zipCode, String city, List<Phone> phones, List<Hobby> hobbies);
  public PersonDTO deletePerson(int id);
  public PersonDTO getPerson(String phoneNumber);
  public PersonsDTO getAllPersons();
  public PersonDTO editPerson(PersonDTO p);
  public CityInfo getAllZipcodes();
  public PersonsDTO getAllPersonsHobbies();
  public PersonsDTO getAllPersonsCity();
  public int getHobbyCount();
  public long personCount();
}
