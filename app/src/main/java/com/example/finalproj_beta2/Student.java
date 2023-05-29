package com.example.finalproj_beta2;

public class Student extends Base_object{

    /**
     * @author		Paz Malul <malul.paz@gmail.com>

     * The student class helps organize information about the student.
     */

    public static final String ACTIVE = "active";
    public static final String GMAIL = "gmail";
    public static final String NAME = "name";
    public static final String SCHOOL = "school";
    public static final String CREDITS = "credits";

    int credits;

    public Student(String name, String school, String gmail) {
        super(name, school, gmail);
        this.credits = 0;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

}
