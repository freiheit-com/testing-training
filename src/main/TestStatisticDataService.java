
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TestStatisticDataService {

    private final ITestStatisticServer server;
    private final ICurrentTimeProvider time;

    private LocalDateTime lastRetrievalTime;
    private List<String> cachedProjects;

    public TestStatisticDataService( final ITestStatisticServer server, final ICurrentTimeProvider time ) {
        this.server = server;
        this.time = time;
    }

    /**
     * This method returns a list of project-names that are currently used in
     * the statistic-server.
     * 
     * Since we call this method very often, we want to cache the project data.
     * The function may return the list of projects as they existed one minute
     * ago in the statistic-server.
     */
    public List<String> getProjects() {

        if ( lastRetrievalTime != null && !dataTooOld() ) {
            return cachedProjects;
        }

        final String rawResult = this.server.readProjects();
        lastRetrievalTime = time.getCurrentTime();

        final JsonParser parser = new JsonParser();
        final JsonElement jsonElement = parser.parse( rawResult ).getAsJsonObject().get( "projects" );

        final JsonArray array = jsonElement.getAsJsonArray();
        final ArrayList<String> result = new ArrayList<>( array.size() );
        for ( int i = 0; i < array.size(); i++ ) {
            final JsonObject obj = array.get( i ).getAsJsonObject();
            result.add( obj.get( "project" ).getAsString() );
        }

        cachedProjects = result;
        return result;
    }

    private boolean dataTooOld() {
        return time.getCurrentTime().minus( Duration.ofMinutes( 1 ) ).isAfter( lastRetrievalTime );
    }
}