package hr.algebra.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.Comparator;

@JacksonXmlRootElement(localName = "director")
public class Director extends BaseEntity implements Comparable<Director>{
    @JacksonXmlProperty(localName = "name")
    private String name;
    @JacksonXmlProperty(localName = "surname")
    private String surname;
    @JacksonXmlProperty(localName = "yearOfBirth")
    private int yearOfBirth;
    @JacksonXmlProperty(localName = "nationality")
    private String nationality;
    @JacksonXmlProperty(localName = "biography")
    private String biography;

    public Director() {
        super(0);
    }

    public Director(String name, String surname, int yearOfBirth, String nationality, String biography) {
        super(0);
        this.name = name;
        this.surname = surname;
        this.yearOfBirth = yearOfBirth;
        this.nationality = nationality;
        this.biography = biography;
    }

    public Director(int id, String name, String surname, int yearOfBirth, String nationality, String biography) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.yearOfBirth = yearOfBirth;
        this.nationality = nationality;
        this.biography = biography;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFullName(){
        return name + " " + surname;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public int compareTo(Director director) {
        return Comparator.comparing(Director::getSurname, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Director::getName, String.CASE_INSENSITIVE_ORDER)
                .compare(this, director);
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
        return "Director{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", yearOfBirth=" + yearOfBirth +
                ", nationality='" + nationality + '\'' +
                ", biography='" + biography + '\'' +
                '}';
    }



}
