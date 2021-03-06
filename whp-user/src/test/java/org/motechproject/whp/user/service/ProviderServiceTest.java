package org.motechproject.whp.user.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.scheduler.context.EventContext;
import org.motechproject.security.domain.MotechWebUser;
import org.motechproject.security.service.MotechAuthenticationService;
import org.motechproject.security.service.MotechUser;
import org.motechproject.whp.common.event.EventKeys;
import org.motechproject.whp.user.contract.ProviderRequest;
import org.motechproject.whp.user.domain.Provider;
import org.motechproject.whp.user.domain.WHPRole;
import org.motechproject.whp.user.repository.AllProviders;

import java.util.Arrays;
import java.util.Map;

import static junit.framework.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.now;
import static org.motechproject.whp.user.builder.ProviderBuilder.newProviderBuilder;

@RunWith(MockitoJUnitRunner.class)
public class ProviderServiceTest {

    @Mock
    private MotechAuthenticationService motechAuthenticationService;

    @Mock
    private AllProviders allProviders;

    @Mock
    private EventContext eventContext;

    private ProviderService providerService;


    @Before
    public void setUp() {
        initMocks(this);
        providerService = new ProviderService(motechAuthenticationService, allProviders, eventContext);
    }

    @Test
    public void shouldFetchProvidersByDistrictAndProviderId() {
        providerService.fetchBy("district", "providerId");
        verify(allProviders).findByDistrictAndProviderId("district", "providerId");
    }

    @Test
    public void shouldFetchProvidersByOnlyDistrict_WhenProviderIdIsEmpty() {
        providerService.fetchBy("district", "");
        verify(allProviders).findByDistrict("district");
    }

    @Test
    public void shouldFetchProvidersByOnlyDistrict() {
        providerService.fetchBy("district");
        verify(allProviders).findByDistrict("district");
    }

    @Test
    public void shouldFetchAllProviderWebUsers() {
        MotechUser motechUser = new MotechUser(new MotechWebUser("provider", "password", "externalId", null));
        when(motechAuthenticationService.findByRole(WHPRole.PROVIDER.name())).thenReturn(Arrays.asList(motechUser));

        Map<String, MotechUser> motechUserMap = providerService.fetchAllWebUsers();

        verify(motechAuthenticationService).findByRole(WHPRole.PROVIDER.name());
        assertNotNull(motechUserMap);
        assertEquals(1, motechUserMap.size());
        assertTrue(motechUserMap.containsKey("provider"));
        assertEquals(motechUser, motechUserMap.get("provider"));
    }

    @Test
    public void shouldFetchProviderById() {
        String providerId = "providerId";
        Provider expectedProvider = new Provider();
        when(allProviders.findByProviderId(providerId)).thenReturn(expectedProvider);
        Provider provider = providerService.findByProviderId(providerId);
        assertEquals(expectedProvider, provider);
        verify(allProviders).findByProviderId(providerId);
    }

    @Test
    public void shouldReturnTrueIfProviderExists_forGivenMobileNumber() {
        String mobileNumber = "1234567890";
        Provider provider = newProviderBuilder().withDefaults().withPrimaryMobileNumber(mobileNumber).build();
        when(allProviders.findByMobileNumber(mobileNumber)).thenReturn(provider);

        Provider returnedProvider = providerService.findByMobileNumber(mobileNumber);

        assertThat(returnedProvider, is(provider));
        verify(allProviders).findByMobileNumber(mobileNumber);
    }

    @Test
    public void shouldCreateProvider(){
        String providerId = "providerId";
        ProviderRequest providerRequest = new ProviderRequest(providerId, "district", "primaryMobile", now());
        when(allProviders.findByProviderId(providerId)).thenReturn(null);

        providerService.createOrUpdateProvider(providerRequest);

        verify(allProviders).findByProviderId(providerRequest.getProviderId());
        verify(allProviders).addOrReplace(providerRequest.makeProvider());
    }

    @Test
    public void shouldUpdateProvider(){
        String providerId = "providerId";
        ProviderRequest providerRequest = new ProviderRequest(providerId, "district", "primaryMobile", now());
        Provider provider = providerRequest.makeProvider();
        Provider providerFromDatabase = providerRequest.makeProvider();

        when(allProviders.findByProviderId(providerRequest.getProviderId())).thenReturn(providerFromDatabase);

        providerService.createOrUpdateProvider(providerRequest);

        verify(allProviders).findByProviderId(providerRequest.getProviderId());
        verify(allProviders).addOrReplace(provider);
    }

    @Test
    public void shouldUpdateProviderAndSendEvent_whenDistrictHasChanged(){
        String providerId = "providerId";
        ProviderRequest providerRequest = new ProviderRequest(providerId, "district", "primaryMobile", now());
        Provider provider = providerRequest.makeProvider();
        Provider providerFromDatabase = providerRequest.makeProvider();
        providerFromDatabase.setDistrict("oldDistrict");

        when(allProviders.findByProviderId(providerRequest.getProviderId())).thenReturn(providerFromDatabase);

        providerService.createOrUpdateProvider(providerRequest);

        verify(eventContext).send(EventKeys.PROVIDER_DISTRICT_CHANGE, provider.getProviderId());
        verify(allProviders).findByProviderId(providerRequest.getProviderId());
        verify(allProviders).addOrReplace(provider);
    }


    @Test
    public void shouldReturnFalseIfProviderDoesNotExists_forGivenMobileNumber() {
        String mobileNumber = "1234567899";
        when(allProviders.findByMobileNumber(mobileNumber)).thenReturn(null);

        Provider returnedProvider = providerService.findByMobileNumber(mobileNumber);

        verify(allProviders).findByMobileNumber(mobileNumber);
        assertThat(returnedProvider, nullValue());
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(allProviders);
        verifyNoMoreInteractions(eventContext);
        verifyNoMoreInteractions(motechAuthenticationService);
    }
}
