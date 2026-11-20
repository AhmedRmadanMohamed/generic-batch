package gov.socialdev.spl.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import gov.socialdev.spl.common.Constants;
import gov.socialdev.spl.exception.InvalidSPLResponseException;
import gov.socialdev.spl.exception.SPLNoRecordFoundException;
import gov.socialdev.spl.exception.SPLServiceUnavailableException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class SPLResponseDecoder implements Decoder {

    private final Decoder defaultDecoder;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        String body = null;
        if (response.body() != null) {
            body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
            response = response.toBuilder()
                    .body(body.getBytes(StandardCharsets.UTF_8))
                    .build();
        }
        Object result = defaultDecoder.decode(response, type);
        if (body != null) {
            String code = extractSPLCode(body);
            if (code != null) {
                throwSPLException(code);
            }
        }
        return result;
    }

    private void throwSPLException(String code) {
        switch (code) {
            case Constants.NO_RECORD_FOUND_CODE ->
                    throw new SPLNoRecordFoundException(Constants.NO_RECORD_FOUND_MESSAGE_AR);
            case Constants.SPL_INTERNAL_ERROR_CODE ->
                    throw new SPLServiceUnavailableException(Constants.SPL_INTERNAL_ERROR_MESSAGE_AR);
        }
    }

    private String extractSPLCode(String jsonBody) {
        try {
            JsonNode root = mapper.readTree(jsonBody);
            JsonNode codeNode = root.path("header").path("status").path("code");
            if (codeNode.isMissingNode() || codeNode.isNull()) {
                return null;
            }
            String code = codeNode.asText();
            if (Constants.SUCCESS_CODE.equals(code)) {
                return null;
            }
            return code;
        } catch (Exception ignored) {
            return null;
        }
    }
}
