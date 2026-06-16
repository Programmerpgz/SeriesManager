package hr.algebra.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "actor")
public class Actor extends BaseEntity implements Comparable<Actor>{
    @JacksonXmlProperty(localName = "name")
    private String name;
    @JacksonXmlProperty(localName = "surname")
    private String surname;
    @JacksonXmlProperty(localName = "yearOfBirth")
    private int yearOfBirth;
    @JacksonXmlProperty(localName = "nationality")
    private String nationality;

    public Actor(String name, String surname, int yearOfBirth, String nationality) {
        super(0);
        this.name = name;
        this.surname = surname;
        this.yearOfBirth = yearOfBirth;
        this.nationality = nationality;
    }

    public Actor(int id, String name, String surname, int yearOfBirth, String nationality) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.nationality = nationality;
        this.yearOfBirth = yearOfBirth;
    }

    public Actor(int id, String name, String surname) {
        super(id);
        this.name = name;
        this.surname = surname;
    }

    public Actor() {
        super(0);
    }

    public String getName() {
        return name;
    }
    public String getFullName() {
        return name + " " + surname;
    }

    public String getSurname() {
        return surname;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @Override
    public int compareTo(Actor actor) {
            int bySurname=this.surname.compareToIgnoreCase(actor.surname);
            if(bySurname != 0){
                return bySurname;
            }
            return this.name.compareToIgnoreCase(actor.name);
        }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Actor{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", yearOfBirth=" + yearOfBirth +
                ", nationality='" + nationality + '\'' +
                '}';
    }
}
