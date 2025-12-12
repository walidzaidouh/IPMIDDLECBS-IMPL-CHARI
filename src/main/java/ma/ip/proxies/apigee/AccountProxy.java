package ma.ip.proxies.apigee;


import ma.ip.annotations.AuditableServiceActivity;
import ma.ip.config.interceptor.ApigeeRequestInterceptor;
import ma.ip.dto.account.AccountRootRequestDto;
import ma.ip.dto.account.AccountRootResponse;
import ma.ip.enums.AuditableUserActivityEnum;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "account-service-cbs",
        url = "${external.api.url.account}",
        configuration = ApigeeRequestInterceptor.class)
public interface AccountProxy {


    @PostMapping("/api/walit/authorize")
    @AuditableServiceActivity(activityName = "authorize", type = AuditableUserActivityEnum.WRITE, saveRequest = true, saveResponse = true, destination = "external.api.url.account", resource = "api/walit/authorize")
    AccountRootResponse details(@RequestBody AccountRootRequestDto accountRootRequestDto);

}

