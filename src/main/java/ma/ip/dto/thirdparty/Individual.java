package ma.ip.dto.thirdparty;

import lombok.Data;

import java.util.List;


@Data
public class Individual{

    private String sex;
    private Title title;
    private MaritalStatus maritalStatus;
    private String childrenNumber;
    private String birthDate;
    private String birthPlace;
    private BirthCountry birthCountry;
    private ResidenceCountry residenceCountry;
    private List<Occupation> occupation;
    private PrimaryNationalityCountry primaryNationalityCountry;
    private Identification identification;
    private String lastCertificationDate ;
}
