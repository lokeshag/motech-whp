package org.motechproject.whp.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.whp.patient.builder.PatientBuilder;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.repository.AllPatients;
import org.motechproject.whp.refdata.domain.WHPConstants;
import org.motechproject.whp.service.TreatmentCardService;
import org.motechproject.whp.uimodel.DailyAdherenceRequest;
import org.motechproject.whp.uimodel.TreatmentCardModel;
import org.motechproject.whp.uimodel.UpdateAdherenceRequest;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TreatmentCardControllerTest {

    @Mock
    TreatmentCardService treatmentCardService;
    @Mock
    AllPatients allPatients;
    @Mock
    Model uiModel;
    @Mock
    HttpServletRequest request;
    Patient patient;

    TreatmentCardController treatmentCardController;

    @Before
    public void setup() {
        initMocks(this);
        treatmentCardController = new TreatmentCardController(treatmentCardService, allPatients);
        patient = new PatientBuilder().withDefaults().build();
        when(allPatients.findByPatientId(patient.getPatientId())).thenReturn(patient);
    }

    @Test
    public void shouldReturnTreatmentCardModelToView() {
        TreatmentCardModel treatmentCardModel = new TreatmentCardModel();
        when(treatmentCardService.getIntensivePhaseTreatmentCardModel(patient)).thenReturn(treatmentCardModel);

        String view = treatmentCardController.show(patient.getPatientId(), uiModel, request);

        verify(uiModel).addAttribute("patientId", patient.getPatientId());
        verify(uiModel).addAttribute("treatmentCard", treatmentCardModel);
        assertEquals("treatment-card/show", view);
    }

    @Test
    public void shouldSaveAdherenceData() throws IOException {
        UpdateAdherenceRequest adherenceData = new UpdateAdherenceRequest();
        adherenceData.setPatientId("test");
        DailyAdherenceRequest dailyAdherenceRequest1 = new DailyAdherenceRequest(6, 7, 2012, 1);
        DailyAdherenceRequest dailyAdherenceRequest2 = new DailyAdherenceRequest(13, 8, 2012, 2);
        adherenceData.setDailyAdherenceRequests(asList(dailyAdherenceRequest1, dailyAdherenceRequest2));

        when(allPatients.findByPatientId(adherenceData.getPatientId())).thenReturn(patient);

        String view = treatmentCardController.update(adherenceData, uiModel, request);

        assertEquals("treatment-card/show", view);
        verify(uiModel, times(1)).addAttribute(WHPConstants.NOTIFICATION_MESSAGE, "Treatment Card saved successfully");
        verify(treatmentCardService, times(1)).addLogsForPatient(adherenceData, patient);
    }

}