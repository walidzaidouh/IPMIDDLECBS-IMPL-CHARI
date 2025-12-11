package ma.ip.dto.thirdparty;


import lombok.Data;

@Data
public class PersonRelationship{
    private String linkedBusinessLinkId;
    private PersonRelationshipType personRelationshipType;
}
