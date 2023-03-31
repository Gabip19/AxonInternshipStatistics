package axon.statistics.processor.dataloader;

import axon.statistics.domain.Submission;

import java.io.InputStream;
import java.util.HashMap;

public interface SubmissionLoader {
    HashMap<String, Submission> loadSubmissions(InputStream stream);
}
