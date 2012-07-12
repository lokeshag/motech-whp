package org.motechproject.whp.patient.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.LocalDate;

import java.util.List;

import static org.motechproject.util.DateUtil.*;

@Data
public class TreatmentInterruption {

    @JsonProperty
    private LocalDate pauseDate;

    @JsonProperty
    private String reasonForPause;

    @JsonProperty
    private LocalDate resumptionDate;

    @JsonProperty
    private String reasonForResumption;

    //required for ektorp
    private TreatmentInterruption() {
    }

    public TreatmentInterruption(String reasonForPause, LocalDate pauseDate) {
        this.reasonForPause = reasonForPause;
        this.pauseDate = pauseDate;
    }

    public void resumeTreatment(String reasonForResumption, LocalDate resumptionDate) {
        this.reasonForResumption = reasonForResumption;
        this.resumptionDate = resumptionDate;
    }

    @JsonIgnore
    public boolean isCurrentlyPaused() {
        return (pauseDate != null && resumptionDate == null);
    }

    @JsonIgnore
    public boolean isTreatmentInterrupted(List<LocalDate> treatmentWeekDates) {
        if (isCurrentlyPaused()) {
            for (LocalDate treatmentWeekDate : treatmentWeekDates) {
                if (isOnOrAfter(newDateTime(treatmentWeekDate), newDateTime(pauseDate))) {
                    return true;
                }
            }
        } else {
            for (LocalDate treatmentWeekDate : treatmentWeekDates) {
                if (isOnOrAfter(newDateTime(treatmentWeekDate), newDateTime(pauseDate)) && isOnOrBefore(newDateTime(treatmentWeekDate), newDateTime(resumptionDate))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDoseDateInInterruptionPeriod(LocalDate doseDate) {
        LocalDate pauseDate = getPauseDate();
        LocalDate resumptionDate = getResumptionDate();
        return isCurrentlyPaused() && isOnOrAfter(newDateTime(doseDate), newDateTime(pauseDate)) ||
                (isOnOrAfter(newDateTime(doseDate), newDateTime(pauseDate))
                        && isOnOrBefore(newDateTime(doseDate), newDateTime(resumptionDate)));
    }

}
