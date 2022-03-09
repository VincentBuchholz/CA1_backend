package entities;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "street")
    private String street;
    @Column(name = "additional_info")
    private String additionalInfo;

    @OneToMany(mappedBy = "address")
    private Set<Person> persons = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "city_info_id")
    private CityInfo cityInfo;

    public Address() {
    }

    public Address(String street, String additionalInfo) {
        this.street = street;
        this.additionalInfo = additionalInfo;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Set<Person> getPersons() {
        return this.persons;
    }

    public void addPerson(Person p) {
        this.persons.add(p);
        if(p.getAddress() != this){
            p.addAddress(this);
        }
    }

    public CityInfo getCityInfo() {
        return cityInfo;
    }
    public void addCityInfo(CityInfo c) {
        this.cityInfo=c;
        if(!c.getAdresses().contains(this)){
            c.addAddress(this);
        }
    }
}
