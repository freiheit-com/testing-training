
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TestStatisticDataService {

    private final ITestStatisticServer _server;
    private final ICurrentTimeProvider _timeProvider;

    private List<String> _cachedData = null;
    private LocalDateTime _cachedDataTime = null;

    public TestStatisticDataService( final ITestStatisticServer server, final ICurrentTimeProvider timeProvider ) {
        _server = server;
        _timeProvider = timeProvider;
    }

    public List<String> getProjects() {

        if ( !dataTooOld() ) {
            return _cachedData;
        }

        final String projectsRaw = _server.readProjects();
        final JsonParser parser = new JsonParser();
        final JsonElement elem = parser.parse( projectsRaw ).getAsJsonObject().get( "projects" );

        final JsonArray array = elem.getAsJsonArray();
        final ArrayList<String> result = new ArrayList<>( array.size() );

        for ( int i = 0; i < array.size(); i++ ) {
            final JsonObject obj = array.get( i ).getAsJsonObject();
            result.add( obj.get( "project" ).getAsString() );
        }

        _cachedData = Collections.unmodifiableList( result );
        _cachedDataTime = _timeProvider.getCurrentTime();
        return _cachedData;
    }

    private boolean dataTooOld() {
        return _cachedData == null || _cachedDataTime == null ||
                _timeProvider.getCurrentTime().isAfter( _cachedDataTime.plus( Duration.ofSeconds( 60 ) ) );
    }
}
