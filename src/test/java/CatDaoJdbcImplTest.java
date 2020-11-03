import com.netcracker.cats.util.JdbcConnectionUtil;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CatDaoJdbcImplTest {

    @Test
    public void shouldReturnTrue() throws SQLException {
        Connection connection = new JdbcConnectionUtil().getConnection();
        final ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM netcracker.cats " +
                "LEFT JOIN netcracker.parents ON cats.id = parents.child_id " +
                "ORDER BY child_id IS NULL desc ");
        resultSet.next();
        Long childId = resultSet.getLong("child_id");
        System.out.println(resultSet.getString("name"));
        System.out.println(childId);
        Assert.assertNull(childId);
    }

}
