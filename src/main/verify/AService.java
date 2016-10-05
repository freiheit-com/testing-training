package verify;

public class AService {

    private final ADao _dao;

    public AService(final ADao dao) {
        _dao = dao;
    }

    public void doesSomething( final String str ) {
        _dao.storeSomething( str + "_stored" );
    }
}
