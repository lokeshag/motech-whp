package org.motechproject.whp.domain.v1;

public enum SampleInstanceV1 {
    PreTreatment("Pre-Treatment"), EndIP("End of Intensive Phase"), ExtendedIP("Extended IP"), TwoMonthsIntoCP("Two Months Into CP"), EndTreatment("End Treatment");

    private String value;

    SampleInstanceV1(String value) {
       this.value = value;
    }

    public String value(){
        return value;
    }

}

