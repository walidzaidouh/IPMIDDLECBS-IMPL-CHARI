package ma.ip.dto.account;

import lombok.Data;

@Data
public class AccountResponseDTO {

    private String fullName;
    private String segmentCode;
    private String residenceCountry;
    private String localisation;
    private String birthDate;
    private String placeBirth;
    private String birthCountry;
}
