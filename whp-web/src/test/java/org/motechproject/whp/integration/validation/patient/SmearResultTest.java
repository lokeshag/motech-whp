package org.motechproject.whp.integration.validation.patient;

import org.junit.Test;
import org.motechproject.whp.builder.PatientWebRequestBuilder;
import org.motechproject.whp.request.PatientWebRequest;
import org.motechproject.whp.validation.ValidationScope;

public class SmearResultTest extends BasePatientTest {
    @Test
    public void shouldThrowException_WhenSmearTest1DateFormatIsIncorrect() {
        expectWHPException("field:smear_test_date_1:Invalid format: \"03/04/2012  11:23:40\" is malformed at \"  11:23:40\"");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withSmearTestDate1("03/04/2012  11:23:40").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldThrowException_WhenSmearTest2DateFormatIsIncorrect() {
        expectWHPException("field:smear_test_date_2:Invalid format: \"03/04/2012  11:23:40\" is malformed at \"  11:23:40\"");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withSmearTestDate2("03/04/2012  11:23:40").build();
        validator.validate(webRequest, ValidationScope.create);
    }

    @Test
    public void shouldNotThrowException_WhenSmearTestResultsIsNull_ForUpdateScope() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withSimpleUpdateFields().withSmearTestResults(null, null, null, null, null).build();
        validator.validate(webRequest, ValidationScope.simpleUpdate);
    }
}