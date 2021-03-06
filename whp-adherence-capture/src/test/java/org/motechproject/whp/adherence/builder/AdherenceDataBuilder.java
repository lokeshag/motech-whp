package org.motechproject.whp.adherence.builder;

import org.joda.time.LocalDate;
import org.motechproject.whp.adherence.domain.Adherence;
import org.motechproject.whp.adherence.domain.PillStatus;

public class AdherenceDataBuilder {

    public static Adherence createLog(LocalDate doseDate, String providerId, PillStatus pillStatus) {
        Adherence log = new Adherence();
        log.setPatientId("externalid");
        log.setPillDate(doseDate);
        log.setPillStatus(pillStatus);
        log.setProviderId(providerId);
        return log;
    }

    public static Adherence createLog(LocalDate doseDate, String providerId, String tbId, PillStatus pillStatus) {
        Adherence log = new Adherence();
        log.setPatientId("externalid");
        log.setPillDate(doseDate);
        log.setPillStatus(pillStatus);
        log.setProviderId(providerId);
        log.setTbId(tbId);
        return log;
    }
}