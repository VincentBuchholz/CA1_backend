package dtos;

import entities.Address;
import entities.Hobby;
import entities.Person;
import entities.Phone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PersonDTO {
    private int id;
    private String fName;
    private String lName;
    private String email;
    private Set<HobbyDTO> hobbies = new HashSet<>();
    private Set<PhoneDTO> phones = new HashSet<>();
    private AddressDTO address;

    public PersonDTO(Person person) {
        this.id = person.getId();
        this.fName = person.getFirstName();
        this.lName = person.getLastName();
        this.email = person.getEmail();
        for (Hobby hobby : person.getHobbies()) {
            this.hobbies.add(new HobbyDTO(hobby));
        }
        for (Phone phone: person.getPhones()) {
            this.phones.add(new PhoneDTO(phone));
        }
         this.address = new AddressDTO(person.getAddress());
    }

    public static List<PersonDTO> getDtos(List<Person> personList){
        List<PersonDTO> personDTOS = new ArrayList();
        personList.forEach(person->personDTOS.add(new PersonDTO(person)));
        return personDTOS;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<HobbyDTO> getHobbiesDTOs() {
        return hobbies;
    }

    public void setHobbies(Set<HobbyDTO> hobbyDTOs) {
        this.hobbies = hobbyDTOs;
    }

    public Set<PhoneDTO> getPhones() {
        return phones;
    }

    public void setPhones(Set<PhoneDTO> phones) {
        this.phones = phones;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

}
