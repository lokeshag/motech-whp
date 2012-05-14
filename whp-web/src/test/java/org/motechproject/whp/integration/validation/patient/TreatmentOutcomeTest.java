package org.motechproject.whp.integration.validation.patient;

import org.junit.Test;
import org.motechproject.whp.builder.PatientWebRequestBuilder;
import org.motechproject.whp.request.PatientWebRequest;
import org.motechproject.whp.validation.ValidationScope;

public class TreatmentOutcomeTest extends BasePatientTest {
    @Test
    public void shouldThrowExceptionIfTreatmentOutcomeIsNull() {
        expectWHPException("field:treatment_outcome:value should not be null");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withOnlyRequiredTreatmentUpdateFields().withTreatmentOutcome(null).build();
        validator.validate(webRequest, ValidationScope.closeTreatment);
    }

    @Test
    public void shouldThrowExceptionIfTreatmentOutcomeIsEmpty() {
        expectWHPException("field:treatment_outcome:The value should be one of : [Cured, Died, Failure, Defaulted, TransferredOut, SwitchedOverToMDRTBTreatment, TreatmentCompleted]");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withOnlyRequiredTreatmentUpdateFields().withTreatmentOutcome("").build();
        validator.validate(webRequest, ValidationScope.closeTreatment);
    }

    @Test
    public void shouldThrowExceptionIfTreatmentOutcomeIsAnInvalidReason() {
        expectWHPException("field:treatment_outcome:The value should be one of : [Cured, Died, Failure, Defaulted, TransferredOut, SwitchedOverToMDRTBTreatment, TreatmentCompleted]");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withOnlyRequiredTreatmentUpdateFields().withTreatmentOutcome("PatientGotBored").build();
        validator.validate(webRequest, ValidationScope.closeTreatment);
    }

    @Test
    public void shouldNotThrowExceptionIfTreatmentOutcomeIsValid() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withOnlyRequiredTreatmentUpdateFields().withTreatmentOutcome(org.motechproject.whp.refdata.domain.TreatmentOutcome.Cured.name()).build();
        validator.validate(webRequest, ValidationScope.closeTreatment);
    }
}