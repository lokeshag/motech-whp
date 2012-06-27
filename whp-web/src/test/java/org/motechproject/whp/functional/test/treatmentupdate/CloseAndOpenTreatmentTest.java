package org.motechproject.whp.functional.test.treatmentupdate;

import org.junit.Test;
import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.functional.page.ProviderPage;
import org.motechproject.whp.patient.builder.PatientRequestBuilder;
import org.motechproject.whp.patient.command.UpdateScope;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.domain.TreatmentCategory;
import org.motechproject.whp.refdata.domain.PatientType;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CloseAndOpenTreatmentTest extends TreatmentUpdateTest {

    @Test
    public void shouldUpdateTreatmentCategoryForPatientOnCloseOfCurrentTreatmentAndOpenOfNewTreatment() {
        ProviderPage providerPage = loginAsProvider(provider);
        assertTrue(providerPage.hasPatient(patient.getFirstName()));
        assertEquals("RNTCP Category 1", providerPage.getTreatmentCategoryText(patient.getCaseId()));

        PatientRequest closeTreatmentUpdateRequest = new PatientRequestBuilder()
                .withMandatoryFieldsForCloseTreatment()
                .withTbId(patient.getTbId())
                .withCaseId(patient.getCaseId())
                .withDateModified(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .build();
        factory.updateFor(UpdateScope.closeTreatment).apply(closeTreatmentUpdateRequest);
        providerPage.logout();
        providerPage = loginAsProvider(provider);

        assertFalse(providerPage.hasPatient(patient.getFirstName()));

        TreatmentCategory newCategory = new TreatmentCategory("Do Not Copy", "10", 3, 8, 18, 24, 54, Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday));

        PatientRequest openNewTreatmentUpdateRequest = new PatientRequestBuilder()
                .withMandatoryFieldsForOpenNewTreatment()
                .withCaseId(patient.getCaseId())
                .withTreatmentCategory(newCategory)
                .withProviderId(patient.getProviderId())
                .withDateModified(DateUtil.newDateTime(2012, 3, 17, 4, 55, 50))
                .withPatientType(PatientType.Chronic)
                .withTbId("elevenDigit")
                .build();
        factory.updateFor(UpdateScope.openTreatment).apply(openNewTreatmentUpdateRequest);

        providerPage.logout();
        providerPage = loginAsProvider(provider);

        assertTrue(providerPage.hasPatient(patient.getFirstName()));
        assertEquals(openNewTreatmentUpdateRequest.getTreatment_category().getName(), providerPage.getTreatmentCategoryText(patient.getCaseId()));
    }
}
