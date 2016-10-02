import java.util.function.Function;

public class Dummy {

    private final Function<String, String> _f;

    public Dummy( final Function<String, String> dummyDep ) {
        this._f = dummyDep;
    }

    public String callIt() {
        return _f.apply( "foo" );
    }
}
