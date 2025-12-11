package ma.ip.dto.thirdparty;

import lombok.Data;

import java.util.List;


@Data
public class ThirdPartyRootResponse{
    private BusinessLinkName businessLinkName;
    private PersonType personType;
    private String businessLinkId;
    private String businessLinkStartDate;
    private String detailedClientCategory;
    private ManagingOperationalUnit managingOperationalUnit;
    private BusinessLinkLifeCycle businessLinkLifeCycle;
    private LocalEconomicActivitySector localEconomicActivitySector;
    private CustomerAdvisor customerAdvisor;
    private CounterpartyMainActivity counterpartyMainActivity;
    private String mobilePhoneNumbers;
    private String homePhoneNumber;
    private String officePhoneNumber;
    private String emailAddress;
    private MainHolder mainHolder;
    private StandardClientType standardClientType;
    private MainOccupationAddress mainOccupationAddress;
    private PersonImposition personImposition;
    private List<ClientSpecialCondition> clientSpecialConditions;
    private List<PersonRelationship> personRelationships;
    private KycDetails kycDetails;
}
