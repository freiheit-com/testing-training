import java.util.function.Function;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

public class TestDummy {
    @Test
    private void test() {

        final Function<String, String> dummyDep = Mockito.mock( Function.class );
        given( dummyDep.apply( any() ) ).willReturn( "bar" );

        final Dummy dummy = new Dummy( dummyDep );
        final String result = dummy.callIt();

        assertThat( result, is( "baz" ) );
    }
}
