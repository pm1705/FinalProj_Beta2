package com.example.finalproj_beta2;

public class Student_print {
    public static final String COPIES = "copies";
    public static final String COLORFUL = "colorful";
    public static final String VERTICAL = "vertical";
    public static final String DOUBLE_SIDED = "double_sided";
    public static final String FILE_ID = "file_id";
    public static final String USER_ID = "user_id";
    public static final String DATE_REQUESTED = "date_requested";
    public static final String USER_NAME = "user_name";

    boolean printed;
    int copies;
    boolean colorful;
    boolean vertical;
    boolean double_sided;
    String file_id;
    String user_id;
    String date_requested;
    String user_name;

    public Student_print(boolean printed, int copies, boolean colorful, boolean vertical, boolean double_sided, String file_id, String user_id, String date_requested, String user_name) {
        this.printed = printed;
        this.copies = copies;
        this.colorful = colorful;
        this.vertical = vertical;
        this.double_sided = double_sided;
        this.file_id = file_id;
        this.user_id = user_id;
        this.date_requested = date_requested;
        this.user_name = user_name;
    }

    public static String getCOPIES() {
        return COPIES;
    }

    public static String getCOLORFUL() {
        return COLORFUL;
    }

    public static String getVERTICAL() {
        return VERTICAL;
    }

    public static String getDoubleSided() {
        return DOUBLE_SIDED;
    }

    public static String getFileId() {
        return FILE_ID;
    }

    public static String getUserId() {
        return USER_ID;
    }

    public static String getDateRequested() {
        return DATE_REQUESTED;
    }

    public static String getUserName() {
        return USER_NAME;
    }

    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public boolean isColorful() {
        return colorful;
    }

    public void setColorful(boolean colorful) {
        this.colorful = colorful;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public boolean isDouble_sided() {
        return double_sided;
    }

    public void setDouble_sided(boolean double_sided) {
        this.double_sided = double_sided;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_requested() {
        return date_requested;
    }

    public void setDate_requested(String date_requested) {
        this.date_requested = date_requested;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


}
