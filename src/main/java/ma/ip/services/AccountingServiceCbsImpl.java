package ma.ip.services;

import lombok.extern.slf4j.Slf4j;
import ma.ip.dto.accounting.CreateCreRequestDTO;
import ma.ip.dto.accounting.CreateCreResponseDTO;
import ma.ip.dto.accounting.request.AccountingRootRequest;
import ma.ip.dto.accounting.response.AccountingMainRootResponse;
import ma.ip.dto.accounting.response.AccountingRootResponse;
import ma.ip.enums.AccountingStatusEnum;
import ma.ip.enums.DecisionEnum;
import ma.ip.enums.MessageDirectionEnum;
import ma.ip.exceptions.ProxyRequestException;
import ma.ip.mappers.AccountingMapper;
import ma.ip.proxies.apigee.AccountingProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Service AccountingServiceCbsImpl.
 */
@Service
@Slf4j
public class AccountingServiceCbsImpl implements AccountingServiceCbs{

    private final AccountingProxy accountingProxy;

    @Autowired
    private AccountingMapper accountingMapper;

    public AccountingServiceCbsImpl(AccountingProxy accountingProxy) {
        this.accountingProxy = accountingProxy;
    }

    @Value("${ip.apigee.regex.pattern:[';()|&<>*^\\\\\\x00]}")
    String specialCharactersList;

    @Override
    public CreateCreResponseDTO createCre(CreateCreRequestDTO createCreRequestDTO) {

        CreateCreResponseDTO result = new CreateCreResponseDTO();
        AccountingRootRequest accountingRootRequest = new AccountingRootRequest();
//        if (MessageDirectionEnum.SEND.getCode().equals(createCreRequestDTO.getSens()))
//            accountingRootRequest.setSens("DEBIT");
//        else
//            accountingRootRequest.setSens("CREDIT");
//
        if(DecisionEnum.RJCT.code().equals(createCreRequestDTO.getSens()))
        {
            accountingRootRequest.setSens(DecisionEnum.RJCT.code());
        }else if (MessageDirectionEnum.SEND.getCode().equals(createCreRequestDTO.getSens()))
        {
            accountingRootRequest.setSens("DEBIT");
        }else {
            accountingRootRequest.setSens("CREDIT");
        }

        accountingRootRequest.setTransferId(createCreRequestDTO.getMsgId());
        accountingRootRequest.setTransferBankId(createCreRequestDTO.getTransferBankId());
        accountingRootRequest.setTxId(createCreRequestDTO.getTxId());
        accountingRootRequest.setRibSender(createCreRequestDTO.getRibSender());
        accountingRootRequest.setRibReceiver(createCreRequestDTO.getRibReceiver());
        accountingRootRequest.setFullNameSender(createCreRequestDTO.getFullNameSender());
        accountingRootRequest.setFullNameReceiver(createCreRequestDTO.getFullNameReceiver());
        accountingRootRequest.setDate(createCreRequestDTO.getDate());
        accountingRootRequest.setAmount(createCreRequestDTO.getAmount());

//        AccountingRootResponse accountRootResponse = new AccountingRootResponse();
        AccountingMainRootResponse accountRootResponse = null;
        log.info("[ REQUEST - POST - EVENT  ] TRANSACTION CODE "+ accountingRootRequest.getTransferBankId() + " TX ID " + accountingRootRequest.getTxId());
        long start = System.currentTimeMillis();
        try {
//            accountRootResponse = accountingProxy.createCre(accountingRootRequest);
            accountRootResponse = accountingProxy.createCre(accountingRootRequest);
            long executionTime = System.currentTimeMillis() - start;
            result = accountingMapper.toCreateCreResponse(accountRootResponse.getData());
            result.setStatus(AccountingStatusEnum.OK.getValue());
            log.info("[ RESPONSE - POST - EVENT SUCCESS ] DURATION " + executionTime + " ms " + " RESPONSE " + accountRootResponse );
        }catch(ProxyRequestException e) {
            long executionTime = System.currentTimeMillis() - start;
            log.error("[ RESPONSE - POST - EVENT FAILURE ] " + " STATUS " + e.getStatus() + " DURATION " + executionTime + " ms");
            log.error("INFO PROXY REQUEST EXCEPTION : {}" , e.getBody());

            Map<String, ?> body = (Map<String, ?>) (Map) e.getBody();

            Map<String, ?> data = null;
            if (body != null && body.get("data") instanceof Map) {
                data = (Map<String, ?>) body.get("data");
            }

            log.info("DATA : {} ", data);

            String code = null;
            String message = null;

            if (data != null) {
                Object codeObj = data.get("code");
                Object msgObj = data.get("message");

                if (codeObj != null) code = codeObj.toString();
                if (msgObj != null) message = msgObj.toString();
            }

            result.setCode(code);
            result.setMessage(message);
            result.setStatus(AccountingStatusEnum.OK.getValue());
        }catch(feign.RetryableException e) {
            // Timeout client
            long executionTime = System.currentTimeMillis() - start;
            result.setStatus(AccountingStatusEnum.TIMEOUT.getValue());
            result.setMessage("TIME OUT");
            log.error("TIMEOUT_ERROR ACCOUNTING API TIMEOUT EXCEEDED " + executionTime + " ms");
        }catch (Exception e) {
            result.setStatus(AccountingStatusEnum.ERROR.getValue());
            log.error("UNEXPECTED_ERROR " + e.getMessage());
        }

        return result;
    }
}
