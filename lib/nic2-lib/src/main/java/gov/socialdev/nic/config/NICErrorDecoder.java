package gov.socialdev.nic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
public class NICErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Feign error in {} - Status {}, Reason: {}, URL: {}",
                methodKey, response.status(), response.reason(), response.request().url());

        if (response.status() == 500 && response.body() != null) {
            try (InputStream bodyIs = response.body().asInputStream()) {
                String body = new String(bodyIs.readAllBytes());
                log.error("NIC 500 response body: {}", body);
                return new RuntimeException("NIC returned 500: " + body);
            } catch (Exception e) {
                return new RuntimeException("NIC returned 500, failed to read body", e);
            }
        }

        return defaultDecoder.decode(methodKey, response);
    }
}
