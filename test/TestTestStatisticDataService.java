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


    @Test
    public void shouldReturnEmptyListIfThereAreNoProjectsOnTheServer() {
        //given

        final String serverResult = "{\"projects\":[]}";

        final ITestStatisticServer dummyServer = mock( ITestStatisticServer.class );
        given( dummyServer.readProjects() ).willReturn( serverResult );

        final ICurrentTimeProvider timeProvider = mock( ICurrentTimeProvider.class );
        given( timeProvider.getCurrentTime() ).willReturn( LocalDateTime.of( 2010, 8, 7, 8, 43 ) );

        final TestStatisticDataService service = new TestStatisticDataService( dummyServer, timeProvider );
        //when
        final List<String> result = service.getProjects();
        //then
        assertThat( result, is( Collections.emptyList() ) );
    }

    @Test
    public void shouldReturnGivenProjectFromServer() {
        //given
        final String serverResult = "{\"projects\":[{\"project\":\"betty_ordercapture\",\"subprojects\":[]}]}";

        final ITestStatisticServer dummyServer = mock( ITestStatisticServer.class );
        given( dummyServer.readProjects() ).willReturn( serverResult );

        final ICurrentTimeProvider timeProvider = mock( ICurrentTimeProvider.class );
        given( timeProvider.getCurrentTime() ).willReturn( LocalDateTime.of( 2010, 8, 7, 8, 43 ) );
        
        final TestStatisticDataService service = new TestStatisticDataService( dummyServer, timeProvider );

        //when
        final List<String> result = service.getProjects();
        //then
        assertThat( result, is( Arrays.asList( "betty_ordercapture" ) ) );
    }

    @Test
    public void shouldReturnGivenProjectsFromServer() {
        //given
        final String serverResult =
                "{\"projects\":[{\"project\":\"betty_ordercapture\",\"subprojects\":[]}, {\"project\":\"betty_price\",\"subprojects\":[]}, {\"project\":\"betty_foo\",\"subprojects\":[]}]}";

        final ITestStatisticServer dummyServer = mock( ITestStatisticServer.class );
        given( dummyServer.readProjects() ).willReturn( serverResult );

        final ICurrentTimeProvider timeProvider = mock( ICurrentTimeProvider.class );
        given( timeProvider.getCurrentTime() ).willReturn( LocalDateTime.of( 2010, 8, 7, 8, 43 ) );

        final TestStatisticDataService service = new TestStatisticDataService( dummyServer, timeProvider );

        //when
        final List<String> result = service.getProjects();
        //then
        assertThat( result, is( Arrays.asList( "betty_ordercapture", "betty_price", "betty_foo" ) ) );
    }

    @Test
    public void shouldReturnTheSameListForOneMinuteAndAfterwardsReturnTheNewList() {
        //given
        final String serverResult = "{\"projects\":[{\"project\":\"initial\",\"subprojects\":[]}]}";

        final ITestStatisticServer dummyServer = mock( ITestStatisticServer.class );
        given( dummyServer.readProjects() ).willReturn( serverResult );
        
        final ICurrentTimeProvider timeProvider = mock(ICurrentTimeProvider.class);
        given(timeProvider.getCurrentTime()).willReturn(LocalDateTime.of( 2010, 8, 7, 8, 43 ));
        final TestStatisticDataService service = new TestStatisticDataService( dummyServer, timeProvider );

        //when
        final List<String> resultFirst = service.getProjects();
        assertThat( resultFirst, is( Arrays.asList( "initial") ) );
        
        given( dummyServer.readProjects() ).willReturn( "{\"projects\":[{\"project\":\"manipulated\",\"subprojects\":[]}]}");;
        final List<String> resultSecond = service.getProjects();
        assertThat( resultSecond, is( Arrays.asList( "initial" ) ) );
        
        given( timeProvider.getCurrentTime() ).willReturn( LocalDateTime.of( 2010, 8, 7, 8, 45 ) );

        final List<String> resultThird = service.getProjects();
        assertThat( resultThird, is( Arrays.asList( "manipulated" ) ) );
    }
}
