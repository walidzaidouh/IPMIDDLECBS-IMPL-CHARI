package ma.ip.proxies.apigee;


import ma.ip.annotations.AuditableServiceActivity;
import ma.ip.config.interceptor.ApigeeRequestInterceptor;
import ma.ip.dto.thirdparty.ThirdPartyRootResponse;
import ma.ip.enums.AuditableUserActivityEnum;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "third-party-service-cbs",
        url = "${external.api.url.thirdparty:}",
        configuration = ApigeeRequestInterceptor.class)
public interface ThirdPartyProxy {

    @AuditableServiceActivity(activityName = "thirdParty", type = AuditableUserActivityEnum.READ, destination = "external.api.url.thirdparty", resource = "getByClientID")
    @GetMapping("{customerId}")
    ThirdPartyRootResponse details(@PathVariable(value="customerId") String customerId);
}
