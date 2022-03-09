/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import entities.*;
import utils.EMF_Creator;

/**
 *
 * @author Viktor and Vincent
 */
public class Populator {
    public static void populate(){
        EntityManagerFactory emf= EMF_Creator.createEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        Person person = new Person("test@test.dk","Karl","Larsen");
        Person person2 = new Person("test@test.dk","Hans","Larsen");
        Person person3 = new Person("test@test.dk","Viggo","Hansen");
        Phone phone1 = new Phone("2314121","DK");
        Phone phone2 = new Phone("9314121","DK");
        Hobby hobby = new Hobby("BasketBall","play ball");
        Hobby hobby2 = new Hobby("Tennis","play ball");
        Address address = new Address("Ulrikkenborg Alle","33");
        Address address2 = new Address("Ulrikkenborg Alle","230");
        Address address3 = new Address("Vægterparken","193");
        CityInfo cityInfo = new CityInfo(2800,"Kgs. Lyngby");
        CityInfo cityInfo2 = new CityInfo(2770,"Kastrup");

        person.addHobby(hobby);
        person2.addHobby(hobby);
        person3.addHobby(hobby2);

        person.addPhone(phone1);
        person2.addPhone(phone2);

        person.addAddress(address);
        person2.addAddress(address2);
        person3.addAddress(address3);

        address.addCityInfo(cityInfo);
        address2.addCityInfo(cityInfo);
        address3.addCityInfo(cityInfo2);

        try{
            em.getTransaction().begin();
            em.persist(person);
            em.persist(person2);
            em.persist(person3);
            em.persist(phone1);
            em.persist(phone2);
            em.persist(hobby);
            em.persist(hobby2);
            em.persist(address);
            em.persist(address2);
            em.persist(address3);
            em.persist(cityInfo);
            em.persist(cityInfo2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    public static void main(String[] args) {
        populate();
    }
}
