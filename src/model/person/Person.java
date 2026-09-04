package model.person;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public abstract class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private LocalDate birthDate;
    private String documentType;
    private String documentNumber;


    public Person(String name, LocalDate birthDate,
                  String documentType, String documentNumber) {

        this.name = name;
        this.birthDate = birthDate;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
}