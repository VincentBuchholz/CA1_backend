package dtos;

import entities.Hobby;
import entities.Person;

import java.util.*;

public class HobbyDTO {
    private int id;
    private String name;
    private String desc;

    public HobbyDTO(Hobby hobby) {
        this.id = hobby.getId();
        this.name = hobby.getName();
        this.desc = hobby.getDescription();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HobbyDTO hobbyDTO = (HobbyDTO) o;
        return id == hobbyDTO.id && Objects.equals(name, hobbyDTO.name) && Objects.equals(desc, hobbyDTO.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, desc);
    }
}
