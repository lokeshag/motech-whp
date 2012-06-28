package org.motechproject.whp.adherence.mapping;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.adherence.domain.Adherence;
import org.motechproject.whp.adherence.domain.AdherenceConstants;
import org.motechproject.whp.adherence.domain.PillStatus;
import org.motechproject.whp.patient.builder.PatientBuilder;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.Treatment;
import org.motechproject.whp.patient.domain.Therapy;
import org.motechproject.whp.refdata.domain.TreatmentCategory;
import org.motechproject.whp.refdata.domain.DiseaseClass;
import org.motechproject.whp.refdata.domain.PatientType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.model.DayOfWeek.Monday;

public class AdherenceRecordMapperTest {

    public static final String PROVIDER_ID = "providerid";
    public static final String TB_ID = "tb_id";

    private Patient patient;

    @Before
    public void setup() {
        Therapy therapy = new Therapy(new TreatmentCategory(), DiseaseClass.E, 10);

        Treatment currentTreatment = new Treatment(PROVIDER_ID, TB_ID, PatientType.New);
        currentTreatment.setTherapy(therapy);

        patient = new PatientBuilder().withDefaults().withCurrentTreatment(currentTreatment).build();
    }

    @Test
    public void shouldSetExternalIdOnRequest() {
        Adherence day = new Adherence(patient.getPatientId(), patient.currentTherapyId(), Monday, DateUtil.today(), PillStatus.NotTaken, TB_ID, PROVIDER_ID);

        assertEquals(patient.getPatientId(), AdherenceRecordMapper.map(day).externalId());
    }

    @Test
    public void shouldSetProviderIdOnRequest() {
        Adherence day = new Adherence(patient.getPatientId(), patient.currentTherapyId(), Monday, DateUtil.today(), PillStatus.NotTaken, TB_ID, PROVIDER_ID);

        assertEquals(PROVIDER_ID, AdherenceRecordMapper.map(day).meta().get(AdherenceConstants.PROVIDER_ID));
    }

    @Test
    public void shouldSetTbIdAsMetaDataOnRequest() {
        Adherence day = new Adherence(patient.getPatientId(), patient.currentTherapyId(), Monday, DateUtil.today(), PillStatus.Unknown, TB_ID, PROVIDER_ID);

        assertNotNull(patient.tbId());
        assertEquals(patient.tbId(), AdherenceRecordMapper.map(day).meta().get(AdherenceConstants.TB_ID));
    }

    @Test
    public void shouldMarkDosesTaken() {
        Adherence day = new Adherence(patient.getPatientId(), patient.currentTherapyId(), Monday, DateUtil.today(), PillStatus.Taken, TB_ID, PROVIDER_ID);

        assertEquals(1, AdherenceRecordMapper.map(day).status());
    }

    @Test
    public void shouldMarkDosesMissed() {
        Adherence day = new Adherence(patient.getPatientId(), patient.currentTherapyId(), Monday, DateUtil.today(), PillStatus.NotTaken, TB_ID, PROVIDER_ID);

        assertEquals(2, AdherenceRecordMapper.map(day).status());
    }
}
