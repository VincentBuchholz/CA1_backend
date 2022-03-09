package entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "email")
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;

    @ManyToMany
    private Set<Hobby> hobbies = new HashSet<>();

    @OneToMany(mappedBy = "person")
    private Set<Phone> phones = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;


    public Person() {
    }

    public Person(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Hobby> getHobbies() {
        return hobbies;
    }

    public void addHobby(Hobby h) {
        this.hobbies.add(h);
        if(!h.getPersons().contains(this)){
           h.addPerson(this);
        }
    }

    public Set<Phone> getPhones() {
        return phones;
    }
    public void addPhone(Phone p) {
        this.phones.add(p);
        if(p.getPerson()!=this){
            p.addPerson(this);
        }
    }

    public Address getAddress() {
        return address;
    }
    public void addAddress(Address a) {
        this.address=a;
        if(!a.getPersons().contains(this)){
            a.addPerson(this);
        }
    }
}
