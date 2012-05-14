package org.motechproject.whp.integration.validation.provider;

import org.junit.Test;
import org.motechproject.whp.builder.ProviderRequestBuilder;
import org.motechproject.whp.request.ProviderWebRequest;
import org.motechproject.whp.validation.ValidationScope;

public class ProviderIdTest extends BaseProviderTest {
    @Test
    public void shouldThrowAnExceptionIfProviderIdIsNull() {
        expectWHPException("field:provider_id:may not be empty");
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId(null).withDate("17/03/1990 17:03:56").withDistrict("Chambal").withPrimaryMobile("9880000000").build();
        validator.validate(providerWebRequest, ValidationScope.create); //Can be any scope. None of the validation is scope dependent.
    }

    @Test
    public void shouldThrowAnExceptionIfProviderIdIsEmpty() {
        expectWHPException("field:provider_id:may not be empty");
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId("").withDate("17/03/1990 17:03:56").withDistrict("Chambal").withPrimaryMobile("9880000000").build();
        validator.validate(providerWebRequest, ValidationScope.create); //Can be any scope. None of the validation is scope dependent.
    }
}