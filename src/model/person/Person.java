package model.person;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

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


    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public int getAge(){
        if(birthDate != null)
            return java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears();
        return 0;
    }

    // Two people are the same when their personal data matches (document numbers alone are not unique in the data)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person other = (Person) o;
        return Objects.equals(name, other.name)
                && Objects.equals(birthDate, other.birthDate)
                && Objects.equals(documentType, other.documentType)
                && Objects.equals(documentNumber, other.documentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, birthDate, documentType, documentNumber);
    }

}