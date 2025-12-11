package ma.ip.dto.thirdparty;


import lombok.Data;

@Data
public class FiscalAddress{
    private String recipient;
    private String recipientDetails;
    private String buildingNumber;
    private String postalCode;
    private String streetName;
    private String city;
    private Country country;
}
