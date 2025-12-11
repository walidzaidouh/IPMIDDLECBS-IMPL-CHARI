package ma.ip.dto.thirdparty;


import lombok.Data;

@Data
public class LegalEntity{
    private BankIdentification bankIdentification;
    private Identification identification;
    private String lastCertificationDate ;
    private FinancialProfile financialProfile;
}
