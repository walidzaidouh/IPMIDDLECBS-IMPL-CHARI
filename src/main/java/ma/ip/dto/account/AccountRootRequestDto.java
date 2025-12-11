package ma.ip.dto.account;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRootRequestDto {

    private String txId;
    private String rib;
    private BigDecimal amount;
    private String transferId;

    @Override
    public String toString() {
        return "{" +
                "\"txId\":\"" + txId  + "\"" +
                ",\"rib\":\"" + rib + "\"" +
                ",\"amount\":" + amount  +
                ",\"transferId\":\"" + transferId + "\"" +
                "}";
    }
}
