package ma.ip.dto.thirdparty;


import lombok.Data;

@Data
public class MainHolder{
    private String personId;
    private Individual individual;
    private LegalEntity legalEntity;
    private ResidenceCountry residenceCountry;
}
