package org.motechproject.whp.controller;

import org.motechproject.security.domain.MotechWebUser;
import org.motechproject.security.domain.Role;
import org.motechproject.security.repository.AllMotechWebUsers;
import org.motechproject.whp.patient.domain.Provider;
import org.motechproject.whp.patient.repository.AllCmfLocations;
import org.motechproject.whp.patient.repository.AllProviders;
import org.motechproject.whp.refdata.domain.WHPRole;
import org.motechproject.whp.uimodel.ProviderRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.util.StringUtils.hasText;

@Controller
@RequestMapping(value = "/providers")
public class ProviderController {

    AllMotechWebUsers allMotechWebUsers;
    AllProviders allProviders;
    AllCmfLocations allDistricts;
    public static final String PROVIDER_LIST = "providerList";
    private static final String DISTRICT_LIST = "districts";
    private static final String QUERY_PROVIDER_ID = "queryProviderId";

    @Autowired
    public ProviderController(AllProviders allProviders, AllCmfLocations allDistricts, AllMotechWebUsers allMotechWebUsers) {
        this.allProviders = allProviders;
        this.allDistricts = allDistricts;
        this.allMotechWebUsers = allMotechWebUsers;
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String loadProviderSearchPage(Model uiModel) {
        prepareQueryModel(uiModel, "");
        prepareResultsModel(uiModel, filterByProviderId(null), fetchAllProviderWebUsers());
        return "provider/list";
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public String searchMatchingProviders(String providerId, Model uiModel) {
        prepareQueryModel(uiModel, providerId);
        prepareResultsModel(uiModel, filterByProviderId(providerId), fetchAllProviderWebUsers());
        return "provider/list";
    }

    private Set<MotechWebUser> fetchAllProviderWebUsers() {
        return new HashSet<MotechWebUser>(allMotechWebUsers.findByRoles(new Role(WHPRole.PROVIDER.name())));
    }

    private List<Provider> filterByProviderId(String providerId) {
        List<Provider> matchingProviders = new ArrayList<Provider>();
        if(hasText(providerId)) {
            Provider matchingProvider = allProviders.findByProviderId(providerId);
            if(matchingProvider != null) {
                matchingProviders.add(matchingProvider);
            }
        } else {
            matchingProviders.addAll(allProviders.list());
        }
        return matchingProviders;
    }

    private void prepareQueryModel(Model uiModel, String providerId) {
        uiModel.addAttribute(QUERY_PROVIDER_ID, providerId);
        // Commented as Story #3 is de-scoped not to show districts drop-down menu
        /*List<CmfLocation> cmfLocations = allDistricts.getAll();
        uiModel.addAttribute(DISTRICT_LIST, extract(cmfLocations, on(CmfLocation.class).getLocation()));*/
    }

    private void prepareResultsModel(Model uiModel, List<Provider> matchingProviders, Set<MotechWebUser> motechWebUsers) {
        List<ProviderRow> providerRows = new ArrayList<ProviderRow>();
        for(Provider provider : matchingProviders) {
            for(MotechWebUser webUser : motechWebUsers) {
                if(provider.getId().equals(webUser.getExternalId())) {
                    providerRows.add(new ProviderRow(provider, webUser.isActive()));
                }
            }
        }
        uiModel.addAttribute(PROVIDER_LIST, providerRows);
    }
}