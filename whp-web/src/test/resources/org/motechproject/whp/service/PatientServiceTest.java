package org.motechproject.whp.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.PatientType;
import org.motechproject.whp.patient.repository.AllPatients;
import org.motechproject.whp.patient.repository.AllTreatments;
import org.motechproject.whp.request.PatientRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientServiceTest {

    @Mock
    AllPatients allPatients;
    @Mock
    AllTreatments allTreatments;

    PatientService patientService;

    @Before
    public void setUp() {
        initMocks(this);
        patientService = new PatientService(allPatients, allTreatments);
    }

    @Test
    public void shouldCreatePatient() {
        PatientRequest patientRequest = new PatientRequest()
                .setPatientInfo("caseId", "Foo", "Bar", "M", PatientType.PHSTransfer.name(), "12345667890")
                .setRegistrationDetails("regNum", DateUtil.today().toString())
                .setSmearTestResults("Pre-treatment1", DateUtil.today().minusDays(10).toString(), "result1", "Pre-treatment2", DateUtil.today().minusDays(5).toString(), "result2")
                .setTreatmentData("01", "providerId01seq1", "providerId");

        when(allPatients.findByPatientId("caseId")).thenReturn(null);
        patientService.createCase(patientRequest);

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);

        verify(allPatients).add(patientArgumentCaptor.capture());

        Patient patient = patientArgumentCaptor.getValue();
        assertEquals("caseId", patient.getPatientId());
    }

}