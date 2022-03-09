package dtos;

import entities.Address;
import entities.CityInfo;
import entities.Person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddressDTO {
    private int id;
    private String street;
    private String info;
    private CityInfoDTO cityInfo;

    public AddressDTO(Address address) {
        this.id = address.getId();
        this.street = address.getStreet();
        this.info = address.getAdditionalInfo();
        this.cityInfo = new CityInfoDTO(address.getCityInfo());
    }

    public static List<AddressDTO> getDtos(List<Address> addressList){
        List<AddressDTO> addressDTOS = new ArrayList();
        addressList.forEach(address->addressDTOS.add(new AddressDTO(address)));
        return addressDTOS;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public CityInfoDTO getCityInfo() {
        return cityInfo;
    }

    public void setCityInfo(CityInfoDTO cityInfo) {
        this.cityInfo = cityInfo;
    }
}
