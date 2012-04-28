package org.motechproject.whp.refdata.seed;


import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.security.service.MotechAuthenticationService;
import org.motechproject.whp.refdata.WHPRefDataConstants;
import org.motechproject.whp.refdata.domain.WHPRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AdministratorSeed {
    @Autowired
    private MotechAuthenticationService authenticationService;

    @Seed(priority = 0)
    public void load() {
        authenticationService.register("admin", "password", WHPRefDataConstants.ADMIN_USER_TYPE, null, Arrays.asList(WHPRole.ADMIN.name()));
    }
}
