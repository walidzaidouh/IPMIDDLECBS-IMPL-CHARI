package ma.ip.dto.accounting.response;


import lombok.Data;

import java.util.Map;

@Data
public class AccountingRootResponse {

    private String refCre;
    private String code;
    private Map<String, Object> outputData;
}
