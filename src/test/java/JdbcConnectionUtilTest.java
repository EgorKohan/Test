import com.netcracker.cats.util.JdbcConnectionUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class JdbcConnectionUtilTest {

    private static JdbcConnectionUtil jdbcConnectionUtil;

    @BeforeClass
    public static void initialize() {
        jdbcConnectionUtil = JdbcConnectionUtil.getInstance();
    }

    @Test
    public void foo() {
        Assert.assertNotNull(jdbcConnectionUtil.getConnection());
    }

}
