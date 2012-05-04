package org.motechproject.whp.patient.builder;

import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.patient.domain.*;

import java.util.Arrays;
import java.util.List;

import static org.motechproject.whp.patient.domain.SmearTestResult.Negative;
import static org.motechproject.whp.patient.domain.SmearTestSampleInstance.PreTreatment;

public class PatientBuilder {

    List<DayOfWeek> threeDaysAWeek = Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday);

    private final Patient patient;

    public PatientBuilder() {
        patient = new Patient();
    }

    public PatientBuilder withDefaults() {
        patient.setPatientId("patientId");
        patient.setFirstName("firstName");
        patient.setLastName("lastName");
        patient.setGender(Gender.O);
        patient.setPatientType(PatientType.New);
        patient.setPhoneNumber("1234567890");
        patient.setCurrentProvidedTreatment(defaultProvidedTreatment());
        return this;
    }

    public Patient build() {
        return patient;
    }

    private ProvidedTreatment defaultProvidedTreatment() {
        ProvidedTreatment providedTreatment = new ProvidedTreatment();
        providedTreatment.setTreatment(defaultTreatment());
        providedTreatment.setPatientAddress(defaultAddress());
        return providedTreatment;
    }

    private Treatment defaultTreatment() {
        Treatment treatment = new Treatment();
        LocalDate today = DateUtil.today();
        treatment.setTreatmentCategory(new TreatmentCategory("RNTCP Category 1", "01", 3, 8, 18, threeDaysAWeek));
        treatment.addSmearTestResult(new SmearTestResults(PreTreatment, today, Negative, today, Negative));
        treatment.addWeightStatistics(new WeightStatistics(WeightInstance.PreTreatment, 100.0, today));
        return treatment;
    }

    private Address defaultAddress() {
        return new Address("10", "banyan tree", "10", "chambal", "muzzrafapur", "bhiar");
    }
}
