import com.netcracker.cats.model.Cat;
import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;
import org.junit.Test;

import java.util.List;

public class CatServiceImplTest {

    @Test
    public void shouldWriteCatNamesWithSubstring(){
        CatService catService = new CatServiceImpl();
        final List<Cat> as = catService.findCatsByName("as");
        for (Cat cat : as) {
            System.out.println(cat);
        }
    }

}
