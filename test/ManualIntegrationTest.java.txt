public class ManualIntegrationTest {
    public static void main( final String[] args ) {
        final ITestStatisticServer server = new LiveTestStatisticServer();
        final TestStatisticDataService service = new TestStatisticDataService( server,
                new CurrentTimeProvider() );

        System.out.println( service.getProjects() );
    }
}
