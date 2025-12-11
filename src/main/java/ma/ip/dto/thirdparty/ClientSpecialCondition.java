package ma.ip.dto.thirdparty;

import lombok.Data;

import java.util.List;


@Data
public class ClientSpecialCondition{
    private String type;
    private List<ClientSpecialConditionValue> clientSpecialConditionValues ;
}
