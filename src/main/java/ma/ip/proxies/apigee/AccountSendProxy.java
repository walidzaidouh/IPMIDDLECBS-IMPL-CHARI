package ma.ip.proxies.apigee;


import ma.ip.annotations.AuditableServiceActivity;
import ma.ip.config.interceptor.ApigeeRequestInterceptor;
import ma.ip.dto.account.AccountRootResponse;
import ma.ip.enums.AuditableUserActivityEnum;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "account-service-send-cbs",
        url = "${external.api.url.account:}",
        configuration = ApigeeRequestInterceptor.class)
public interface AccountSendProxy {


    @GetMapping("extended/{accountNumber}")
    @AuditableServiceActivity(activityName = "accountDetails", type = AuditableUserActivityEnum.READ, saveRequest = true, saveResponse = true, destination = "external.api.url.account", resource = "/account/extended")
    AccountRootResponse details(@PathVariable(value="accountNumber") String accountNumber,
                                @RequestParam(value = "amount") String amount,
                                @RequestParam(value = "transactionId") String transactionId,
                                @RequestParam(value = "operationCode") String operationCode,
                                @RequestParam(value = "currency") String currency);

}

