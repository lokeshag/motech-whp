package org.motechproject.whp.container.validation;

import org.motechproject.whp.common.error.ErrorWithParameters;
import org.motechproject.whp.container.contract.CmfAdminContainerRegistrationRequest;
import org.motechproject.whp.container.contract.ContainerRegistrationRequest;
import org.motechproject.whp.container.mapping.service.AdminContainerMappingService;
import org.motechproject.whp.container.mapping.service.ProviderContainerMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.motechproject.whp.container.contract.ContainerRegistrationMode.NEW_CONTAINER;
import static org.motechproject.whp.container.contract.ContainerRegistrationMode.ON_BEHALF_OF_PROVIDER;

@Component
public class CmfAdminContainerRegistrationValidator implements ContainerRegistrationValidator <CmfAdminContainerRegistrationRequest> {

    private final CommonContainerRegistrationValidator containerRegistrationRequestValidator;
    private final AdminContainerMappingService adminContainerMappingService;
    private final ProviderContainerMappingService providerContainerMappingService;

    @Autowired
    public CmfAdminContainerRegistrationValidator(CommonContainerRegistrationValidator containerRegistrationRequestValidator, AdminContainerMappingService adminContainerMappingService, ProviderContainerMappingService providerContainerMappingService) {
        this.containerRegistrationRequestValidator = containerRegistrationRequestValidator;
        this.adminContainerMappingService = adminContainerMappingService;
        this.providerContainerMappingService = providerContainerMappingService;
    }

    public List<ErrorWithParameters> validate(CmfAdminContainerRegistrationRequest request) {
        List<ErrorWithParameters> errors = containerRegistrationRequestValidator.validate(request);
        if(!errors.isEmpty()){
            return errors;
        }

        if(request.getContainerRegistrationMode() == NEW_CONTAINER){
            if(!adminContainerMappingService.isValidContainer(Long.parseLong(request.getContainerId()))) {
                errors.add(new ErrorWithParameters("container.id.invalid.error", request.getContainerId()));
            }
            return errors;
        }

        if(request.getContainerRegistrationMode() == ON_BEHALF_OF_PROVIDER){
            if(!providerContainerMappingService.isValidContainerForProvider(request.getProviderId(), request.getContainerId())) {
                errors.add(new ErrorWithParameters("container.id.invalid.error", request.getContainerId()));
            }
            return errors;
        }

        return errors;
    }
}