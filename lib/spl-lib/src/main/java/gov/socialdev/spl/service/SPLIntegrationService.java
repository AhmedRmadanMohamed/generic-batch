package gov.socialdev.spl.service;

import feign.FeignException;
import gov.socialdev.spl.common.Constants;
import gov.socialdev.spl.exception.InvalidSPLResponseException;
import gov.socialdev.spl.exception.SPLNoRecordFoundException;
import gov.socialdev.spl.exception.SPLServiceUnavailableException;
import gov.socialdev.spl.feign.SPLClient;
import gov.socialdev.spl.model.address.AddressDTO;
import gov.socialdev.spl.model.response.SaudiPostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SPLIntegrationService {

    private final SPLClient splClient;

    private static final String SUCCESS_CODE = Constants.SUCCESS_CODE;

    public SaudiPostResponse getAddresses(String socialId) {
        try {
            SaudiPostResponse response = splClient.getAddresses(socialId);
            if (isValidResponse(response)) {
                String code = response.getHeader().getStatus().getCode();
                if (SUCCESS_CODE.equals(code)) {
                    return response;
                }
                if (Constants.NO_RECORD_FOUND_CODE.equals(code)) {
                    throw new SPLNoRecordFoundException();
                }
                log.warn("SPL service returned code: {}", code);
                throw new InvalidSPLResponseException("SPL returned code: " + code);
            }
            log.warn("Invalid SPL response structure");
            throw new InvalidSPLResponseException();
        } catch (FeignException e) {
            log.error("SPL Feign client error - Status: {}, Response: {}", e.status(), safeContent(e), e);
            throw new SPLServiceUnavailableException();
        }
    }

    public List<AddressDTO> getAddressList(String socialId) {
        SaudiPostResponse response = getAddresses(socialId);
        if (response.getData() != null && response.getData().getAddresses() != null) {
            return response.getData().getAddresses();
        }
        return Collections.emptyList();
    }

    private boolean isValidResponse(SaudiPostResponse response) {
        return response != null &&
               response.getHeader() != null &&
               response.getHeader().getStatus() != null &&
               response.getHeader().getStatus().getCode() != null;
    }

    private String safeContent(FeignException e) {
        try {
            return e.contentUTF8();
        } catch (Exception ex) {
            return "<unavailable>";
        }
    }
}
