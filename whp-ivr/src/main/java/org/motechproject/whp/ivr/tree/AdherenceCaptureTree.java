package org.motechproject.whp.ivr.tree;

import org.motechproject.decisiontree.model.*;
import org.motechproject.decisiontree.repository.AllTrees;
import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.whp.adherence.service.WHPAdherenceService;
import org.motechproject.whp.ivr.transition.CaptureAdherence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdherenceCaptureTree {

    private AllTrees allTrees;

    private WHPAdherenceService whpAdherenceService;

    @Autowired
    public AdherenceCaptureTree(AllTrees allTrees, WHPAdherenceService whpAdherenceService) {
        this.allTrees = allTrees;
        this.whpAdherenceService = whpAdherenceService;
    }

    @Seed(priority = 0, version = "1.0")
    public void load() {
        Tree adherenceCapture = new Tree()
                .setName("adherenceCapture")
                .setRootNode(new Node()
                        .addPrompts(new TextToSpeechPrompt().setMessage("Press 1 to enter adherence"))
                        .addTransition(
                                "1", new CaptureAdherence()
                        )
                );
        allTrees.addOrReplace(adherenceCapture);
    }
}