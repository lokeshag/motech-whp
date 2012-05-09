package org.motechproject.whp.patient.builder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.domain.TreatmentCategory;
import org.motechproject.whp.refdata.domain.*;

import java.util.Arrays;

public class PatientRequestBuilder {

    private PatientRequest patientRequest = new PatientRequest();

    public PatientRequest build() {
        return patientRequest;
    }

    public PatientRequestBuilder withDefaults() {
        TreatmentCategory category = new TreatmentCategory("RNTCP Category 1", "01", 3, 8, 18, Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday));
        patientRequest = new PatientRequest()
                .setPatientInfo("1234567890", "Foo", "Bar", Gender.M, PatientType.PHSTransfer, "1234567890", "phi")
                .setTreatmentData(category, "12345678901", "123456", DiseaseClass.P, 50, "registrationNumber", DateUtil.newDateTime(2010, 6, 21, 10, 0, 5))
                .setSmearTestResults(SmearTestSampleInstance.PreTreatment, DateUtil.newDate(2010, 5, 19), SmearTestResult.Positive, DateUtil.newDate(2010, 5, 21), SmearTestResult.Positive)
                .setPatientAddress("house number", "landmark", "block", "village", "district", "state")
                .setWeightStatistics(WeightInstance.PreTreatment, 99.7, DateUtil.newDate(2010, 5, 19));
        return this;
    }

    public PatientRequestBuilder withSimpleUpdateFields() {
        patientRequest = new PatientRequest()
                .setPatientInfo("1234567890", null, null, null, null, "9087654321", null)
                .setPatientAddress("new_house number", "new_landmark", "new_block", "new_village", "new_district", "new_state")
                .setSmearTestResults(SmearTestSampleInstance.EndTreatment, DateUtil.newDate(2010, 7, 19), SmearTestResult.Negative, DateUtil.newDate(2010, 9, 20), SmearTestResult.Negative)
                .setWeightStatistics(WeightInstance.EndTreatment, 99.7, DateUtil.newDate(2010, 9, 20))
                .setTreatmentData(null, "elevenDigit", null, null, 50, "newRegistrationNumber", DateUtil.newDateTime(2010, 9, 20, 10, 10, 0));
        return this;
    }

    public PatientRequestBuilder withProviderId(String providerId) {
        patientRequest.setProvider_id(providerId);
        return this;
    }

    public PatientRequestBuilder withCaseId(String caseId) {
        patientRequest.setCase_id(caseId);
        return this;
    }

    public PatientRequestBuilder withTbRegistrationNumber(String tbRegistationNumber) {
        patientRequest.setTb_registration_number(tbRegistationNumber);
        return this;
    }

    public PatientRequestBuilder withPatientInfo(String caseId, String firstName, String lastName, Gender gender, PatientType patientType, String patientMobileNumber, String phi) {
        patientRequest.setPatientInfo(caseId, firstName, lastName, gender, patientType, patientMobileNumber, phi);
        return this;
    }

    public PatientRequestBuilder withFirstName(String firstName) {
        patientRequest.setFirst_name(firstName);
        return this;
    }

    public PatientRequestBuilder withPatientAddress(String houseNumber, String landmark, String block, String village, String district, String state) {
        patientRequest.setPatientAddress(houseNumber, landmark, block, village, district, state);
        return this;
    }

    public PatientRequestBuilder withPatientAge(int age) {
        patientRequest.setAge(age);
        return this;
    }

    public PatientRequestBuilder withSmearTestResults(SmearTestSampleInstance smearSampleInstance, LocalDate smearTestDate1, SmearTestResult smear_result_1, LocalDate smearTestDate2, SmearTestResult smearResult2) {
        patientRequest.setSmearTestResults(smearSampleInstance, smearTestDate1, smear_result_1, smearTestDate2, smearResult2);
        return this;
    }

    public PatientRequestBuilder withWeightStatistics(WeightInstance weightInstance, Double weight, LocalDate measuringDate) {
        patientRequest.setWeightStatistics(weightInstance, weight, measuringDate);
        return this;
    }

    public PatientRequestBuilder withPatientType(PatientType type) {
        patientRequest.setPatient_type(type);
        return this;
    }

    public PatientRequestBuilder withTbId(String tbId) {
        patientRequest.setTb_id(tbId);
        return this;
    }

    public PatientRequestBuilder withLastModifiedDate(DateTime lastModifiedDate) {
        patientRequest.setDate_modified(lastModifiedDate);
        return this;
    }

    public PatientRequestBuilder withTreatmentCategory(TreatmentCategory treatmentCategory) {
        patientRequest.setTreatment_category(treatmentCategory);
        return this;
    }
}