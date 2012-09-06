package org.motechproject.whp.patient.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.whp.patient.builder.TreatmentBuilder;
import org.motechproject.whp.refdata.domain.PatientType;
import org.motechproject.whp.refdata.domain.TreatmentOutcome;

import static junit.framework.Assert.*;
import static org.motechproject.util.DateUtil.now;
import static org.motechproject.util.DateUtil.today;

public class TreatmentTest {

    @Test
    public void shouldCloseTreatment() {
        DateTime now = now();

        Treatment treatment = new Treatment();
        treatment.close(TreatmentOutcome.Cured, now);

        assertEquals(today(), treatment.getEndDate());
        assertEquals(TreatmentOutcome.Cured, treatment.getTreatmentOutcome());
    }

    @Test
    public void shouldPauseTreatment() {
        DateTime now = now();

        Treatment treatment = new Treatment();
        treatment.pause("paws", now);

        assertTrue(treatment.isPaused());
    }

    @Test
    public void shouldResumeTreatment() {
        DateTime now = now();

        Treatment treatment = new Treatment();
        treatment.pause("paws", now);
        treatment.resume("swap", now);

        assertFalse(treatment.isPaused());
    }

    @Test
    public void shouldStoreIdsInLowerCase() {
        Treatment treatment = new Treatment();
        treatment.setProviderId("QWER");
        treatment.setTbId("ASD");
        assertEquals("qwer", treatment.getProviderId());
        assertEquals("asd", treatment.getTbId());

        treatment = new Treatment("QWER", "providerDistrict", "asd", PatientType.New);
        assertEquals("qwer", treatment.getProviderId());
        assertEquals("asd", treatment.getTbId());
    }

    @Test
    public void shouldHandleNullValuesForId() {
        Treatment treatment = new Treatment(null, "providerDistrict", null, PatientType.New);
        assertEquals(null, treatment.getProviderId());
        assertEquals(null, treatment.getTbId());

        treatment = new Treatment("", "providerDistrict", "", PatientType.New);
        treatment.setProviderId(null);
        treatment.setTbId(null);
        assertEquals(null, treatment.getProviderId());
        assertEquals(null, treatment.getTbId());
    }

    @Test
    public void shouldReturnTrueIfDoseDateIsTheFirstDayOfTreatment() {
        Treatment treatment = new TreatmentBuilder().withStartDate(new LocalDate(2012, 1, 1)).build();
        assertTrue(treatment.isDateInTreatment(new LocalDate(2012, 1, 1)));
    }

    @Test
    public void shouldReturnTrueIfDoseDateBelongsToTreatment() {
        Treatment treatment = new TreatmentBuilder().withStartDate(new LocalDate(2012, 1, 1)).build();
        assertTrue(treatment.isDateInTreatment(new LocalDate(2013, 10, 20)));
    }

    @Test
    public void shouldReturnTrueIfDoseDateIsTheLastDayOfTreatment() {
        Treatment treatment = new TreatmentBuilder().withStartDate(new LocalDate(2012, 1, 1)).withEndDate(new LocalDate(2012, 5, 1)).build();
        assertTrue(treatment.isDateInTreatment(new LocalDate(2012, 5, 1)));
    }

    @Test
    public void shouldReturnFalseIfDoseDateIsBeforeTreatmentStartDate() {
        Treatment treatment = new TreatmentBuilder().withStartDate(new LocalDate(2012, 1, 2)).withEndDate(new LocalDate(2012, 5, 1)).build();
        assertFalse(treatment.isDateInTreatment(new LocalDate(2012, 1, 1)));
    }

    @Test
    public void shouldReturnFalseIfDoseDateIsAfterTreatmentEndDate() {
        Treatment treatment = new TreatmentBuilder().withStartDate(new LocalDate(2012, 1, 2)).withEndDate(new LocalDate(2012, 5, 1)).build();
        assertFalse(treatment.isDateInTreatment(new LocalDate(2012, 5, 2)));
    }

}
