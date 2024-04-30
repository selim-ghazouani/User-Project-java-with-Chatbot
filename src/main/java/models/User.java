package models;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class User {
    private int id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String token;
    private String photo;
    private String[] role;
    private int code;

    private String fullname;

    public User() {

    }

    public User(int id, String username, String email, String phone, String password, String token,String photo, int code, String[] role, String fullname) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.token = token;
        this.photo = photo;
        this.code = code;
        this.role = role;
        this.fullname = fullname;
    }



    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {

            this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {

            this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public Patient getPatient() {
        // You may need to handle cases where this method is called for a non-patient user
        return null;
    }

    public Doctor getDoctor() {
        // You may need to handle cases where this method is called for a non-patient user
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public void setPassword(String password) {
            this.password = password;

    }

    public String getToken() {
        return token;
    }

    public boolean isBlocked() {
        // Check if the token is "0"
        return "0".equals(token);
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setTokento0() {
        this.token = "0";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getId() == user.getId() && getCode() == user.getCode() && Objects.equals(getUsername(), user.getUsername()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getPhone(), user.getPhone()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getToken(), user.getToken()) && Objects.equals(getPhoto(), user.getPhoto()) && Objects.equals(role, user.role) && Objects.equals(getFullname(), user.getFullname());
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", code=" + code +
                ", role=" + Arrays.toString(role) +
                ", fullname='" + fullname + '\'' +
                '}';
    }


    public String getRole() {
        return role.length > 0 ? role[0] : null;
    }


    public void setRole(String[] role) {
        this.role = role;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


    public static boolean isValidEmail(String email) {
        // Regular expression for validating email format
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.length() == 8;
    }
    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
}
