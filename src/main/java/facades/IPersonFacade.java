/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.CityInfosDTO;
import dto.HobbiesDTO;
import dto.HobbyDTO;
import dto.PersonDTO;
import dto.PersonsDTO;
import dto.PhoneDTO;
import dto.PhonesDTO;
import entities.CityInfo;
import entities.Hobby;
import entities.Phone;
import exceptions.CityNotFoundException;
import exceptions.HobbyNotFoundException;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import java.util.List;

/**
 *
 * @author jacobsimonsen
 */
public interface IPersonFacade {
  public PersonDTO addPerson(String firstName, String lastName, String email, String street, String zipCode, String city, List<PhoneDTO> phones, List<HobbyDTO> hobbies) throws MissingInputException;
  public PersonDTO deletePerson(int id) throws PersonNotFoundException;
  public PersonDTO getPerson(String phoneNumber) throws PersonNotFoundException;
  public PersonsDTO getAllPersons();
  public PersonDTO editPerson(PersonDTO p) throws MissingInputException, PersonNotFoundException;
  public CityInfosDTO getAllZipcodes();
  public PersonsDTO getAllPersonsHobbies(String hobby) throws HobbyNotFoundException;
  public PersonsDTO getAllPersonsCity(String city) throws CityNotFoundException;
  public long getHobbyCount(String hobbyName) throws HobbyNotFoundException;
  public long personCount();
}
