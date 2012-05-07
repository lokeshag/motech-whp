package org.motechproject.whp.functional;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.functional.data.TestProvider;
import org.motechproject.whp.functional.framework.BaseTest;
import org.motechproject.whp.functional.framework.MyPageFactory;
import org.motechproject.whp.functional.page.LoginPage;
import org.motechproject.whp.functional.page.ProviderPage;
import org.motechproject.whp.functional.service.ProviderDataService;
import org.motechproject.whp.patient.builder.PatientRequestBuilder;
import org.motechproject.whp.patient.builder.TreatmentUpdateRequestBuilder;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.contract.TreatmentUpdateRequest;
import org.motechproject.whp.patient.domain.TreatmentCategory;
import org.motechproject.whp.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/applicationFunctionalTestContext.xml")
public class TreatmentUpdateTest extends BaseTest {

    @Autowired
    PatientService patientService;

    ProviderDataService providerDataService;

    PatientRequest patientRequest;
    TestProvider provider;

    @Override
    public void setUp() {
        super.setUp();
        setupProvider();
        setupPatientForProvider();
    }

    public void setupProvider() {
        providerDataService = new ProviderDataService(webDriver);
        provider = providerDataService.createProvider();
    }

    public void setupPatientForProvider() {
        TreatmentCategory oldCategory = new TreatmentCategory("RNTCP Category 1", "01", 3, 8, 18, Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday));
        patientRequest = new PatientRequestBuilder()
                .withDefaults()
                .withLastModifiedDate(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .withTreatmentCategory(oldCategory)
                .withCaseId(UUID.randomUUID().toString())
                .withProviderId(provider.getProviderId())
                .build();
        patientService.createPatient(patientRequest);
    }

    @Test
    public void shouldUpdateTreatmentCategoryForPatientOnCloseOfCurrentTreatmentAndOpenOfNewTreatment() {
        ProviderPage providerPage = loginAsProvider();
        assertTrue(providerPage.hasPatient(patientRequest.getFirst_name()));
        assertTrue(providerPage.hasTreatmentCategory(patientRequest.getTreatment_category().getName()));

        TreatmentUpdateRequest closeTreatmentUpdateRequest = TreatmentUpdateRequestBuilder.startRecording()
                                                                                          .withMandatoryFieldsForCloseTreatment()
                                                                                          .withTbId(patientRequest.getTb_id())
                                                                                          .withCaseId(patientRequest.getCase_id())
                                                                                          .withDateModified(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                                                                                          .build();
        patientService.performTreatmentUpdate(closeTreatmentUpdateRequest);

        TreatmentCategory newCategory = new TreatmentCategory("Do Not Copy", "10", 3, 8, 18, Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday));

        TreatmentUpdateRequest openNewTreatmentUpdateRequest = TreatmentUpdateRequestBuilder.startRecording()
                                                                                            .withMandatoryFieldsForOpenNewTreatment()
                                                                                            .withCaseId(patientRequest.getCase_id())
                                                                                            .withTreatmentCategory(newCategory)
                                                                                            .withDateModified(DateUtil.newDateTime(2012, 3, 17, 4, 55, 50))
                                                                                            .withTbId("elevenDigit")
                                                                                            .build();
        patientService.performTreatmentUpdate(openNewTreatmentUpdateRequest);

        providerPage.logout();
        providerPage = loginAsProvider();

        assertTrue(providerPage.hasPatient(patientRequest.getFirst_name()));
        assertTrue(providerPage.hasTreatmentCategory(openNewTreatmentUpdateRequest.getTreatment_category().getName()));
    }

    ProviderPage loginAsProvider() {
        return MyPageFactory.initElements(webDriver, LoginPage.class).loginWithProviderUserNamePassword(provider.getProviderId(), provider.getPassword());
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
    }
}
