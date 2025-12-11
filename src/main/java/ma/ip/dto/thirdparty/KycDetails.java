package ma.ip.dto.thirdparty;


import lombok.Data;

@Data
public class KycDetails{
    private String nextRecertificationDate;
    private String noteScoreDisplayed;
    private String noteScoreReceived;
}
