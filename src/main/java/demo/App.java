package demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SpringBootApplication
@Controller
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    JavaScriptEngine nashornEngine() {
        return new JavaScriptEngine()
                .polyfillToNashorn()
                .loadFromClassPath("META-INF/resources/webjars/react/0.12.2/react.min.js")
                .loadFromClassPath("META-INF/resources/webjars/showdown/0.3.1/compressed/showdown.js")
                .loadFromClassPath("static/tutorial.js");
    }

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JavaScriptEngine nashorn;

    static final List<Comment> comments = new CopyOnWriteArrayList<>();

    @RequestMapping("/")
    String hello(Model model) throws JsonProcessingException {
        String markup = nashorn.invokeFunction("renderOnServer", String::valueOf, comments);
        String initialData = objectMapper.writeValueAsString(comments);
        model.addAttribute("markup", markup);
        model.addAttribute("initialData", initialData);
        return "index";
    }

    @ResponseBody
    @RequestMapping(value = "/comments", method = RequestMethod.GET)
    List<Comment> getComments() {
        return comments;
    }

    @ResponseBody
    @RequestMapping(value = "/comments", method = RequestMethod.POST)
    List<Comment> postComments(@RequestBody Comment comment) {
        comments.add(comment);
        return comments;
    }

    @PostConstruct
    void init() {
        comments.addAll(Arrays.asList(
                new Comment("Pete Hunt", "This is one comment"),
                new Comment("Jordan Walke", "This is *another* comment")));
    }
}

class Comment {
    public String author;
    public String text;

    Comment() {
    }

    public Comment(String author, String text) {
        this.author = author;
        this.text = text;
    }
}
