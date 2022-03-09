package dtos;

import entities.Hobby;
import entities.Person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HobbyDTO {
    private int id;
    private String name;
    private String desc;
    private Set<Person> persons = new HashSet<>();

    public HobbyDTO(Hobby hobby) {
        this.id = hobby.getId();
        this.name = hobby.getName();
        this.desc = hobby.getDescription();
        this.persons = hobby.getPersons();
    }

    public static List<HobbyDTO> getDtos(List<Hobby> hobbyList){
        List<HobbyDTO> hobbyDTOS = new ArrayList();
        hobbyList.forEach(hobby->hobbyDTOS.add(new HobbyDTO(hobby)));
        return hobbyDTOS;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Set<Person> getPersons() {
        return persons;
    }

    public void setPersons(Set<Person> persons) {
        this.persons = persons;
    }
}
