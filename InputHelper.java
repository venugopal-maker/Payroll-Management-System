package com.payroll.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputHelper {
    private static final Scanner SC = new Scanner(System.in);

    public static String str(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("  Value cannot be empty.");
        }
    }

    /** Returns empty string if user just presses Enter (used for "keep current" on update). */
    public static String optional(String prompt) {
        System.out.print(prompt);
        return SC.nextLine().trim();
    }

    public static int integer(String prompt, int min, int max) {
        while (true) {
            try {
                int v = Integer.parseInt(str(prompt));
                if (v >= min && v <= max) return v;
                System.out.println("  Enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number.");
            }
        }
    }

    public static double decimal(String prompt, double min) {
        while (true) {
            try {
                double v = Double.parseDouble(str(prompt));
                if (v >= min) return v;
                System.out.println("  Value must be at least " + min + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number.");
            }
        }
    }

    public static LocalDate date(String prompt) {
        while (true) {
            try {
                return LocalDate.parse(str(prompt));
            } catch (DateTimeParseException e) {
                System.out.println("  Use format YYYY-MM-DD.");
            }
        }
    }

    public static String email(String prompt) {
        while (true) {
            String s = str(prompt);
            if (s.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) return s;
            System.out.println("  Invalid email.");
        }
    }

    public static String phone(String prompt) {
        while (true) {
            String s = str(prompt);
            if (s.matches("\\d{10}")) return s;
            System.out.println("  Phone must be 10 digits.");
        }
    }
}
