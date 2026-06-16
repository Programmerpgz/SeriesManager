package hr.algebra.models;

public class User extends BaseEntity implements Comparable<User>{
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
    private Role role;

    public User(){
        super(0);
    }

    public User(String username, String password, String name, String surname, String email, Role role) {
        super(0);
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
    }

    public User(int id, String username, String password, String surname, String name, String email, Role role) {
        super(id);
        this.username = username;
        this.password = password;
        this.surname = surname;
        this.name = name;
        this.email = email;
        this.role = role;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
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
        return surname + " " + name;

    }
    public String getEmail() {
        return email;
    }
    public Role getRole() {
        return role;
    }
    public boolean isAdministrator(){
        return Role.ADMINISTRATOR.equals(role);
    }

    @Override
    public int compareTo(User user) {
        return this.username.compareToIgnoreCase(user.username);
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
        return  "User{id=" + getId() +
                ", username='" + username + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", role=" + role + '}';
    }
}
