package org.example;

public enum Status {
    TO_DO("К выполнению"),
    IN_PROGRESS("В процессе"),
    DONE("Выполнена");

    private final String russianName;

    Status(String russianName) {
        this.russianName = russianName;
    }

    public String getRussianName() {
        return russianName;
    }
}