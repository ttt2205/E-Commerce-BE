package com.backend.e_commerce.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Convert {
    public static LocalDateTime convertStringToLocalDateTime(String dateTimeString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSSSSS");
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString + " 00:00:00.000000", formatter);
            return localDateTime;
        } catch (DateTimeParseException e) {
            System.out.println("Lỗi chuyển đổi ngày sinh: " + e.getMessage());
            return null; // Hoặc throw Exception nếu cần thiết
        }
    }
}
