import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LiveTestStatisticServer implements ITestStatisticServer {

    @Override
    public String readProjects() {
        
        if ( true ) {
            throw new IllegalStateException(
                    "need auth-token to talk to live statistic server (can be found in the slack channel" );
        }

        try {
            final ProcessBuilder processBuilder = new ProcessBuilder("curl", "-kv", "https://130.211.118.12/meta/projects", 
                    "-H", "Content-Type: application/json", 
                    "-H", "auth-token: <add-auth-token-here>" );
            
            final BufferedReader reader = new BufferedReader( new InputStreamReader( processBuilder.start().getInputStream() ) );
            
            final StringBuilder result = new StringBuilder();
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                result.append( line );
            }
            return result.toString();
        } catch ( final IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public static void main( final String[] args ) {
        final LiveTestStatisticServer liveTestStatisticServer = new LiveTestStatisticServer();
        liveTestStatisticServer.readProjects();
    }
}