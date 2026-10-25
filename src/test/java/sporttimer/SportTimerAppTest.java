package sporttimer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SportTimerAppTest {

    @Autowired
    MockMvc mvc;

    @Test
    void servesThePage() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("index.html"));
    }

    // sw.js saves these files for offline use; if one is missing, the install fails and the app
    // doesn't work without internet
    @Test
    void servesEveryFileTheOfflineCopyNeeds() throws Exception {
        for (String file : new String[]{"index.html", "style.css", "app.js", "sw.js", "manifest.webmanifest",
                "favicon.png", "apple-touch-icon.png", "icon-192.png", "icon-512.png"}) {
            mvc.perform(get("/" + file))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Cache-Control", containsString("no-cache")));
        }
    }
}
