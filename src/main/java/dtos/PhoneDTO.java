package dtos;

import entities.Person;
import entities.Phone;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhoneDTO {
    private int id;
    private String nr;
    private String desc;
    private int personId;

    public PhoneDTO(Phone phone) {
        this.id = phone.getId();
        this.nr = phone.getNumber();
        this.desc = phone.getDescription();
        this.personId = phone.getPerson().getId();
    }

    public static List<PhoneDTO> getDtos(List<Phone> phoneList){
        List<PhoneDTO> phoneDTOs = new ArrayList();
        phoneList.forEach(phone->phoneDTOs.add(new PhoneDTO(phone)));
        return phoneDTOs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPerson(int id) {
        this.personId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneDTO phoneDTO = (PhoneDTO) o;
        return id == phoneDTO.id && personId == phoneDTO.personId && Objects.equals(nr, phoneDTO.nr) && Objects.equals(desc, phoneDTO.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nr, desc, personId);
    }
}

