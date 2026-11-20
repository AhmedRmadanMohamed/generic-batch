package gov.socialdev.nic.feign;

import gov.socialdev.nic.config.NICFeignConfig;
import gov.socialdev.nic.model.alienVisitor.AlienVisitorInfoRequestV3;
import gov.socialdev.nic.model.familyRelation.FamilyRelationshipResponse;
import gov.socialdev.nic.model.requests.PersonDetailsRequestParent;
import gov.socialdev.nic.model.alienVisitor.AlienVisitorInfoResponse;
import gov.socialdev.nic.model.dependents.DependentsInquiryRequest;
import gov.socialdev.nic.model.dependents.DependentsInquiryResponse;
import gov.socialdev.nic.model.familyTree.FamilyTreeInquiryRequest;
import gov.socialdev.nic.model.familyTree.FamilyTreeInquiryResponse;
import gov.socialdev.nic.model.vehiclesInfo.VehiclesInfoRequest;
import gov.socialdev.nic.model.vehiclesInfo.VehiclesInfoResponse;
import gov.socialdev.nic.model.requests.PersonDetailsRequest;
import gov.socialdev.nic.model.requests.PersonRelationDetailsRequest;
import gov.socialdev.nic.model.responses.PersonDetailsResponse;
import gov.socialdev.nic.model.responses.PersonRelationDetailsResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "NIC" , configuration = NICFeignConfig.class)
public interface NICClient {


    @PostMapping("/v1/person/basicInfo")
    @Retry(name = "personDetailsRetry")
    @CircuitBreaker(name = "personDetailsCircuitBreaker")
    PersonDetailsResponse getPersonDetails(@RequestBody PersonDetailsRequest request,
                                           @RequestHeader("Request-ID") String requestId);

    @PostMapping("/v1/persons/family-relationship")
    @Retry(name = "personRelationRetry")
    @CircuitBreaker(name = "personRelationCircuitBreaker")
    FamilyRelationshipResponse getPersonRelationDetails(@RequestBody PersonRelationDetailsRequest request,
                                                        @RequestHeader("Request-ID") String requestId);

    @PostMapping("/v3/getAlienVisitorInfo")
    AlienVisitorInfoResponse getAlienVisitorInfoV3(@RequestBody AlienVisitorInfoRequestV3 request,
                                                   @RequestHeader("Request-ID") String requestId);

    @PostMapping("/v2/person/dependents")
    DependentsInquiryResponse inquireDependents(@RequestBody DependentsInquiryRequest request,
                                                @RequestHeader("Request-ID") String requestId);

    @PostMapping("/v1/persons/family-tree-inquiry")
    FamilyTreeInquiryResponse inquireFamilyTree(@RequestBody FamilyTreeInquiryRequest request,
                                                @RequestHeader("Request-ID") String requestId);

    @PostMapping("/v1/vehicles/info")
    VehiclesInfoResponse getVehiclesInfo(@RequestBody VehiclesInfoRequest request,
                                         @RequestHeader("Request-ID") String requestId);
}
