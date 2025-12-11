package ma.ip.services;

import lombok.extern.slf4j.Slf4j;
import ma.ip.constants.Constants;
import ma.ip.dto.account.*;
import ma.ip.enums.ReasonPacs002ISOEnum;
import ma.ip.exceptions.ProxyRequestException;
import ma.ip.exceptions.CustomerException;
import ma.ip.proxies.apigee.AccountProxy;
import ma.ip.proxies.apigee.AccountSendProxy;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

/**
 * The account service CBS Impl
 */
@Service
@Slf4j
public class AccountServiceCbsImpl implements AccountServiceCbs {


    private final AccountProxy accountProxy;

    private final AccountSendProxy accountSendProxy;

    private final MessageSource messageSource;

    public AccountServiceCbsImpl(AccountProxy accountProxy, AccountSendProxy accountSendProxy,  MessageSource messageSource) {
        this.accountProxy = accountProxy;
        this.accountSendProxy = accountSendProxy;
        this.messageSource = messageSource;
    }


    @Override
    public GetAccountDetailsResponseDTO details(GetAccountDetailsRequestDTO request) {

        GetAccountDetailsResponseDTO result = null;

        AccountRootRequestDto accountRootRequestDto = new AccountRootRequestDto();
        accountRootRequestDto.setAmount(request.getAmount());
        accountRootRequestDto.setTxId(request.getTxId());
        accountRootRequestDto.setRib(request.getRib());
        accountRootRequestDto.setTransferId(request.getRequestId());
        AccountRootResponse response = null;

        log.info("[ REQUEST - GET - ACCOUNT DETAILS  ]  " + request.getRib() + " TXID " + request.getTxId());

        long start = System.currentTimeMillis();
        try {

            response = accountProxy.details(accountRootRequestDto);

            long executionTime = System.currentTimeMillis() - start;
            log.info("[ RESPONSE - GET - ACCOUNT DETAILS  SUCCESS ]  DURATION " +  executionTime + " ms ");
        }catch (ProxyRequestException e ) {
            long executionTime = System.currentTimeMillis() - start;
            log.error("RESPONSE - GET - ACCOUNT DETAILS FAILURE " + " STATUS " + e.getStatus() + " DURATION " +  executionTime + " ms ");
            Map<String, String> errorsMap = e.getBody();

            // Functional Error in this case
            if ("400.T.01".equals(errorsMap.get("code"))) {
                if (request.getSens().equals("E")) {
                    throw new CustomerException(messageSource.getMessage("transfer.account.not.found", new Object[]{request.getRib()}, Locale.getDefault()), Constants.IP_MANAGER_ACCOUNT_NOT_FOUND, HttpStatus.BAD_REQUEST);
                }else {
                    throw new CustomerException(ReasonPacs002ISOEnum.AC03.value(), ReasonPacs002ISOEnum.AC03.code(), HttpStatus.BAD_REQUEST);
                }
            }

            // Other Error to throw
            throw e;
        }catch(feign.RetryableException e) {
            // Timeout client
            long executionTime = System.currentTimeMillis() - start;
            // Timeout client
            log.error("TIMEOUT_ERROR ACCOUNT API TIMEOUT EXCEEDED " + executionTime + " ms");
            throw e;
        }

        // Start Mapping Response returned from API Account
        if (response != null) {
            result = new GetAccountDetailsResponseDTO();
            result.setCode(response.getCode());
            result.setMessage(response.getMessage());
            if (response.getOutputData() != null) {
                result.setCurrency(response.getOutputData().getCurrency());
                result.setTransferBankId(response.getOutputData().getTransferBankId());

            }

        }

        return result;

    }
}
