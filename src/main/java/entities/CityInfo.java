package entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "city_info")
@NamedQuery(name = "CityInfo.deleteAllRows", query = "DELETE from CityInfo")
public class CityInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "zip_code")
    private int zipCode;

    @Column(name = "city")
    private String city;

    @OneToMany(mappedBy = "cityInfo")
    private Set<Address> adresses = new HashSet<>();

    public CityInfo() {
    }

    public CityInfo(int zipCode, String city) {
        this.zipCode = zipCode;
        this.city = city;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Set<Address> getAdresses() {
        return adresses;
    }

    public void addAddress(Address address) {
        this.adresses.add(address);
        if(address.getCityInfo() != this){
            address.addCityInfo(this);
        }
    }

}
