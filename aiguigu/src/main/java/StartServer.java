import java.io.IOException;

/**
 * Created by chenminghe on 2017/6/17.
 */
public class StartServer {
    public static void main(String[] args) {
        _05TestNonBlockingNIO tt = new _05TestNonBlockingNIO();
        try {
            tt.server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
