package org.motechproject.whp.patient.domain;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.LocalDate;
import org.motechproject.whp.common.WHPDate;
import org.motechproject.whp.refdata.domain.Phase;
import org.motechproject.whp.refdata.domain.TreatmentCategory;

@Data
public class PhaseRecord {

    private LocalDate startDate;
    private LocalDate endDate;
    private Phase name;
    /*Has to be updated under multiple cases. Identified so far:
    1) CMF Admin adherence update: Triggered by AdherenceController.update() -> PhaseUpdateOrchestrator
    2) Provider adherence update: Triggered by AdherenceController.update() -> PhaseUpdateOrchestrator
    3) CMF Admin startDate/endDate update: Triggered by PatientController.update() -> PhaseUpdateOrchestrator
    4) PhaseRecord transition: TODO
    */
    private int numberOfDosesTaken;
    private int numberOfDosesTakenAsOfLastSunday;

    public PhaseRecord() {
    }

    public PhaseRecord(Phase phaseName) {
        this.name = phaseName;
    }

    public boolean hasStarted() {
        return startDate != null;
    }

    public int remainingDoses(TreatmentCategory treatmentCategory) {
        return treatmentCategory.numberOfDosesForPhase(name) - numberOfDosesTaken;
    }

    public void start(PhaseRecord previousPhase) {
        this.startDate = previousPhase.endDate.plusDays(1);
    }

    public void stop(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void unset() {
        this.startDate = null;
        this.endDate = null;
    }

    @JsonIgnore
    public String getEndDateAsString() {
        return WHPDate.date(endDate).value();
    }
}
