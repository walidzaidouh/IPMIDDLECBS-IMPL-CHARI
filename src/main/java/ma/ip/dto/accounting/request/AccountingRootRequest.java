package ma.ip.dto.accounting.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountingRootRequest {

    private String sens;
    private String transferId;
    private String transferBankId;
    private String ribSender;
    private String ribReceiver;
    private String txId;
    private String fullNameSender;
    private String fullNameReceiver;
    private String date;
    private BigDecimal amount;

    @Override
    public String toString() {
        return "{" +
                "\"sens\":\"" + sens  + "\"" +
                ",\"transferId\":\"" + transferId  + "\"" +
                ",\"transferBankId\":\"" + transferBankId  + "\"" +
                ",\"ribSender\":\"" + ribSender  + "\"" +
                ",\"ribReceiver\":\"" + ribReceiver  + "\"" +
                ",\"txId\":\"" + txId  + "\"" +
                ",\"fullNameSender\":\"" + fullNameSender  + "\"" +
                ",\"fullNameReceiver\":\"" + fullNameReceiver  + "\"" +
                ",\"date\":\"" + date  + "\"" + ",\"amount\":\"" + amount+ "\""+
                "}";
    }
}
