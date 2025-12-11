package ma.ip.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class AccountRootResponse {

    private String code;

    private String message;

    private GetAccountDetailsSuplimentaryDataDTO outputData;
}
