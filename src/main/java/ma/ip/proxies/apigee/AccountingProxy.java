package ma.ip.proxies.apigee;


import ma.ip.annotations.AuditableServiceActivity;
import ma.ip.config.interceptor.ApigeeRequestInterceptor;
import ma.ip.dto.accounting.request.AccountingRootRequest;
import ma.ip.dto.accounting.response.AccountingMainRootResponse;
import ma.ip.dto.accounting.response.AccountingRootResponse;
import ma.ip.enums.AuditableUserActivityEnum;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "accounting-service-cbs",
        url = "${external.api.url.accounting}",
        configuration = ApigeeRequestInterceptor.class)
public interface AccountingProxy {

    @PostMapping("/api/walit/advice")
    @AuditableServiceActivity(activityName = "advice", type = AuditableUserActivityEnum.WRITE, saveRequest = true, saveResponse = true, destination = "external.api.url.accounting", resource = "api/walit/advice")
    AccountingMainRootResponse createCre(@RequestBody AccountingRootRequest accountingRootRequest);
}
