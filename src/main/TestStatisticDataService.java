
import java.util.List;

public class TestStatisticDataService {

    public TestStatisticDataService() {
    }

    /**
     * This method returns a list of project-names that are currently used in
     * the statistic-server.
     * 
     * Since we call this method very often, we want to cache the project data.
     * The function may return the list of projects as they existed one minute
     * ago in the statistic-server.
     * 
     */
    public List<String> getProjects() {
        return null;
    }
}
