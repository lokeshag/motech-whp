package org.motechproject.whp.patient.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CmfAdminTest {

    @Test
    public void shouldsetUserNameToLowerCase() {
        CmfAdmin cmfAdmin = new CmfAdmin("USER","a@b.com","dept",null);
        assertEquals("user",cmfAdmin.getUserId());
    }
}
