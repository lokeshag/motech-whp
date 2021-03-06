package org.motechproject.whp.patient.builder;

import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.patient.domain.*;
import org.motechproject.whp.refdata.domain.*;

import java.util.Arrays;
import java.util.List;

public class TreatmentBuilder {

    List<DayOfWeek> threeDaysAWeek = Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday);

    private final Treatment treatment;

    public TreatmentBuilder() {
        treatment = new Treatment();
    }

    public static Treatment treatment() {
        return new TreatmentBuilder().withDefaults().build();
    }

    public TreatmentBuilder withDefaults() {
        LocalDate today = DateUtil.today();
        treatment.setTbId("elevenDigit");
        treatment.setPatientAddress(defaultAddress());
        treatment.setPatientType(PatientType.New);
        treatment.addSmearTestResult(new SmearTestRecord(SampleInstance.PreTreatment, today, SmearTestResult.Negative, today, SmearTestResult.Negative, "labName", "labNumber"));
        treatment.addWeightStatistics(new WeightStatisticsRecord(SampleInstance.PreTreatment, 100.0, today));
        return this;
    }

    private Therapy defaultTherapy() {
        Therapy therapy = new Therapy();
        therapy.setTreatmentCategory(new TreatmentCategory("RNTCP Category 1", "01", 3, 12, 36, 4, 12, 22, 66, threeDaysAWeek));
        therapy.setDiseaseClass(DiseaseClass.P);
        return therapy;
    }

    private Address defaultAddress() {
        return new Address("10", "banyan tree", "10", "chambal", "muzzrafapur", "bhiar");
    }

    public TreatmentBuilder withProviderId(String providerId) {
        treatment.setProviderId(providerId);
        return this;
    }

    public TreatmentBuilder withPatientType(PatientType patientType) {
        treatment.setPatientType(patientType);
        return this;
    }

    public Treatment build() {
        return treatment;
    }

    public TreatmentBuilder withStartDate(LocalDate date) {
        treatment.setStartDate(date);
        return this;
    }

    public TreatmentBuilder withEndDate(LocalDate date) {
        treatment.setEndDate(date);
        return this;
    }

    public TreatmentBuilder withTbId(String tbId) {
        treatment.setTbId(tbId);
        return this;
    }

    public TreatmentBuilder withTbRegistrationNumber(String tbRegistrationNo) {
        treatment.setTbRegistrationNumber(tbRegistrationNo);
        return  this;
    }

    public TreatmentBuilder withAddress(Address address) {
        treatment.setPatientAddress(address);
        return this;
    }

    public TreatmentBuilder withWeightStatistics(WeightStatistics weightStatistics) {
        treatment.setWeightStatistics(weightStatistics);
        return this;
    }

    public TreatmentBuilder withSmearTestResults(SmearTestResults smearTestResults) {
        treatment.setSmearTestResults(smearTestResults);
        return this;
    }
}
