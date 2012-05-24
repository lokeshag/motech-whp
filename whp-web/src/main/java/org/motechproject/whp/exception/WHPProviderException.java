package org.motechproject.whp.exception;

import org.motechproject.provider.registration.exception.OpenRosaRegistrationValidationException;
import org.motechproject.whp.patient.exception.WHPError;
import org.motechproject.whp.patient.exception.WHPRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class WHPProviderException extends OpenRosaRegistrationValidationException {

    public WHPProviderException(WHPRuntimeException exception) {
        super(exception.getMessage(), HttpStatus.BAD_REQUEST, buildErrorMessageMap(exception));
    }

    private static Map<String, String> buildErrorMessageMap(WHPRuntimeException exception) {
        HashMap<String, String> errors = new HashMap<String, String>();
        for (WHPError whpError : exception.getErrors()) {
            errors.put(whpError.getErrorCode().name(), whpError.getMessage());
        }
        return errors;
    }

}
