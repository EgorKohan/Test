import com.netcracker.cats.dao.CatDao;
import com.netcracker.cats.dao.CatDaoJdbcImpl;
import com.netcracker.cats.model.Cat;
import com.netcracker.cats.model.Gender;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;

public class CatDaoJdbcImplTest {

    @Ignore
    @Test
    public void foo() throws SQLException {
        CatDao catDao = new CatDaoJdbcImpl();
        Cat cat = catDao.create(Cat.builder()
                .name("Rizhik")
                .age(2)
                .color("gray")
                .gender(Gender.MALE)
                .build()
        );
        Assert.assertNotNull(cat);
    }

}
