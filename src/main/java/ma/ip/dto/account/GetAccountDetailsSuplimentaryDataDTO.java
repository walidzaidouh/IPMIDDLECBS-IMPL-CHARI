package ma.ip.dto.account;

import lombok.Data;

@Data
public class GetAccountDetailsSuplimentaryDataDTO {

    private String status;
    private String txId;
    private String transferId;
    private String transferBankId;
    private String rib;
    private String currency;
    private String accountType;
    private boolean convertible;
    private AccountResponseDTO account;
}
