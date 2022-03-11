package dtos;

import entities.Address;
import entities.CityInfo;
import entities.Person;

import java.util.*;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressDTO that = (AddressDTO) o;
        return id == that.id && Objects.equals(street, that.street) && Objects.equals(info, that.info) && Objects.equals(cityInfo, that.cityInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, street, info, cityInfo);
    }
}
