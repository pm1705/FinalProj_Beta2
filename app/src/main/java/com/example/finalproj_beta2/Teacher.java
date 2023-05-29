package com.example.finalproj_beta2;

public class Teacher extends Base_object{

    /**
     * @author		Paz Malul <malul.paz@gmail.com>

     * the teacher class helps organize information about the teacher
     */

    public static final String ACTIVE = "active";
    public static final String GMAIL = "gmail";
    public static final String LEVEL = "level";
    public static final String NAME = "name";
    public static final String SCHOOL = "school";

    private int level;

    public Teacher(String name, String school, String gmail, int level) {
        super(name, school, gmail);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
