package org.motechproject.whp.ivr.transition;

import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.ITransition;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.whp.ivr.WHPIVRMessage;
import org.motechproject.whp.ivr.service.AdherenceRecordingService;
import org.motechproject.whp.ivr.util.IvrSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.whp.ivr.prompts.AdherenceSummaryPrompts.adherenceSummaryPrompts;
import static org.motechproject.whp.ivr.prompts.CallCompletionPrompts.callCompletionPrompts;
import static org.motechproject.whp.ivr.prompts.CaptureAdherencePrompts.captureAdherencePrompts;


@Component
public class AdherenceSummaryTransition implements ITransition {

    @Autowired
    private WHPIVRMessage whpivrMessage;
    @Autowired
    private AdherenceRecordingService recordingService;

    /*Required for platform autowiring*/
    public AdherenceSummaryTransition() {
    }

    public AdherenceSummaryTransition(WHPIVRMessage whpivrMessage, AdherenceRecordingService recordingService) {
        this.whpivrMessage = whpivrMessage;
        this.recordingService = recordingService;
    }

    @Override
    public Node getDestinationNode(String input, FlowSession flowSession) {
        IvrSession ivrSession = recordingService.prepareSession(flowSession);

        Node captureAdherenceNode = new Node();
        captureAdherenceNode.addPrompts(adherenceSummaryPrompts(whpivrMessage, ivrSession.patientsWithAdherence(), ivrSession.patientsWithoutAdherence()));
        if (ivrSession.hasPatientsWithoutAdherence()) {
            captureAdherenceNode.addPrompts(captureAdherencePrompts(whpivrMessage, ivrSession.currentPatientId(), ivrSession.currentPatientNumber()));
            captureAdherenceNode.addTransition("?", new AdherenceCaptureTransition());
            return captureAdherenceNode;
        } else {
            captureAdherenceNode.addPrompts(callCompletionPrompts(whpivrMessage));
            return captureAdherenceNode;
        }
    }

}
