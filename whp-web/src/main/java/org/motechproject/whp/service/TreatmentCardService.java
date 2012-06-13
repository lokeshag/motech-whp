package org.motechproject.whp.service;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.motechproject.adherence.contract.AdherenceData;
import org.motechproject.adherence.repository.AllAdherenceLogs;
import org.motechproject.whp.contract.TreatmentCardModel;
import org.motechproject.whp.patient.domain.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TreatmentCardService {
    AllAdherenceLogs allAdherenceLogs;

    @Autowired
    public TreatmentCardService(AllAdherenceLogs allAdherenceLogs) {
        this.allAdherenceLogs = allAdherenceLogs;
    }

    public TreatmentCardModel getIntensivePhaseTreatmentCardModel(Patient patient) {
        if (patient != null && patient.latestTherapy() != null && patient.latestTherapy().getStartDate() != null) {

            TreatmentCardModel ipTreatmentCard = new TreatmentCardModel();
            LocalDate ipStartDate = patient.latestTherapy().getStartDate();
            LocalDate endDate = ipStartDate.plusMonths(5);

            List<AdherenceData> adherenceData = allAdherenceLogs.findLogsInRange(patient.getPatientId(), ipStartDate, endDate);
            Period period = new Period().withMonths(5);
            ipTreatmentCard.addAdherenceDataForGivenTherapy(patient, adherenceData, patient.latestTherapy(), period);

            return ipTreatmentCard;
        }
        return null;
    }
}