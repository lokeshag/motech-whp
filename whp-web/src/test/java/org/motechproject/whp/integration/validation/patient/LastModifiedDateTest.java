package org.motechproject.whp.integration.validation.patient;

import org.junit.Test;
import org.motechproject.whp.builder.PatientWebRequestBuilder;
import org.motechproject.whp.request.PatientWebRequest;
import org.motechproject.whp.validation.ValidationScope;

public class LastModifiedDateTest extends BasePatientTest {
    @Test
    public void shouldNotThrowException_WhenLastModifiedDateFormatIsCorrect() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate("03/04/2012 02:20:30").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowException_WhenLastModifiedDateFormatIsNotTheCorrectDateTimeFormat() {
        expectWHPException("03-04-2012\" is malformed at \"-04-2012");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate("03-04-2012").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowException_WhenLastModifiedDateFormatDoesNotHaveTimeComponent() {
        expectWHPException("field:date_modified:Invalid format: \"03/04/2012\" is too short");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate("03/04/2012").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowAnExceptionIfLastModifiedDateIsEmpty() {
        expectWHPException("field:date_modified:Invalid format: \"\"");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate("").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowExceptionWhenLastModifiedDateFormatIsNull() {
        expectWHPException("field:date_modified:value should not be null");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate(null).build();
        validator.validate(webRequest, ValidationScope.create);
    }
}