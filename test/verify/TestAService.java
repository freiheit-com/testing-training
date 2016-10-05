package verify;

import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestAService {
    @Test
    public void shouldWriteToDao() {
        //given
        final ADao dao = mock( ADao.class );
        final AService service = new AService( dao );
        //when
        service.doesSomething( "value" );
        //then

        verify( dao ).storeSomething( "value_stored" );
    }
}
