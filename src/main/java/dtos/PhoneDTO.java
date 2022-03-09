package dtos;

import entities.Person;
import entities.Phone;

import java.util.ArrayList;
import java.util.List;

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
}

