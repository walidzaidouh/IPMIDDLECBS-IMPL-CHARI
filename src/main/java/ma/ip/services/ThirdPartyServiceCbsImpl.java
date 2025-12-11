package ma.ip.services;

import lombok.extern.slf4j.Slf4j;
import ma.ip.dto.thirdparty.GetThirdPartyRequestCBS;
import ma.ip.dto.thirdparty.GetThirdPartyResponseCBS;
import ma.ip.dto.thirdparty.ThirdPartyRootResponse;
import ma.ip.exceptions.ProxyRequestException;
import ma.ip.proxies.apigee.ThirdPartyProxy;
import org.springframework.stereotype.Service;


/**
 * The third party service CBS Impl
 */
@Service
@Slf4j
public class ThirdPartyServiceCbsImpl implements ThirdPartyServiceCbs{


    private final ThirdPartyProxy thirdPartyProxy;

    public ThirdPartyServiceCbsImpl(ThirdPartyProxy thirdPartyProxy) {
        this.thirdPartyProxy = thirdPartyProxy;

    }

    @Override
    public GetThirdPartyResponseCBS details(GetThirdPartyRequestCBS request) {

        GetThirdPartyResponseCBS result = null;

//        ThirdPartyRootResponse response = null;
        ThirdPartyRootResponse response = new ThirdPartyRootResponse();
        log.info("[ REQUEST - GET - ThirdParty  ] CUSTOMER " + request.getCustomerNumber());
        long start = System.currentTimeMillis();
        try {

//            response = thirdPartyProxy.details(request.getCustomerNumber());

            long executionTime = System.currentTimeMillis() - start;
            log.info("[ RESPONSE - GET - ThirdParty SUCCESS ] DURATION " + executionTime + " ms ");
        }catch (ProxyRequestException e) {
            long executionTime = System.currentTimeMillis() - start;
            log.error("[ RESPONSE - GET - ThirdParty FAILURE ] " + " STATUS " + e.getStatus() + " DURATION " + executionTime + " ms ");
            throw e;
        }catch(feign.RetryableException e) {
            // Timeout client
            log.error("TIMEOUT_ERROR THIRD PARTY API TIMEOUT EXCEEDED");
            throw e;
        }
//         if (response != null) {
             result = new GetThirdPartyResponseCBS();
             StringBuilder nameBuilder = new StringBuilder();
//             if (response.getBusinessLinkName() != null && response.getBusinessLinkName().getLastName() != null) {
//                 nameBuilder.append(response.getBusinessLinkName().getLastName());
//                 if (response.getBusinessLinkName().getFirstName() != null) {
//                     nameBuilder.append(" ");
//                     nameBuilder.append(response.getBusinessLinkName().getFirstName());
//                 }
//                 result.setFullName(nameBuilder.toString());
//             }
             result.setFullName("HYGULOYIZ SITOQUEC");

//             if (response.getMainHolder() != null && response.getMainHolder().getIndividual() != null) {
//                 result.setBirthDate(response.getMainHolder().getIndividual().getBirthDate());
//                 result.setPlaceBirth(response.getMainHolder().getIndividual().getBirthPlace());
//                 if (response.getMainHolder().getIndividual().getBirthCountry() != null) {
//                     result.setBirthCountry(response.getMainHolder().getIndividual().getBirthCountry().getCode());
//                 }
//                 if (response.getMainHolder().getResidenceCountry() != null) {
//                     result.setResidenceCountry(response.getMainHolder().getResidenceCountry().getCode());
//                 }
//
//                 if (response.getMainHolder().getLegalEntity() != null &&  response.getMainHolder().getLegalEntity().getIdentification() != null) {
//                     result.setRegistrationNumber(response.getMainHolder().getLegalEntity().getIdentification().getRegistrationNumber());
//                 }
//             }
        result.setBirthCountry("MA");
        result.setPlaceBirth("CASABLANCA");
        result.setResidenceCountry("MA");

//             if (response.getMainOccupationAddress() != null) {
//                 StringBuffer addressBuffer = new StringBuffer("");
//
//                 if (response.getMainOccupationAddress().getCountry() != null) {
//                     addressBuffer.append(" " + response.getMainOccupationAddress().getCountry().getLabel());
//                 }
//
//                 if (response.getMainOccupationAddress().getCity() != null) {
//                     addressBuffer.append(" " + response.getMainOccupationAddress().getCity());
//                 }
//
//                 if (response.getMainOccupationAddress().getStreetName() != null) {
//                     addressBuffer.append(" " + response.getMainOccupationAddress().getStreetName());
//                 }
//
//                 if (response.getMainOccupationAddress().getPostalCode() != null) {
//                     addressBuffer.append(" " + response.getMainOccupationAddress().getPostalCode());
//                 }
//
//                 result.setLocalisation(addressBuffer != null && addressBuffer.length() > 70 ? addressBuffer.substring(0, 70) : addressBuffer.toString());
//             }
        result.setLocalisation("Maroc,casablanca,derb sultan");


//             if (response.getStandardClientType() != null) {
//
//                 result.setSegmentCode(response.getStandardClientType().getCode());
//             }

        result.setSegmentCode("05");
//         }

        return result;
    }
}
