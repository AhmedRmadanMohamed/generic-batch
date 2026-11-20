
package gov.socialdev.nic.service;

import feign.FeignException;
import gov.socialdev.nic.exception.InvalidNICResponseException;
import gov.socialdev.nic.exception.ServiceUnavailableException;
import gov.socialdev.nic.feign.NICClient;
import gov.socialdev.nic.model.alienVisitor.AlienVisitorInfoRequestV3;
import gov.socialdev.nic.model.alienVisitor.AlienVisitorInfoResponse;
import gov.socialdev.nic.model.dependents.DependentsInquiryRequest;
import gov.socialdev.nic.model.dependents.DependentsInquiryResponse;
import gov.socialdev.nic.model.dependents.DependentsInquiryResult;
import gov.socialdev.nic.model.familyRelation.FamilyRelationshipData;
import gov.socialdev.nic.model.familyRelation.FamilyRelationshipResponse;
import gov.socialdev.nic.model.familyTree.FamilyTreeInquiryRequest;
import gov.socialdev.nic.model.familyTree.FamilyTreeInquiryResponse;
import gov.socialdev.nic.model.familyTree.FamilyTreeInquiryResult;
import gov.socialdev.nic.model.vehiclesInfo.VehiclesInfoRequest;
import gov.socialdev.nic.model.vehiclesInfo.VehiclesInfoResponse;
import gov.socialdev.nic.model.vehiclesInfo.VehiclesInfoResult;
import gov.socialdev.nic.model.requests.PersonDetailsRequest;
import gov.socialdev.nic.model.requests.PersonDetailsRequestParent;
import gov.socialdev.nic.model.requests.PersonRelationDetailsRequest;
import gov.socialdev.nic.model.responses.PersonDetailsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static gov.socialdev.nic.util.DateUtil.normalizeDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NICIntegrationService {

    private final NICClient nicClient;

    private static final String SUCCESS_CODE = "I000000";
    private static final String NO_DATA_FOUND_CODE1= "E700328";
    private static final String NO_DATA_FOUND_CODE2= "E213006";


    public PersonDetailsResponse getPersonDetails(PersonDetailsRequest request) {
        try {
            request.setBirthDate(normalizeDate(request.getBirthDate()));
            String requestId = UUID.randomUUID().toString();
            PersonDetailsResponse response = nicClient.getPersonDetails(request, requestId);
            if (isValidPersonDetailsResponse(response)) {
                String code = response.getHeader().getStatus().getCode();
                if (SUCCESS_CODE.equals(code)) {
                    return response;
                }
                String desc = response.getHeader().getStatus().getDescription();
                log.error("NIC service returned code: {}, description: {}", code, desc);
                throw new InvalidNICResponseException("NIC basicInfo returned code: " + code + " - " + desc);
            }
            log.warn("Invalid NIC response structure for person details");
            throw new InvalidNICResponseException("Invalid NIC response structure for basicInfo");
        } catch (InvalidNICResponseException e) {
            throw e;
        } catch (FeignException e) {
            log.error("Feign client error - Status: {}, Response: {}", e.status(), safeContent(e), e);
            throw new ServiceUnavailableException("NIC basicInfo unavailable: HTTP " + e.status() + " - " + safeContent(e));
        } catch (Exception e) {
            log.error("Unexpected error calling NIC basicInfo: {}", e.getMessage(), e);
            throw new ServiceUnavailableException("NIC basicInfo error: " + e.getMessage());
        }
    }

    public PersonDetailsResponse getPersonDetails(PersonDetailsRequestParent request) {
        try {
            request.setBirthDate(normalizeDate(request.getBirthDate()));
            PersonDetailsRequest personDetailsRequest = PersonDetailsRequest.builder()
                    .socialId(request.getSocialId())
                    .birthDate(request.getBirthDate())
                    .operatorSocialId(request.getOperatorSocialId())
                    .build();
            String requestId = UUID.randomUUID().toString();
            PersonDetailsResponse response = nicClient.getPersonDetails(personDetailsRequest, requestId);
            if (isValidPersonDetailsResponse(response)) {
                String code = response.getHeader().getStatus().getCode();
                if (SUCCESS_CODE.equals(code)) {
                    return response;
                }
                String desc = response.getHeader().getStatus().getDescription();
                log.error("NIC service returned code: {}, description: {}", code, desc);
                throw new InvalidNICResponseException("NIC basicInfo returned code: " + code + " - " + desc);
            }
            throw new InvalidNICResponseException("Invalid NIC response structure for basicInfo");
        } catch (InvalidNICResponseException e) {
            throw e;
        } catch (FeignException e) {
            log.error("Feign client error - Status: {}, Response: {}", e.status(), safeContent(e), e);
            throw new ServiceUnavailableException("NIC basicInfo unavailable: HTTP " + e.status() + " - " + safeContent(e));
        } catch (Exception e) {
            log.error("Unexpected error calling NIC basicInfo: {}", e.getMessage(), e);
            throw new ServiceUnavailableException("NIC basicInfo error: " + e.getMessage());
        }
    }

    public AlienVisitorInfoResponse getAlienVisitorInfoV3(AlienVisitorInfoRequestV3 request) {
        try {
            String requestId = UUID.randomUUID().toString();
            AlienVisitorInfoResponse response = nicClient.getAlienVisitorInfoV3(request, requestId);
            if (isValidGenericResponse(response)) {
                String code = response.getHeader().getStatus().getCode();
                if (SUCCESS_CODE.equals(code)) {
                    return response;
                }
                String desc = response.getHeader().getStatus().getDescription();
                log.error("NIC service returned code: {}, description: {}", code, desc);
                throw new InvalidNICResponseException("NIC alienVisitor returned code: " + code + " - " + desc);
            }
            throw new InvalidNICResponseException("Invalid NIC response structure for alienVisitor");
        } catch (InvalidNICResponseException e) {
            throw e;
        } catch (FeignException e) {
            log.error("Feign client error - Status: {}, Response: {}", e.status(), safeContent(e), e);
            throw new ServiceUnavailableException("NIC alienVisitor unavailable: HTTP " + e.status() + " - " + safeContent(e));
        } catch (Exception e) {
            log.error("Unexpected error calling NIC alienVisitor: {}", e.getMessage(), e);
            throw new ServiceUnavailableException("NIC alienVisitor error: " + e.getMessage());
        }
    }

    public DependentsInquiryResult inquireDependents(DependentsInquiryRequest request) {
        try {
            request.setBirthDate(normalizeDate(request.getBirthDate()));
            String requestId = UUID.randomUUID().toString();
            DependentsInquiryResponse response = nicClient.inquireDependents(request, requestId);
            if (isValidGenericResponse(response)) {
                String code = response.getHeader().getStatus().getCode();
                if (SUCCESS_CODE.equals(code)) {
                    return response.getResult();
                }
                if(NO_DATA_FOUND_CODE1.equals(code) || NO_DATA_FOUND_CODE2.equals(code)){
                    return null;
                }
                String desc = response.getHeader().getStatus().getDescription();
                log.error("NIC service returned code: {}, description: {}", code, desc);
                throw new InvalidNICResponseException("NIC dependents returned code: " + code + " - " + desc);
            }
            throw new InvalidNICResponseException("Invalid NIC response structure for dependents");
        } catch (InvalidNICResponseException e) {
            throw e;
        } catch (FeignException e) {
            log.error("Feign client error - Status: {}, Response: {}", e.status(), safeContent(e), e);
            throw new ServiceUnavailableException("NIC dependents unavailable: HTTP " + e.status() + " - " + safeContent(e));
        } catch (Exception e) {
            log.error("Unexpected error calling NIC dependents: {}", e.getMessage(), e);
            throw new ServiceUnavailableException("NIC dependents error: " + e.getMessage());
        }
    }

    public FamilyTreeInquiryResult inquireFamilyTree(FamilyTreeInquiryRequest request) {
        try {
            request.setBirthDate(normalizeDate(request.getBirthDate()));
            String requestId = UUID.randomUUID().toString();
            FamilyTreeInquiryResponse response = nicClient.inquireFamilyTree(request, requestId);
            if (isValidGenericResponse(response)) {
                String code = response.getHeader().getStatus().getCode();
                if (SUCCESS_CODE.equals(code)) {
                    return response.getResult();
                }
                String desc = response.getHeader().getStatus().getDescription();
                log.error("NIC service returned code: {}, description: {}", code, desc);
                throw new InvalidNICResponseException("NIC familyTree returned code: " + code + " - " + desc);
            }
            throw new InvalidNICResponseException("Invalid NIC response structure for familyTree");
        } catch (InvalidNICResponseException e) {
            throw e;
        } catch (FeignException e) {
            log.error("Feign client error - Status: {}, Response: {}", e.status(), safeContent(e), e);
            throw new ServiceUnavailableException("NIC familyTree unavailable: HTTP " + e.status() + " - " + safeContent(e));
        } catch (Exception e) {
            log.error("Unexpected error calling NIC familyTree: {}", e.getMessage(), e);
            throw new ServiceUnavailableException("NIC familyTree error: " + e.getMessage());
        }
    }

    public VehiclesInfoResult inquireVehiclesInfo(VehiclesInfoRequest request) {
        try {
            request.setBirthDate(normalizeDate(request.getBirthDate()));
            String requestId = UUID.randomUUID().toString();
            VehiclesInfoResponse response = nicClient.getVehiclesInfo(request, requestId);
            if (isValidGenericResponse(response)) {
                String code = response.getHeader().getStatus().getCode();
                if (SUCCESS_CODE.equals(code)) {
                    return response.getResult();
                }
                if (NO_DATA_FOUND_CODE1.equals(code) || NO_DATA_FOUND_CODE2.equals(code)) {
                    return null;
                }
                String desc = response.getHeader().getStatus().getDescription();
                log.error("NIC service returned code: {}, description: {}", code, desc);
                throw new InvalidNICResponseException("NIC vehiclesInfo returned code: " + code + " - " + desc);
            }
            throw new InvalidNICResponseException("Invalid NIC response structure for vehiclesInfo");
        } catch (InvalidNICResponseException e) {
            throw e;
        } catch (FeignException e) {
            log.error("Feign client error - Status: {}, Response: {}", e.status(), safeContent(e), e);
            throw new ServiceUnavailableException("NIC vehiclesInfo unavailable: HTTP " + e.status() + " - " + safeContent(e));
        } catch (Exception e) {
            log.error("Unexpected error calling NIC vehiclesInfo: {}", e.getMessage(), e);
            throw new ServiceUnavailableException("NIC vehiclesInfo error: " + e.getMessage());
        }
    }

    public FamilyRelationshipData getRelationshipInfo(PersonRelationDetailsRequest nicRequest) {
        try {
            nicRequest.setMainBirthDate(normalizeDate(nicRequest.getMainBirthDate()));
            nicRequest.setSubBirthDate(normalizeDate(nicRequest.getSubBirthDate()));
            String requestId = UUID.randomUUID().toString();
            FamilyRelationshipResponse response = nicClient.getPersonRelationDetails(nicRequest, requestId);

            if(isValidFamilyRelationshipResponse(response)){
                String code = response.getHeader().getStatus().getCode();
                if (SUCCESS_CODE.equals(code)) {
                    return response.getResult();
                }
                String desc = response.getHeader().getStatus().getDescription();
                log.error("NIC service returned code: {}, description: {}", code, desc);
                throw new InvalidNICResponseException("NIC familyRelationship returned code: " + code + " - " + desc);
            }
            throw new InvalidNICResponseException("Invalid NIC response structure for familyRelationship");
        } catch (InvalidNICResponseException e) {
            throw e;
        } catch (FeignException e) {
            log.error("Feign client error - Status: {}, Response: {}", e.status(), safeContent(e), e);
            throw new ServiceUnavailableException("NIC familyRelationship unavailable: HTTP " + e.status() + " - " + safeContent(e));
        } catch (Exception e) {
            log.error("Unexpected error calling NIC familyRelationship: {}", e.getMessage(), e);
            throw new ServiceUnavailableException("NIC familyRelationship error: " + e.getMessage());
        }
    }

    private boolean isValidFamilyRelationshipResponse(FamilyRelationshipResponse response) {
        return response != null &&
               response.getHeader() != null &&
               response.getHeader().getStatus() != null &&
               response.getHeader().getStatus().getCode() != null;
    }

    private boolean isValidPersonDetailsResponse(PersonDetailsResponse response) {
        return response != null &&
               response.getHeader() != null &&
               response.getHeader().getStatus() != null &&
               response.getHeader().getStatus().getCode() != null;
    }

    private boolean isValidGenericResponse(Object response) {
        // Attempt runtime checks by reflection would be heavier; assume response classes follow same header pattern
        if (response == null) return false;
        try {
            Object header = response.getClass().getMethod("getHeader").invoke(response);
            if (header == null) return false;
            Object status = header.getClass().getMethod("getStatus").invoke(header);
            if (status == null) return false;
            Object code = status.getClass().getMethod("getCode").invoke(status);
            return code != null;
        } catch (Exception e) {
            log.warn("Failed to validate response structure via reflection: {}", e.getMessage());
            return false;
        }
    }

    private String safeContent(FeignException e) {
        try {
            return e.contentUTF8();
        } catch (Exception ex) {
            return "<unavailable>";
        }
    }
}
