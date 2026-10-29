package UDP;

import java.io.Serializable;

// Lop do de cung cap: GIU NGUYEN package, field, serialVersionUID.
public class Student implements Serializable {
    private static final long serialVersionUID = 20171107L;

    private String id, code, name, email;

    public Student(String id, String code, String name, String email) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.email = email;
    }

    // De trong anh co them constructor nay.
    public Student(String code) {
        this.code = code;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Student{" + "id=" + id + ", code=" + code
                + ", name=" + name + ", email=" + email + '}';
    }
}
