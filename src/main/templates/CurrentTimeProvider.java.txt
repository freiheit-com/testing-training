import java.time.LocalDateTime;

public class CurrentTimeProvider implements ICurrentTimeProvider {
    @Override
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
}
