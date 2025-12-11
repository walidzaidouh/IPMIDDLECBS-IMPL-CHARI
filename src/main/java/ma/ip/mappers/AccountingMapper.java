package ma.ip.mappers;


import ma.ip.dto.accounting.CreateCreResponseDTO;
import ma.ip.dto.accounting.response.AccountingRootResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface AccountingMapper {

    CreateCreResponseDTO toCreateCreResponse(AccountingRootResponse accountingRootResponse);
}
