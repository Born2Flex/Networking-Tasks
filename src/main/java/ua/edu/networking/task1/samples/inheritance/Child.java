package ua.edu.networking.task1.samples.inheritance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Child extends Parent {
    private String lastName;
    private String hobby;

    public Child(int id, String firstName, String lastName, String hobby) {
        super(id, firstName);
        this.lastName = lastName;
        this.hobby = hobby;
    }
}
