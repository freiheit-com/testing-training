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

    private static final String PROJECT_FOO_PROJECT_BAR = "[{\"project\": \"Foo\"}, {\"project\": \"Bar\"}]";
    private static final List<String> FOO_BAR_LIST = Arrays.asList( "Foo", "Bar" );

    @Test
    public void shouldReadEmptyList() {
        //given
        final ITestStatisticServer iServer = mock( ITestStatisticServer.class );
        given( iServer.readProjects() ).willReturn( "[]" );

        final TestStatisticDataService service = makeDataService( iServer );
        //when
        final List<String> projects = service.getProjects();
        //then
        assertThat( projects, is( Collections.emptyList() ) );
    }

    @Test
    public void shouldAlwaysReadDataFromServerOnFirstCall() {
        //given
        final ITestStatisticServer iServer = mock(ITestStatisticServer.class);
        given( iServer.readProjects() ).willReturn( PROJECT_FOO_PROJECT_BAR );
        
        final TestStatisticDataService service = makeDataService( iServer );
        //when
        final List<String> projects = service.getProjects();
        //then
        assertThat( projects, is( FOO_BAR_LIST ) );
    }

    @Test
    public void shouldReturnCachedDataIfDataFreshEnough() {
        //given
        final ITestStatisticServer iServer = mock( ITestStatisticServer.class );
        given( iServer.readProjects() ).willReturn( PROJECT_FOO_PROJECT_BAR );

        final TestStatisticDataService service = makeDataService( iServer );
        //when
        final List<String> projects = service.getProjects();
        assertThat( projects, is( FOO_BAR_LIST ) );
        
        // data changed on server
        given( iServer.readProjects() ).willReturn( "[{\"project\": \"New\"}, {\"project\": \"Projects\"}]" );

        // then
        final List<String> projectsSecondRead = service.getProjects();
        assertThat( projectsSecondRead, is( FOO_BAR_LIST ) ); // still returns the "old" data since they are not old enough
    }

    @Test
    public void shouldRequeryDataIfDataTooOld() {
        //given
        final ITestStatisticServer iServer = mock( ITestStatisticServer.class );
        given( iServer.readProjects() ).willReturn( PROJECT_FOO_PROJECT_BAR );

        final ICurrentTimeProvider timeProvider = mock( ICurrentTimeProvider.class );
        final LocalDateTime fakeNow = LocalDateTime.of( 2010, 8, 7, 8, 43 );
        given( timeProvider.getCurrentTime() ).willReturn( fakeNow );

        final TestStatisticDataService service = makeDataService( iServer, timeProvider );
        //when
        final List<String> projects = service.getProjects();
        assertThat( projects, is( FOO_BAR_LIST ) );

        // data changed on server
        given( iServer.readProjects() ).willReturn( "[{\"project\": \"New\"}, {\"project\": \"Projects\"}]" );

        // and one minute passed
        given( timeProvider.getCurrentTime() ).willReturn( fakeNow.plus( Duration.ofSeconds( 61 ) ) );

        // then
        final List<String> projectsSecondRead = service.getProjects();
        assertThat( projectsSecondRead, is( Arrays.asList( "New", "Projects" ) ) );
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
        return makeDataService( server, new CurrentTimeProvider() );
    }

    private TestStatisticDataService makeDataService( final ITestStatisticServer server, final ICurrentTimeProvider timeProvider ) {
        return new TestStatisticDataService( server, timeProvider );
    }

    // TODO Write actual ITestStaticServer implementation with HTTPClient -> fails, because
    //      format wrong ---> something for integration tests??!!!

}
