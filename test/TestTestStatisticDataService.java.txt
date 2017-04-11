import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class TestTestStatisticDataService {

    private static final String PROJECT_FOO_PROJECT_BAR = "{\"projects\": [{\"project\": \"Foo\"}, {\"project\": \"Bar\"}]}";

    @Test
    public void shouldReturnEmptyListIfServerReturnsEmptyList() {
        //given
        final ITestStatisticServer mockedServer = mock( ITestStatisticServer.class );
        given( mockedServer.readProjects() ).willReturn( "{\"projects\": []}" );
        final TestStatisticDataService service = makeDataService( mockedServer );
        //when
        final List<String> result = service.getProjects();
        //then
        assertThat( result, is( Collections.emptyList() ) );
    }

    @Test
    public void shouldReturnProjectsNamesReturnFromJsonList() {
        //given
        final ITestStatisticServer mockedServer = mock( ITestStatisticServer.class );
        given( mockedServer.readProjects() ).willReturn( PROJECT_FOO_PROJECT_BAR );
        final TestStatisticDataService service = makeDataService( mockedServer );
        //when
        final List<String> projects = service.getProjects();
        //then
        assertThat( projects, is( Arrays.asList( "Foo", "Bar" ) ) );
    }

    @Test
    public void shouldNotReadDataFromServerIfDataAreNotTooOld() {
        //given
        final ITestStatisticServer mockedServer = mock( ITestStatisticServer.class );
        given( mockedServer.readProjects() ).willReturn( PROJECT_FOO_PROJECT_BAR );
        final TestStatisticDataService service = makeDataService( mockedServer );
        //when
        service.getProjects();
        
        given( mockedServer.readProjects() ).willReturn( "{\"projects\": [{\"project\": \"New\"}, {\"project\": \"Project\"}]}" );
        
        final List<String> secondRead = service.getProjects();
        //then
        assertThat( secondRead, is( Arrays.asList( "Foo", "Bar" ) ) );
    }

    @Test
    public void shouldRequeryDataIfOneMinuteHasPassed() throws Exception {
        //given
        final ITestStatisticServer mockedServer = mock( ITestStatisticServer.class );
        given( mockedServer.readProjects() ).willReturn( PROJECT_FOO_PROJECT_BAR );

        final ICurrentTimeProvider time = mock( ICurrentTimeProvider.class );
        final LocalDateTime startTime = LocalDateTime.of( 2010, 8, 7, 8, 43 );
        given( time.getCurrentTime() ).willReturn( startTime );

        final TestStatisticDataService service = makeDataService( mockedServer, time );
        //when
        service.getProjects();

        given( mockedServer.readProjects() ).willReturn( "{\"projects\": [{\"project\": \"New\"}, {\"project\": \"Project\"}]}" );
        given( time.getCurrentTime() ).willReturn( startTime.plus( Duration.ofSeconds( 61 ) ) );
        final List<String> secondRead = service.getProjects();
        //then
        assertThat( secondRead, is( Arrays.asList( "New", "Project" ) ) );
    }

    @Test( expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ".*terribly.*" )
    public void shouldFailIfDataReadFails() {
        //given
        final ITestStatisticServer iServer = mock( ITestStatisticServer.class );
        given( iServer.readProjects() ).willThrow( new RuntimeException( "reading projects failed terribly" ) );

        final TestStatisticDataService service = makeDataService( iServer );
        //when
        service.getProjects();
        //then
        // all good if test fails!
    }

    // helper methods

    private TestStatisticDataService makeDataService( final ITestStatisticServer server ) {
        return new TestStatisticDataService( server, new CurrentTimeProvider() );
    }

    private TestStatisticDataService makeDataService( final ITestStatisticServer server, final ICurrentTimeProvider time ) {
        return new TestStatisticDataService( server, time );
    }

}
