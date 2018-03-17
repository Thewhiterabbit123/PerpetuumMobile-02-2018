package server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.MvcConfig;
import server.messages.Message;
import server.messages.MessageStates;
import server.model.Paginator;
import server.model.Scoreboard;
import server.model.User;
import server.services.UserService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

//@CrossOrigin(origins = MvcConfig.getOrigins(), allowCredentials = MvcConfig.getAllowCredentials())
@CrossOrigin(origins = {"http://127.0.0.1:3000", "http://localhost:3000", "https://blend-front.herokuapp.com", "https://blendocu.herokuapp.com"}, allowCredentials = "true")
@RestController
public class ScoreboardController {

    private final UserService userService;

    public ScoreboardController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/users", produces = "application/json")
    public ResponseEntity scoreboard(@RequestBody Scoreboard pageNum, HttpSession httpSession) {
        Integer pageNumber = Integer.parseInt(pageNum.getPage());
        Integer onOnePage = 10;
        Integer to = pageNumber * onOnePage;

        if (pageNumber < 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(MessageStates.BAD_DATA));
        }

        List<User> usersFromDb = userService.getAllUsers();

        usersFromDb.sort((user1, user2) -> user2.getScore() - user1.getScore());

        Integer from = (pageNumber - 1) * onOnePage;

        to = to > usersFromDb.size() ? usersFromDb.size() : to;

        usersFromDb = usersFromDb.subList(from, to);

        List<User> users = new ArrayList<>();

        for (User copyLoginScore : usersFromDb) {
            users.add(new User(copyLoginScore.getLogin(), "", "", copyLoginScore.getScore()));
        }

        Integer maxPageNum = (usersFromDb.size() + (onOnePage - 1)) / onOnePage;

        Paginator<List<User>> paginator = new Paginator<>(maxPageNum, users);

        return ResponseEntity.status(HttpStatus.OK).body(paginator);
    }
}
