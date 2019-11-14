package com.danielogbuti.androideatitserver.common;

import com.danielogbuti.androideatitserver.model.User;

public class Common {
    public static User currentUser;

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static final int PICK_IMAGE_REQUEST = 23;

    public static String convertToStatus(String status) {
        if (status.equals("0")){
            return "Placed";
        }else if (status.equals("1")){
            return "On my way";
        }else {
            return "Shipped";
        }
    }
}
