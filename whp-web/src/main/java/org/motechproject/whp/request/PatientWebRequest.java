package org.motechproject.whp.request;

import lombok.Data;
import org.motechproject.validation.constraints.Enumeration;
import org.motechproject.validation.constraints.NamedConstraint;
import org.motechproject.validation.constraints.Scope;
import org.motechproject.whp.patient.domain.*;
import org.motechproject.whp.validation.ProviderIdValidator;
import org.motechproject.whp.validation.ValidationScope;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class PatientWebRequest {

    @NotNull
    private String case_id;

    @DateTimeFormat(pattern = "dd/MM/YYYY HH:mm:ss")
    private String date_modified;

    private String case_type;

    private String tb_registration_number;

    private String phi;

    @NotNull
    @Scope(scope = {ValidationScope.create})
    private String first_name;

    @NotNull
    @Scope(scope = {ValidationScope.create})
    private String last_name;

    @Enumeration(type = Gender.class)
    @Scope(scope = {ValidationScope.create})
    private String gender;

    @Enumeration(type = PatientType.class)
    @Scope(scope = {ValidationScope.create})
    private String patient_type;

    @Pattern(regexp = "^$|[0-9]{10}", message = "Mobile number should be empty or should have 10 digits")
    private String mobile_number;

    @Enumeration(type = DiseaseClass.class)
    @Scope(scope = {ValidationScope.create})
    private String disease_class;

    @NotNull
    @Scope(scope = {ValidationScope.create})
    private String address_location;

    private String address_landmark;

    @NotNull
    @Scope(scope = {ValidationScope.create})
    private String address_village;

    @NotNull
    @Scope(scope = {ValidationScope.create})
    private String address_block;

    @NotNull
    @Scope(scope = {ValidationScope.create})
    private String address_district;

    @NotNull
    @Scope(scope = {ValidationScope.create})
    private String address_state;

    @Enumeration(type = SmearTestSampleInstance.class)
    @Scope(scope = {ValidationScope.create})
    private String smear_sample_instance;

    @Scope(scope = {ValidationScope.create})
    @DateTimeFormat(pattern = "dd/MM/YYYY")
    private String smear_test_date_1;

    @Enumeration(type = SmearTestResult.class)
    @Scope(scope = {ValidationScope.create})
    private String smear_test_result_1;

    @DateTimeFormat(pattern = "dd/MM/YYYY")
    @Scope(scope = {ValidationScope.create})
    private String smear_test_date_2;

    @Enumeration(type = SmearTestResult.class)
    @Scope(scope = {ValidationScope.create})
    private String smear_test_result_2;

    @Enumeration(type = WeightInstance.class)
    @Scope(scope = {ValidationScope.create})
    private String weight_instance;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = Integer.MAX_VALUE, message = "Weight must be a real number")
    @Scope(scope = {ValidationScope.create})
    private String weight;

    @NotNull
    @Size(min = 11, max = 11)
    @Scope(scope = {ValidationScope.create})
    private String tb_id;

    @NamedConstraint(name = ProviderIdValidator.PROVIDER_ID_CONSTRAINT)
    @Scope(scope = {ValidationScope.create})
    private String provider_id;

    @NotNull
    @Scope(scope = {ValidationScope.create})
    @Pattern(regexp = "[0|1][1|2]")
    private String treatment_category;

    @NotNull
    @Digits(integer = 3, fraction = 0, message = "Age must be numeric")
    @Scope(scope = {ValidationScope.create})
    private String age;

    public PatientWebRequest() {
    }

    public PatientWebRequest setPatientInfo(String caseId, String firstName, String lastName, String gender, String patientType, String patientMobileNumber, String phi) {
        this.case_id = caseId;
        this.first_name = firstName;
        this.last_name = lastName;
        this.gender = gender;
        this.patient_type = patientType;
        this.mobile_number = patientMobileNumber;
        this.phi = phi;
        return this;
    }

    public PatientWebRequest setPatientAddress(String houseNumber, String landmark, String block, String village, String district, String state) {
        this.address_location = houseNumber;
        this.address_landmark = landmark;
        this.address_block = block;
        this.address_village = village;
        this.address_district = district;
        this.address_state = state;
        return this;
    }

    public PatientWebRequest setTreatmentData(String category, String tbId, String providerId, String diseaseClass, String patientAge, String registrationNumber) {
        this.treatment_category = category;
        this.tb_id = tbId;
        this.provider_id = providerId;
        this.disease_class = diseaseClass;
        this.age = patientAge;
        this.tb_registration_number = registrationNumber;
        return this;
    }

    public PatientWebRequest setSmearTestResults(String smear_sample_instance_1, String smear_test_date_1, String smear_result_1, String smear_test_date_2, String smear_result_2) {
        this.smear_sample_instance = smear_sample_instance_1;
        this.smear_test_date_1 = smear_test_date_1;
        this.smear_test_result_1 = smear_result_1;
        this.smear_test_date_2 = smear_test_date_2;
        this.smear_test_result_2 = smear_result_2;
        return this;
    }

    public PatientWebRequest setWeightStatistics(String weightInstance, String weight) {
        this.weight_instance = weightInstance;
        this.weight = weight;
        return this;
    }
}