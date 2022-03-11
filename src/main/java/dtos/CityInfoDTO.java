package dtos;

import entities.Address;
import entities.CityInfo;
import entities.Person;

import java.util.*;

public class CityInfoDTO {
    private int id;
    private int zip;
    private String city;

    public CityInfoDTO(CityInfo cityInfo) {
       this.id = cityInfo.getId();
       this.zip = cityInfo.getZipCode();
       this.city = cityInfo.getCity();
    }

    public static List<CityInfoDTO> getDtos(List<CityInfo> cityInfoList){
        List<CityInfoDTO> cityInfoDTOs = new ArrayList();
        cityInfoList.forEach(cityInfo->cityInfoDTOs.add(new CityInfoDTO(cityInfo)));
        return cityInfoDTOs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityInfoDTO that = (CityInfoDTO) o;
        return id == that.id && zip == that.zip && Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, zip, city);
    }
}
