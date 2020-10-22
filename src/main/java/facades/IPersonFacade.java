/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.CityInfosDTO;
import dto.HobbiesDTO;
import dto.PersonDTO;
import dto.PersonsDTO;
import dto.PhonesDTO;
import entities.CityInfo;
import entities.Hobby;
import entities.Phone;
import java.util.List;

/**
 *
 * @author jacobsimonsen
 */
public interface IPersonFacade {
  public PersonDTO addPerson(String firstName, String lastName, String email, String street, String zipCode, String city, PhonesDTO phones, HobbiesDTO hobbies);
  public PersonDTO deletePerson(int id);
  public PersonDTO getPerson(String phoneNumber);
  public PersonsDTO getAllPersons();
  public PersonDTO editPerson(PersonDTO p);
  public CityInfosDTO getAllZipcodes();
  public PersonsDTO getAllPersonsHobbies(String hobby);
  public PersonsDTO getAllPersonsCity(String city);
  public long getHobbyCount(String hobbyName);
  public long personCount();
}
